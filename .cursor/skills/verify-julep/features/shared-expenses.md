# Shared expenses

Shared expenses shows the couple's current balance and the actions to settle or export a PDF. On the baseline seed the ledger is empty, the balance is zero, and both actions stay disabled.

## Sub-features

- `shared-open` opens Compartidos from the bottom bar.
- `shared-empty` shows a zero balance with settle and PDF disabled.
- `shared-settle-dialog` is the confirm step, and it stays unreachable while the balance is zero.

## How to get to it (user POV)

- Choose `Compartidos` in the bottom bar.

## Driving it with verify-julep

Preconditions:

- `verify-julep doctor` reports the recorded serial and version.
- `entry_history` has no shared rows. Confirm with `verify-julep db query "SELECT COUNT(*) AS n FROM entry_history WHERE is_shared = 1"`. The count is `0`.

- **Open tab.** Choose `Compartidos`. Run `verify-julep ui tap --text "Compartidos"` and `verify-julep ui wait --text "Balance actual:  $ 0"`. The title text is `Compartidos`.
- **Disabled actions.** Run `verify-julep ui dump --out .cursor/skills/verify-julep/artifacts/shared-expenses/empty.xml`. The text nodes `Saldar cuentas` and `Generar y Compartir PDF` stay `enabled=true`. The clickable parent of each label has `enabled=false`. The balance text node is exactly `Balance actual:  $ 0`.
- **Proof.** Run `verify-julep ui screenshot --out .cursor/skills/verify-julep/artifacts/shared-expenses/empty.png`. The screenshot shows the version label, the title `Compartidos`, and `Balance actual:`.
- **Settle stays closed.** Do not treat a tap on a disabled button as opening the dialog. The dump after the tap still has no node `¿Estás seguro de querer saldar estos gastos?`.

## Gotchas

- The label `Saldar cuentas` is a text node with `enabled=true`. The button is the clickable parent, and that parent is `enabled=false` while the balance is zero. Read the parent.
- `Saldar cuentas` becomes enabled only when the balance is not zero and no settlement is still syncing. The baseline seed does not create that balance. Reporting the dialog as verified from this seed is a skip.
- `Generar y Compartir PDF` becomes enabled when the shared list is non-empty. A disabled button is the empty-state proof.
- The undo icon on this tab still uses the content description `Undo last entry button`. It is not the settle action.
