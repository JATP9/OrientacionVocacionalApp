# Vocacional · Android + iOS

Aplicación móvil multiplataforma de la Universidad de San Buenaventura Bogotá.
Comparte la interfaz, navegación, reglas de negocio, acceso REST y modelos entre
Android e iOS mediante Kotlin Multiplatform y Compose Multiplatform, conservando
la identidad visual y el contrato actual del backend Spring Boot.

## Compatibilidad

- Android 7.0 o posterior (API 24+).
- iOS 15.0 o posterior, para iPhone y iPad.
- JDK 17, Android SDK 36 y Gradle Wrapper incluido.
- macOS con Xcode 16 o posterior para compilar, firmar o ejecutar iOS.

## Estructura

| Ruta | Responsabilidad |
| --- | --- |
| `shared/src/commonMain` | UI Compose, navegación, ViewModels, validaciones, cliente Ktor, sesión y recursos |
| `shared/src/androidMain` | Adaptaciones de Android del módulo compartido |
| `shared/src/iosMain` | Adaptaciones de iOS y `MainViewController` para SwiftUI |
| `app` | Host Android, persistencia privada y generación/compartición del PDF |
| `iosApp` | Host SwiftUI, proyecto Xcode, icono y generación/compartición del PDF |

La aplicación conserva inicio de sesión JWT, registro, recuperación de
contraseña, perfil, prueba de 180 preguntas, revisión, encuesta de conformidad,
resultados, historial y recomendaciones. Las funciones administrativas continúan
remitiendo a la versión web, como en la aplicación Android original.

Al finalizar una prueba, la persona debe calificar su experiencia del 1 al 5.
La calificación se envía al backend en el campo `satisfaccion`, junto con las
respuestas; la navegación a resultados ocurre únicamente después de que el
servidor confirma el registro.

## Configurar el backend

El backend local usa el puerto `8088`.

### Android

El emulador usa de forma predeterminada:

```text
http://10.0.2.2:8088/
```

Para cambiarlo, crea `local.properties` en la raíz (no se versiona):

```properties
API_BASE_URL=http://192.168.1.25:8088/
```

### iOS

Edita `iosApp/Configuration/Config.xcconfig`:

```xcconfig
API_BASE_URL = http:/$()/localhost:8088/
```

`localhost` funciona en el simulador cuando el backend se ejecuta en el Mac.
Para un iPhone físico usa una dirección accesible desde su red. Antes de
distribuir cualquiera de las dos aplicaciones configura una URL HTTPS pública.

## Ejecutar Android

1. Inicia el backend.
2. Abre la raíz del proyecto en Android Studio.
3. Selecciona el módulo `app` y ejecuta un emulador o dispositivo.

También puedes compilar desde la terminal:

```bash
./gradlew :shared:testAndroidHostTest :app:assembleDebug
```

En Windows y Linux, Gradle configura únicamente el destino Android y no ejecuta
el commonizer de Kotlin/Native. Los destinos iOS se habilitan automáticamente al
abrir el mismo proyecto en macOS, donde están disponibles Xcode y las bibliotecas
Apple necesarias.

## Ejecutar iOS

1. Inicia el backend en el Mac.
2. Abre `iosApp/iosApp.xcodeproj` en Xcode.
3. En `iosApp/Configuration/Config.xcconfig`, establece `TEAM_ID` con el Team de
   Apple Developer si vas a usar un dispositivo físico.
4. Si la organización lo requiere, cambia `PRODUCT_BUNDLE_IDENTIFIER` por el
   identificador registrado en Apple Developer.
5. Elige un simulador o dispositivo y pulsa **Run**. La fase
   `Compile Kotlin Framework` construye e integra automáticamente `Shared`.

También puedes comprobar el framework del simulador desde macOS:

```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

## Restablecimiento de contraseña

Ambas plataformas aceptan:

```text
https://tecnosoft.ingusb.com/vocacional/restablecer-contrasena?token=...
usbvocacional://restablecer-contrasena?token=...
```

Para enlaces HTTPS verificados:

- Android requiere `/.well-known/assetlinks.json` con el `applicationId` y la
  huella SHA-256 del certificado de producción.
- iOS requiere `/.well-known/apple-app-site-association` con el Team ID, Bundle
  ID y la ruta `/vocacional/restablecer-contrasena*`.

El esquema `usbvocacional` funciona como alternativa sin esos archivos.

## Sesión, red e informes

El módulo común adjunta `Authorization: Bearer <JWT>` a las rutas protegidas y
usa Ktor con OkHttp en Android y Darwin en iOS. El token permanece en memoria,
salvo que la persona active **Recordarme**; entonces cada host lo guarda en el
almacenamiento privado de la aplicación.

El botón de informe genera un PDF nativo y abre la hoja para compartir: Android
usa `PdfDocument`/`FileProvider`; iOS usa `UIGraphicsPDFRenderer` y
`UIActivityViewController`.

## Publicación

Antes de generar una versión de producción:

1. Configura el backend HTTPS y desactiva cualquier URL local.
2. Publica los archivos de asociación de enlaces del dominio.
3. Define firma, versión e identificadores en Android Studio y Xcode.
4. Ejecuta las pruebas en al menos un dispositivo real de cada plataforma.

No se incluyen credenciales, certificados ni tokens en el proyecto.
