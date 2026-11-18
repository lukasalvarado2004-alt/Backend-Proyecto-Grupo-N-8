
## 📋 Estado de implementación del módulo de usuarios

La siguiente tabla muestra el estado actual de desarrollo de las funcionalidades relacionadas con la gestión de usuarios.

| Funcionalidad | Estado | Descripción |
|---|---|---|
| **Tabla de usuarios** | ✅ Listo | La tabla existe en la base de datos del equipo. Los datos persisten después de reiniciar el backend y las contraseñas se almacenan mediante hash. |
| **Inicio de sesión** | ✅ Listo | El login verifica las credenciales del usuario, comprueba el hash de la contraseña y genera un token JWT. Las credenciales incorrectas devuelven un error genérico. |
| **Registro (crear usuario)** | ✅ Listo | Permite registrar nuevos usuarios en la base de datos, asignando el rol desde el servidor. |
| **Modificar usuario** | 🟡 En avance | La ruta y su lógica están iniciadas, con commits registrados en el repositorio. |
| **Listar usuarios** | 🟡 En avance | La ruta y su lógica están iniciadas, con commits registrados en el repositorio. |
| **Eliminar o desactivar usuario** | 🟡 En avance | La ruta y su lógica están iniciadas, con commits registrados en el repositorio. |

