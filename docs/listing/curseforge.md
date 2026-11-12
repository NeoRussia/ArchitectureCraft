# CurseForge listing

Field values for `https://authors.curseforge.com/#/projects/create/choose-game`.
CurseForge reviews a new project only after the first file is uploaded, and
requires a file of type Release before the project syncs with the app.

| Field | Value |
|---|---|
| Game | Minecraft |
| Class | Mods |
| Name | ArchitectureCraft: Renovated |
| Summary | A 1.12.2 continuation of ArchitectureCraft: roof slopes, siding, walls, fences and paneling, with rebuilt rendering. |
| Licence | MIT |
| Primary category | Building |
| Additional categories | Cosmetic, Utility & QoL (only if offered for this game) |
| Logo | `src/main/resources/assets/architecturecraft/textures/logo.png` (512x512) |
| Links | Source: GitHub repository, Issues: GitHub issues, Changelog: CHANGELOG.md |

## Description

The description editor takes HTML, and CurseForge requires English and a
description that actually explains the project; the text below is the Modrinth
copy converted to simple HTML with `<p>`, `<strong>` and `<ul>` only.

```html
<p>ArchitectureCraft: Renovated is a 1.12.2 continuation of TridentMC's
ArchitectureCraft. The sawbench turns a block into architectural shapes that
keep its material, and this fork adds to that set while leaving every shape ID
from 0 to 116 and every existing world untouched.</p>

<p><strong>Roofing.</strong> Nine slope families (A, B and C, in 1x2, 1x3 and
1x4 runs), each as start, middle and end pieces that interlock, plus
self-emissive versions of all nine, a Glow Roof Tile, Glow Square and Glow
Slab, an Angled Roof Ridge and a Double Roof Tile. Slopes collide as eight
slices, so you walk up them smoothly.</p>

<p><strong>Wall and fence.</strong> A connection-driven wall: a slim core for
straight runs, a full post at corners, T junctions and crossings, with
collision that follows the visible arms. A fence built the vanilla way: 4x16x4
post, rails to every connected side, 1.5-block collision so it cannot be
jumped, and no cladding.</p>

<p><strong>Walls and interiors.</strong> Siding: the roof tile and the nine
slopes mounted upright, with the grain turned so the material does not read
sideways. Paneling: a 3 px panel flush with the block boundary, cladding on the
room-facing side. Double Siding: a full block split down the middle, one half
taking the cladding material. A GregTech plaque with a 2 px plate.</p>

<p><strong>Rendering and behaviour.</strong> Seams are dropped between
translucent shapes, light opacity follows how much of the block a shape fills,
shader packs no longer double-light custom geometry, placement previews are
stable, and shapes sound like the material they were cut from. Mobs path around
fences and walls.</p>

<p><strong>Requirements.</strong> Minecraft 1.12.2, Forge 14.23.4.2747 or
newer (built against 14.23.5.2860), Java 8. Cleanroom 0.7.x works too. No
dependencies: drop the jar into <code>mods/</code>.</p>

<p><strong>Credits and licence.</strong> MIT. Original ArchitectureCraft by
Greg Ewing (gcewing); 1.12 port by darkevilmac, unascribed and the TridentMC
contributors; shapes 94-116 and several menu assets from GTNewHorizons' fork.
See CREDITS.md in the repository. Not an official TridentMC release.</p>
```

## File 0.3

| Field | Value |
|---|---|
| Upload | architecturecraft-renovated.3.108-0.3.jar |
| Display name | 0.3 |
| Release type | Release |
| Supported version | 1.12.2 |
| Loader | Forge |
| Changelog | the `3.108-0.3` section of CHANGELOG.md |

The sources jar is not a separate CurseForge file; keep it on the GitHub
release, or add it as an extra file on the same version if you want it there.

## After approval

The project ID from the dashboard is what goes into the packwiz entry and into
the upload automation (`project_id` for the API, with an author token from
`https://www.curseforge.com/account/api-tokens`).
