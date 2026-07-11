# Julep (ex MintDrop)

> App Android de **finanzas personales para parejas** que usa **Google Sheets** como backend remoto y **Room** como cache local.

**Kotlin** 1.9.10 · **Compose** BOM 2023.03.00 · **minSdk** 28 · **targetSdk** 34

---

## Tabla de contenidos

- [¿Qué hace Julep?](#qué-hace-julep)
- [Stack técnico](#stack-técnico)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Arquitectura](#arquitectura)
- [Setup y build](#setup-y-build)
- [Configuración inicial obligatoria](#configuración-inicial-obligatoria)
- [Backend: Google Apps Script](#backend-google-apps-script)
- [Persistencia y migraciones](#persistencia-y-migraciones)
- [Backup de la base de datos](#backup-de-la-base-de-datos)
- [Convenciones de código](#convenciones-de-código)
- [Documentación detallada](#documentación-detallada)
- [Estado del proyecto y limitaciones](#estado-del-proyecto-y-limitaciones)

---

## ¿Qué hace Julep?

Permite cargar **gastos** e **ingresos** desde el celular y mantenerlos sincronizados con una **planilla de Google Sheets** que actúa como _source of truth_ remoto. Funcionalidades:

- **Categorías y subcategorías** sincronizadas desde la planilla.
- **Carga de gastos / ingresos** con monto, descripción, fecha, método de pago.
- **Gastos compartidos** entre dos usuarios con cálculo de balance.
- **Saldar cuentas** generando un movimiento compensatorio.
- **Exportar PDF** del estado de gastos compartidos para conciliar.
- **Deshacer último** (compensa la celda en el Sheet con un valor negativo).
- **Balance mensual** cacheado por subcategoría / categoría / total.

> Esta app fue inicialmente desarrollada bajo el nombre **MintDrop** (de ahí el package `com.eflc.mintdrop`). El rebrand a **Julep** está parcialmente completado: ver [Estado del proyecto y limitaciones](#estado-del-proyecto-y-limitaciones).

---

## Stack técnico

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Kotlin | 1.9.10 (JVM 17) |
| UI | Jetpack Compose + Material 3 | BOM 2023.03.00 |
| Navegación | Navigation Compose | 2.5.3 |
| DI | Hilt + KSP | 2.48.1 |
| Persistencia | Room (SQLite) | 2.6.0 |
| HTTP | Retrofit + Moshi | 2.9.0 / 1.15.0 |
| Async | Kotlin Coroutines + StateFlow | — |
| Charts | `co.yml:ycharts` | 2.1.0 |
| Build | AGP / Gradle | 8.11.0 / 8.x |

---

## Estructura del proyecto

```
mintdrop/
├── app/
│   ├── build.gradle.kts          # Config del módulo (compileSdk 34, minSdk 28)
│   ├── proguard-rules.pro
│   ├── schemas/                  # Schemas Room exportados (1.json..6.json)
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/eflc/mintdrop/
│       │   │   ├── activities/    # MainActivity (única Activity)
│       │   │   ├── api/           # GoogleSheetsAPI (Retrofit)
│       │   │   │   └── reference/ # Apps Script (.gs) de referencia
│       │   │   ├── di/            # Módulos Hilt
│       │   │   ├── graph/         # NavHosts (Root + Home)
│       │   │   ├── navigation/    # Routes (sealed)
│       │   │   ├── models/        # DTOs no persistentes
│       │   │   ├── repository/    # interfaces + impl/
│       │   │   ├── room/          # JulepDatabase + DAOs + entidades + migraciones
│       │   │   ├── service/       # EntryRecordService + SharedExpenseService
│       │   │   ├── ui/
│       │   │   │   ├── components/  # Cards, Dialogs, Pickers, Charts
│       │   │   │   ├── screens/     # home, expense, income, category, expenseentry, shared
│       │   │   │   └── theme/       # Color, Theme, Type
│       │   │   ├── utils/           # Constants, FormatUtils, PdfUtils, RandomColor
│       │   │   ├── MintDropApplication.kt
│       │   │   └── ...
│       │   └── res/                # Recursos (drawables SVG, strings, colors, themes)
│       ├── test/                   # vacío (sin tests configurados)
│       └── androidTest/            # vacío (sin tests configurados)
├── build.gradle.kts                # Top-level
├── settings.gradle.kts             # rootProject.name = "Mint Drop" (legacy)
├── gradle.properties               # Optimizaciones de build (parallel, R8 fullMode, ...)
├── local.properties                # SDK path (no commitear)
├── docs/                           # (vacío) — documentación adicional
└── scripts/                        # (vacío) — scripts auxiliares
```

---

## Arquitectura

**MVVM + arquitectura por capas**, mono-módulo:

```
Compose UI
    ↓
ViewModel  (Hilt, StateFlow, viewModelScope)
    ↓
Service    (EntryRecordService, SharedExpenseService — orquesta Repos)
    ↓
Repository (interface + Impl)
    ↓
DAO (Room) ─────  +  ─────  Retrofit (GoogleSheetsAPI)
                                     ↓
                       Google Apps Script Web App
                                     ↓
                              Google Sheets
```

- **Single Activity** (`MainActivity`) + **Compose** + **Navigation Compose** anidado (`RootNavGraph` → `HomeNavGraph`).
- **DI con Hilt** (4 módulos: `DatabaseModule`, `GoogleSheetsModule`, `ServiceModule`, `CoroutineModule`).
- **Room** es la _source of truth_ local (cache); el **Sheet** es el _source of truth_ remoto. Las escrituras se hacen primero localmente y luego se postean al Sheet — **no son atómicas** (ver gaps).
- **Coroutines** para concurrencia, `StateFlow` para estado, `db.withTransaction { }` para operaciones compuestas.

---

## Setup y build

### Requisitos

- **JDK 17** (configurar `JAVA_HOME` o la ruta del JDK en `gradle.properties` / Android Studio según tu entorno).
- **Android Studio** Hedgehog (2023.1) o superior.
- **Android SDK** 34 instalado.
- **Google Spreadsheet** propio + script de Apps Script desplegado como Web App (ver [Backend](#backend-google-apps-script)).

### Build local

```bash
# Compilar APK debug
./gradlew assembleDebug

# Instalar en dispositivo conectado
./gradlew installDebug

# Run tests (actualmente sin tests)
./gradlew test
./gradlew connectedAndroidTest
```

### Build optimizado

El `gradle.properties` ya activa: parallel, build cache, configure-on-demand, R8 fullMode, KSP/Kotlin/Room incremental. La función `calculateBuildOptions()` en `app/build.gradle.kts` ajusta memoria del Kotlin daemon en runtime según los cores/RAM disponibles.

---

## Configuración inicial obligatoria

> **Nota:** La app crashea con NPE si no hay un `external_sheet_ref` para el año en curso. No existe (todavía) UI de onboarding.

Para que la app arranque la primera vez hay que insertar manualmente en la DB local:

```sql
INSERT INTO external_sheet_ref(year, sheet_id) VALUES (2026, '<TU_SPREADSHEET_ID>');
```

Vías posibles:
- Desde Android Studio → `App Inspection` → `Database Inspector` (con la app en debug, base `julep.db`).
- O temporalmente seedeando en una migración nueva mientras se desarrolla.

Esto está documentado como gap **G-02** en [`AGENTS.md`](AGENTS.md#áreas-con-deuda-técnica-conocida).

---

## Backend: Google Apps Script

Julep no tiene backend propio. Usa un script GAS desplegado como Web App público. El endpoint base se configura en `utils/Constants.kt` (no documentado en este repositorio).

**Endpoints implementados** (todos sobre `/exec`):
- `GET /exec?spreadsheetId=...&sheet=...` → devuelve la jerarquía categoría/subcategoría/rowNumber.
- `POST /exec` con body `ExpenseEntryRequest` → escribe en la celda `(row, month + 3)` y agrega a la nota.

**Scripts de referencia** (en `app/src/main/java/com/eflc/mintdrop/api/reference/`, NO se ejecutan en el cliente, son la fuente de verdad del código GAS):
- `getSheetData.gs`
- `postSheetData.gs`
- `categoryManagement.gs` _(no consumido aún por la app)_

Para deployar:
1. Crear un proyecto en Google Apps Script.
2. Habilitar la **Sheets API** desde Servicios.
3. Pegar los `.gs` en archivos del proyecto.
4. **Deploy → New deployment → Web App**, ejecutar como "Yo", acceso "Cualquier persona".
5. Copiar la URL del deployment Web App (`/exec`) en `Constants.BASE_URL` (solo en tu entorno local; no commitear la URL de producción si el repo es público).

---

## Persistencia y migraciones

- **DB:** `julep.db` (Room)
- **Versión actual:** `6`
- **Esquemas exportados:** `app/schemas/com.eflc.mintdrop.room.JulepDatabase/{1..6}.json`
- **Migraciones declaradas:** 1→2, 2→3, 3→4, 4→5, 5→6.

Reglas:
- **Subir la versión** cada vez que cambie cualquier `@Entity`.
- **Agregar siempre** una `Migration` correspondiente. Nunca usar `fallbackToDestructiveMigration()` en main.
- **Verificar** el schema JSON generado.

Entidades principales: `Category`, `Subcategory`, `SubcategoryRow`, `EntryHistory`, `PaymentMethod`, `SubcategoryMonthlyBalance`, `ExternalSheetRef`, `SharedExpenseConfiguration`, `SharedExpenseConfigurationDetail`, `SharedExpenseEntryDetail`, `SharedExpenseSettlement`.

---

## Backup de la base de datos

Julep no implementa export/import manual de la DB. La persistencia ante desinstalación o cambio de dispositivo depende del **Auto Backup de Android** (Google Backup), habilitado en el Manifest con `allowBackup="true"`.

### Cómo funciona

1. Android programa backups periódicos (típicamente con Wi‑Fi, batería y el dispositivo inactivo).
2. Antes de copiar archivos, `JulepBackupAgent` ejecuta `PRAGMA wal_checkpoint(TRUNCATE)` para consolidar el WAL de SQLite en `julep.db`.
3. Se respaldan `julep.db`, `julep.db-shm` y `julep.db-wal` (reglas en `res/xml/data_extraction_rules.xml` y `backup_rules.xml`).
4. Al reinstalar la app con la misma cuenta de Google, Android puede restaurar esos archivos en `databases/`.

**Qué se preserva:** historial local, gastos compartidos, `external_sheet_ref`, balances cacheados y categorías locales. **Qué no cubre:** el contenido del Google Sheet (vive en Google, no en la DB).

### Requisitos en el dispositivo

- Cuenta de Google configurada en el teléfono.
- Backup de apps activado (ruta típica: **Ajustes → Google → Copia de seguridad** o **Ajustes → Sistema → Copia de seguridad**, según fabricante y versión de Android).
- La app no debe tener el backup desactivado a nivel de sistema (raro en instalaciones normales).

### Cómo saber si el backup se realizó

Julep **no tiene pantalla de estado de backup**. Las formas de comprobarlo:

#### En el teléfono (uso normal)

- Revisar que el backup de Google esté **activado** y que la **última copia** tenga fecha reciente en Ajustes.
- Tras un backup exitoso del agente, en **Logcat** (con la app instalada en debug) puede aparecer:
  - `JulepBackupAgent`: `WAL checkpoint ejecutado correctamente` — el agente corrió antes de la copia.
  - `JulepBackupAgent`: `Base de datos restaurada desde backup` — solo al restaurar tras reinstalar.

No hay confirmación visible dentro de la app para el usuario final.

#### Con ADB (desarrollo / verificación explícita)

Con el dispositivo conectado y la app instalada:

```bash
# 1. Asegurarse de que el servicio de backup está habilitado
adb shell bmgr enable true

# 2. Forzar backup solo de Julep (applicationId)
adb shell bmgr backupnow com.eflc.mintdrop

# 3. Ver estado general del backup (buscar com.eflc.mintdrop en la salida)
adb shell dumpsys backup

# 4. Logcat del agente mientras corre el backup
adb logcat -s JulepBackupAgent BackupManagerService
```

Si `backupnow` termina sin error y en logcat ves el checkpoint, el agente hizo su parte. Android no expone un timestamp por app en la UI; `dumpsys backup` es la fuente más fiable desde la PC.

**Probar la restauración de punta a punta:**

1. Cargar datos en la app.
2. `adb shell bmgr backupnow com.eflc.mintdrop` y esperar unos segundos.
3. Desinstalar: `adb uninstall com.eflc.mintdrop`
4. Reinstalar: `./gradlew installDebug`
5. Abrir la app y comprobar que los datos locales siguen ahí.

### Cómo forzar un backup

| Contexto | Acción |
|---|---|
| Desarrollo (recomendado) | `adb shell bmgr backupnow com.eflc.mintdrop` |
| Todo el dispositivo | `adb shell bmgr run` (más lento; respalda todas las apps elegibles) |
| Uso diario | No se puede forzar desde Julep; depende de la programación de Android |

Tras forzar con `backupnow`, dejar el dispositivo desbloqueado unos segundos y revisar logcat. En emulador el backup suele ser poco fiable; probar en un dispositivo físico con cuenta Google.

### Limitaciones

- El backup **no es inmediato**: puede haber horas entre un cambio en la DB y su inclusión en la nube.
- **Desinstalar** o **Borrar datos** sin backup previo implica pérdida de la DB local.
- Si el backup de Google está desactivado, estas reglas no ayudan.
- El checkpoint puede fallar sin cancelar el backup (se loguea un warning); en ese caso el restore podría ser inconsistente.

---

## Convenciones de código

- **Kotlin code style:** `official` (configurado en `gradle.properties`).
- **Package convention:** mantener `com.eflc.mintdrop.<feature>.<role>` por ahora (rebrand pendiente, ver más abajo).
- **Repositorios:** `interface XRepository` en `repository/` + `XRepositoryImpl` en `repository/impl/`. Inyectado vía Hilt.
- **DAOs:** marcados `@Dao`, prefieren `@Upsert` sobre `@Insert + @Update`. **Toda nueva query debe ser `suspend fun`** (varias existentes no lo son — gap G-06).
- **ViewModels:** un `@HiltViewModel` por screen, exponen `MutableStateFlow` privado + `.asStateFlow()` público. Cargas iniciales vía `LaunchedEffect(true) { vm.getX() }` desde la pantalla.
- **State del form:** se usa `data class XFormState(...)` + `var state by remember { mutableStateOf(...) }` (ver `ExpenseEntryScreen`). Para forms críticos, usar `rememberSaveable`.
- **Composables:** sin lógica de negocio, solo presentación + delegación al VM. Memoizar valores derivados con `remember(key) { derivedStateOf { ... } }`.
- **Concurrencia:** preferir `viewModelScope.launch` + `withContext(Dispatchers.IO)` sobre `viewModelScope.launch(IO)` directo. Operaciones que tocan más de un repo: envolver en `db.withTransaction { ... }`.
- **Inmutables por defecto:** `val` salvo necesidad. En entidades, usar `var` solo donde un `Upsert` deba mutar.
- **Constantes mágicas:** centralizar en `utils/Constants.kt` mientras no haya Settings persistido.

### Idioma
- **Comunicación de PRs y comentarios:** español (alineado con el resto del codebase).
- **Identificadores y APIs públicas:** inglés.

---

## Documentación detallada

La documentación del proyecto vive en este repositorio:

- Este `README.md` — overview, arquitectura, setup, backend, persistencia y convenciones.
- [`AGENTS.md`](AGENTS.md) — reglas para contribuir, checklist de cambios, deuda técnica priorizada (gaps G-01…G-17) e instrucciones para agentes de IA.
- Código fuente — entidades Room en `room/`, DTOs en `models/`, Apps Script de referencia en `api/reference/`, schemas en `app/schemas/`.

---

## Estado del proyecto y limitaciones

- **Funcional para uso personal** del autor (carga de gastos/ingresos + sync con Sheet + gastos compartidos para 2 usuarios).
- **Rebrand MintDrop → Julep incompleto**: package, clases, paleta de tema y `rootProject.name` siguen siendo "MintDrop" / "Mint Drop". Solo el `app_name` y el nombre de la DB (`julep.db`) están actualizados.
- **Modelo cerrado de 2 usuarios** (`MY_USER_ID = 1`, `THEIR_USER_ID = 2`, nombres de usuario hardcodeados en `PdfUtils`).
- **Sin tests** (`test/` y `androidTest/` vacíos).
- **Sin auth** (backend expuesto como Web App público de Apps Script).
- **Sincronización Room↔Sheet no atómica** (riesgo de inconsistencia ante caída de red).
- **Splits sólo SHARES** implementado (`EQUAL_PARTS` y `PERCENTAGES` declarados pero sin lógica).
- **Sin onboarding del `external_sheet_ref`** → crash NPE en install limpio o cambio de año.

Lista completa y priorizada en [`AGENTS.md`](AGENTS.md#áreas-con-deuda-técnica-conocida).

---

## Licencia

Sin licencia explícita declarada. Proyecto privado.
