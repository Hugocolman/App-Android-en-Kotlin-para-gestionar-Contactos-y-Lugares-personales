# App Android en Kotlin para gestionar Contactos y Lugares personales

Aplicación Android nativa (Kotlin) que permite:
- Gestionar Contactos: crear, editar, listar y marcar como favoritos.
- Guardar Lugares personales: obtiene la ubicación actual (GPS), permite nombrarla y la lista en la app.
- Navegar entre secciones con un menú inferior (BottomNavigation) y Navigation Component (Safe Args).

## Funcionalidades
- Contactos
  - Lista con pestañas: "Todos" y "Favoritos".
  - Marcar/desmarcar favorito desde la lista con un toque.
  - Agregar/editar contacto desde un formulario simple.
- Lugares
  - Solicita permiso de ubicación fina cuando corresponde.
  - Obtiene latitud/longitud con FusedLocationProviderClient (Play Services).
  - Guarda el lugar con un nombre y lo muestra en una lista.

## Stack y arquitectura
- Lenguaje: Kotlin
- UI/UX: AndroidX, Material Components, ViewBinding, RecyclerView + ListAdapter
- Arquitectura: MVVM ligero con ViewModel + LiveData
- Persistencia: Room (KSP)
- Navegación: Navigation Component + Safe Args
- Ubicación: Google Play Services Location

## Requisitos
- Android Studio reciente (AGP 8.x)
- JDK 17 instalado (Gradle/AGP)
- SDK de Android instalado (compile/targetSdk 36, minSdk 24)

## Configuración rápida
1. SDK de Android
   - Asegúrate de tener el SDK en `C:\\Users\\<tu_usuario>\\AppData\\Local\\Android\\Sdk` (o tu ruta equivalente).
   - Crea `local.properties` en la raíz del proyecto (este archivo NO se versiona):
     ```
     sdk.dir=C:\\Users\\<tu_usuario>\\AppData\\Local\\Android\\Sdk
     ```
   - Alternativamente, define `ANDROID_HOME` o `ANDROID_SDK_ROOT` en tus variables de entorno.

2. Abrir en Android Studio
   - File > Open > selecciona la raíz del proyecto.
   - Espera el Gradle Sync (descarga dependencias la primera vez).

## Compilación
- Android Studio
  - Build > Make Project o botón Run ▶ para instalar en dispositivo/emulador.

- Línea de comandos (Windows)
  - Compilar debug:
    ```
    .\\gradlew.bat :app:assembleDebug
    ```
  - Limpiar:
    ```
    .\\gradlew.bat clean
    ```
  - Instalar directamente en un dispositivo conectado (adb):
    ```
    .\\gradlew.bat :app:installDebug
    ```
  - APK generado (debug): `app/build/outputs/apk/debug/app-debug.apk`

## Ejecutar en dispositivo/emulador
- Emulador
  - Tools > Device Manager > crear AVD > Iniciar.
- Dispositivo físico
  - Activar "Depuración por USB" y conectar por cable.
  - Verificar conexión:
    ```
    adb devices
    ```
  - Instalar APK (si compilaste con assemble):
    ```
    adb install -r app\\build\\outputs\\apk\\debug\\app-debug.apk
    ```

## Permisos
- Ubicación: `ACCESS_FINE_LOCATION` (solicitado en la sección de Lugares cuando se requiere).

## Detalles técnicos
- Base de datos Room (`version = 2`) con entidades:
  - `Contact(id, name, phone, isFavorite)`
  - `Place(id, name, latitude, longitude)`
- DAOs y ViewModels
  - `ContactDao` (+ favoritos), `PlaceDao` (insert/listar)
  - `ContactViewModel` (Factory), `PlaceViewModel` (AndroidViewModel)
- Navegación
  - `MainActivity` con `NavHostFragment`, `Toolbar` y `BottomNavigationView`
  - `nav_graph.xml` define destinos y Safe Args para editar contacto

## Notas y mejoras futuras
- La BD usa `fallbackToDestructiveMigration` (útil en desarrollo). Para producción, añadir migraciones controladas.
- Posibles extensiones:
  - Eliminar/editar lugares; borrar contactos desde la lista.
  - Pantalla de detalle de lugar con mapa (Maps SDK o Intent a Google Maps).
  - Tests de UI (Espresso) para flujos principales.

## Licencia
Este proyecto se comparte sin licencia explícita. Si deseas agregar una licencia (por ejemplo, MIT), indícalo y la incorporamos.

