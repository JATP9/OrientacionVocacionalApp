# USB Orientación Vocacional · Android

Aplicación móvil en Kotlin y Jetpack Compose conectada al backend Kotlin/Spring
Boot de `ProyectoVocacional`.

## Funcionalidades conectadas

- Inicio de sesión por correo o nombre de usuario mediante JWT
  (`POST /api/v1/auth/login`).
- Registro con nombre de usuario, validación de contraseña segura, comprobación
  automática y confirmación de mayoría de edad, y consentimientos independientes.
- Relación institucional equivalente a web: `No`, `Inscrito` o `Estudiante`;
  programa y semestre solo se solicitan al estudiante activo.
- Registro con departamentos, municipios y programas obtenidos del backend,
  seguido de redirección al inicio de sesión.
- Recuperación y restablecimiento de contraseña por correo.
- Consulta y actualización del perfil autenticado con los catálogos reales de
  departamento y municipio.
- Edición de programa y semestre para estudiantes universitarios, restringida
  a programas con enlace institucional vigente.
- Cambio autenticado de contraseña desde el perfil.
- Eliminación del perfil mediante desactivación de la cuenta y cierre local de
  sesión.
- Descarga de las 180 preguntas vigentes del instrumento.
- Envío de respuestas con escala de 1 a 4 y presentación del resultado calculado
  por el backend.
- Resultados con la misma estructura visual de la versión web: área principal,
  afinidad por barras, perfil interactivo, carreras recomendadas, financiamiento
  e informe PDF local. En móvil, la ilustración principal queda centrada y el
  perfil vocacional cambia a una composición vertical para dar ancho completo
  a nombres de áreas y características extensas.
- Informe PDF móvil equivalente al generado por Web: página A4, encabezado
  naranja, tipografía y jerarquía de secciones, tablas de afinidad y programas,
  aviso de orientación, pie, numeración y nombre de archivo institucional.
- Apartado **Mis resultados** con el historial cronológico del usuario, consulta
  del detalle de cualquier prueba anterior y estados de carga, vacío y error.
  Su interfaz móvil replica la identidad del resultado Web mediante fondo
  degradado naranja-azul, paneles blancos, títulos y acentos naranjas, tarjetas
  compactas y metadatos organizados para pantallas pequeñas.

La navegación conserva el recorrido Introducción → Preguntas → Revisión →
Resultados. Desde la introducción también se puede abrir **Mis resultados**;
esta sección usa `GET /api/v1/pruebas/mis-pruebas` y
`GET /api/v1/pruebas/{id}/resultado`. Si falta alguna respuesta, el envío permanece bloqueado. Las
funciones administrativas continúan reservadas para la versión web.

La vista de Resultados incluye un regreso contextual: vuelve a **Mis resultados**
cuando se abrió una prueba histórica, o a la introducción de la prueba cuando el
resultado corresponde a un intento recién finalizado.

Los formatos de los requests y las rutas del backend se conservan. Los nuevos
consentimientos se validan en el cliente porque el contrato actual de Spring no
los recibe todavía.

## Enlaces de restablecimiento

La app reconoce el enlace público usado por el backend:

```text
https://tecnosoft.ingusb.com/vocacional/restablecer-contrasena?token=...
```

También admite `usbvocacional://restablecer-contrasena?token=...`. Para que el
enlace HTTPS abra la app directamente en Android 12 o posterior, publica el
archivo `/.well-known/assetlinks.json` del dominio con el `applicationId` y la
huella SHA-256 del certificado de firma de producción.

## Configurar la URL del backend

El valor predeterminado apunta desde el emulador Android al servidor local:

```text
http://10.0.2.2:8088/
```

Para usar otra dirección, agrega `API_BASE_URL` al archivo local
`local.properties` (este archivo no se versiona):

```properties
API_BASE_URL=http://192.168.1.25:8088/
```

La URL debe terminar en `/`; Gradle también la normaliza si se omite. En un
dispositivo físico usa la IP de la computadora dentro de la misma red. Para
producción configura una URL HTTPS.

## Ejecutar

1. Inicia `ProyectoVocacional`; el perfil local del backend usa el puerto 8088.
2. Abre este proyecto en Android Studio.
3. Usa JDK 17 y un SDK compatible con `compileSdk 36.1`.
4. Sincroniza Gradle y ejecuta la configuración `app` en Android 7.0 (API 24) o
   posterior.

## Sesión y seguridad

El cliente añade `Authorization: Bearer <JWT>` a los endpoints protegidos. El
token se mantiene solo durante el proceso, salvo que el usuario active
“Recordarme”; en ese caso se conserva en el almacenamiento privado de la app.
No se incluyen credenciales ni tokens en el repositorio.

## Verificación

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```
