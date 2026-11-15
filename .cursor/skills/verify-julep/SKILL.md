---
name: verify-julep
description: Drive the Julep Android app (package com.eflc.mintdrop) on an isolated emulator and prove a user-facing flow from the feature map. Use when a change touches Julep or MintDrop screens, navigation, Room data the UI reads, or you need a screenshot and accessibility dump as proof.
---

# Verify Julep

Julep is an Android app (Kotlin, Jetpack Compose, one activity). The harness is `verify-julep`: an emulator this run starts, `uiautomator` for taps, and a pull of `julep.db` for side effects. There is no browser and no CLI for the product itself.

Read [features/README.md](features/README.md) before driving. Use the matching feature file as the recipe. One run drives one feature unless the user asks for more.

## Launch

From the repo root:

```bash
.cursor/skills/verify-julep/scripts/verify-julep launch
```

`launch` builds `./gradlew assembleDebug`, starts AVD `julep-verify` on `emulator-5574` with `-no-window`, installs `app/build/outputs/apk/debug/app-debug.apk`, clears the app, lets Room create `databases/julep.db`, seeds it, and starts `com.eflc.mintdrop/.activities.MainActivity`.

The emulator is started in its own session, so it keeps running after `launch` returns. Ready means the accessibility tree contains `Gastos diarios`. The script prints `ready serial=emulator-5574`. The month-total line is on screen and missing from that tree; a screenshot is how you check it. If a dialog says `isn't responding`, the wait taps `Wait` and keeps going.

The AVD lives in `$HOME/.julep-verify` (override with `JULEP_VERIFY_HOME`). It is not an Android Studio AVD. App data is cleared on every launch. The system image `system-images/android-36/google_apis_playstore/x86_64` is reused.

A second instance needs its own home and an even port, then the same command:

```bash
JULEP_VERIFY_HOME=/tmp/julep-verify-b JULEP_VERIFY_PORT=5576 .cursor/skills/verify-julep/scripts/verify-julep launch
```

If the default pid is alive or TCP 5574 is taken, launch exits. Do not install onto a phone or an emulator this run did not start.

Seed written into Room after the schema exists:

- `external_sheet_ref.sheet_id` = `verify-no-sheet` for the emulator's current year
- expense category `Gastos diarios` / subcategory `Supermercado`
- income category `Sueldo` / subcategory `Mensual`

`verify-no-sheet` is intentional. A real spreadsheet id would make a save write the user's Google Sheet.

## Doctor

Run this first whenever the UI, install, or database looks wrong:

```bash
.cursor/skills/verify-julep/scripts/verify-julep doctor
```

Doctor is read-only. It passes only when all of these are true: the recorded emulator pid is alive and its command line contains this AVD name and port, `adb` reports `device` for that serial, `sys.boot_completed` is `1`, the installed `versionName` equals `aapt dump badging` of the apk recorded at launch, and `run-as com.eflc.mintdrop` can read `databases/julep.db`. Anything else: stop, then launch again. Do not drive a half-booted or foreign serial.

## Drive

Stable handles are the visible text and the content description. Prefer those over coordinates.

```bash
.cursor/skills/verify-julep/scripts/verify-julep ui dump --out .cursor/skills/verify-julep/artifacts/<feature>/screen.xml
.cursor/skills/verify-julep/scripts/verify-julep ui tap --text "Gastos diarios"
.cursor/skills/verify-julep/scripts/verify-julep ui tap --desc "Undo last entry button"
.cursor/skills/verify-julep/scripts/verify-julep ui text --text "Ingresar gasto" --value "1500"
.cursor/skills/verify-julep/scripts/verify-julep ui wait --text "Supermercado" --timeout 20
.cursor/skills/verify-julep/scripts/verify-julep ui screenshot --out .cursor/skills/verify-julep/artifacts/<feature>/screen.png
.cursor/skills/verify-julep/scripts/verify-julep db query "SELECT name, type FROM category"
```

`ui tap --text` matches `text` or `content-desc`. A clickable match wins. If none are clickable, the lower node wins, so the bottom bar wins over a title that repeats `Gastos`, `Ingresos`, or `Compartidos`. Spaces in `--value` are sent as `%s` because `adb input text` treats a space as a separator. `ui wait --text` is an exact match on the full text node, including the amount.

`ui dump` prints one line per labeled node (`text=`, `desc=`, `clickable=`, `bounds=`) and writes the uiautomator XML.

Bottom navigation, in order: `Gastos`, `Compartidos`, `Ingresos`. The floating action's content description is `Undo last entry button`. The top-left version label starts with `v`.

## Evidence

Save proof under `.cursor/skills/verify-julep/artifacts/<feature>/`. Cleanup does not delete that directory.

A proof contains:

- the uiautomator XML from before the action and from the resulting screen
- a screenshot of the resulting screen that shows the version label and the screen title
- for a write, a `db query` of the row plus the history text on screen

Exercise the path in the feature file. Room seed is the local cache the screens read; it is not a substitute for tapping. `sheet_id=verify-no-sheet` blocks a real Sheets write. If a sync icon's content description is `Sincronizado`, the seed was replaced and the run may have posted to a real spreadsheet: stop and treat the proof as invalid.

The in-app identity is the version label plus the title (`Gastos`, `Compartidos`, `Ingresos`, or the category name). The status bar does not show the name Julep.

## Cleanup

```bash
.cursor/skills/verify-julep/scripts/verify-julep stop
```

`stop` sends `emu kill` to the recorded serial, then signals the recorded pid if it is still alive. It removes the state file. It does not kill other emulators, does not run `killall`, and does not delete `artifacts/`. After a failed launch, run `stop` before trying again so the port and pid are released.

Confirm the proof files are still on disk after `stop`.

## Helpers

The only helper is the executable script. Invoke it as shown above. `verify-julep` with no args prints the command list.

Host tools it expects: `sqlite3`, Python 3, `lsof`, and the Android SDK from `local.properties` (`sdk.dir`) or `ANDROID_HOME`, including `platform-tools/adb`, `emulator/emulator`, `build-tools/*/aapt`, and the android-36 Google Play system image.
