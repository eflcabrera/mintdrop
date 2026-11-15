# Julep verification map

This directory is the maintained source for verifying the user-facing behavior of Julep. Read the index before driving the app, then use the matching feature file as the recipe.

## Baseline preconditions

- Launch with `.cursor/skills/verify-julep/scripts/verify-julep launch` and require the ready line for `emulator-5574` (or the serial printed when `JULEP_VERIFY_PORT` is set).
- The app data dir was cleared and reseeded by that launch.
- Seeded labels: expense category `Gastos diarios`, subcategory `Supermercado`, income category `Sueldo`, subcategory `Mensual`.
- `external_sheet_ref.sheet_id` is `verify-no-sheet` for the emulator's current year.
- Run `verify-julep doctor` and require the recorded pid, serial, and `versionName`.
- Never drive an emulator or phone that this launch did not start.

## Driving conventions

- Start every recipe from the baseline state unless its preconditions say otherwise.
- Prefer visible text and content descriptions. The commands are `verify-julep ui tap`, `ui text`, `ui wait`, `ui dump`, and `ui screenshot`.
- Treat every command as literal. Keep quoted labels unchanged.
- A tap on `Gastos`, `Compartidos`, or `Ingresos` hits the bottom bar. The helper chooses a clickable match, or else the lower node when the title repeats the same word.
- Restore seeded rows after a mutation by running `verify-julep seed` and starting the activity again. Do not remove proof artifacts during cleanup.

## Proof and skip reporting

- Capture the user action and the resulting state, not only the final screen.
- UI proof includes a uiautomator XML dump and a screenshot that shows the version label and the screen title.
- Mutation proof includes `verify-julep db query` of the stored row and the same value on screen.
- Record the feature file name with every artifact.
- Report an unreachable path with the attempted command and the unmet precondition.
- Do not report a skipped entry point as verified through a different path.

## Feature entry contract

Each feature file starts with an H1 title and one paragraph describing the user-visible behavior. It then uses exactly four H2 sections in this order.

1. `Sub-features` lists short IDs with one line for each behavior.
2. `How to get to it (user POV)` lists every user entry point.
3. `Driving it with verify-julep` starts with `Preconditions:` and uses labeled bullets that pair each user action with an exact command and observable result.
4. `Gotchas` lists traps that can waste or invalidate a verification run.

## Features

- [Browse expenses](./browse-expenses.md) covers the Gastos tab, a category, and its subcategory.
- [Record an expense](./record-expense.md) covers the entry form and the local history row.
- [Browse income](./browse-income.md) covers the Ingresos tab, a category, and its subcategory.
- [Shared expenses](./shared-expenses.md) covers the balance, settle, and PDF actions on an empty ledger.
- [Undo last entry](./undo-last-entry.md) covers the undo dialog after an entry exists.
