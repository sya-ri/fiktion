# Contribution Guide

## Branches

Fiktion uses a stable `main` branch and release-line development branches.

- `main` points at the latest published release.
- `release/0.x` is the development branch for the current pre-1.0 release line.
- Feature, fix, and dependency update pull requests should target `release/0.x`.
- Release preparation is merged into `release/0.x` first, then `main` is advanced to the published release commit after artifacts are published.
- A publish pull request from `release/0.x` to `main` is created automatically when `release/0.x` is updated.

## Pull Requests

Open pull requests against `release/0.x` unless a maintainer explicitly asks for another base branch.

Use short, descriptive titles without agent-specific prefixes. Keep changes focused on one behavior, fix, or release task.

## Dependency Updates

Renovate is configured to open dependency update pull requests against `release/0.x`.

## Quality Checks

Run Qodana locally with the repository `qodana.yaml` configuration:

```shell
docker run -it -v "$PWD":/data/project jetbrains/qodana-jvm-community:2026.1
```

## Publishing

Publish pull requests target `main` from `release/0.x`. Merge the publish pull request with a merge commit only when the
release branch is ready to become the latest published state.

Do not squash or rebase publish pull requests. A merge commit preserves the release branch commits and keeps the next
publish pull request based on the previous published merge point.

After the publish pull request is merged, publish artifacts from `main`, verify that they are available, then create the
GitHub Release.

## Protected Branches

The protected branch set is:

- `main`
- `release/*`

Do not force-push or delete protected branches. Changes should go through pull requests.
Rulesets should allow only merge commits into `main` and only squash merges into `release/*`.
The repository ruleset requires one approving review, except for maintainers listed as pull-request bypass actors.
Required status checks cannot be bypassed.
