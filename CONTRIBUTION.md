# Contribution Guide

## Branches

Fiktion uses a stable `main` branch and release-line development branches.

- `main` points at the latest published release.
- `release/0.x` is the development branch for the current pre-1.0 release line.
- Feature, fix, and dependency update pull requests should target `release/0.x`.
- Release preparation is merged into `release/0.x` first, then `main` is advanced to the published release commit after artifacts are published.

## Pull Requests

Open pull requests against `release/0.x` unless a maintainer explicitly asks for another base branch.

Use short, descriptive titles without agent-specific prefixes. Keep changes focused on one behavior, fix, or release task.

## Dependency Updates

Renovate is configured to open dependency update pull requests against `release/0.x`.

## Protected Branches

The protected branch set is:

- `main`
- `release/*`

Do not force-push or delete protected branches. Changes should go through pull requests.
