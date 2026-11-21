[← Documentación](README.md)

# Cómo ejecutarlo

Emulador de Firebase, datos de prueba, variantes, pruebas y APK firmado.

Necesitas Android Studio con JDK 21 (el que pide `gradle/gradle-daemon-jvm.properties`) y un
emulador o dispositivo con **Android 8.0 o superior** (`minSdk 26`). El `google-services.json` ya viene en `app/`.

```bash
git clone https://github.com/AndresContreras1/Mixtapp.git
```

Abre la carpeta en Android Studio y espera a que Gradle sincronice. **Debug** usa el emulador de
Firebase (ver abajo); **Play** con la variante *release* usa el Firebase real. Desde la terminal,
`./gradlew assembleDebug` (en Windows `gradlew.bat assembleDebug`).

<details>
<summary>Modo debug: el emulador de Firebase (Firebase CLI)</summary>

En debug, `BaseApplication` conecta Firestore y Authentication al emulador que corre en el
computador (`10.0.2.2` desde el emulador de Android): `if (BuildConfig.DEBUG)` con
`useEmulator("10.0.2.2", 8080)` y `useEmulator("10.0.2.2", 9099)`. Para eso el `build.gradle.kts`
tiene el tipo `debug` y `buildConfig = true`.

Hace falta Node.js. Desde [mixtapp-functions](https://github.com/AndresContreras1/mixtapp-functions),
que tiene los puertos en su `firebase.json`:

```bash
npm install -g firebase-tools
firebase emulators:start --only firestore,auth --project mixtapp-720eb
```

La interfaz queda en `http://127.0.0.1:4000/`, con las pestañas Authentication y Firestore. Los
datos se borran al cerrar la CLI: hay que registrarse otra vez o cargar los datos con Faker.

</details>

<details>
<summary>Catálogo y datos falsos con Faker</summary>

El script está en la carpeta `seed/` de
[mixtapp-functions](https://github.com/AndresContreras1/mixtapp-functions). Carga el catálogo real
de `catalogo.json` (50 álbumes, 126 canciones y 56 artistas de rock, metal, nu metal y
reggaetón, cada artista con la animación de su género), sus datos curiosos de `historias.json` y 20 usuarios de Faker con sus seguidores,
me gusta, reseñas y comentarios, con los mismos campos de la tabla de arriba. Por defecto escribe en el emulador; para el Firebase real se cambian
tres líneas. Las instrucciones están en ese README.

</details>

<details>
<summary>Pruebas con mocks (sin emulador)</summary>

Están en `app/src/test`, por capa. Corren en el computador, sin emulador: clic derecho en la carpeta
`test` → **Run 'Tests in…'**. MockK simula las capas de abajo (`mockk`, `coEvery { … } returns …`,
`any()`) y se prueba solo la capa de arriba.

| Clase | Pruebas | Qué revisa |
|---|---|---|
| `repository/CatalogoRepositoryTest` | 11 | el promedio, el total, el % que recomienda y si ya califiqué; la canción en tres líneas y sus likes como `1.2K`; el artista que no existe; el data source que falla; el like sin sesión; el detalle en vivo (guardado, ya calificado y su promedio) y el que no existe; cada álbum del catálogo con sus reseñas; la historia que no existe y la sección vacía que no se muestra; los lanzamientos de la banda de un artista que me gusta, del más nuevo al más viejo |
| `repository/ReviewRepositoryTest` | 9 | la reseña nueva se guarda donde estoy, sin permiso se guarda sin ubicación y al editarla no se pide la ubicación ni cambia la fecha de creación; mis reseñas de la última editada a la más vieja; la respuesta debajo de su comentario; la discusión de una reseña que no existe; en el mapa solo las que tienen ubicación, la de hace más de 24 horas sale y el corazón lleno de la que tiene mi like |
| `repository/UsuarioRepositoryTest` | 7 | el perfil con `bio` y `fotoUrl` nulas (quedan vacías); sin documento; la búsqueda no me incluye; guardar el perfil lo modifica si tengo documento y lo crea si no; seguirme a mí mismo y seguir sin sesión |
| `repository/UbicacionRepositoryTest` | 3 | la ubicación del celular, sin dato (`UbicacionNoDisponibleException`) y sin permiso |
| `repository/StorageRepositoryTest` | 2 | la foto subida retorna su URL y sin sesión no se sube |
| `viewmodels/SignUpViewModelTest` | 10 | registro válido, contraseña de 4 caracteres, contraseñas distintas, correo ya registrado, datos que Authentication rechaza, sin aceptar los términos, usuario con espacios, correo sin arroba, el correo se registra sin espacios y, si Firestore falla, no navega y pide iniciar sesión |
| `viewmodels/LoginViewModelTest` | 7 | credenciales válidas, contraseña incorrecta, campos vacíos, correo sin arroba, contraseña corta, el correo entra sin espacios y si falla el token se entra igual |
| `viewmodels/WriteReviewViewModelTest` | 4 | sin texto, fecha con mes 13, reseña válida y el error sin conexión (no navega) |
| `viewmodels/MapaViewModelTest` | 8 | un marcador por reseña, la reseña nueva aparece sin volver a entrar, el flujo que falla, tocar un marcador muestra su reseña, tocarlo otra vez la cierra, el like sin conexión, el mapa se centra donde estoy y sin ubicación no se mueve |
| `viewmodels/DetailViewModelTest` | 5 | el item que existe se muestra sin error, el que no existe da su mensaje, el like sin conexión (el error es del botón, el item se sigue viendo) y poner el item a sonar (aviso, o error sin conexión) |
| `viewmodels/FollowingViewModelTest` | 8 | las reseñas de quienes sigo y lo que suena llegan sin carga; el error de los seguidos; el de lo que suena va aparte; el 🔥 que falla no borra la fila y el que funciona limpia el error; quitar lo que suena; el like sin conexión; filtrar por el nombre del autor |
| `viewmodels/DiscussionViewModelTest` | 5 | la discusión que no existe, a quién se responde, la respuesta a una respuesta queda en el hilo del comentario principal, el comentario vacío y cancelar la respuesta |
| `viewmodels/NotificationsViewModelTest` | 5 | Hoy y Antes, los lanzamientos que fallan solo marcan su pestaña, la ya leída no se vuelve a marcar, la que falla al marcarse y elegir pestaña |
| `viewmodels/ProfileViewModelTest` | 5 | mis datos y mis listas sin carga; mis guardados que fallan solo marcan su lista; la cuenta sin documento muestra el usuario del correo y el aviso; la foto que no se puede subir y la que sí |
| `repository/MixtapeRepositoryTest` | 12 | mis mixtapes de la más nueva a la más vieja con su resumen y en cuáles ya está un item; la mixtape con sus items en el orden en que se agregaron y su creador; la que no existe; crear sin sesión y crear retorna el id; agregar a una que ya no existe, bien y con el data source que falla; quitar un item bien y sin sesión; borrarla |
| `viewmodels/CrearMixtapeViewModelTest` | 5 | sin nombre, nombre de 41 caracteres, descripción de 151, datos válidos (navega y quita los espacios) y el error sin conexión |
| `viewmodels/AgregarAMixtapeViewModelTest` | 5 | mis mixtapes con cuál ya tiene el item y el item; el error del item no tapa las mixtapes; agregar navega; en una que ya lo tiene no se repite; la mixtape que ya no existe |
| `viewmodels/MixtapeDetailViewModelTest` | 5 | la mixtape con sus items, la que no existe, quitar un item que falla, borrarla y borrar antes de que llegue |
| `repository/SonandoRepositoryTest` | 9 | solo lo mío y lo de quienes sigo, con lo mío primero; lo de hace más de 12 horas no sale; el mapeo del item, las reacciones y hace cuánto; poner a sonar sin sesión y con el data source que falla; no se le reacciona a lo mío y sí a lo de otro; quitarlo con y sin sesión |
| `viewmodels/ResumenViewModelTest` | 11 | el total y el promedio con un decimal, la reseña del año pasado no cuenta, el género y el artista favoritos, sin reseñas solo salen dos tarjetas, la mejor calificada y la de más me gusta, los seguidores, el error de mis reseñas ocupa la pantalla y el de mis me gusta solo avisa, Siguiente no pasa de la última tarjeta y Ver de nuevo vuelve a la primera |
| `repository/DueloRepositoryTest` | 6 | el duelo sin documento (0 votos y sin items), los items del documento con su tipo, mi voto entre los votos (mi opción y `esMio`), votar sin sesión, votar bien y el data source que falla |
| `viewmodels/DuelosViewModelTest` | 8 | el par de hoy son dos distintos del mismo género y tipo, el tipo sin dos del mismo género no tiene duelo, si alguien ya votó los items salen del documento, votar sin elegir, votar bien limpia el formulario, el error sin conexión conserva la opinión, la opinión se corta en 140 y cambiar de pestaña limpia el formulario |
| `viewmodels/HomeViewModelTest` | 9 | el catálogo que falla da el error; mis me gusta que fallan solo avisan; Para ti con 9 me gusta de metal y 1 de reggaetón saca un solo reggaetón; Descubre no muestra lo que ya tiene mi me gusta y pone primero mi género (si no alcanza, completa con otros); Tendencias pone primero el Top Colombia aunque me guste otro género y su tarjeta es el #1 de Colombia; la tarjeta de Para ti es un álbum en lista de mi género, también al volver a entrar varias veces, y sin álbumes en lista es el más popular |
| `dtos/FormatosTest`, `dtos/InteraccionesSocialesTest`, `dtos/DueloMapeosTest`, `local/DatosLocalesTest` | 14 + 8 + 6 + 5 | `14K`, `3 h`, los links de YouTube, Spotify e Instagram, el subgénero que no tiene el backend, los contadores y las marcas del mapeo, los porcentajes y el ganador de un duelo y los datos fijos de la app (los cuatro géneros) |
| `model/ReglasTest` | 9 | el correo sin arroba, el usuario del correo, el usuario con espacios, el Instagram vacío, con tilde y más largo que el límite, y la fecha de escucha (31 de febrero, mañana y hoy) |

Los ViewModels reciben el dispatcher de prueba en el constructor (`@IoDispatcher`),
`Dispatchers.setMain` lo pone como principal y `advanceUntilIdle()` espera a que terminen sus
corrutinas antes de los asserts. En `build.gradle.kts`, `unitTests.isReturnDefaultValues = true`
hace que `Log.d` responda 0 en el computador, donde no hay Android.

</details>

<details>
<summary>Pruebas de integración</summary>

Están en `app/src/androidTest`, contra el emulador de Firebase: necesitan el emulador de Android
prendido y la Firebase CLI corriendo (`firebase emulators:start --only firestore,auth --project mixtapp-720eb`).

| Clase | Pruebas | Qué revisa |
|---|---|---|
| `datasource/UsuarioFirestoreDataSourceTest` | 8 | leer por id (válido y que no existe → `null`), registrar, seguir, el contador de seguidores, dejar de seguir, la lista de seguidores y las reseñas de un usuario |
| `datasource/AlbumFirestoreDataSourceTest` | 7 | leer por id (válido y que no existe → `null`), el like a un álbum (queda `liked`, sube `likesCount` y deja la copia en `usuarios/<uid>/likes`, cada uno en su prueba), quitarlo y el listener con mi like |
| `datasource/MixtapeFirestoreDataSourceTest` | 7 | crear una mixtape aparece en las del usuario; agregar un item sube el contador y guarda su portada, dos veces el mismo no se repite y a una que no existe retorna `false`; quitar uno de dos baja el contador; mis mixtapes marcan en cuáles ya está un item; borrarla borra sus items |
| `datasource/SonandoFirestoreDataSourceTest` | 9 | poner a sonar aparece en la lista, la más reciente primero, lo de hace mucho no llega, el 🔥 sube el contador y queda marcado, el segundo 🔥 lo quita y baja el contador, volver a poner lo mismo reinicia las reacciones y quitarlo borra el documento con sus reacciones |
| `datasource/DueloFirestoreDataSourceTest` | 6 | el duelo en el que nadie ha votado (`null`), el primer voto crea el duelo con ese voto, el voto de otro suma sin crear otro duelo, cambiar de opción pasa el voto al otro, votar dos veces por el mismo no suma dos veces y las opiniones del más nuevo al más viejo |
| `datasource/HistoriaFirestoreDataSourceTest` | 2 | la historia de un álbum con sus secciones en orden, y la de un item que no tiene (`null`) |
| `datasource/ReviewFirestoreDataSourceTest` | 9 | leer por id, like y su contador, comentar y `commentsCount`, el orden de los comentarios, like a un comentario y borrar la reseña con sus comentarios y las reseñas desde hace 24 horas (la de hoy sí, las de 2025 no) |
| `repository/ReviewRepositoryIntegrationTest` | 8 | guardar la reseña (`Result` bien y sin sesión), la copia del nombre y del item, like sin sesión, comentar, el hilo de respuestas, la discusión que no existe y eliminar (el documento ya no existe) |
| `repository/UsuarioRepositoryIntegrationTest` | 9 | mi perfil (con documento, sin documento, sin sesión), registrar, seguirme a mí mismo, seguir a otro (sube su contador), seguir sin tener documento (se crea), seguir a alguien que no existe y modificar el perfil |
| `viewmodels/SignUpViewModelIntegrationTest` | 2 | el registro válido navega y guarda el usuario; el correo ya registrado muestra su error. La prueba espera el estado con `uiState.map { … }.first { … }` |

Cada clase tiene `@Before` (conecta al emulador con `conectarEmulador()`, limpia y carga sus propios
datos con `DatosDePrueba.kt`) y `@After` (borra las colecciones con sus subcolecciones en batches; en
los repositorios también borra la cuenta de Authentication con `borrarCuentaDePrueba`, aunque la
prueba haya cerrado la sesión, y la cierra). Las cuentas de prueba se crean con
`crearCuentaDePrueba` y la contraseña `CONTRASENA_DE_PRUEBA`. Se corren con clic derecho en la carpeta `androidTest` → **Run 'Tests in…'**.

Las pruebas usan el mismo emulador que la app en debug y lo dejan vacío. Después de correrlas hay
que volver a cargar el seed; la cuenta con la que se entra a la app recupera su documento sola al
iniciar sesión o al seguir a alguien.

**Antes de correr `androidTest`:**

1. Prender el emulador de Firebase en `mixtapp-functions`, solo con lo que usan las pruebas:
   `firebase emulators:start --only firestore,auth --project mixtapp-720eb`.
2. Borrar los datos de la app en el emulador de Android: `adb shell pm clear com.example.mixtapp`.
   Las e2e abren la app antes del `@Before`: si quedó una sesión guardada, el inicio empieza a leer
   Firestore antes de `conectarEmulador()`, la conexión al emulador falla y la prueba seguiría
   contra el Firebase real. Si pasa, el Logcat lo muestra con el filtro `DatosDePrueba`.
3. Abrir la app una vez y aceptar el permiso de notificaciones (`pm clear` también lo quita).
4. Al terminar, volver a cargar el seed (`node seed.js` en `mixtapp-functions/seed`).

</details>

<details>
<summary>Pruebas end to end</summary>

Están en `app/src/androidTest/…/e2e`, una clase por caso de uso del sprint 13. Un robot toca la
pantalla y escribe, desde el registro hasta la base del emulador. Usan Hilt (`@HiltAndroidTest`,
`HiltAndroidRule` y el `HiltTestRunner` de `androidTest`) y arrancan en `MainActivity` con
`createAndroidComposeRule`. Los elementos que tocan o revisan llevan un `testTag`, y la raíz de
cada pantalla tiene el suyo (`loginScreen`, `homeScreen`, `detailScreen`…) para esperar a que
aparezca.

| Clase | Caso de uso |
|---|---|
| `RegistroYLikeE2E` | Un usuario nuevo se registra con la contraseña `1234` y ve el error; la corrige a `123456` y entra. Mientras tanto sigue en la pantalla de registro. Abre la primera publicación y revisa su título y su artista, da like a la primera reseña (el contador pasa de 0 a 1), vuelve atrás, entra otra vez y quita el like (vuelve a 0). |
| `SeguirUsuarioE2E` | Un usuario ya registrado inicia sesión, abre el perfil del autor de la primera reseña y revisa su nombre, su @usuario y sus 10 seguidores, lo sigue (pasa a 11), vuelve al inicio, abre Siguiendo y encuentra la reseña de ese usuario. |

Cada `@Before` conecta el emulador dentro de un `try` (con la aplicación de prueba de Hilt no corre
`BaseApplication`), limpia la base y carga sus datos con `DatosDePrueba.kt`; el `@After` borra la
cuenta con su correo (aunque la prueba haya fallado antes de entrar) y vuelve a limpiar. Las esperas (`esperarTag`, `esperarTexto`) y la limpieza son comunes a
los dos casos y están en `e2e/EsperasE2E.kt`. Necesitan el emulador de Android y la Firebase CLI corriendo, como las de
integración. El permiso de notificaciones tiene que estar concedido antes de correrlas (abrir la
app una vez y aceptarlo): si el diálogo aparece, tapa la pantalla y el robot no encuentra nada.

</details>

<details>
<summary>Firestore: crear la base y los datos iniciales</summary>

1. Consola de Firebase → **Firestore Database → Crear base de datos**, en **modo de prueba**.
2. Cargar el catálogo y los usuarios de ejemplo con el seed de
   [mixtapp-functions](https://github.com/AndresContreras1/mixtapp-functions) (ver *Catálogo y datos
   falsos con Faker*). Si la base tiene los álbumes de antes (`1` a `8` o `album_1` a `album_15`) y
   reseñas con `albumId`, primero se borran las colecciones `albumes`, `reviews` y `usuarios` desde
   la consola.
3. Crear una cuenta en la app: el registro guarda tus datos en `usuarios` con tu uid.

Las reglas de seguridad están en el README de
[mixtapp-functions](https://github.com/AndresContreras1/mixtapp-functions) y se publican en
**Firestore Database → Reglas**; cada vez que cambian hay que volver a publicarlas. Las de
**Storage → Reglas** siguen en modo de prueba, que vence unos 30 días después de crearlo.

</details>

<details>
<summary>Cloud Functions y notificaciones</summary>

Las funciones se publican desde [mixtapp-functions](https://github.com/AndresContreras1/mixtapp-functions)
con `firebase deploy --only functions` (necesitan el plan Blaze). Para probar las notificaciones,
inicia sesión en dos celulares o emuladores con cuentas distintas, acepta el permiso de
notificaciones, deja uno en segundo plano y dale like desde el otro a una de sus reseñas.

El token de cada celular sale en el Logcat con el filtro `Token`. Con él se puede enviar una
notificación de prueba desde **Messaging → Enviar mensaje de prueba**.

</details>

<details>
<summary>Usar el backend de Express en vez de Firestore</summary>

En `data/injection/DataSourceModule.kt` cambia la implementación de cada `@Binds`
(`...FirestoreDataSourceImpl`) por la de Retrofit (`...RetrofitDataSourceImpl`) y corre
`npm run dev` en `mixtapp-backend`. Los repositorios no cambian: reciben el contrato. El emulador lo
encuentra en `http://10.0.2.2:3000/`.

- Los usuarios de Firebase no existen en el backend, así que la implementación de Retrofit trata
  al usuario `1` como el usuario de la app: guarda mis reseñas a su nombre y las devuelve con mi uid
  para que el repositorio sepa que son mías.
- El backend tiene álbumes, canciones y artistas, pero sus reseñas son solo de álbumes:
  `ReviewRetrofitDto` convierte cada una a la reseña genérica de la app. No tiene rutas para crear
  ni modificar usuarios, ni likes, guardados, seguidores, comentarios o notificaciones. Igual que en
  clase, esas funciones quedan como *to-do* en las implementaciones de Retrofit y las listas salen
  vacías: solo funcionan con Firestore.
- Con HTTP no hay una conexión continua: el flujo del inicio manda una sola vez lo que responde
  `GET /reviews`. La discusión de una reseña usa `GET /reviews/:id`, que trae el usuario y el álbum
  con `include` en la misma consulta.

</details>

<details>
<summary>Dos ajustes en la consola de Firebase</summary>

Sin ellos, dos de los mensajes de error nunca salen:

1. **Authentication → Settings → Protección contra enumeración de correos: desactivada.** Mientras
   esté activa, Firebase no distingue "no existe la cuenta" de "contraseña incorrecta".
2. **Authentication → Settings → Política de contraseñas**: mínimo 6 caracteres, sin exigir
   mayúscula ni número. Es la misma regla que valida la app antes de registrar.

</details>
