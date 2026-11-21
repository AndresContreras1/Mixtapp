[← Documentación](README.md)

# Decisiones

Las decisiones de diseño y de arquitectura, y por qué se tomaron.

- **La navegación no entra al ViewModel.** Las pantallas reciben lambdas `onXClick` y las resuelve
  `AppNavigation.kt`. La única excepción es `BottomNav`, que existe solo para navegar.
- **A las pantallas de detalle les llega el id, no el objeto.** El ViewModel busca la entidad y la
  pantalla decide qué pintar si no existe.
- **Un solo `Scaffold`**, en `Mixtapp.kt`, con la barra inferior (Inicio, Buscar, Duelos, Mapa y
  Perfil) y el botón flotante en sus ranuras. `NavigationLogic` decide en qué rutas se ven: el botón
  **+** abre el catálogo para elegir qué calificar y no sale en el mapa, donde taparía la tarjeta de
  la reseña. Cada pestaña de la barra vuelve al inicio con `popUpTo`, así atrás desde cualquier
  pestaña lleva al inicio y desde el inicio sale de la app.
- **Un solo detalle para álbumes, canciones y artistas.** La ruta lleva el tipo y el id
  (`detail/{tipo}/{itemId}`, como `followList`) y el repositorio escucha el data source de ese tipo.
  Los datos curiosos usan la misma ruta en su pantalla.
- **Una sola calificación por item.** La pantalla de escribir carga mi reseña de ese item si ya
  existe: si llega su id se modifica y si no se crea.
- **Una sola tarjeta de reseña** (`ReviewCard`) para toda la app: el inicio, el detalle, los perfiles,
  Mis reseñas, Siguiendo, la discusión y el mapa. El lápiz abre Escribir reseña y la papelera la
  elimina.
- **Los componentes que usan dos o más pantallas van en `ui/utils`** y reciben todas sus acciones,
  sin valores por defecto: la fila de filtros y pestañas (`FilaDeOpciones`), la tarjeta translúcida,
  la fila de un item (`ItemRow`), el pie del login y del registro, el tiempo transcurrido, la tarjeta
  de una mixtape y el encabezado de un item. Una acción que no existe en una pantalla se pasa como
  `null` (la flecha de volver en las pestañas de la barra, el item en el detalle).
- **Las reglas van en `data/model`**, como funciones y constantes que usan los ViewModels: la
  contraseña, el correo y el usuario de la cuenta, los límites del perfil, la fecha de escucha de la
  reseña, la opinión del duelo y el nombre y la descripción de la mixtape.
- **El registro no entra si Firestore falla.** La cuenta ya quedó en Authentication: se cierra la
  sesión y el mensaje pide iniciar sesión, que crea el documento con el usuario del correo.
- **El promedio y el porcentaje que recomienda se calculan en el repositorio** con las reseñas de
  cada item.
- **Mi reseña la marca el repositorio.** Cada reseña llega con `isMine` (su `usuarioId` es mi uid),
  así el inicio, el detalle y la pantalla de escribir saben cuál es la mía sin otra petición.
- **Las filas del inicio las arma el ViewModel.** En Para ti, Descubre y Seguidos cada género pesa cuántos items de él me gustan
  (mis me gusta y mis reseñas de 3 estrellas o más) y recibe esa parte de los 10 cupos, sin mínimo:
  si casi todo es metal, el reggaetón puede quedar con uno o con ninguno. Los géneros se intercalan
  y, si sobran cupos, se completa primero con más de mis géneros.
  - **Para ti** ordena por popularidad.
  - **Descubre** quita lo que ya tiene mi me gusta o mi reseña y ordena por mi subgénero, por lo
    más nuevo y por popularidad.
  - **Tendencias** no se reparte por mis géneros: pone primero lo que está en el Top Colombia de
    Deezer, por su puesto, y completa con lo más popular. Los puestos vienen guardados en el
    catálogo (no hay una API externa en la app).
- **El álbum En tendencia cambia al volver al inicio.** La pantalla llama a
  `elegirAlbumEnTendencia()` en su `LaunchedEffect(Unit)`, que corre cada vez que el inicio vuelve a
  aparecer. El ViewModel elige al azar uno de los álbumes que están en la lista de Deezer de mis
  géneros y guarda su id en el estado, para que un like o una reseña nueva no lo cambien. Si ninguno
  está en una lista, queda el más popular. En la pestaña Tendencias la tarjeta es el #1 de Colombia.
  En Descubre y Seguidos no hay tarjeta: el ViewModel la deja en `null`. Después de la tarjeta van
  las reseñas de quienes sigo, en una fila, y si no hay ninguna, el botón **Descubrir personas**.
  - **Seguidos** muestra lo que reseñaron los usuarios que sigo.
