---
name: release
description: "Prepare Fiktion releases: bump versions, update docs and changelog, publish, and write GitHub release notes."
---

# Release

Use this skill for Fiktion repository release preparation.

## Version Update

When bumping Fiktion from an old version to a new version:

1. Confirm the requested version and current branch.
2. Update the root project version in `build.gradle.kts`.
3. Add or update the matching top section in `CHANGELOG.md`.
4. Update public dependency snippets in `README.md`.
5. Update project-local Codex skill references under `.codex/skills/**` when they contain public dependency snippets or maintainer workflow examples.
6. Do not update historical changelog sections, generated API dumps, lockfiles, or unrelated dependency versions merely because they contain the old version.

Use targeted searches instead of broad blind replacement:

```bash
rg -n "0\\.2\\.1|v0\\.2\\.1" README.md CHANGELOG.md .codex/skills skills build.gradle.kts fiktion-* gradle -g '!**/build/**'
```

Adjust the old version in the search pattern to match the release being replaced.

## Release Order

Use this order for an actual release:

1. Prepare a version bump PR.
2. Merge the version bump PR into `main`.
3. Sync local `main` and verify it points at the merged commit.
4. Publish artifacts from `main`.
5. Confirm published artifacts are available before announcing the release.
6. Create the GitHub Release last, using the published version tag and the release note format below.

Do not create the GitHub Release before publishing succeeds. If publishing fails, fix publishing on `main` before creating release notes or tags that users may follow.

Publishing usually requires credentials and network access. Before running publish tasks, confirm the exact command with the user and make sure the working tree is clean.

## Release Notes

Use this public-facing format for GitHub Release notes:

````markdown
## Highlights

- One short sentence explaining the main user-visible change.

## What's Changed

- Changed ...
- Fixed ...
- Added ...

## Upgrade

Use version `x.y.z` for all Fiktion artifacts:

```kotlin
plugins {
    id("dev.s7a.fiktion") version "x.y.z"
}

dependencies {
    testImplementation("dev.s7a:fiktion-core:x.y.z")
    testImplementation("dev.s7a:fiktion-addon-arrow-core:x.y.z")
    testImplementation("dev.s7a:fiktion-addon-java:x.y.z")
    testImplementation("dev.s7a:fiktion-addon-kotlinx-datetime:x.y.z")
}
```

## Artifacts

- `dev.s7a:fiktion-core:x.y.z`
- `dev.s7a:fiktion-addon-arrow-core:x.y.z`
- `dev.s7a:fiktion-addon-java:x.y.z`
- `dev.s7a:fiktion-addon-kotlinx-datetime:x.y.z`
- `dev.s7a:fiktion-compiler-plugin:x.y.z`
- Gradle plugin `dev.s7a.fiktion` version `x.y.z`
````

Keep release notes concise and user-facing. Prefer user-visible behavior over implementation detail. Mention implementation detail only when it changes upgrade behavior, compatibility, or troubleshooting. Do not include internal verification logs; verify publishing before creating the GitHub Release, then publish a clean release note.

## Verification

Always run:

```bash
git diff --check
```

For documentation-only version snippet changes, no Gradle test is usually needed. For Gradle plugin wiring or build logic changes, run:

```bash
./gradlew :fiktion-gradle-plugin:test
```

For compiler plugin behavior changes, run a targeted compiler plugin test first, then broaden only when the changed surface requires it.

## Commit Hygiene

Before committing:

1. Check `git status --short --branch`.
2. Stage only release-related files.
3. Review `git diff --cached --stat`.
4. Prefer amending the active release-preparation commit when the user asks for follow-up cleanup on the same branch.
