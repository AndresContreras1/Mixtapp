[← Documentación](../README.md)

# Sprints 3 a 5: interfaz, navegación y MVVM

*14 de agosto al 4 de septiembre de 2026*

## Qué pedía

Construir las pantallas en Jetpack Compose, conectarlas con navegación y pasar el estado a ViewModels.

## Qué se hizo

- Pantallas de login, registro, inicio, búsqueda, detalle, escribir reseña, discusión, seguidos y notificaciones, cada una separada en sus componentes.
- Navegación con un solo `NavHost`, `Scaffold` y barra inferior; los formularios llevan de una pantalla a otra.
- State hoisting: los componentes reciben el estado y avisan los eventos; los composables grandes se partieron.
- MVVM: cada pantalla tiene su `UiState` y su ViewModel con `MutableStateFlow`, y por la ruta viaja solo el id del contenido.
- Cada pantalla se divide en la versión con ViewModel y la versión sin estado, para que los previews funcionen.

## Cómo se prueba

Los previews de cada pantalla en Android Studio y la navegación completa en el emulador.

## Pull requests

[#1](https://github.com/AndresContreras1/Mixtapp/pull/1) · [#2](https://github.com/AndresContreras1/Mixtapp/pull/2) · [#3](https://github.com/AndresContreras1/Mixtapp/pull/3) · [#4](https://github.com/AndresContreras1/Mixtapp/pull/4) · [#5](https://github.com/AndresContreras1/Mixtapp/pull/5) · [#6](https://github.com/AndresContreras1/Mixtapp/pull/6) · [#7](https://github.com/AndresContreras1/Mixtapp/pull/7) · [#8](https://github.com/AndresContreras1/Mixtapp/pull/8) · [#9](https://github.com/AndresContreras1/Mixtapp/pull/9) · [#10](https://github.com/AndresContreras1/Mixtapp/pull/10) · [#11](https://github.com/AndresContreras1/Mixtapp/pull/11)
