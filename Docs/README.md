![Logo de Mixtapp](https://raw.githubusercontent.com/AndresContreras1/Mixtapp/master/Docs/logo.jpg)

# Mixtapp

Mixtapp es una aplicación móvil para Android enfocada en la exploración y seguimiento de música. La aplicación busca ofrecer un espacio donde los usuarios puedan descubrir álbumes, calificarlos, escribir reseñas, guardar favoritos y visualizar la actividad de otros usuarios.

---

## Características

Entre las funcionalidades y pantallas actualmente desarrolladas se encuentran:

* **Inicio de sesión**

  * Campo de correo electrónico.
  * Campo de contraseña.
  * Botón de ingreso.
  * Acceso al registro de nuevos usuarios.

* **Registro de usuarios**

  * Creación de nombre de usuario.
  * Registro mediante correo electrónico.
  * Creación y confirmación de contraseña.
  * Aceptación de términos y condiciones.
  * Diseño visual personalizado.

* **Página principal**

  * Sección de álbumes populares.
  * Álbum actualmente en tendencia.
  * Actividad de amigos.
  * Filtros de contenido:

    * For you
    * Trending
    * Friends
  * Barra de navegación inferior.

* **Perfil**

  * Información básica del usuario.
  * Cantidad de reseñas, álbumes y listas.
  * Sección de álbumes favoritos.
  * Actividad reciente.
  * Calificaciones.

---

## Tecnologías utilizadas

El proyecto está desarrollado utilizando:

* **Kotlin**
* **Android**
* **Jetpack Compose**
* **Material 3**
* **Gradle Kotlin DSL**

---

## Estructura del proyecto

```text
Mixtapp/
├── app/
│   ├── src/
│   │   ├── androidTest/
│   │   ├── main/
│   │   │   ├── java/com/example/mixtapp/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   ├── LoginScreen.kt
│   │   │   │   ├── ProfileScreen.kt
│   │   │   │   ├── SignUp.kt
│   │   │   │   └── ui/
│   │   │   │       └── theme/
│   │   │   │           ├── Color.kt
│   │   │   │           ├── Theme.kt
│   │   │   │           └── Type.kt
│   │   │   └── res/
│   │   │       ├── drawable/
│   │   │       ├── mipmap/
│   │   │       ├── values/
│   │   │       └── xml/
│   │   └── test/
│   │
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
│
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle.kts
```

---

## Instalación y ejecución

### Requisitos

Para ejecutar el proyecto necesitas:

* Android Studio.
* JDK 11 o compatible.
* Android SDK.
* Un dispositivo Android físico o un emulador.

### 1. Clonar el repositorio

```bash
git clone https://github.com/USUARIO/Mixtapp.git
cd Mixtapp
```

Reemplaza `USUARIO/Mixtapp` por la URL de tu repositorio de GitHub.

### 2. Abrir el proyecto

Abre la carpeta del proyecto desde **Android Studio** y espera a que Gradle sincronice las dependencias.

### 3. Ejecutar la aplicación

Selecciona un dispositivo físico o un emulador y presiona:

**Run ▶**

También puedes ejecutar la compilación desde la terminal:

```bash
./gradlew assembleDebug
```

En Windows:

```bash
gradlew.bat assembleDebug
```

---

## Diseño

Mixtapp utiliza una identidad visual basada principalmente en tonos oscuros, rosados, vino y púrpura.

La interfaz está construida completamente con Jetpack Compose, utilizando componentes reutilizables y un tema personalizado.

Algunos de los elementos visuales principales son:

* Fondo oscuro.
* Tarjetas para contenido musical.
* Colores de acento rosa y vino.
* Bordes redondeados.
* Navegación inferior.
* Componentes adaptados para Material 3.

---

## Fncionalidades

Como parte del desarrollo futuro de MixtApp se contempla implementar:

* [ ] Autenticación real de usuarios.
* [ ] Registro y almacenamiento de usuarios.
* [ ] Conexión con una base de datos.
* [ ] Persistencia de álbumes, reseñas y calificaciones.
* [ ] Sistema de búsqueda de álbumes y artistas.
* [ ] Información real de álbumes.
* [ ] Sistema de calificación de álbumes.
* [ ] Creación de listas personalizadas.
* [ ] Gestión de álbumes favoritos.
* [ ] Reseñas de usuarios.
* [ ] Sistema de amigos y actividad social.
* [ ] Navegación completa entre las diferentes pantallas.
* [ ] Integración con una API de música.

---

## 📌 Estado del proyecto

**🟡 En desarrollo**

La versión actual corresponde principalmente a un prototipo funcional de interfaz. Las pantallas principales y componentes visuales están siendo desarrollados antes de integrar la lógica de negocio, persistencia de datos y servicios externos.

---


La licencia del proyecto puede modificarse posteriormente según las necesidades del equipo.
