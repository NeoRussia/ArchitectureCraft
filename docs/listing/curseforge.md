# CurseForge listing

Field values for `https://authors.curseforge.com/#/projects/create/choose-game`.
CurseForge reviews a new project only after the first file is uploaded, and
requires a file of type Release before the project syncs with the app.

The description editor takes Markdown (the WYSIWYG view renders it), so the
text below is plain Markdown — no HTML. It is the same copy as the Modrinth
listing.

| Field | Value |
|---|---|
| Game | Minecraft |
| Class | Mods |
| Name | ArchitectureCraft: Renovated |
| Summary | A 1.12.2 continuation of ArchitectureCraft: roof slopes, corner wedges, siding, walls and fences, with rebuilt rendering. |
| Licence | MIT |
| Primary category | Building |
| Additional categories | Cosmetic, Utility & QoL (only if offered for this game) |
| Logo | `src/main/resources/assets/architecturecraft/textures/logo.png` (512x512) |
| Links | Source: GitHub repository, Issues: GitHub issues, Changelog: CHANGELOG.md |

## Description

```markdown
**Keeping the sawbench turning.** This fork is kept by one person, standing on
other people's shoulders: the original ArchitectureCraft by Greg Ewing, the
1.12 port by the TridentMC contributors, and others. The
siding, wall, fence and wedge sets, the menus and the packaging are what the
evenings go into now.

If it has saved you a wall, a roof or an entire cathedral, you can keep the
coffee flowing here:

[ko-fi.com/neeyo](https://ko-fi.com/neeyo)

ArchitectureCraft: Renovated is a 1.12.2 continuation of
[TridentMC's ArchitectureCraft](https://github.com/TridentMC/ArchitectureCraft).
The sawbench turns a block into architectural shapes that keep its material,
and this fork adds to that set with many more features including improved rendering, shader support, improved shape collision, new UI, bugfixes, and much more! Here are some:

**Roofing.** Nine slope families (A, B and C, in 1x2, 1x3 and 1x4 runs), each
as start, middle and end pieces that interlock, plus self-emissive versions of
all nine, a Glow Roof Tile, Glow Square and Glow Slab, an Angled Roof Ridge and
a Double Roof Tile. Slopes collide as eight slices, so you walk up them
smoothly.

**Corner wedges.** Nine pieces in the same three families, each cut from one
corner pyramid: the base band fills the block, the bands above shrink toward
the apex, and the apex sits on the top corner of the tip block, so a set joins
into one straight hip. Glow duplicates included.

**Wall and fence.** A connection-driven wall: a slim core for straight runs, a
full post at corners, T junctions and crossings, with collision that follows
the visible arms. A fence built the vanilla way: 4x16x4 post, rails to every
connected side, 1.5-block collision so it cannot be jumped, and no cladding.

**Walls and interiors.** Siding: the roof tile and the nine slopes mounted
upright, with the grain turned so the material does not read sideways.
Paneling: a 3 px panel flush with the block boundary, cladding on the
room-facing side. Double Siding: a full block split down the middle, one half
taking the cladding material. A GregTech plaque with a 2 px plate.

**Rendering and behaviour.** Seams are dropped between translucent shapes,
light opacity follows how much of the block a shape fills, shader packs no
longer double-light custom geometry, placement previews are stable, and shapes
sound like the material they were cut from. Mobs path around fences and walls.

**Requirements.** Minecraft 1.12.2, Forge 14.23.4.2747 or newer (built against
14.23.5.2860), Java 8. Cleanroom 0.7.x works too. No dependencies: drop the jar
into `mods/`.

**Credits and licence.** MIT. Original ArchitectureCraft by Greg Ewing
(gcewing); 1.12 port by darkevilmac, unascribed and the TridentMC contributors;
shapes 94-116 and several menu assets from GTNewHorizons' fork. See CREDITS.md
in the repository. Not an official TridentMC release.

**On AI assistance.** The upstream code is entirely human-written. The fork's
own additions - the shape families from 117 upward, their placement and
collision logic, the sawbench pages and the tooling - were written with assistance from
DeepSeek V4.1 Flash in OpenCode, working under my direction, and
every change is reviewed, tested and understood by me before it
ships.
```

## File 0.4

| Field | Value |
|---|---|
| Upload | architecturecraft-renovated.3.108-0.4.jar |
| Display name | 0.4 |
| Release type | Release |
| Supported version | 1.12.2 |
| Loader | Forge |
| Changelog | the `3.108-0.4` section of CHANGELOG.md |

The sources jar is not a separate CurseForge file; keep it on the GitHub
release, or add it as an extra file on the same version if you want it there.

## After approval

The project ID from the dashboard is what goes into the packwiz entry and into
the upload automation (`project_id` for the API, with an author token from
`https://www.curseforge.com/account/api-tokens`).
