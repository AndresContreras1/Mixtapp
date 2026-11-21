[← Documentación](../README.md)

# Sprints 7 y 8: errores con Result, imágenes y auditorías

*12 de septiembre al 9 de octubre de 2026*

## Qué pedía

Manejo de errores por capas con mensajes según el tipo de error, imágenes en línea y foto de perfil.

## Qué se hizo

- El repositorio devuelve `Result` y el ViewModel toma el camino de éxito o de error, con un mensaje propio para cada acción.
- Excepciones propias por tipo de error (por ejemplo, el correo no registrado tiene su propio mensaje).
- Imágenes por URL con Coil, resolviendo carga y error dentro del componente.
- Foto de perfil subida a Firebase Storage, de abajo hacia arriba por capas.
- Paleta de Material 3: los colores salen del tema y no de cada pantalla.
- Tres auditorías contra el material de clase: las entidades pasan a `data/model`, una sola entidad por concepto y la capa de datos deja de depender de la interfaz.

## Cómo se prueba

Iniciar sesión con un correo que no existe, con una contraseña errada y sin internet: cada caso muestra su mensaje.

## Pull requests

[#22](https://github.com/AndresContreras1/Mixtapp/pull/22) · [#23](https://github.com/AndresContreras1/Mixtapp/pull/23) · [#24](https://github.com/AndresContreras1/Mixtapp/pull/24) · [#25](https://github.com/AndresContreras1/Mixtapp/pull/25) · [#26](https://github.com/AndresContreras1/Mixtapp/pull/26) · [#27](https://github.com/AndresContreras1/Mixtapp/pull/27) · [#28](https://github.com/AndresContreras1/Mixtapp/pull/28) · [#29](https://github.com/AndresContreras1/Mixtapp/pull/29) · [#30](https://github.com/AndresContreras1/Mixtapp/pull/30) · [#31](https://github.com/AndresContreras1/Mixtapp/pull/31) · [#32](https://github.com/AndresContreras1/Mixtapp/pull/32) · [#34](https://github.com/AndresContreras1/Mixtapp/pull/34) · [#35](https://github.com/AndresContreras1/Mixtapp/pull/35)
