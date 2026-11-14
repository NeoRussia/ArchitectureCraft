# How shapes are drawn, and what a rebuild costs

Notes for anyone touching the render path. Numbers are from Flare samples of
the client thread in a test world holding a castle of about four thousand glass
shapes, which is the worst case: dense, translucent, and standing still.

## The path to the screen

Shapes are **not** tile entity renderers. `ClientProxy.registerTileEntitySpecialRenderers`
is empty; `CustomBlockDispatcher` replaces the vanilla `BlockRendererDispatcher`
and `renderBlock` writes each shape's geometry straight into the `BufferBuilder`
of the chunk section being compiled:

```text
RenderGlobal.setupTerrain
└─ ChunkRenderDispatcher → ChunkRenderWorker
   └─ RenderChunk.rebuildChunk          (func_178581_b)
      └─ CustomBlockDispatcher.renderBlock
         └─ customRenderBlockToWorld
            ├─ ShapeRenderDispatch.renderBlock → ShapeKind.renderShape
            │  └─ RenderTargetWorld.addVertex → lightVertex → aoLightVertex
            └─ RenderTargetWorld.end → findHiddenFaces (probe) → emit
```

`ShapeRenderDispatch.renderShapeTE` is also used for item stacks and the
block-preview highlight, which are a different, much cheaper path.

**Consequence:** a static castle costs almost nothing per frame. The cost
appears when a section is compiled again, and it is proportional to how many
shapes that section holds.

## What triggers a rebuild

A block change marks its 16x16x16 section for re-compilation. A light change
marks every section the light reached — and light passes through translucent
shapes, so one block placed beside a glass castle can mark most of it. This is
why interacting with *any* block near a dense build stutters, not only with the
build's own blocks.

## What was measured

| Sample | Workload | setupTerrain incl. rebuilds | Our block render |
|---|---|---|---|
| one minute, idle | standing still | 34.1% | — |
| `8jQaiAb3h2` | placing and breaking | 25.9% | 22.0% |
| `p9hLZz6PGU` | placing and breaking | 10.1% | 5.9% |

Inside a rebuild, before the changes below: `aoLightVertex` 16.6%, seam probe
2.4%, geometry generation ~1.8%, emission and `end` ~1.5%.

Two thirds of a rebuild was light sampling, and almost none of it was geometry.

## Changes

1. **The shape's light value is read once.** `Block.getPackedLightmapCoords`
   re-reads `state.getLightValue()` for every sampled cell; for a shape that
   ends in `BlockShape.getLightValue` → `state.getValue(LIGHT)`, a Guava map
   lookup, 5.2% of the client thread on its own. `RenderTargetWorld` now keeps
   `lightValue` and calls `world.getCombinedLight(pos, lightValue)`, which is
   the same thing for a block that is not a slab.

2. **A memo of the cells a shape samples.** `aoLightVertex` walks a 3x3x3 cube
   around every vertex, and a shape has dozens of vertices over the same small
   neighbourhood. `RenderTargetWorld.LightSamples` keeps each cell's light and
   ambient-occlusion value for the duration of one shape. It is reset per shape,
   not per rebuild, because a value carried across rebuilds could outlive the
   block change that altered it.

3. **Seam probes are cached.** Culling a face that is buried between two shapes
   renders the neighbour into a `ShapeProbe`. The result now comes from a
   thread-local cache keyed by `ShapeProbe.keyFor`: shape, side, turn, the
   chisel's connection bits, the material flags, and
   `ShapeKind.neighbourSignature`.

   That last part is the safety rule. Roof and window shapes read the shapes
   around them *while drawing* (slope junctions, window frames), so their
   signature is `-1` and they are never cached. `Model` and `Cladding` report
   `0`; `ConnectedShape` (walls, fences) reports its four horizontal connection
   bits, which is exactly what `renderShape` reads. A new shape kind inherits
   the `-1` default and is simply slower until someone proves otherwise.

4. **The sampling loop computes each axis once.** The triple loop used to
   recompute each axis' weight for every combination of the other two, and
   allocate a `BlockPos` per lookup. It now collects at most three candidates
   per axis, reuses one mutable position, and visits the same cells in the same
   order, so the sums are bit-identical.

## Result

A section full of shapes compiles roughly three to four times faster; a freeze
that felt like two seconds in game fell below half a second.

## Rejected, with the measurement that rejected it

**A per-variant quad cache** — bake each shape variant's quads once and replay
them — was the next idea on the list. The numbers no longer support it: of the
5.9% a rebuild costs after the changes, 3.4% is `lightVertex`, which is per
vertex *and per position* and therefore cannot be cached across blocks; the
model traversal and UV projection that a cache would remove are together under
0.6%. It would add a large cache with a large invalidation surface for almost
nothing.

The remaining lever is architectural: draw shapes outside the chunk mesh so an
unrelated block edit does not re-bake them. That buys a hitched rebuild at the
price of a permanent per-frame cost, plus rethinking translucency ordering and
the seam culling. Not worth it at half a second.

## Measuring again

Flare is in the pack. `/flarec` is the client command (`/flare` is the server,
which shows nothing useful here):

```text
/flarec sampler start --timeout 60 --save-to-file
```

Reproduce the same interaction during the window, then read
`config/flare/profiler/*.sparkprofile`. Useful names in the tree:
`setupTerrain` (`func_174970_a`), `rebuildChunk` (`func_178581_b`),
`customRenderBlockToWorld`, `lightVertex`, `findHiddenFaces`.
