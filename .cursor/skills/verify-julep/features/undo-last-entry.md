# Undo last entry

Undo last entry asks the user to confirm reverting the newest entry from the last 30 days, then removes it. The dialog is the decision; the missing row is the result.

## Sub-features

- `undo-open` opens the dialog titled `Deshacer último` from the floating button.
- `undo-cancel` closes the dialog and leaves the row in place.
- `undo-confirm` removes the entry after `Deshacer`.

## How to get to it (user POV)

- After saving an entry, choose the floating button at the top right (content description `Undo last entry button`) on Gastos or Ingresos.

## Driving it with verify-julep

Preconditions:

- `verify-julep doctor` reports the recorded serial and version.
- An `entry_history` row from the last 30 days already exists. Create it with the record-expense recipe (`Cafe verify`, amount `1500`) before this one. The baseline seed has no such row.
- The screen is Gastos or Ingresos, not a form pushed on top of the home scaffold. Press back until the version label and `Undo last entry button` are in the dump.

- **Open dialog.** Choose the undo button. Run `verify-julep ui tap --desc "Undo last entry button"` and `verify-julep ui wait --text "Deshacer último"`. The body contains `Cafe verify` and `Supermercado`. The quoted place is the subcategory name. The buttons are `Deshacer` and `Cancelar`.
- **Cancel.** Choose `Cancelar`. Run `verify-julep ui tap --text "Cancelar"` and `verify-julep db query "SELECT COUNT(*) AS n FROM entry_history WHERE description = 'Cafe verify'"`. The count stays `1`. The dialog text is gone.
- **Confirm.** Open the dialog again and choose `Deshacer`. Run `verify-julep ui tap --desc "Undo last entry button"`, `verify-julep ui wait --text "Deshacer último"`, and `verify-julep ui tap --text "Deshacer"`. Then `verify-julep db query "SELECT COUNT(*) AS n FROM entry_history WHERE description = 'Cafe verify'"`. The count is `0`.
- **Proof.** Run `verify-julep ui dump --out .cursor/skills/verify-julep/artifacts/undo-last-entry/dialog.xml` and `verify-julep ui screenshot --out .cursor/skills/verify-julep/artifacts/undo-last-entry/dialog.png` while the dialog is open, before confirm. Capture the query output next to those files.

## Gotchas

- The baseline seed has no `entry_history` row. Tapping undo in that state crashes: the newest-entry query is declared non-null and returns nothing. That crash is the unmet precondition, not a successful undo.
- The query only considers rows with `date` in the last 30 days. An older seeded row does not open this dialog.
- Confirming undo also enqueues a reversing sync. With `sheet_id=verify-no-sheet` the remote cell is not updated. The local delete is the proof.
- On Compartidos the same button is visible. This recipe starts from Gastos or Ingresos, where the dialog copy quotes the last expense.
