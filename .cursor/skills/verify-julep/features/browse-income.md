# Browse income

Browse income lets a user open the Ingresos tab, see the month total and the category `Sueldo`, and open it to reach the subcategory `Mensual`.

## Sub-features

- `income-tab` opens Ingresos from the bottom bar.
- `income-category` shows `Sueldo` and the month total.
- `income-subcategory` opens `Mensual` on the income entry form.

## How to get to it (user POV)

- Choose `Ingresos` in the bottom bar.

## Driving it with verify-julep

Preconditions:

- `verify-julep doctor` reports the recorded serial and version.
- The seed contains income category `Sueldo` and subcategory `Mensual`.

- **Open tab.** Choose `Ingresos`. Run `verify-julep ui tap --text "Ingresos"` and `verify-julep ui wait --text "Sueldo"`. The title is `Ingresos` and `Total del mes:` is visible.
- **Open category.** Choose `Sueldo`. Run `verify-julep ui tap --text "Sueldo"` and `verify-julep ui wait --text "Mensual"`. The title becomes `Sueldo`.
- **Open subcategory.** Choose `Mensual`. Run `verify-julep ui tap --text "Mensual"` and `verify-julep ui wait --text "Ingresar ingreso"`. The form title is `Mensual` and the amount label is `Ingresar ingreso`. The save label is `Guardar ingreso`.
- **Proof.** Run `verify-julep ui dump --out .cursor/skills/verify-julep/artifacts/browse-income/form.xml` and `verify-julep ui screenshot --out .cursor/skills/verify-julep/artifacts/browse-income/form.png`. Both show `Mensual` and `Ingresar ingreso`.

## Gotchas

- With no income categories, Ingresos calls Google Sheets and throws if `external_sheet_ref` for the emulator year is missing. The baseline seed includes `Sueldo` so the tab can open.
- `Ingresos` is both the title and the bottom-bar label. `ui tap --text "Ingresos"` selects the lower node.
- On the Gastos home screen the month total is missing from the uiautomator tree. Ingresos uses the same top-bar overlay. If the dump omits `Total del mes:`, the screenshot is the proof of that line.
- The amount label is `Ingresar ingreso`, not `Ingresar gasto`. The sheet name decides the word.
