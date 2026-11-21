[← Documentación](../README.md)

# Sprint 6: autenticación con Firebase

*6 al 9 de septiembre de 2026*

## Qué pedía

Registro e inicio de sesión reales, con la capa de datos separada de la interfaz.

## Qué se hizo

- Capa de datos nueva (data source, repositorio e inyección) con Hilt en todos los ViewModels.
- Registro e inicio de sesión con Firebase Authentication; el splash decide si hay una sesión abierta.
- El `try/catch` de la autenticación vive en el repositorio y los botones muestran la carga mientras Firebase responde.
- Los textos pasan a `strings.xml`, la navegación sale de la composición a `LaunchedEffect` y los componentes repetidos se unifican.
- Correcciones de validación de formularios y de la zona táctil de los campos de texto.

## Cómo se prueba

Crear una cuenta, cerrar sesión e iniciar sesión otra vez; con la sesión abierta la app entra directo al inicio.

## Pull requests

[#12](https://github.com/AndresContreras1/Mixtapp/pull/12) · [#13](https://github.com/AndresContreras1/Mixtapp/pull/13) · [#14](https://github.com/AndresContreras1/Mixtapp/pull/14) · [#16](https://github.com/AndresContreras1/Mixtapp/pull/16) · [#17](https://github.com/AndresContreras1/Mixtapp/pull/17) · [#18](https://github.com/AndresContreras1/Mixtapp/pull/18) · [#19](https://github.com/AndresContreras1/Mixtapp/pull/19) · [#20](https://github.com/AndresContreras1/Mixtapp/pull/20) · [#21](https://github.com/AndresContreras1/Mixtapp/pull/21)
