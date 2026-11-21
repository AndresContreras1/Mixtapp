[← Documentación](../README.md)

# Sprint 11: likes, seguidores, tiempo real y notificaciones

*23 de octubre de 2026*

## Qué pedía

Interacción social en vivo: me gusta, seguir y notificaciones.

## Qué se hizo

- Dar y quitar me gusta con subcolecciones y contadores, en transacciones para que el número no se descuadre.
- Seguir y dejar de seguir, con las listas de seguidores y seguidos.
- Tiempo real por capas: el listener vive en el data source y la pantalla se actualiza sola, sin recargar.
- Notificaciones con Cloud Functions ([mixtapp-functions](https://github.com/AndresContreras1/mixtapp-functions)) y Firebase Cloud Messaging.

## Cómo se prueba

Con dos cuentas: una da me gusta o sigue a la otra, y en la segunda el contador y la notificación cambian al instante.

## Pull requests

[#40](https://github.com/AndresContreras1/Mixtapp/pull/40) · [#41](https://github.com/AndresContreras1/Mixtapp/pull/41) · [#42](https://github.com/AndresContreras1/Mixtapp/pull/42)