- **Los links de YouTube, Spotify e Instagram salen del mapeo**, con el artista y el título (las
  canciones buscan su video oficial). No se guardan en la base y se abren con `LocalUriHandler`. En
  el detalle van con el logo de cada app (`ic_youtube` e `ic_spotify`).
- **El fragmento de 30 segundos suena dentro de la app**, en el detalle y en la tarjeta en
  tendencia. Va por capas, como la ubicación:
  - `VistaPreviaRetrofitDataSourceImpl` busca la canción en la API pública de Deezer
    (`DeezerRetrofitService`, sin llave, con su propio Retrofit en `AppModule`) y retorna el enlace
    del mp3 o `null`. El enlace trae un token que vence, por eso se pide al tocar play y no se guarda.
  - `ReproductorAndroidDataSourceImpl` (en `impl/android`) lo hace sonar con el `MediaPlayer` de
    Android dentro de un `callbackFlow`: emite `Cargando`, `Sonando` y `Terminada`, y `awaitClose`
    libera el audio. El volumen sube y baja con un fade (`volumenDelFade` en `data/model`).
  - `ReproductorRepository` arma la búsqueda (`busquedaDeVistaPrevia`) y decide el error
    (`SinVistaPreviaException`, sin conexión). El ViewModel guarda qué item suena y cancelar su
    corrutina detiene el audio; la pantalla lo detiene al salir con `DisposableEffect`.
- **Duelos sin votos no muestran 50 y 50:** `DueloUi.sinVotos` cambia la barra por "Sé el primero
  en votar" y oculta los porcentajes. Votar queda apagado hasta elegir uno (`puedeVotar` en
  `ReglasDeDuelo`); la tarjeta elegida lleva borde, check y elevación, y la barra se anima cuando
  llega un voto.
- **Tu año se ve como una historia:** una tarjeta a la vez con los segmentos de progreso y, al
  final, Compartir (un `Intent.ACTION_SEND` con el texto). Son cuatro tarjetas: mis reseñas (con la
  más querida, el total y el promedio), mi género con la portada grande de un álbum de ese género, mi
  artista con su foto y lo que mejor califiqué. Con imagen, el degradado de la tarjeta sale del color
  de esa imagen (Palette). Con
  pocos datos (`hayPocosDatos` en `ReglasDelResumen`) no afirma el género ni el artista.
- **Las fechas se leen igual en toda la app** (`fechaConMes` en `data/model/Fechas.kt`): "3 h", "2 d"
  y, desde una semana, "21 oct" (con el año si es de otro). Los lanzamientos muestran su fecha de
  salida y en Buscar se pueden ordenar de lo más nuevo a lo más viejo o al revés.
- **Buscar** tiene los tres criterios con ícono y el mismo degradado de la marca, y los cuatro géneros
  (Rock, Metal, Nu metal y Reggaetón) en tarjetas horizontales: la portada en gris con el degradado
  rosado y morado encima, así se ven parejas.
- **Sonando ahora** (`SonandoAhoraRow` en `ui/utils`) sale en Siguiendo y arriba de las
  notificaciones.
- **Playlists hechas para mí:** la fila "Hecho para ti" del inicio. `armarPlaylists` (en
  `data/model/ReglasDePlaylist`) arma, con el catálogo y lo que me gusta, una mezcla por cada uno de
  mis dos géneros, Descubre (lo de mis géneros que todavía no me gusta), Top Colombia y Lo más
  escuchado. Como el resultado siempre es el mismo para los mismos datos, la pantalla de la
  playlist la vuelve a armar con su id. Cada canción suena 30 segundos y "Reproducir todo" las
  encadena: al terminar una sigue la siguiente.
- **Todo lo que se toca rebota** (`Modifier.rebote()` en `ui/utils`): se encoge un poco mientras
  el dedo está encima y vuelve con un resorte al soltar. Escucha el toque sin consumirlo, así que el
  clic sigue igual. Lo usan los botones, los chips, las tarjetas, las pastillas, el play y el corazón.
- **Comunidad** (la ruta de Siguiendo) tiene cuatro pestañas:
  - **Siguiendo:** la pantalla de siempre con su `FollowingViewModel`.
  - **Afinidad:** las personas con gustos parecidos, aunque no las siga. `ComunidadRepository` lee
    mis likes y los de los demás una vez y calcula el match con `porcentajeDeAfinidad` en
    `data/model/ReglasDeComunidad` (60 % los items en común y 40 % los géneros).
  - **Foros:** uno por álbum, canción o artista (`foros/{itemId}` con su subcolección `mensajes`,
    en vivo). El mensaje y el documento del foro se escriben en un batch. También se entra desde el
    detalle.
  - **Mensajes:** chats directos (`chats/{uidA_uidB}`; el id sale de `idDelChat` con los dos uid
    ordenados, y así lo protegen las reglas). Se abren desde Afinidad o con "Enviar mensaje" en el
    perfil de otro usuario.
  - Todo va por capas: `ComunidadRemoteDataSource` (Firestore, y en Retrofit las listas vacías),
    `ComunidadRepository`, `ComunidadViewModel`, `ForoViewModel` y `ChatViewModel`.
