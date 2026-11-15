# Browse expenses

Browse expenses lets a user open the Gastos tab, see the month total and a category, and open that category to reach a subcategory.

## Sub-features

- `expenses-home` shows the Gastos title, the month total, and the category `Gastos diarios`.
- `expenses-category` opens `Gastos diarios` and lists the subcategory `Supermercado`.
- `expenses-subcategory` opens `Supermercado` on the entry form.

## How to get to it (user POV)

- Cold start opens Gastos. It is the start destination.
- Choose `Gastos` in the bottom bar from Ingresos or Compartidos.

## Driving it with verify-julep

Preconditions:

- `verify-julep doctor` reports the recorded serial and version.
- The seed contains category `Gastos diarios` and subcategory `Supermercado`.
- The accessibility tree already contains `Gastos diarios`. Launch waits for that text.

- **Home.** Leave the app on the start screen. Run `verify-julep ui wait --text "Gastos diarios"`. It prints `visible:`.
- **Capture home.** Run `verify-julep ui dump --out .cursor/skills/verify-julep/artifacts/browse-expenses/home.xml` and `verify-julep ui screenshot --out .cursor/skills/verify-julep/artifacts/browse-expenses/home.png`. The dump lists `Gastos diarios`, `Supermercado`, the version label, and the bottom-bar labels `Gastos`, `Compartidos`, `Ingresos`. The screenshot shows the version label, the title `Gastos`, and `Total del mes:`.
- **Open category.** Choose `Gastos diarios`. Run `verify-julep ui tap --text "Gastos diarios"` and `verify-julep ui wait --text "Supermercado"`. The dump now contains the exact text `Total del mes:  $ 0`, which the home dump does not.
- **Capture category.** Run `verify-julep ui dump --out .cursor/skills/verify-julep/artifacts/browse-expenses/category.xml` and `verify-julep ui screenshot --out .cursor/skills/verify-julep/artifacts/browse-expenses/category.png`. Both artifacts show `Gastos diarios`, `Supermercado`, and `Total del mes:`.
- **Open subcategory.** Choose `Supermercado`. Run `verify-julep ui tap --text "Supermercado"` and `verify-julep ui wait --text "Ingresar gasto"`. The form title is `Supermercado` and the amount field label is `Ingresar gasto`.
- **Bottom bar return.** From another tab, choose `Gastos`. Run `verify-julep ui tap --text "Compartidos"`, `verify-julep ui wait --text "Balance actual:  $ 0"`, `verify-julep ui tap --text "Gastos"`, and `verify-julep ui wait --text "Gastos diarios"`. The category card is back. The lower `Gastos` node is the bar, not the title.

## Gotchas

- With no expense categories, Gastos calls Google Sheets and throws if `external_sheet_ref` for the emulator year is missing. The baseline seed exists so this screen can open. An empty list is a failed seed, not a successful empty state.
- `Gastos` is both the screen title and the bottom-bar label. Neither node is clickable. `ui tap --text "Gastos"` selects the lower one, the bar.
- On the Gastos home screen, `Total del mes:` and the top title are on the screenshot and absent from the uiautomator tree. After opening a category, the dump text is exactly `Total del mes:  $ 0`.
- The recent strip lists expense subcategories ordered by `last_entry_on`, including rows whose date is null. On a fresh seed, `Supermercado` is already on the home screen. Tapping it opens the form. The category card is still a separate entry point.
- A zero total's text node is `Total del mes:  $ 0` (two spaces, no fraction digits). `ui wait` matches the whole string.
- Opening `Supermercado` from the category screen leaves the grid. This recipe stops on the form.
