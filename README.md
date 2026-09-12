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

Las once pantallas están construidas y navegables. El resto de los datos todavía sale de
proveedores locales en `data/local/`, a la espera de la base de datos.

| Módulo | Estado |
|---|---|
| Registro e inicio de sesión con Firebase Auth | Funcionando |
| Sesión persistente y splash que decide a dónde entrar | Funcionando |
| Mensajes de error en español según el tipo de fallo | Funcionando |
| Portadas cargadas por URL con Coil | Funcionando |
| Navegación entre las 12 rutas | Funcionando |
| Calificar, guardar, dar like y filtrar | Funcionando, en memoria |
| Subir la foto de perfil a Firebase Storage | Escrito, sin probar (ver más abajo) |
| Persistencia de reseñas y álbumes | Pendiente |
| Buscador con resultados | Pendiente |
| Modo claro | Pendiente |

**Sobre Firebase Storage:** la cadena completa está escrita y compila, pero Google retiró
Storage del plan gratuito Spark para proyectos creados después del cambio. El proyecto
`mixtapp-720eb` se creó el 7 de septiembre de 2026, así que activarlo exige plan Blaze. El
selector de galería, el paso del `Uri` hacia arriba y el camino de error sí se probaron.

---

## Arquitectura

Dos capas: UI y datos. La capa de dominio queda fuera.

```
Pantalla  ──►  ViewModel  ──►  Repository  ──►  DataSource  ──►  Firebase
   │              │                │
 pinta       estado y lógica   try/catch y Result
```

**El data source** declara las peticiones y devuelve lo que pidió quien llama. Nada más.

**El repositorio** hace el `try/catch`, traduce cada excepción de Firebase a una propia en
español y devuelve un `Result<T>` con el mismo tipo que devolvió el data source.

**El ViewModel** mira `result.isSuccess` y toma uno de los dos caminos. No conoce ninguna
excepción de Firebase.

**La pantalla** pinta y avisa. La navegación no entra al ViewModel: las lambdas `onXClick`
las resuelve `AppNavigation.kt`.

### Decisiones que vale la pena explicar

**Un solo `Scaffold`**, en `Mixtapp.kt`, con la barra inferior en su ranura. `NavigationLogic`
decide en qué rutas se ve.

**El `navController` no baja a las pantallas.** Reciben lambdas. La única excepción es
`BottomNav`, que existe solo para navegar.

**A las pantallas de detalle les llega el id, no el objeto.** El ViewModel busca la entidad y
la pantalla decide qué pintar si no existe.

**`MutableStateFlow` y un `UiState` por pantalla.** Nada de `LiveData`.

**El color sale del `MaterialTheme`.** Los 36 roles del esquema llevan la paleta del Figma, y
no hay un solo hex suelto fuera de `Color.kt`.

**Hilt construye la cadena.** El módulo solo declara `FirebaseAuth` y `FirebaseStorage`; el
resto se resuelve por `@Inject constructor`.

---

## Pantallas

| Pantalla | Qué hace |
|---|---|
| Splash | Comprueba si hay sesión y entra a Home o a Login |
| Login | Correo y contraseña, con validación y mensajes de error |
| Registro | Usuario, correo, contraseña, confirmación y términos |
| Home | Álbum en tendencia, populares y actividad de amigos |
| Detalle de álbum | Portada, etiquetas, estadísticas, calificar y reseñas |
| Escribir reseña | Estrellas, texto, estados de ánimo, fecha y favorito |
| Mis reseñas | Las reseñas propias, con cinco filtros |
| Siguiendo | Reseñas de a quien sigues, con buscador de amigos |
| Discusión | Una reseña y su hilo de comentarios |
| Notificaciones | Todas y no leídas, agrupadas por día |
| Perfil | Foto, estadísticas, actividad, calificaciones y cerrar sesión |
| Buscar | Buscador y categorías de exploración |

---

## Estructura

```text
app/src/main/java/com/example/mixtapp/
├── data/
│   ├── datasource/      AuthRemoteDataSource · StorageRemoteDataSource
│   ├── injection/       FirebaseHiltModule
│   ├── local/           proveedores de datos de prueba
│   └── repository/      AuthRepository · StorageRepository · AuthExceptions
├── navigation/
│   ├── AppNavigation.kt NavHost con las 12 rutas
│   ├── NavigationLogic.kt
│   └── Screen.kt        sealed class con las rutas
├── ui/
│   ├── components/      compartidos por dos o más pantallas
│   ├── screens/<pantalla>/
│   │   ├── <X>Screen.kt
│   │   ├── <X>State.kt
│   │   ├── <X>ViewModel.kt
│   │   ├── components/
│   │   └── model/
│   └── theme/           Color.kt · Theme.kt
├── BaseApplication.kt   @HiltAndroidApp
├── MainActivity.kt      @AndroidEntryPoint
└── Mixtapp.kt           Scaffold + barra + AppNavigation
```

---

## Tecnologías

| Qué | Para qué |
|---|---|
| Kotlin 2.2.10 | Lenguaje |
| Jetpack Compose | UI declarativa |
| Material Design 3 | Paleta, componentes y tipografía |
| Navigation Compose | Una Activity, un `NavHost`, rutas en `sealed class` |
| ViewModel y StateFlow | MVVM con un `UiState` por pantalla |
| Corrutinas | `suspend` y `viewModelScope.launch` |
| Dagger Hilt 2.60.1 con KSP | Inyección de dependencias |
| Firebase Auth | Registro, inicio de sesión y sesión persistente |
| Firebase Storage | Foto de perfil |
| Coil 2.4.0 | Portadas y avatares por URL |
| Gradle Kotlin DSL | Las 22 dependencias van por `libs.versions.toml` |

---

## Cómo ejecutarlo

Necesitas Android Studio, JDK 11 y un emulador o un dispositivo con Android 7.0 o superior
(`minSdk 24`).

```bash
git clone https://github.com/AndresContreras1/Mixtapp.git
cd Mixtapp
```

Pon tu `google-services.json` en `app/`. Sin ese archivo el build falla con
`File google-services.json is missing`. Para verlo en Android Studio hay que cambiar la vista
de **Android** a **Project**.

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
| Andrés Contreras |  |
| Andrés | |
| Andrés Loreto Quiros | |
| Laura Aponte | |

---

## Flujo de trabajo

Ramas cortas desde `master` actualizado, una por bloque de trabajo, y un pull request por
rama. Los mensajes de commit van en español, sin tildes, con prefijo `feat:`, `fix:`,
`refactor:`, `style:`, `chore:` o `docs:`.
