# AGENTS.md — Guía para agentes de IA en el repo Julep

> Este archivo orienta a agentes (Cursor, Claude Code, Codex, GitHub Copilot Workspace, etc.) que vayan a trabajar sobre este codebase. **Léelo antes de hacer cualquier cambio.**

---

## TL;DR

- App Android **Kotlin + Jetpack Compose**, single-module (`:app`), MVVM con Hilt + Room + Retrofit.
- Backend "serverless": Google Apps Script + Google Sheets.
- Comunicarse con el usuario y los PRs **en español**.
- **Mantener Kotlin oficial code style.** Sin emojis en código a menos que se pidan.
- **Antes de tocar Room:** subir versión + agregar `Migration` + verificar el JSON de `app/schemas/`.
- **No agregar `fallbackToDestructiveMigration`** en `DatabaseModule`.
- **No introducir nuevas serializaciones** (Gson/Moshi/Kotlinx). Hoy hay 3, la dirección es consolidar en Moshi.
- **No tocar `Constants.BASE_URL`** sin el OK explícito del autor (endpoint de producción configurado fuera de la documentación pública).
- Si vas a refactorizar, leer primero [`README.md`](README.md) (arquitectura, setup, backend) y este archivo para entender el codebase y los gaps conocidos.

---

## Cómo está organizado el código

```
com.eflc.mintdrop
├── activities/        Single Activity (Compose)
├── api/               Retrofit interface + Apps Script de referencia (.gs)
├── di/                Hilt modules (Database, GoogleSheets, Service, Coroutine)
├── graph/             RootNavGraph + HomeNavGraph (Navigation Compose)
├── navigation/        AppScreens (sealed routes), Graphs (constants)
├── models/            DTOs (no persistentes)
├── repository/        interfaces + impl/
├── room/              JulepDatabase + DAOs + entities + migrations
├── service/           Orquestación (EntryRecordService, SharedExpenseService)
├── ui/                Compose: components/, screens/, theme/
└── utils/             Constants, FormatUtils, PdfUtils, RandomColor
```

