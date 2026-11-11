# Credits

ArchitectureCraft: Reloaded is a 1.12.2 continuation of
TridentMC/ArchitectureCraft with shapes ported from
GTNewHorizons/ArchitectureCraft.

## Original ArchitectureCraft

- **Greg Ewing (gcewing)** — original ArchitectureCraft code and models.
- **darkevilmac / unascribed / TridentMC contributors** — the 1.12 port and the
  original 1.12 shape models and menu icons.

## GTNH shape assets and code used by this port

The following is based on the commit history of
[GTNewHorizons/ArchitectureCraft](https://github.com/GTNewHorizons/ArchitectureCraft)
at tag `1.12.19`.

| Contributor | Contribution |
|---|---|
| **Sampsa Vanninen (`S4mpsa`)** | 1x2/1x3/1x4 slope renderer and sawbench entries; slope menu icons (2023-03-14). |
| **Superfrogman98** | Angled Roof Ridge model (originally `.smeg`); Double Roof Tile model and its cladding/diagonal-split fixes; glow-shape menu icons; sawbench IDs >109 and the split icon sheet; the glowbrush (2023–2024). |
| **Imgood (`ImgoodWK`)** | Glow shape category and `shapeSE`/glow handling; the Square model (2024). |
| **Jimbno** | Shape-menu atlas, sawbench background and GUI art tweaks (2023-04-08). The commit messages do not identify individual icon cells. |
| **UltraProdigy** | Image optimization of the GTNH GUI atlases (2026-03-08). Not original icon design. |
| **Jason Mitchell** | `.smeg` → `.objson` model-format rename (2024-02-24). Not original model design. |
| **Albi (`Flanisch`)** | Shape/name localization support and selected-page colour overrides (2024). |
| **Ranzu (`Ranzuu`)** | Localization keys for shape values (2026). |

The common shapes and models not added by GTNH remain the work of gcewing and
the TridentMC/1.12 contributors.

## Reloaded additions

- **Wall (shape ID 117)** — new code, models and menu icon written for this
  fork. The connection behaviour is modelled on Industrial Renewal's concrete
  wall (`BlockBaseWall`), which is CC BY-NC-SA 4.0; no Industrial Renewal code,
  models or textures are used.
- **Fence (shape ID 118)** — new code, models and menu icon written for this
  fork. It reproduces vanilla fence behaviour and proportions; no Mojang code,
  models or textures are used.
- **Siding (shape IDs 119-128), Paneling (129) and Double Siding (130)** — new
  entries, code and models written for this fork. Siding reuses the existing
  roof and slope renderer; the paneling and double-siding models carry their
  own cladding faces.
- **GregTech (shape ID 131)** - model and code written for this fork. It
  reproduces the GregTech logo as a pixel pattern; GregTech is a mod by
  GregoriusT, and the logo remains its author's.
- **Hash Brown item** - model and texture by **Kasugay**
  (https://sketchfab.com/Kasugay), from
  https://sketchfab.com/3d-models/macdonald-55641025cc30468e953eebbbd5ba80be,
  used under the Sketchfab Standard licence. Converted to a block-space objson
  and stitched as an item texture; no other changes to the source.

## Licence

GTNewHorizons/ArchitectureCraft is MIT, Copyright (c) 2016 gcewing. The full
notice is in [NOTICE](NOTICE) and `META-INF/NOTICE`.

This project is MIT; see [LICENSE](LICENSE).
