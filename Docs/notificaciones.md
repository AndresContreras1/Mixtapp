[← Documentación](README.md)

# Notificaciones

Qué dispara cada notificación y qué Cloud Function la manda.

La app usa **Firebase Cloud Messaging**. `MyFirebaseMessagingService` está en
`data/datasource/services` y declarado en el manifiesto con el permiso `POST_NOTIFICATIONS`. En
Android 13 o superior, `Mixtapp.kt` pide el permiso al abrir la app (solo si todavía no está
concedido). El token del celular se guarda en `usuarios/<uid>.fcmtoken` al registrarse y al iniciar
sesión.

Las crean cinco Cloud Functions de
[mixtapp-functions](https://github.com/AndresContreras1/mixtapp-functions):
`sendLikeNotification` y `sendCommentLikeNotification` («Nuevo me gusta» a mi reseña o a mi
comentario), `sendFollowNotification` («Nuevo seguidor»), `sendCommentNotification` («Nuevo
comentario» al autor de la reseña y «Nueva respuesta» al autor del comentario que se responde) y
`sendSonandoReactionNotification` (el 🔥 a lo que estoy escuchando).
Cada una guarda la notificación en `usuarios/<uid>/notificaciones` y, si el usuario tiene token,
envía el push. Para que el push llegue en las pruebas, la app debe estar en segundo plano o cerrada.

La pantalla de notificaciones tiene tres pestañas, todas en vivo:

- **Mías:** escucha esa subcolección, la agrupa en Hoy y Antes, y al tocar una la marca leída y abre
  la discusión de la reseña o, si es un seguidor nuevo o un 🔥, el perfil de quien la hizo. A dónde
  lleva lo decide `AppNavigation`.
- **Discusiones:** las reseñas nuevas de quienes sigo, del mismo flujo de Siguiendo. Cada una abre
  su discusión.
- **Lanzamientos:** los álbumes y canciones de los artistas que están en mis me gusta o mis
  guardados, del más nuevo al más viejo. El repositorio lee el catálogo una vez y lo combina con mis
  dos listas en vivo: no hace falta otra Cloud Function.
