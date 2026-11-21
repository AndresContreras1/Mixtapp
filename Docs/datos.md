[← Documentación](README.md)

# Datos en Firestore

Las colecciones de Cloud Firestore y cómo se guardan los likes, los seguidores y la discusión.

| Colección | Documento | Campos |
|---|---|---|
| `usuarios` | el uid de Authentication | `username`, `nombre` (texto) · `bio`, `fotoUrl`, `instagram` (texto o null; el Instagram va sin la @) · `followersCount`, `followingCount` (número) · `fcmtoken` (texto) |
| `usuarios/<uid>/followers` | el uid de quien me sigue | `timestamp` |
| `usuarios/<uid>/following` | el uid de quien sigo | `timestamp` |
| `usuarios/<uid>/likes` | el id del item | `tipo`, `titulo`, `subtitulo`, `imagenUrl`, `genero`, `createdAt` (texto): la copia del item que me gusta |
| `usuarios/<uid>/guardados` | el id del item | los mismos campos: la copia del item que guardé |
| `usuarios/<uid>/notificaciones` | id aleatorio | `tipo` (`like`, `like_comentario`, `comentario`, `respuesta`, `seguidor` o `reaccion_sonando`) · `actorId`, `actorNombre`, `actorFotoUrl` · `reviewId`, `itemTitulo`, `imagenUrl` (texto o null) · `leida` (booleano) · `createdAt` (texto ISO). Las escriben las Cloud Functions |
| `albumes` | `album_<nombre>` | `titulo`, `artista`, `portadaUrl`, `genero`, `subgenero`, `fechaLanzamiento` (texto, `AAAA-MM-DD`) · `anio`, `likesCount` (número) · `tendencia` (número o null: el puesto en la lista de Deezer de su género) · `tendenciaColombia` (número o null: el puesto en el Top Colombia de Deezer) |
| `canciones` | `cancion_<nombre>` | los del álbum más `albumId` y `album` (el nombre del álbum) |
| `artistas` | `artista_<nombre>` | `nombre`, `fotoUrl`, `genero`, `subgenero` (texto) · `grupo`, `instagram` (texto o null) · `likesCount` (número) · `tendencia`, `tendenciaColombia` (número o null) |
| `historias` | el id del item | `tipo` (texto) · `secciones` (arreglo de mapas con `titulo` y `texto`): los datos curiosos. Van aparte del item para que el inicio no descargue ese texto |
| `duelos` | `dia_<número del día>_<tipo>` | `dia` (número) · `tipo`, `genero` (texto) · `itemA`, `itemB` (mapas con `id`, `titulo`, `subtitulo` e `imagenUrl`: la copia de los dos items) · `votosA`, `votosB` (número) · `createdAt` (texto ISO). Lo crea el primer voto del día |
| `mixtapes` | id aleatorio | `nombre` (texto, hasta 40) · `descripcion` (texto o null, hasta 150) · `usuarioId` · `usuario` (mapa con `nombre` y `fotoUrl`: la copia de quién la creó) · `itemsCount` (número) · `portadas` (arreglo con las portadas de los primeros 4 items, para la tarjeta) · `createdAt` (texto ISO) |
| `mixtapes/<id>/items` | el id del item | `tipo`, `titulo`, `subtitulo`, `imagenUrl` (la copia del item) · `agregadoEn` (texto ISO) |
| `sonando` | el uid de quien escucha | `itemId`, `tipo`, `titulo`, `subtitulo`, `imagenUrl` (la copia del item) · `usuario` (mapa con `nombre` y `fotoUrl`) · `reaccionesCount` (número) · `actualizadoEn` (texto ISO). Uno por usuario: poner otro lo reemplaza |
| `sonando/<uid>/reacciones` | `<itemId>_<uid de quien reaccionó>` | `usuarioId`, `itemId`, `createdAt`. El item va en el id para que una reacción a lo que sonaba antes no cuente para lo que suena ahora |
| `duelos/<id>/votos` | el uid de quien votó | `opcion` (`a` o `b`) · `opinion` (texto, hasta 140) · `usuario` (mapa con `nombre` y `fotoUrl`) · `createdAt` (texto ISO). Cada usuario tiene un solo voto por duelo |
| `albumes/<id>/likes`, `canciones/<id>/likes`, `artistas/<id>/likes` | el uid de quien dio like | `timestamp` |
| `reviews` | id aleatorio | `usuarioId`, `itemId`, `tipo` (texto) · `calificacion` (número) · `comentario` (texto) · `fechaEscucha` (texto `AAAA-MM-DD` o null) · `createdAt`, `updatedAt` (texto ISO) · `likesCount`, `commentsCount` (número) · `usuario` y `item` (mapas) · `latitud`, `longitud` (número o null: dónde se publicó, para el mapa) |
| `reviews/<id>/likes` | el uid de quien dio like | `timestamp` |
| `reviews/<id>/comments` | id aleatorio | `usuarioId`, `contenido` (texto) · `parentCommentId` (texto o null) · `likesCount` (número) · `createdAt` (texto ISO) · `usuario` (mapa) |
| `reviews/<id>/comments/<id>/likes` | el uid de quien dio like | `timestamp` |

