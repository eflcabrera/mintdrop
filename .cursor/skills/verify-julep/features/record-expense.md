# Record an expense

Record an expense lets a user type an amount and a description for a subcategory and see that entry in the on-screen history. The local row is the stored result. With the verification spreadsheet id, the remote sheet does not accept the sync.

## Sub-features

- `record-open` opens the form for `Supermercado`.
- `record-validate` keeps `Guardar gasto` disabled until the amount is non-empty.
- `record-save` writes the amount and description locally and lists them under `Últimas entradas`.

## How to get to it (user POV)

- On Gastos, choose the category `Gastos diarios`, then the subcategory `Supermercado`.
- On Gastos, choose `Supermercado` in the recent-subcategory strip when that strip is showing it.

## Driving it with verify-julep

Preconditions:

- `verify-julep doctor` reports the recorded serial and version.
- `external_sheet_ref.sheet_id` is `verify-no-sheet`. Confirm with `verify-julep db query "SELECT sheet_id FROM external_sheet_ref"`.
- No `entry_history` row has description `Cafe verify`.

- **Open form.** Choose `Gastos diarios`, then `Supermercado`. Run `verify-julep ui tap --text "Gastos diarios"`, `verify-julep ui wait --text "Supermercado"`, `verify-julep ui tap --text "Supermercado"`, and `verify-julep ui wait --text "Ingresar gasto"`. The form shows `Supermercado`, `Descripción (opc.)`, and `Guardar gasto`.
- **Empty amount.** Do not type an amount. The dump's `Guardar gasto` node has `enabled=false`. Run `verify-julep ui dump --out .cursor/skills/verify-julep/artifacts/record-expense/disabled.xml` and read that attribute.
- **Enter amount and description.** Choose the amount field and type `1500`, then the description field and type `Cafe verify`. Run `verify-julep ui text --text "Ingresar gasto" --value "1500"` and `verify-julep ui text --text "Descripción (opc.)" --value "Cafe verify"`. `Guardar gasto` is enabled.
- **Save.** Choose `Guardar gasto`. Run `verify-julep ui tap --text "Guardar gasto"` and `verify-julep ui wait --text "Cafe verify" --timeout 20`. The history section shows `Cafe verify`. The amount field is empty again.
- **Confirm the row.** Run `verify-julep db query "SELECT description, amount, synced_to_sheets FROM entry_history WHERE description = 'Cafe verify'"`. One row has amount `1500.0`. `synced_to_sheets` stays `0` while the icon content description is `Pendiente de sincronización` or `Error de sincronización. Tocar para reintentar`.
- **Proof.** Run `verify-julep ui dump --out .cursor/skills/verify-julep/artifacts/record-expense/saved.xml` and `verify-julep ui screenshot --out .cursor/skills/verify-julep/artifacts/record-expense/saved.png`. The artifacts show `Supermercado` and `Cafe verify`.

## Gotchas

- `Guardar gasto` posts through the sync outbox. `sheet_id=verify-no-sheet` makes that post fail. A content description `Sincronizado` means the run hit a real spreadsheet. Stop and discard the proof.
- The recent strip already shows `Supermercado` on a fresh seed, because null `last_entry_on` values are included in the top three. Tapping that chip opens the same form as the category path. This recipe uses the category card.
- `adb input text` cannot type a comma or a space as themselves. The helper encodes spaces as `%s`. Use `Cafe verify` and `1500`, then assert those values.
- The error channel from a failed sync is not a snackbar on this screen. The history icon and the `entry_history` row are the observable result.
