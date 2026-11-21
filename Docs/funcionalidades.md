[← Documentación](README.md)

# Funcionalidades

Qué hace la app, módulo por módulo, y con qué servicio funciona cada uno.

| Módulo | Estado |
|---|---|
| Registro, inicio de sesión y sesión persistente con Firebase Auth | Funcionando |
| Al registrarse, el nombre, el usuario y el token de notificaciones quedan guardados en Firestore. Si no se pueden guardar, el registro no entra: avisa y pide iniciar sesión, que crea el documento | Funcionando |
| Catálogo real de cuatro géneros (rock, metal, nu metal y reggaetón) (50 álbumes, 126 canciones y 56 artistas), con portadas de 1000 px, fechas de salida, el Instagram de cada artista y su puesto en la lista de Deezer de su género | Funcionando, Firestore (seed en mixtapp-functions) |
| Inicio con **Para ti**, **Descubre**, **Tendencias** y **Seguidos**: la tarjeta En tendencia, filas de canciones, álbumes y artistas y las reseñas de quienes sigo, **en tiempo real** | Funcionando, Firestore |
| **Para ti** y **Descubre** reparten cada fila entre los géneros que me gustan (mis me gusta y mis reseñas de 3 estrellas o más), en proporción a cuánto pesa cada uno: si casi todo es metal, casi no sale reggaetón. El álbum En tendencia sale al azar de las listas de Deezer de mis géneros y cambia cada vez que vuelvo al inicio | Funcionando |
| **Tendencias** es el Top Colombia de Deezer para todos, sin repartir por mis géneros, con el #1 de Colombia en la tarjeta grande | Funcionando |
| **Descubre**: lo de mis géneros que todavía no tiene mi me gusta ni mi reseña, primero mi subgénero y lo más nuevo | Funcionando |
| Buscar: catálogo y personas por texto, tarjetas por fecha de lanzamiento, más populares, mejor calificados y por cada uno de los cuatro géneros | Funcionando, Firestore |
| Detalle de un álbum, una canción o un artista: promedio, total de calificaciones, % que lo recomienda, me gusta, guardar y sus reseñas, **en tiempo real** | Funcionando, Firestore |
| Video original en YouTube y Spotify de cada canción, Spotify de álbumes y artistas, e Instagram del artista | Funcionando (abren afuera de la app) |
| Datos curiosos de cada álbum, canción y artista por secciones (historia, cómo se hizo, inspiración, conciertos icónicos), en su propia pantalla | Funcionando, Firestore |
| Escribir, modificar y eliminar mi reseña desde el detalle, mi perfil y Mis reseñas | Funcionando, Firestore |
| Me gusta a un álbum, una canción o un artista: queda en mis Favoritos | Funcionando, Firestore |
| Guardar un álbum, una canción o un artista: queda en la pestaña Guardados de mi perfil | Funcionando, Firestore |
| Mi perfil: foto, nombre, usuario, seguidores y seguidos (abren su lista), biografía e Instagram; pestañas Perfil (Tu año, favoritos, actividad reciente y cerrar sesión), Guardados, Mixtapes y Cambiar foto | Funcionando, Firestore |
| Perfil de cualquier otro usuario con el mismo encabezado, sus favoritos y sus reseñas | Funcionando, Firestore |
| Dar y quitar like a una reseña y a un comentario, con el total de likes | Funcionando, Firestore |
| Seguir y dejar de seguir, con el total de seguidores y seguidos | Funcionando, Firestore |
| Discusión de una reseña: comentarios, respuestas y likes a cada comentario, **en tiempo real**. Se abre desde cualquier tarjeta de reseña, aunque no sigas a quien la escribió | Funcionando, Firestore |
| Notificaciones en tres pestañas, **en tiempo real**: Mías (me gusta a mis reseñas y a mis comentarios, comentarios, respuestas y seguidores nuevos, marcadas como leídas), Discusiones (reseñas nuevas de quienes sigo) y Lanzamientos (lo más nuevo de los artistas que me gustan) | Funcionando, Firestore y Cloud Functions |
| **Duelos del día** (pestaña Duelos de la barra): cada día un duelo de álbumes, uno de canciones y uno de artistas, dos del mismo género; se vota por el mejor del día con una opinión opcional y los porcentajes y las opiniones cambian **en tiempo real** | Funcionando, Firestore |
| **Mapa** (pestaña Mapa de la barra): un mapa de OpenStreetMap con un marcador donde se publicó cada reseña de las **últimas 24 horas**; al tocar un marcador sale la tarjeta de la reseña (autor, item, calificación, texto, me gusta y discusión), **en tiempo real**. La reseña nueva guarda la ubicación del celular si se da el permiso | Funcionando, Firestore y OpenStreetMap |
| **Mixtapes**: listas propias de álbumes, canciones y artistas. Se crean desde la pestaña Mixtapes del perfil, se llenan con el botón de lista del detalle y en su detalle se quitan items o se borra la mixtape, **en tiempo real** | Funcionando, Firestore |
| **Tu año en Mixtapp** (desde el perfil): tarjetas que se descubren una por una con mis reseñas y mi promedio, mi género y mi artista, lo que mejor califiqué, mi reseña con más me gusta y mis números en la comunidad | Funcionando, Firestore |
| **Sonando ahora**: desde el detalle pongo lo que estoy escuchando; en Siguiendo aparece en vivo lo que escuchan quienes sigo (de las últimas 12 horas) y les puedo dar 🔥, que les llega como notificación | Funcionando, Firestore y Cloud Functions |
| Notificación push de las mismas acciones | FCM y Cloud Functions, con el Firebase real (en modo debug las funciones no corren) |
| **Fragmento de 30 segundos** de cada canción dentro de la app, con la API pública de Deezer: play en el inicio, el detalle y las playlists, con ecualizador, anillo de tiempo y vinilo que gira | Funcionando, Deezer y MediaPlayer |
| **Comunidad** (en lugar de Siguiendo): Siguiendo, **Afinidad** (personas con gustos parecidos y su porcentaje), **Foros** de cada álbum, canción y artista, y **Mensajes** directos, en tiempo real | Funcionando, Firestore |
| **Playlists hechas para ti** en el inicio: una mezcla por cada género que me gusta, Descubre, Top Colombia y Lo más escuchado, con Reproducir todo | Funcionando |
| Color de la portada (Palette) en el degradado del detalle y en los datos curiosos | Funcionando |
| Animaciones: al tocar, entre pantallas, en la carga, confeti al votar y al terminar Tu año, video de fondo en el login y la animación del género en el detalle de cada artista | Funcionando |
| Foto de perfil subida a Firebase Storage | Funcionando |
| Consumo del backend de Express con Retrofit | Implementado, intercambiable con Firestore |
| Modo claro y modo oscuro: la app sigue el del celular | Funcionando |
| Modo debug contra el emulador de Firebase (Firebase CLI) | Funcionando |
| Catálogo y datos falsos con Faker para el emulador o el Firebase real | Script en [mixtapp-functions](https://github.com/AndresContreras1/mixtapp-functions) |
| Pruebas de integración de los data sources y los repositorios contra el emulador | 48 + 17 pruebas |
| Pruebas con mocks (MockK) de los repositorios y de los ViewModels | 59 + 100 pruebas, más 42 de mapeos, reglas y datos locales |
| Pruebas de integración del ViewModel de registro contra el emulador | 2 pruebas |
| Pruebas end to end de los dos casos de uso del sprint 13 | 2 pruebas |