- **El detalle y los datos curiosos toman el color de la portada** con Palette (`PortadaConColor`),
  pero solo en un degradado decorativo arriba (`DegradadoDePortada`): los textos y el resto del fondo
  no cambian. En el detalle, Datos curiosos va destacado justo debajo de Calificar; después, Escuchar
  y ver en una fila (el play, YouTube y Spotify), Descubre más y Reseñas de la comunidad.
- **Los datos curiosos van en su propia colección** (`historias`), con el mismo id del item: el
  inicio escucha todo el catálogo y así no descarga ese texto. La pantalla de datos curiosos los lee
  una sola vez, porque no cambian mientras se usa la app.
- **Las reseñas, los perfiles y la discusión están en tiempo real.** Cada data source escucha
  con `callbackFlow` y `addSnapshotListener`, el repositorio mapea el flujo y el ViewModel lo
  recolecta una sola vez con `catch` y `collect`. Están en vivo el inicio, Buscar, el detalle, la
  discusión, Siguiendo con Sonando ahora, mi perfil con sus pestañas, el perfil de otro usuario, Mis
  reseñas, las listas de seguidores, las notificaciones, los duelos, el mapa y las mixtapes. Los
  formularios (escribir reseña, editar perfil), los datos curiosos y el catálogo de Elegir qué
  calificar, de los duelos y de Tu año leen una sola vez, porque no cambian mientras se usa la app.
  Si un listener falla después de cargar, el error sale como aviso y lo que ya llegó se sigue
  viendo; cada lista tiene su propia carga y su propio error, para que una no tape a las demás.
- **El mapa usa OpenStreetMap con osmdroid**, sin servicios de Google: los mapas son libres y la
  ubicación sale del `LocationManager` de Android. Va por capas, como el resto de los datos:
  `UbicacionAndroidDataSourceImpl` (detrás del contrato `UbicacionDataSource`, en `impl/android`, con
  el `LocationManager` que entrega `UbicacionModule`) retorna la ubicación o `null`, y
  `UbicacionRepository` decide el error (sin permiso o sin ubicación). El permiso se pide al abrir el
  mapa y al escribir una reseña (`PedirPermisoDeUbicacion`, con `rememberLauncherForActivityResult`);
  sin permiso la reseña se publica sin ubicación y el mapa se centra en Bogotá. `ReviewRepository`
  pone `latitud` y `longitud` al crear la reseña, y no cambian al editarla. El data source escucha
  solo las reseñas con `createdAt` desde hace 24 horas (texto ISO, así que un filtro de rango sobre
  un campo no pide índice), y el repositorio deja las que tienen ubicación y vuelve a calcular las
  24 horas con cada cambio, así una reseña vieja sale del mapa sin salir de la pantalla. El mapa es una vista de Android: `AndroidView` la mete
  en Compose y cada marcador avisa al ViewModel cuál se tocó; la tarjeta es la misma `ReviewCard`
  de las demás pantallas.
- **Los items de una mixtape van en su subcolección** (`mixtapes/<id>/items`, con el id del item
  como id, así no se repite), como los likes de una reseña. El documento guarda solo lo que pinta la
  tarjeta del perfil: cuántos items tiene y las portadas de los primeros cuatro. Agregar y quitar van
  en una transacción que escribe el item y actualiza el contador y las portadas, así dos celulares no
  se pisan, y borrar la mixtape borra sus items en un batch. La copia del creador (`usuario`) la
  actualiza la Cloud Function cuando cambia su nombre o su foto. Crear una mixtape y elegir en cuál va un item son pantallas propias (`crearMixtape` y
  `agregarAMixtape/{tipo}/{itemId}`), con el formulario y la validación en su ViewModel; al
  terminar vuelven atrás con `LaunchedEffect` sobre `navigate`.
- **Tu año en Mixtapp lo calcula el ViewModel** con mis reseñas del año en curso (en vivo), el
  catálogo leído una vez, mis me gusta y mi perfil. El género y el artista favoritos salen de lo que me
  gusta (me gusta y reseñas de 3 estrellas o más), igual que Para ti. Las tarjetas sin datos no
  salen, y cada una aparece con `AnimatedVisibility` (`fadeIn`) al tocar Siguiente.
