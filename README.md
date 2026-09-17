<p align="center">
<img width="500" height="500" alt="Logo de Mixtapp" src="https://github.com/user-attachments/assets/83f40bd9-b685-42af-a5e3-2a8f3c320ff8" />
</p>

# Mixtapp

App Android para calificar y reseñar álbumes de música. El usuario abre un álbum, le pone de
0 a 5 estrellas, escribe su reseña y ve lo que publican las personas a las que sigue. Es el
proyecto semestral de Computación Móvil en la Pontificia Universidad Javeriana.

Está escrita en Kotlin con Jetpack Compose, una sola Activity y arquitectura en capas sobre
Firebase.

---

## Estado

La autenticación funciona contra Firebase y se probó en el emulador con ocho escenarios:
credenciales incorrectas, correo inexistente, correo ya registrado, contraseña débil, sesión
persistente, cierre de sesión, bloqueo por intentos y sin conexión.

Las doce pantallas están construidas y navegables. Los datos del dominio pasan por data
sources y repositorios, pero la fuente sigue siendo local: los `object` de `data/local/`
hacen de base de datos falsa hasta que entre la real.

| Módulo | Estado |
|---|---|
| Registro e inicio de sesión con Firebase Auth | Funcionando |
| Sesión persistente y splash que decide a dónde entrar | Funcionando |
| Mensajes de error en español según el tipo de fallo | Funcionando |
| Portadas cargadas por URL con Coil | Funcionando |
| Navegación entre las 12 rutas | Funcionando |
| Calificar, guardar, dar like y comentar | Funcionando, en memoria |
| Filtros del inicio, de Siguiendo y categorías de Buscar | Funcionando, en memoria |
| Subir la foto de perfil a Firebase Storage | Escrito, sin probar (ver más abajo) |
| Persistencia de reseñas y álbumes | Pendiente |
| Modo claro | Fuera de alcance: la app es solo oscura |
| Tipografía propia del tema | Pendiente |

**Sobre Firebase Storage:** la cadena completa está escrita y compila, pero Google retiró
Storage del plan gratuito Spark para proyectos creados después del cambio. El proyecto
`mixtapp-720eb` se creó el 7 de septiembre de 2026, así que activarlo exige plan Blaze. El
selector de galería, el paso del `Uri` hacia arriba y el camino de error sí se probaron, y el
código queda listo para cuando se active la facturación.

---

## Arquitectura

Dos capas: UI y datos. La capa de dominio queda fuera.

```
Pantalla  ──►  ViewModel  ──►  Repository  ──►  DataSource  ──►  Firebase / datos locales
   │              │                │
 pinta       estado y lógica   try/catch y Result
```

**El data source** declara las peticiones y devuelve lo que pidió quien llama. Nada más.

**El repositorio** hace el `try`/`catch`, traduce cada excepción a una propia y devuelve un
`Result<T>` con el mismo tipo que devolvió el data source. El ViewModel nunca ve una
excepción de Firebase.

**El ViewModel** mira `result.isSuccess` y toma uno de los dos caminos. Ahí vive la lógica de
negocio: filtrar, ordenar y decidir qué se muestra.

**La pantalla** pinta y avisa. La navegación no entra al ViewModel: las lambdas `onXClick`
las resuelve `AppNavigation.kt`.

### Decisiones que vale la pena explicar

**Las entidades viven en `data/model`.** La capa de datos no importa nada de `ui/`. Los
modelos que llevan `@StringRes` (filtros y pestañas) sí se quedan en `ui/`, porque son de
presentación y no entidades.

**Un solo `Album`.** El álbum no está repetido por pantalla: `SongReviewUi`, `MyReviewUi`,
`FollowingReviewUi`, `DiscussionReviewUi` y `FriendActivityUi` lo referencian.

**Un solo `Scaffold`**, en `Mixtapp.kt`, con la barra inferior y el botón flotante en sus
ranuras. `NavigationLogic` decide en qué rutas se ven: solo en los cuatro destinos de la
barra, así que en las pantallas de detalle no aparece.

**El `navController` no baja a las pantallas.** Reciben lambdas. La única excepción es
`BottomNav`, que existe solo para navegar.

**A las pantallas de detalle les llega el id, no el objeto.** El ViewModel busca la entidad y
la pantalla decide qué pintar si no existe.

**`MutableStateFlow` y un `UiState` por pantalla.** Nada de `LiveData`.

**El color sale del `MaterialTheme`.** Los 36 roles del esquema llevan la paleta del Figma, y
no hay un solo color quemado fuera de `Color.kt`.

**Hilt construye la cadena.** El módulo solo declara `FirebaseAuth` y `FirebaseStorage`; el
resto se resuelve por `@Inject constructor`.

---

## Pantallas