Para una descripción detallada de cada paquete, ver la sección [Arquitectura](README.md#arquitectura) en `README.md`.

---

## Reglas duras (no romper)

### Persistencia (Room)

1. **Versión de DB:** subir `version = N` en `JulepDatabase` para cualquier cambio de schema.
2. **Migración:** crear `room/migration/MigrationFromXToY.kt` y registrarla en `DatabaseModule.provideDatabase`.
3. **Schemas:** verificar que se genera `app/schemas/com.eflc.mintdrop.room.JulepDatabase/N.json` y commitearlo.
4. **Nunca** habilitar `fallbackToDestructiveMigration()` en main; pierde datos del usuario.
5. **Toda nueva query DAO debe ser `suspend fun`** (o devolver `Flow`). Hay queries legacy sin `suspend` (gap G-06) — **no copiar ese patrón**.
6. `@Upsert` por defecto sobre `@Insert + @Update`.

### API / Sincronización

1. La sincronización Room ↔ Sheet **no es atómica** hoy (gap G-04). Si vas a tocar `EntryRecordServiceImpl`, no empeores la situación: idealmente implementá patrón **outbox** + `WorkManager`.
2. **No** llamar a la API desde composables. **Sí** desde ViewModel/Service vía Repository.
3. Mantener `db.withTransaction { ... }` para operaciones que afectan más de una entidad.
4. **No** inventar nuevas URLs ni endpoints sin actualizar también el `.gs` de referencia (en `api/reference/`).

### Coroutines

1. Preferir `viewModelScope.launch { withContext(Dispatchers.IO) { ... } }` sobre `viewModelScope.launch(Dispatchers.IO)`.
2. **No** cancelar el `CoroutineScope` inyectado por `CoroutineModule` (gap G-01 — bug abierto). Idealmente, eliminar la dependencia de ese módulo.
3. Cancelaciones: `viewModelScope` se cancela solo al `onCleared()`, no requiere acción manual.

### Compose / Estado

1. **Una source of truth** por estado. Si vive en el VM, no duplicarlo en `remember`.
2. Memoizar valores derivados con `remember(key) { derivedStateOf { ... } }` cuando sean costosos.
3. Form state crítico → `rememberSaveable` con `Saver` o moverlo al VM (gap G-17).
4. `LazyVerticalGrid`/`LazyColumn`: pasar `key = { it.id }` para diff estable.
5. **No** introducir `mutableStateOf` en lugares donde un `Flow` es más apropiado (especialmente para datos que vienen del repo).

### Navegación

1. Argumentos complejos: hoy se serializan como JSON URI-encoded con Gson (`Uri.encode(Gson().toJson(...))`). **No es una buena práctica** (gap G-15) — preferir pasar IDs y resolver la entidad en el VM via `SavedStateHandle`.
2. Nuevas rutas: agregarlas en `navigation/AppScreens.kt` (sealed class) y registrarlas en `graph/HomeNavGraph.kt` o `RootNavGraph.kt`.

### DI (Hilt)

1. Repos y servicios: `@Singleton`. ViewModels: `@HiltViewModel`. Activity: `@AndroidEntryPoint`.
2. Si agregás un nuevo Repo, registrarlo en `DatabaseModule` o crear un nuevo `@Module`.

---

## Reglas blandas (estilo / convenciones)

- **Nombres en inglés** para clases, funciones, variables. **Comentarios y mensajes en español** (alineado con el resto del codebase).
- **Sin comentarios obvios.** No documentar lo que el código ya dice; documentar el _porqué_ y los trade-offs.
- **Funciones cortas.** Si una composable o función pasa 100 líneas, considerá extraer.
- **Sin `!!`** salvo donde el contrato lo justifique. Hay varios `!!` problemáticos en `ExternalSheetRefRepository` (gap G-02) — no copiar el patrón.
- **No introducir nuevas dependencias** sin justificar y sin removerlas si quedan muertas. Hoy `play-services-auth` y partes de `co.yml:ycharts` están sin uso.
- **No mezclar formatters** — usar `FormatUtils` (no instanciar nuevos `NumberFormat`/`SimpleDateFormat`).

---

## Antes de proponer un cambio

### Checklist mínimo

- [ ] **Leí la documentación relevante del repo** (`README.md` para arquitectura/setup/API; este archivo para reglas y gaps).
- [ ] Si toco Room: bumpeo versión, agrego migración, verifico schema, ejecuto build.
- [ ] Si toco un endpoint: actualizo el `.gs` de referencia y dejo nota en el PR de redeploy del Apps Script.
- [ ] Si elimino código: confirmo que no haya consumidores (Hilt module, navigation, manifest, drawable referenciado).
- [ ] Si introduzco un nuevo `Flow`/state: confirmo que se observe (`collectAsState`).
- [ ] **Compilo** con `./gradlew assembleDebug` antes de declarar terminado.
- [ ] No commiteo `local.properties`.
- [ ] **No commiteo nada con secrets** (no agregar tokens, claves ni URLs de producción en commits).

### Cuándo pedir contexto al usuario

- Cualquier cambio que toque la **modelo de usuarios** (hoy hardcodeado a 2: `MY_USER_ID`/`THEIR_USER_ID`, con nombres fijos en `PdfUtils`). Generalizar a N requiere decisión de producto.
- Cualquier cambio en la **convención del Sheet** (filas "end" / "Total al mes:" / fila vacía). Está acoplado al `.gs` y al usuario lo carga manualmente.
- Cualquier cambio que rompa **compatibilidad** con la planilla histórica del usuario.
- Renombrar el package `com.eflc.mintdrop → com.eflc.julep` (es un cambio de `applicationId` → instalación nueva). Pedir confirmación expresa.

---

## Áreas con deuda técnica conocida

Resumen priorizado (lista completa en esta sección):

| ID | Severidad | Resumen |
|---|---|---|
| G-01 | Alta | `CoroutineScope` singleton cancelado en `onCleared()` |
| G-02 | Alta | NPE si no hay `external_sheet_ref` para el año actual |
| G-03 | Alta | Falta migración 1→2 |
| G-04 | Alta | Sync Room↔Sheet no atómica (sin outbox/retry) |
| G-05 | Alta | `_error` flow no se observa en UI |
| G-06 | Alta | Varias queries DAO no son `suspend` |
| G-07 | Media | Rebrand MintDrop → Julep incompleto |
| G-08 | Media | Constantes hardcoded (URLs, user IDs, subcat IDs) |
| G-09 | Media | `SharedExpenseConfigurationDetailDao` vacío |
| G-10 | Media | Solo `SHARES` implementado para splits |
| G-11 | Media | `syncExpenseCategories` no crea categorías nuevas |
| G-12 | Media | N+1 en `getMonthlyBalance` |
| G-13 | Media | Expense/Income duplicados |
| G-14 | Media | `BalacePieChart` typo + muerto |
| G-15 | Media | Gson + Moshi + JSON-en-deeplink |
| G-16 | Media | `iconRef` modelado pero no usado |
| G-17 | Media | `formState` no `rememberSaveable` |
| T-01 | Baja | Cobertura de tests = 0 |

---

## Buenas prácticas esperadas para nuevas features

### Nueva pantalla

1. Definir ruta en `navigation/AppScreens.kt` (sealed).
2. Registrarla en `graph/HomeNavGraph.kt` (o `RootNavGraph.kt`).
3. Crear paquete `ui/screens/<feature>/` con:
   - `<Feature>.kt` (composable de la pantalla)
   - `<Feature>ViewModel.kt` (`@HiltViewModel`)
4. ViewModel expone `MutableStateFlow` privado + `.asStateFlow()` público.
5. Cargas iniciales en `LaunchedEffect(true) { vm.getX() }` desde la composable.

### Nueva entidad Room

1. Crear `room/dao/entity/MyEntity.kt` (con `@Entity`, `@PrimaryKey(autoGenerate = true)`, `@ColumnInfo`).
2. Crear `room/dao/MyEntityDao.kt` (`@Dao`, queries `suspend`).
3. Registrar en `JulepDatabase`: agregar a `entities = [...]`, agregar `abstract val myEntityDao: MyEntityDao`, **bumpear `version`**.
4. Crear `room/migration/MigrationFromXToY.kt` con SQL DDL + datos iniciales si los hubiera.
5. Registrar la migración en `DatabaseModule.provideDatabase`.
6. Crear `repository/MyEntityRepository.kt` (interface) + `repository/impl/MyEntityRepositoryImpl.kt` (`@Inject`).
7. Provee el repo en `DatabaseModule`.
8. Verificar que se generó el nuevo `app/schemas/.../{N}.json` y commitearlo.

### Nuevo endpoint

1. Agregar el método `suspend fun` en `api/GoogleSheetsAPI`.
2. Crear el DTO request/response en `models/` (Moshi `@JsonClass(generateAdapter = true)`).
3. Exponerlo desde `repository/GoogleSheetsRepository`.
4. Implementar el `.gs` correspondiente en `api/reference/` (es **referencia**, hay que deployearlo en Apps Script aparte).
5. Documentar en el PR el deployment del Apps Script.

### Nuevo test

- **Unit:** `app/src/test/java/...` (JUnit 4).
- **Instrumented:** `app/src/androidTest/java/...` (Espresso / Compose UI Test).
- **Migración Room:** usar `androidx.room.testing.MigrationTestHelper`.

---

## Comandos útiles

```bash
# Build
./gradlew assembleDebug              # APK debug
./gradlew assembleRelease            # APK release (R8 enabled)
./gradlew installDebug               # Build + install en device

# Limpieza
./gradlew clean
./gradlew --stop                     # Detener daemon Gradle

# Tests
./gradlew testDebugUnitTest          # Unit tests
./gradlew connectedAndroidTest       # Instrumented tests

# Lint
./gradlew lintDebug                  # Lint Android
./gradlew ktlintCheck                # (no configurado actualmente — agregar si se necesita)
```

---

## Apertura de PRs

- **Título** breve y descriptivo, en español.
- **Descripción** debe incluir:
  - Qué cambia y por qué.
  - Cómo se probó (manual/automatizado).
  - Si hay cambio de schema Room: número de migración y data afectada.
  - Si hay cambio de Apps Script: pegar el `.gs` resultante o referencia al commit en `api/reference/`.
- **Nada de force-push a main.** Nada de borrar migraciones ya mergeadas.
- Plantilla: `.github/PULL_REQUEST_TEMPLATE.md`.

---

## Recursos

- [`README.md`](README.md) — overview, arquitectura, setup, backend y convenciones
- Schemas Room versionados en `app/schemas/`
- Apps Script de referencia en `app/src/main/java/com/eflc/mintdrop/api/reference/`
- Material 3, Compose, Room y Hilt: documentación oficial de Android / Google / Dagger