- **Sonando ahora** es un documento por usuario en `sonando`. El data source escucha solo lo de las
  últimas 12 horas (`actualizadoEn` desde hace 12 horas) y el repositorio junta dos listeners con
  `combine`: lo que suena y a quiénes sigo, y deja lo mío primero y lo de mis seguidos. El 🔥 va en
  una transacción, como un like (la reacción y el contador). Poner otra cosa a sonar o quitarla va
  en un batch que también borra las reacciones anteriores. La Cloud Function
  `sendSonandoReactionNotification` avisa con una notificación y un push.
- **Los duelos no necesitan un servidor que los cree.** El id es el número del día en Colombia
  (`(milisegundos − 5 h) / 1 día`) y el tipo, así que todos los celulares llegan al mismo documento.
  El par sale del catálogo: cada tipo toma un género que rota con los días (solo los que tienen dos
  items o más) y, dentro del género ordenado por id, dos items distintos que también rotan. El
  primer voto del día crea el documento con la copia de los dos items, y desde ahí el par sale de
  ese documento. El voto va en una transacción: crea el duelo o suma el voto y, si cambié de
  opinión, se lo resta al otro. Los porcentajes y las opiniones llegan por dos listeners
  (`callbackFlow`) que el repositorio junta con `combine`; el ViewModel los escucha una sola vez en
  su `init`, porque los ids de hoy no dependen de la ruta.
- **Las corrutinas de cada ViewModel se lanzan en un dispatcher inyectado** (`@IoDispatcher`, que
  entrega `CoroutineModule`), así las pruebas pueden pasar uno de prueba.
- **El color sale del `MaterialTheme`.** `Color.kt` tiene las cuatro escalas de la paleta (Crimson,
  Mauve, Plum y Gris, del 50 al 600) y `Theme.kt` elige el tono de cada rol: para cambiar el tema
  basta con cambiar un tono en uno de los dos archivos. El modo oscuro usa el rosado chicle solo para
  lo importante (botones, estrellas, filtros elegidos, pestaña activa) sobre un fondo ciruela casi
  negro (`Noche`), con acentos magenta, violeta y coral en los resplandores del fondo, y el modo
  claro, los fondos pastel. Solo la barra de abajo es sólida, en el tono más oscuro
  (`surfaceContainerLowest`); la de arriba (`BarraSuperior`) se funde con el fondo. Las tarjetas que
  informan llevan `texturaDeTarjeta`: sombra suave, degradado, brillo arriba y grano. No hay colores quemados fuera de
  `Color.kt`, salvo el fondo de la ventana en `themes.xml` y los drawables XML (icono y marcadores de
  imagen), que se pintan sin Compose y repiten a mano los colores de la paleta.
- **Un fondo por sección** (`AppBackground` con `TonoDeFondo`): el color de fondo con dos
  resplandores difuminados en las esquinas, las burbujas de la marca (círculos difuminados con un
  borde tenue, en otro lugar en cada sección) y un grano fino. Cada sección tiene su tono (Inicio,
  Buscar, Duelos, Perfil, lo social, el item, Tu año y la cuenta) y los resplandores se desvanecen
  antes del centro, así no pasan detrás del contenido.
- **Un solo campo de texto** (`AppTextField`): un `OutlinedTextField` ovalado de 56 dp con el fondo
  translúcido, la etiqueta siempre arriba, el borde visible sin foco, rosado con foco y rojo con
  error. En el registro y el login el ViewModel dice qué campo falló (`campoConError` con
  `CampoDeLaCuenta`) y ese campo se marca. Lo usan el correo, Buscar, la fecha de
  escucha y los comentarios; la contraseña tiene su propio campo con el ojo (`AppPasswordField`).
- **Una sola tipografía de Google Fonts, Outfit,** del normal al Black. La jerarquía sale del tamaño
  y del grosor de cada estilo de `Type.kt`: display para las cifras grandes, headline para los
  títulos en negrita, title para secciones y tarjetas, body para leer y label para botones y
  metadatos. El certificado está en `res/values/font_certs.xml`. Sin internet, Android usa la fuente por defecto.
- **Hilt se compila con KSP.** El proyecto está en AGP 9.0.0, que trae Kotlin integrado (por eso no
  se aplica `kotlin.android`) y deja `kapt` solo como transición. El plugin de KSP ya estaba en el
  build de clase. `firebase-storage` reemplaza a `firebase-storage-ktx`: desde el BoM 32.5 son el
  mismo módulo.
- **Los errores de Firestore los traduce el repositorio**, igual que el `HttpException` del
  backend: sin conexión, documento que no existe o error del servidor.
- **La app sigue el modo del celular.** `MixtappTheme` elige el esquema claro u oscuro con
  `isSystemInDarkTheme()` y el dynamic color queda apagado.
