[← Documentación](../README.md)

# Sprint 12: emulador, datos de prueba e integración

*1 de noviembre de 2026*

## Qué pedía

Probar la app sin tocar la base real.

## Qué se hizo

- En debug la app usa el emulador de Firebase; en release, el Firebase real.
- Script de datos (seed) con el catálogo y usuarios falsos de Faker.
- Pruebas de integración de los data sources y los repositorios contra el emulador.
- Comentarios de la discusión en Firestore y el dispatcher inyectado para poder probar los ViewModels.
- Las observaciones del profesor: seguir funciona aunque la cuenta no tenga documento y la discusión se abre desde todas las reseñas.

## Cómo se prueba

Prender el emulador, cargar el seed y correr las pruebas de `androidTest` por grupos.

## Pull requests

[#43](https://github.com/AndresContreras1/Mixtapp/pull/43) · [#44](https://github.com/AndresContreras1/Mixtapp/pull/44)
