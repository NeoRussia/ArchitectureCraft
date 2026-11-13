# Publishing

A release is a tag: push `vX.Y` and `release.yml` builds, checksums and publishes.

## What runs automatically

- **GitHub release**: always, with the jar, the sources jar, `SHA256SUMS.txt`
  and the body from `docs/releases/<tag>.md`.
- **Modrinth and CurseForge**: only when the tokens below are set as repository
  secrets, and only after each project exists. That step is
  `continue-on-error`, so a platform failure never blocks the GitHub release.

## Secrets

| Secret | Where it comes from |
|---|---|
| `MODRINTH_TOKEN` | Modrinth account settings, personal access token with `create_version` |
| `MODRINTH_PROJECT_ID` | the project id shown on its settings page once approved |
| `CURSEFORGE_TOKEN` | `curseforge.com/account/api-tokens`, an author token |
| `CURSEFORGE_PROJECT_ID` | the numeric project id on the author dashboard |

Add them under Settings → Secrets and variables → Actions.

## The first upload of each platform is manual

CurseForge reviews a new project only once a file is uploaded, and Modrinth needs
an approved project before the API can target it. So:

1. Create the project page on each platform and upload the current jar by hand.
2. Copy both ids into the secrets above.
3. From the next tag on, the workflow does it.

## Checklist per release

1. Bump `project.gradle` (`version` and `releaseName`), `ArchMod.MOD_VER` and
   `CHANGELOG.md`.
2. Write `docs/releases/<tag>.md` — the workflow fails without it, on purpose.
3. Commit, push the branch, then `git tag vX.Y` and push the tag.
4. If the run is red, read the "Check the jar name matches the tag" step first:
   it exists to catch a forgotten version bump.
