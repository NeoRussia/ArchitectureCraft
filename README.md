# ArchitectureCraft — GTNH Shapes

A 1.12.2 fork of [TridentMC/ArchitectureCraft](https://github.com/TridentMC/ArchitectureCraft)
that adds the 23 shape types from [GTNewHorizons/ArchitectureCraft](https://github.com/GTNewHorizons/ArchitectureCraft)
which are missing from the 1.12 version.

Based on TridentMC branch `1.12` at `8f85b04` (release `1.12-3.108`).
This is not an official TridentMC or GTNH release.

## What is added

The shape IDs are the same as GTNH's fork, so existing IDs `0-93` are untouched:

| IDs | Shapes |
|---|---|
| 94-102 | Slope A/B/C start/middle/end pieces |
| 103-111 | The same slopes, self-emissive |
| 112 | Roof Tile, self-emissive |
| 113 | Square, self-emissive |
| 114 | Slab, self-emissive |
| 115 | Angled Roof Ridge |
| 116 | Double Roof Tile |

The shapes use the existing `architecturecraft:shape` block and `TileShape`.
There is no new block, item, tile entity, or per-shape blockstate. The glow
shapes use the existing `LIGHT` blockstate at level 15; GTNH's separate
`shapeSE` block is not used.

The sawbench gets a new **Glow** page, and the 23 shapes are on the existing
**Other** page. The icon atlas is split at ID 110, following GTNH: IDs 0-109
use `shapemenu_items.png`, IDs 110+ use `shapemenu_items_1.png`.

## Compatibility

- Minecraft 1.12.2, Forge 14.23.4.2747.
- Existing shape IDs `0-93` and existing worlds/structures are unchanged.
- A wall made with the new IDs in a world loaded by an older build will fall
  back to `ROOF_TILE` (the mod's unknown-ID fallback), not crash.
- The new **Glow** sawbench page is forward-compatible; an older build opening
  a save whose selected page is 7 may hit the old page-array bounds check.

## Build

The 1.12 branch uses Gradle 4.1, ForgeGradle 2.3 and a Java 8 JDK. The included
wrapper downloads Gradle 4.1.

```powershell
$env:JAVA_HOME = "<path-to-a-JDK-8>"
# Optional: keep the Gradle cache inside the checkout.
$env:GRADLE_USER_HOME = "$PWD\.gradle-home"
.\gradlew.bat build --console=plain
```

The Gradle artifact name is branch/commit based. The tested release artifact is:

```text
architecturecraft-1.12-3.108-gtnh-shapes.jar
SHA256 64DCC0C1318C87F3DF01696B5A50406607189AF00C7AD19FE6C80BE5691E62E9
```

## Install

This replaces the upstream ArchitectureCraft jar; do not keep both:

1. Back up `mods/architecturecraft-1.12-3.108.jar`.
2. Remove it.
3. Put `architecturecraft-1.12-3.108-gtnh-shapes.jar` in `mods/`.
4. Launch and check the sawbench **Other** and **Glow** pages.

For packwiz, host the jar and add a URL download entry:

```toml
name = "ArchitectureCraft - GTNH Shapes"
filename = "architecturecraft-1.12-3.108-gtnh-shapes.jar"
side = "both"

[download]
hash-format = "sha256"
hash = "64DCC0C1318C87F3DF01696B5A50406607189AF00C7AD19FE6C80BE5691E62E9"
mode = "url"
url = "https://<your-host>/architecturecraft-1.12-3.108-gtnh-shapes.jar"
```

Then run `packwiz refresh` in the pack.

## Status

The fork builds cleanly with Zulu 8 and Gradle 4.1. The 23 shapes, models,
lang keys, atlas cells and enum IDs have been statically verified. In-game
testing is still required for rendering, collision, light 15, GUI alignment,
sawbench output and save/load.

## Credits and licence

See [CREDITS.md](CREDITS.md) for the per-contributor asset and code credits.

- Original ArchitectureCraft: Greg Ewing (gcewing), MIT.
- 1.12 port: TridentMC / darkevilmac / unascribed contributors, MIT.
- GTNH shape additions: Sampsa (slopes), Superfrogman98 (angled ridge, double
  roof tile, glow icons), Imgood (glow category), MIT.
- Double Roof Tile geometry uses the GTNH model; its front/back faces are a
  diagonal base/cladding split, not duplicate quads.

The project is MIT. See [LICENSE](LICENSE) and [NOTICE](NOTICE) for the
upstream and GTNH notices.