Los tipos tienen que coincidir con los DTO: `toObject` falla si `anio` o `calificacion` no son
número, y `whereEqualTo` no encuentra reseñas si `itemId` o `usuarioId` se guardan como número. El id
de cada item lleva su tipo adelante (`album_`, `cancion_`, `artista_`), así que una reseña se busca
solo con `itemId` y no se mezclan las de un álbum con las de una canción.

- El correo y la contraseña se quedan en Authentication; en Firestore no se guardan.
- Firestore no tiene joins, así que cada reseña guarda una copia de lo que se muestra con ella:
  `usuario` (`nombre`, `fotoUrl`) e `item` (`titulo`, `subtitulo`, `imagenUrl`). Los likes y los
  guardados de un usuario también guardan la copia del item, para pintar Favoritos y Guardados sin
  otra consulta.
- Si alguien cambia su nombre o su foto, la Cloud Function `updateUserInfoInReviews` actualiza esa
  copia en sus reseñas, sus comentarios, lo que está escuchando, sus mixtapes y sus votos en los
  duelos. Así las copias no quedan viejas.

## Likes y seguidores

Los likes y los seguidores son relaciones muchos a muchos, así que van en **subcolecciones** y no
como listas de ids dentro del documento: una lista crecería sin límite y cada consulta traería ids
que la tarjeta no usa. La tarjeta solo necesita el número, que vive en el documento principal
(`likesCount`, `followersCount`, `followingCount`).

- **Dar o quitar like** es una sola función: dentro de una **transacción** lee
  `reviews/<id>/likes/<mi uid>`. Si existe, lo borra y resta 1 a `likesCount`; si no, lo crea con
  `FieldValue.serverTimestamp()` y suma 1. Si una de las dos escrituras falla, Firebase deshace la
  otra.
- **Me gusta a un álbum, una canción o un artista** son tres escrituras en una transacción: el
  like en el item, la copia del item en `usuarios/<uid>/likes` y su `likesCount`. Mis likes son mis
  **Favoritos** y, con sus géneros, el inicio **Para ti**.
- **Guardar** crea o borra `usuarios/<uid>/guardados/<id del item>` en una transacción, para que dos
  toques seguidos no lo guarden dos veces. El detalle escucha ese documento y el marcador cambia solo.
- **Seguir** son cuatro escrituras en la misma transacción: `following/<B>` en A,
  `followers/<A>` en B, `followingCount` de A y `followersCount` de B.
- **Si ya di like o ya lo sigo** sale de buscar mi uid en la subcolección con `exists()`. El data
  source recibe `currentUserId` como parámetro opcional, para que Retrofit no lo tenga que usar.
- **La UI no copia el estado a mano.** La transacción cambia el contador del documento, eso
  dispara el listener de la pantalla y llegan el corazón o el botón y el contador ya confirmados,
  en este celular y en los demás.
- **Si la cuenta no tiene documento** en `usuarios` (el registro entró aunque falló Firestore, o
  el emulador se reinició y la sesión siguió en el celular), el repositorio lo crea antes de seguir
  y al iniciar sesión, con la parte del correo antes de la arroba como usuario.
- **La lista de seguidores o de seguidos** escucha la subcolección y trae el documento de cada
  usuario a la vez. Se abre al tocar el contador en cualquier perfil.

## Comentarios de la discusión

Cada reseña tiene su discusión. Los comentarios van en la subcolección `reviews/<id>/comments` y
la reseña guarda el total en `commentsCount`, igual que los likes.

- **Comentar** es una transacción: crea el comentario y suma 1 a `commentsCount`. El comentario
  guarda una copia de mi nombre y mi foto, como la reseña.
- **Responder** guarda el id del comentario principal en `parentCommentId`. El repositorio arma el
  hilo: cada comentario principal va seguido de sus respuestas.
- **Dar like a un comentario** es la misma transacción que el like de la reseña, en
  `comments/<id>/likes/<mi uid>` con su `likesCount`.
- **En tiempo real:** la discusión escucha la reseña y sus comentarios con dos `callbackFlow`, y
  `combine` la arma cada vez que llega cualquiera de los dos. Es como un chat: un comentario desde
  otro celular aparece sin recargar.
- **Borrar la reseña** borra a mano sus likes y sus comentarios (con los likes de cada uno), porque
  Firestore no borra las subcolecciones.

Para comprobarlo con dos cuentas:

1. Dar like a una reseña: en la consola aparece `reviews/<id>/likes/<uid>` y `likesCount` sube.
   Quitarlo borra el documento y el contador baja.
2. Seguir un perfil: aparecen `following` en mi usuario y `followers` en el otro, con los dos
   contadores. Tocar el contador abre la lista.
3. Con el filtro **Seguidos** del inicio, escribir una reseña desde la otra cuenta: aparece sola,
   sin recargar.
4. Abrir la misma discusión en los dos celulares y comentar desde uno: el comentario y el total
   aparecen en el otro. Responder un comentario lo deja debajo del que responde.