| Pantalla | Qué hace |
|---|---|
| Splash | Comprueba si hay sesión y entra a Home o a Login |
| Login | Correo y contraseña, con validación y mensajes de error |
| Registro | Usuario, correo, contraseña, confirmación y términos |
| Home | Álbum en tendencia, populares con filtros y actividad de amigos |
| Detalle de álbum | Portada, etiquetas, estadísticas, calificar y reseñas con like |
| Escribir reseña | Estrellas, texto, estados de ánimo, fecha y favorito |
| Mis reseñas | Las reseñas propias, con cinco filtros |
| Siguiendo | Reseñas de a quien sigues, con filtros y buscador de amigos |
| Discusión | Una reseña y su hilo de comentarios |
| Notificaciones | Todas y no leídas, agrupadas por día |
| Perfil | Foto, estadísticas, actividad, calificaciones y cerrar sesión |
| Buscar | Buscador por nombre y categorías que ordenan los resultados |

---

## Estructura

```text
app/src/main/java/com/example/mixtapp/
├── data/
│   ├── datasource/      Auth · Storage · Album · Review · Social
│   ├── injection/       FirebaseHiltModule
│   ├── local/           proveedores de datos de prueba
│   ├── model/           las entidades del dominio
│   └── repository/      Auth · Storage · Album · Review · Social · AuthExceptions
├── navigation/
│   ├── AppNavigation.kt NavHost con las 12 rutas
│   ├── NavigationLogic.kt
│   └── Screen.kt        sealed class con las rutas
├── ui/
│   ├── components/      compartidos por dos o más pantallas, cada uno con su preview
│   ├── screens/<pantalla>/
│   │   ├── <X>Screen.kt
│   │   ├── <X>State.kt
│   │   ├── <X>ViewModel.kt
│   │   ├── components/
│   │   └── model/       solo filtros y pestañas
│   └── theme/           Color.kt · Theme.kt
├── BaseApplication.kt   @HiltAndroidApp
├── MainActivity.kt      @AndroidEntryPoint
├── Mixtapp.kt           Scaffold + barra + botón flotante + AppNavigation
├── MixtappState.kt
└── MixtappViewModel.kt
```

---

## Tecnologías

| Qué | Para qué |
|---|---|
| Kotlin 2.2.10 | Lenguaje |
| Jetpack Compose | UI declarativa |
| Material Design 3 | Paleta y componentes: Card, IconButton, OutlinedTextField, NavigationBar, FAB |
| Navigation Compose | Una Activity, un `NavHost`, rutas en `sealed class` |
| ViewModel y StateFlow | MVVM con un `UiState` por pantalla |
| Corrutinas | `suspend` y `viewModelScope.launch` |
| Dagger Hilt 2.60.1 con KSP | Inyección de dependencias |
| Firebase Auth | Registro, inicio de sesión y sesión persistente |
| Firebase Storage | Foto de perfil |
| Coil 2.4.0 | Portadas y avatares por URL |
| Gradle Kotlin DSL | Las dependencias van por `libs.versions.toml` |

---

## Cómo ejecutarlo

Necesitas Android Studio, JDK 11 y un emulador o un dispositivo con Android 7.0 o superior
(`minSdk 24`).

```bash
git clone https://github.com/AndresContreras1/Mixtapp.git
cd Mixtapp
```

El `google-services.json` del proyecto de Firebase ya viene en `app/`, así que el build
funciona sin configurar nada más. Para verlo en Android Studio hay que cambiar la vista de
**Android** a **Project**.

Abre la carpeta en Android Studio, espera a que Gradle sincronice y dale a **Run**. Desde la
terminal:

```bash
./gradlew assembleDebug
```

En Windows:

```bash
gradlew.bat assembleDebug
```

### Dos ajustes en la consola de Firebase

Sin ellos, dos de los mensajes de error nunca salen:

1. **Authentication → Settings → Protección contra enumeración de correos: desactivada.**
   Mientras esté activa, Firebase no distingue "no existe la cuenta" de "contraseña
   incorrecta".
2. **Authentication → Settings → Política de contraseñas → Exigir aplicación**, con mayúscula
   y número. En modo *Notificar* el registro se acepta igual. Deja sin marcar "Forzar la
   actualización durante el acceso" para no bloquear a los usuarios de prueba.

### Un ajuste en el emulador

En el AVD, `hw.keyboard` controla el teclado. Con `yes` escribes con el teclado del computador
y el teclado en pantalla no aparece; con `no` es al contrario. Si cambias la opción, arranca el
emulador con **Cold Boot** para que aplique.

---

## Documentación

| Archivo | Qué es |
|---|---|
| `Docs/Diagrama de clases.jpeg` | Diagrama de clases |
| `Docs/Diagrama de relacion.jpeg` | Diagrama entidad-relación |
| `Docs/logo.png` | Logo de la aplicación |

---

## Equipo

Proyecto de **Computación Móvil**, Pontificia Universidad Javeriana, sede Bogotá.
Profesor: Juan Sebastián Angarita Torres.

| Integrante | GitHub |
|---|---|
| Andrés Contreras | [@AndresContreras1](https://github.com/AndresContreras1) |
| Andrés Loreto Quiros | |
| Laura Aponte | |

---

## Flujo de trabajo

Ramas cortas desde `master` actualizado, una por bloque de trabajo, y un pull request por
rama. Los mensajes de commit van en español, sin tildes, con prefijo `feat:`, `fix:`,
`refactor:`, `style:`, `chore:` o `docs:`.
