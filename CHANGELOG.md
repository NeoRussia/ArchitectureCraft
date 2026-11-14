# Changelog

## 3.108-0.5

- The One Probe support: probing a shape shows its name and material with the
  mod name, plus the cladding, the light a glowing shape gives off, the arms of
  walls and fences and, when sneaking, the texture turn the paintbrush set. The
  probe also reports the tool the material needs, and whether the held tool can
  harvest it. Everything is optional: without The One Probe installed nothing
  changes and nothing extra is loaded.
- Shapes need the tool their material needs. A stone or obsidian shape no
  longer drops when broken by hand; it wants the same pickaxe its material
  would. Before this, every shape was treated as needing no tool at all.
- Redrawing a chunk that holds shapes costs a fraction of what it did. Light
  was sampled from the world once per vertex and the shape's own light value
  re-read for every sampled cell; both are now read once per shape, and
  repeated neighbour checks behind the translucent seam culling are cached. A
  dense wall of shapes re-bakes about three to four times faster.

## 3.108-0.4

- Fence picking uses the selection box again instead of the collision list, so
  a click on a fence cap reports the top face and blocks can be placed on
  fences. Collision arms narrowed to vanilla's 2 px.
- The chisel strips cladding from thin shapes: slope ends, sidings, paneling,
  plaque and wedges treat any click as a centre click instead of toggling a
  connection that does not exist. Walls and fences keep their edge zones.
- Added the Paintbrush: use turns a shape's texture a quarter turn, shift-use
  toggles the mirror. The orientation is per block, saved with the tile,
  synced to clients and inherited by a piece placed against a turned one.

## 3.108-0.3

- Siding placement answers the mouse: on a wall the half of the face you click
  decides which way the bevel runs, so both left/right orientations are
  reachable at placement. Holding shift takes the slope's own quadrant rule
  instead, adding the up/down pair, and a siding placed against another shape
  now follows its orientation like a slope does; shift disables that follow.
- Double Siding copies the orientation of a neighbouring Double Siding, so a
  run stays consistent; otherwise the clad half still turns towards the
  player. Shift skips the copy.
- Double Roof Tile: shift skips the neighbour copy, matching Double Siding and
  the ordinary shapes.
- New Slopes page, holding the nine slope tiles beside nine new corner wedges.
  A wedge is one band of a corner pyramid: the base band fills the block, the
  bands above shrink toward the apex, and the apex lands on the top corner of
  the tip block. Three families - 1x2 in two bands, 1x3 in three, 1x4 in four -
  with the family letter on the sawbench icon. Placement is the roof rule, but
  a wedge only continues into its own family; shift skips that. Glow duplicates
  sit on a new Glow 2 page below Glow.
- The Other page now sits below Siding.

## 3.108-0.2

- Fence picking now matches vanilla: the selection and pick box is the post
  plus 4 px arms one block tall (`BlockFence.getBoundingBox`), while entity
  collision keeps the post and the full-width arms at 1.5 blocks. The arms are
  outlined when hovered and the fence still cannot be jumped.
- `BlockArchitecture.collisionRayTrace` now asks `getCollisionBoundingBox` for
  one box, as `Block.collisionRayTrace` does, instead of testing the collision
  list, so picking and physics are independent again.
- Fences report `PathNodeType.FENCE`, other solid shapes `BLOCKED`, and slabs,
  stairs and the roof family stay walkable, so mobs path around a fence
  instead of walking into it.
- Shapes take their step, break and hit sounds from the material they were cut
  from; a wooden fence no longer sounds like stone.
- Paneling and the GregTech plaque lie flat on a floor or ceiling click, with
  the cladding or pattern facing the room and the piece's top pointing away
  from the player. Wall placement is unchanged.
- The GregTech plaque now faces out of every wall. The north case returned the
  west turn, so a plaque on a north wall came out sideways.
- Double Siding turns its clad half towards the player on placement, using the
  clicked face on a wall and the player's facing on floor and ceiling.
- Double Roof Tile placement follows three rules: a tile placed against another
  copies its orientation, a floor or ceiling click turns it to face the player,
  and a wall click keeps the click-point diagonal. The tile is now a full cube
  for collision and side solidity, so torches, levers, rails and buttons attach
  to all six faces and nothing falls into its north edge.

## 3.108-0.1

- Rebuilt both sawbench menu icon atlases from the shapes' own geometry: one
  render per shape, purple with a smooth shade ramp, cladding parts in yellow
  (cladding sheet, double roof tile, double siding), window glazing left out so
  the frames stay open, faint A/B/C letters on the slope families in Other,
  Glow and Siding, and a yellow outline around every Glow icon. Wall now shows
  a straight run instead of a bare post.
- Added shape ID 117, Wall. It connects to other walls and to full cubes in
  the four horizontal directions, using a slim core for straight runs and a
  post for corners, T junctions and crossings. Collision follows the visible
  arms, and walls update when a neighbour appears or disappears.
- The wall is one shape ID; the 16 connection masks are derived from the world
  and respect `TileShape.disabledConnections`.
