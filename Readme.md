# Linkoria Android Frontend

**Trabajo de Fin de Grado (DAM)**

**Autor:** Xinlei

## Descripción

Linkoria es una aplicación de chat en tiempo real inspirada en Discord. Permite a los usuarios registrarse, iniciar sesión, gestionar amistades, participar en conversaciones privadas, unirse a servidores y comunicarse mediante canales de texto.

Este repositorio contiene el frontend Android desarrollado en Kotlin. La aplicación se comunica con un backend Spring Boot mediante REST API y WebSocket.

---

## Tecnologías utilizadas

* Kotlin
* Android SDK
* MVVM
* Clean Architecture
* Hilt
* Retrofit
* OkHttp
* Gson
* DataStore
* Coroutines & Flow
* ViewModel & StateFlow
* ViewBinding
* RecyclerView
* Glide
* STOMP + WebSocket
* Supabase Storage

---

## Arquitectura

El proyecto sigue una arquitectura basada en MVVM y Clean Architecture.

```text
UI
↓
ViewModel
↓
UseCase
↓
Repository
↓
ApiService
↓
Backend
```

### Responsabilidades

* **UI:** muestra información al usuario.
* **ViewModel:** gestiona el estado de la pantalla.
* **UseCase:** representa acciones de negocio.
* **Repository:** obtiene y gestiona los datos.
* **ApiService:** define las llamadas HTTP.
* **Backend:** procesa las peticiones y devuelve respuestas.

---

## Funcionalidades principales

### Autenticación

* Registro
* Inicio de sesión
* JWT Authentication
* Refresh Token
* Persistencia de sesión mediante DataStore

### Amistades

* Enviar solicitudes
* Aceptar o rechazar solicitudes
* Eliminar amistades
* Consultar lista de amigos

### Servidores y canales

* Crear servidores
* Obtener servidores del usuario
* Gestionar canales
* Navegar entre canales

### Conversaciones privadas

* Abrir conversaciones directas
* Consultar historial de mensajes

### Mensajería en tiempo real

* Envío de mensajes mediante WebSocket
* Recepción instantánea de mensajes
* Indicadores de escritura

---

## Estructura del proyecto

```text
auth/
user/
friendship/
server/
channel/
conversation/
message/
typing/
websocket/
core/
di/
root/
```

Cada módulo encapsula una funcionalidad concreta para facilitar el mantenimiento y la escalabilidad del proyecto.

---

## Ejecución

```bash
git clone https://github.com/Jiajiaxihuanwo/android-frontend-linkoria.git
cd android-frontend-linkoria
```

1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. Configurar la URL del backend.
4. Ejecutar la aplicación en un dispositivo o emulador Android.

---

## Estado actual

* ✅ Autenticación JWT
* ✅ Persistencia de sesión
* ✅ Gestión de usuarios
* ✅ Sistema de amistades
* ✅ Conversaciones privadas
* ✅ Servidores y canales
* ✅ Mensajería en tiempo real
* ✅ Arquitectura MVVM + Clean Architecture
* ✅ Integración REST API y WebSocket

---

## Resumen

Linkoria Android está construido siguiendo principios de separación de responsabilidades y arquitectura limpia. La aplicación utiliza tecnologías modernas del ecosistema Android para proporcionar una experiencia de mensajería en tiempo real escalable y mantenible.
