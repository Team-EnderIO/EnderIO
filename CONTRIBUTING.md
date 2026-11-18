# Contributing to EnderIO Upgrade

Thank you for wanting to contribute! This guide covers the process we follow, adapted from the [Team-EnderIO/EnderIO contributing guidelines](https://github.com/Team-EnderIO/EnderIO#contributing).

## Before you start

**Before selecting a task from the tracker, make sure to comment on the issue to say that you are working on it** so others don't do the same thing.

If you are planning to contribute something that isn't already tracked, consider either:

1. making an issue to discuss your proposal, or
2. pushing a very early draft Pull Request for discussion.

## Prerequisites

Ensure you meet the [NeoForge prerequisites](https://docs.neoforged.net/en/latest/gettingstarted/#prerequisites) before you begin:

- **JDK 21** (NeoForge 26.1.2 runs on Java 21)
- **Git**
- An IDE (IntelliJ IDEA is recommended)

## Workflow

1. **Fork this repository** under your own GitHub profile. This means you can push your changes to GitHub for Pull Requests later.
2. **Clone down the forked repository** using a git client or the CLI:

   ```bash
   git clone https://github.com/<your-profile>/enderio-upgrade.git
   cd enderio-upgrade
   ```

3. **Open the project in your preferred IDE** and wait for the Gradle import to complete.
4. **Follow any other relevant NeoForge setup for your IDE:** https://docs.neoforged.net/en/latest/gettingstarted/#from-zero-to-modding
5. **Work on your feature or fix.** Try to commit for each major thing you do — small, focused commits are easier to review.
6. **Create a draft Pull Request early** for big changes so you can receive early feedback.

## Pull Requests

- Open a Pull Request from your fork against this repository's default branch.
- For work in progress, mark the PR as a **draft** and populate the TODO checklist in the PR template.
- Describe your design choices and include a list of things to test.
- List any breaking changes (changed/removed APIs, items, blocks, recipes, or gameplay mechanics).
- Ensure your code follows the style guidelines of this project (see `.editorconfig`; most IDEs will apply this for you).
- Make corresponding changes to documentation where applicable.

## Issue tracker

Use the provided issue templates:

- **Bug Report** — crashes or unexpected behaviour
- **Feature Report** — new features or ideas

Always check whether an existing issue or proposal already covers what you want to report before creating a new one.

## Code style

This project uses [EditorConfig](https://editorconfig.org/) (see `.editorconfig`). Most IDEs will pick this up automatically. Key settings:

- UTF-8 charset
- CRLF line endings
- 4-space indentation
- Final newline at end of file
- Max line length 160

## License

This project is released under [The Unlicense](LICENSE.txt) — free and unencumbered public-domain software.
