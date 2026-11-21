[← Documentación](../README.md)

# Sprint 10: Firestore como base de datos

*13 de octubre de 2026*

## Qué pedía

Pasar los datos a Cloud Firestore sin perder la implementación de Retrofit.

## Qué se hizo

- Álbumes, reseñas y perfiles pasan a Cloud Firestore.
- La implementación de Retrofit se conserva con el mismo contrato: se cambia de base con una línea por contrato en el módulo de inyección.
- Al registrarse, el usuario queda guardado en Firestore con su nombre y su usuario.

## Cómo se prueba

Registrarse y ver el documento del usuario en la consola de Firestore; publicar una reseña y verla en la colección.

## Pull requests

[#39](https://github.com/AndresContreras1/Mixtapp/pull/39)
