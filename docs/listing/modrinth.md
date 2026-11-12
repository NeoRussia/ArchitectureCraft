# Modrinth listing

Field values for `https://modrinth.com/projects/new`. Keep the description as
one flow of bold lead-ins and lists: Modrinth's content rules forbid headings
used as body text, and the summary must carry no formatting and must not repeat
the title.

| Field | Value |
|---|---|
| Name | ArchitectureCraft: Renovated |
| Slug | architecturecraft-renovated |
| Project type | mod |
| Summary | A 1.12.2 continuation of TridentMC's ArchitectureCraft, with nine roof slope families, siding, walls, fences and new paneling. |
| Environment | Client and server |
| Loader | Forge |
| Game versions | 1.12.2 |
| Licence | MIT |
| Source | https://github.com/NeoRussia/ArchitectureCraft-Renovated |
| Issues | https://github.com/NeoRussia/ArchitectureCraft-Renovated/issues |
| Changelog | https://github.com/NeoRussia/ArchitectureCraft-Renovated/blob/1.12/CHANGELOG.md |
| Categories | decoration, utility |
| Content disclosure | "Contains AI-generated content" if the maintainer decides the new code counts as a substantial portion (see the note at the end) |

## Description

ArchitectureCraft: Renovated is a 1.12.2 continuation of
[TridentMC's ArchitectureCraft](https://github.com/TridentMC/ArchitectureCraft).
The sawbench turns a block into architectural shapes that keep its material,
and this fork adds to that set while leaving every shape ID from 0 to 116 and
every existing world untouched.

**Roofing.** Nine slope families (A, B and C, in 1x2, 1x3 and 1x4 runs), each
as start, middle and end pieces that interlock, plus self-emissive versions of
all nine, a Glow Roof Tile, Glow Square and Glow Slab, an Angled Roof Ridge and
a Double Roof Tile. Slopes collide as eight slices, so you walk up them instead
of on a staircase of two boxes.

**Wall and fence.** A connection-driven wall: a slim core for straight runs, a
full post at corners, T junctions and crossings, with collision that follows
the visible arms. A fence built the vanilla way: 4x16x4 post, rails to every
connected side, 1.5-block collision so it cannot be jumped, and no cladding.

**Walls and interiors.** Siding: the roof tile and the nine slopes mounted
upright, with the grain turned so the material does not read sideways.
Paneling: a 3 px panel flush with the block boundary, cladding on the
room-facing side. Double Siding: a full block split down the middle, one half
taking the cladding material. A GregTech plaque with a 2 px plate.

**Rendering and behaviour.** Seams are dropped between translucent shapes so
glass slopes do not show lines, light opacity follows how much of the block a
shape fills, shader packs no longer double-light custom geometry, placement
previews are stable, and shapes sound like the material they were cut from.
Mobs path around fences and walls instead of walking into them.

**Requirements.** Minecraft 1.12.2, Forge 14.23.4.2747 or newer (built against
14.23.5.2860), Java 8. Cleanroom 0.7.x works too. No dependencies: drop the jar
into `mods/`.

**Credits and licence.** MIT. Original ArchitectureCraft by Greg Ewing
(gcewing); 1.12 port by darkevilmac, unascribed and the TridentMC contributors;
shapes 94-116 and several menu assets from GTNewHorizons' fork. See CREDITS.md
in the repository. Not an official TridentMC release.

## Version 0.2

| Field | Value |
|---|---|
| File | architecturecraft-renovated.3.108-0.2.jar |
| Additional file | architecturecraft-renovated.3.108-0.2-sources.jar |
| Version number | 0.2 |
| Channel | release |
| Game versions | 1.12.2 |
| Loader | Forge |
| Changelog | the `3.108-0.2` section of CHANGELOG.md |

## Note on the AI disclosure

Modrinth's content rules (6.1) require the "Contains AI-generated content"
disclosure when a substantial portion of the project's code is AI output. The
upstream code here is human-written; the shapes added in 117-131 and their
placement and mask logic were written with an AI assistant. Disclosing is the
safe reading, and 6.2 only bans AI-generated *images*, which this project does
not use. The pack's own AI policy asks for the same honesty.
