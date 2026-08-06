# USB Orientación Vocacional · Android

Aplicación móvil en Kotlin y Jetpack Compose para el flujo de orientación vocacional de la Universidad de San Buenaventura, sede Bogotá.

## Recorrido disponible

1. Inicio institucional.
2. Inicio de sesión, recuperación y registro completo para mayores de edad.
3. Guía previa con los recursos visuales oficiales.
4. Prueba de demostración con cronómetro, progreso por respuestas, navegación y validación de preguntas pendientes.
5. Revisión final, cálculo de afinidad por cinco áreas y programas recomendados.
6. Informe PDF generado en el dispositivo y compartible desde Android.
7. Perfil editable.
8. Panel administrativo con resumen, usuarios, resultados, reportes y configuración.

## Acceso administrativo de demostración

- Correo: `admin@usb.edu.co`
- Contraseña: cualquier valor de seis o más caracteres.

Los demás correos válidos ingresan con rol de estudiante. La autenticación, los cambios administrativos y los reportes del panel se mantienen como demostraciones locales hasta conectar el backend.

## Abrir y ejecutar

1. Abre la carpeta del proyecto en Android Studio.
2. Configura un SDK Android compatible con `compileSdk 36.1` y usa JDK 17 para Gradle.
3. Sincroniza Gradle.
4. Ejecuta la configuración `app` en un emulador o dispositivo con Android 7.0 (API 24) o posterior.

La prueba contiene 15 preguntas de demostración distribuidas en cinco áreas. Antes de una publicación real debe sustituirse por el instrumento vocacional validado y conectarse a los servicios institucionales.