- One material block makes two walls, the hammer is a no-op for walls, and
  cladding is not accepted.
- Added shape ID 118, Fence. It mirrors vanilla fences: a 4x16x4 post with
  thin rails towards every side that connects to a fence or a full cube,
  1.5-block collision so it cannot be jumped over, and a post-only selection
  box. One material block makes four fences.
- Added shape IDs 119-128, the Siding versions of Roof Tile and the nine
  slopes. They use the same geometry, profiles and connections as the roofing
  shapes but always place upright: on a wall the visible face points out of
  the wall, on the floor or ceiling the piece stands facing the player. Their
  sloped faces use the model UVs with the grain turned, so the material does
  not read sideways on a wall.
- Added shape ID 129, Paneling: a 3 px vertical panel flush with the block
  boundary, mounted on the clicked face, with cladding on its room-facing
  side. One material block makes four panels.
- Added shape ID 130, Double Siding: a full block cut vertically down the
  middle, with one half taking the cladding material.
- Shapes now stop light at their block: `BlockShape` reports the shape kind's
  light opacity (255 for solid shapes, 0 for windows and the cladding sheet),
  where it previously reported 0 through the non-opaque block default.
- Light opacity is now contextual: 15 only for shapes whose model fills the
  whole block, 4 for slab-like and other near-full hulls, 0 for thin ones,
  cached per tile entity.
- The GregTech plaque is drawn as authored again: an earlier build mirrored the
  model along X, which left the logo reading backwards once placed. The model is
  restored, and a per-shape item turn (`ShapeKind.itemTurnOffset`, three
  quarters for the plaque) makes the item show the same face the placed block
  does.
- Added shape ID 131, GregTech: a 2 px plaque carrying the 16x16 GregTech
  logo pattern, mounted on the clicked face.
- Fixed siding item icons: item stacks now read the shape's default side and
  turn, and the item transform's half turn is undone so the long face points at
  the player instead of away.
- Custom block geometry skips its own directional shade while an
  OptiFine/Iris shader pack is active, so shaders light it once instead of
  twice. The API is reached by reflection; no shader mod is required. Vertex
  normals are deliberately not written: Forge's `BufferBuilder` maps each write
  to the next element of the active vertex format, and the terrain format has
  no normal element when shaders are off, so a normal write overwrote the
  position and broke every chunk holding a shape.
- Baked 3D item models now fold the face shade into their vertex colours, so the
  sawbench and other custom 3D items are lit in the HUD and hand instead of
  rendering full-bright.
- Fixed the item texture path used by item sprite registration: item textures now
  resolve under textures/items/ instead of textures/textures/.
- Faces that lie flat on a block boundary are dropped when the neighbouring
  block has a face with the same corners on the same plane. Every shape draws
  the faces of its own block, so two glass shapes side by side draw two coplanar
  faces at their shared boundary; an opaque material hides the extra one behind
  the depth buffer, but a translucent one blends it in as a band of extra glass
  along every block boundary. The neighbour is asked by rendering it into a
  corner probe, so this also works between different shapes, which is what the
  pieces of a slope family are: Slope B start, middle and end are separate
  blocks whose joint faces coincide exactly.
- Fixed sawbench running, landing, hit and destroy particles: the custom block
  particle models are now installed in the model registry, and the particle
  lookup uses the base state instead of the extended state.
- Removed a GL state call from the item-model baking path; item models can be
  baked off-thread, where LWJGL3 aborts the JVM for want of a context.
- Added side solidity for shape faces, so torches, rails, levers and buttons can
  attach to full square, slab and stair faces.
- Fixed the projected texture on the underside of shapes being flipped.
- Reworked shape ambient occlusion to sample a truncated shifted cube, matching
  the surrounding full blocks instead of darkening their edges.
- Fixed the placement preview disappearing for some base materials: the preview
  now binds the block atlas and lightmap before drawing instead of sampling
  whatever texture the entity pass left bound.
- Fixed sprint particles on shapes using a missing texture: running effects now
  spawn the particles of the shape's base material instead of the bare shape
  block.
- Added the Siding sawbench page.

## 1.12.2-3.108-reloaded.1

ArchitectureCraft: Renovated, based on TridentMC 1.12-3.108.

- Added GTNH shapes IDs 94-116: nine 1x2/1x3/1x4 slope pieces, nine glowing
  slope variants, glowing Roof Tile, Square and Slab, Angled Roof Ridge, and
  Double Roof Tile.
- Glow uses the existing `LIGHT` blockstate at 15; no separate `shapeSE` block.
- Preserved glow light through sawbench output, creative-tab items, break drops
  and middle-click pick-block.
- Added the Glow sawbench page and extended the Other page.
- Split the menu icon atlas at ID 110 (`shapemenu_items_1.png`).
- Restored the Double Roof Tile's complementary front/back triangles so the
  base/cladding diagonal split is complete.
- Existing shape IDs 0-93 and existing worlds/structures are unchanged.
