# Proyecto Semestral — Equipo 8

### Curso: Desarrollo FullStack II  — Duoc UC 2026

---

##  Integrantes

* **Franco Villarroel** — fra.villarroelg@duocuc.cl
* **Nicolás Vega** — nicola.vegac@duocuc.cl
* **Lukas Tirapegui** — lu.tirapegui@duocuc.cl

---
##  Links
Swagger para el entorno local está en
[localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html).
Usa HTTPS o un túnel SSH para enviar credenciales o tokens a un servidor remoto.

Dirección de repositorio frontend: github.com/lukasalvarado2004-alt/Proyecto-Sonido-Vivo-N-8

## Módulo de usuarios

El backend usa Oracle Cloud mediante el wallet y escucha en el puerto 8081. El alta
de usuarios guarda un hash BCrypt y asigna el rol CLIENTE. Las cuentas con
contraseñas anteriores en texto plano necesitan restablecerlas mediante un proceso
autorizado. Usa una cuenta ficticia recién registrada para comprobar el login.

### Estado del desarrollo

La columna **Diseñado** describe el comportamiento definido, **Implementado**
indica lo disponible en el código y **Probado** registra el resultado observado.

| Funcionalidad | Diseñado | Implementado | Probado |
|---|---|---|---|
| Conexión a Oracle Cloud | Persistir usuarios en el esquema Oracle mediante el wallet. | Conexión JDBC y validación del esquema al arrancar. | El contenedor inició con la conexión activa. En Postman se registró y consultó una cuenta de prueba. |
| Registro de usuarios | Validar los datos, evitar correos duplicados, guardar el hash y asignar `CLIENTE`. | `POST /api/v1/usuarios` usa BCrypt y asigna el rol desde el servidor. | `201` en Postman para un alta válida. `409` por correo duplicado en una prueba aislada. |
| Inicio de sesión | Verificar el hash y emitir un JWT Bearer de 15 minutos. | `POST /api/v1/auth/login` entrega `accessToken` o un `401` genérico si fallan las credenciales. | `200` con token y `401` con credenciales incorrectas, observados en Postman. |
| Perfil y consulta por ID | Obtener el perfil desde el JWT y restringir la consulta por ID al titular o al administrador. | `GET /api/v1/auth/me` usa el sujeto del JWT. `GET /api/v1/usuarios/{id}` verifica titularidad. | Consulta propia por ID con `200` en Postman. `/auth/me` sin token devuelve `401` en el servidor. El caso con token válido devolvió `200` en una prueba aislada. |
| Listado y búsqueda por correo | Reservar el acceso al rol `ADMINISTRADOR`. | `GET /api/v1/usuarios` y `GET /api/v1/usuarios/email/{email}` exigen ese rol. | Listado con token de cliente rechazado con `403` en prueba aislada. Pendiente probar el listado y la búsqueda con un administrador. |
| Edición de usuarios | Permitir que el titular o un administrador edite nombre, teléfono y dirección. | `PUT /api/v1/usuarios/{id}` valida los datos y exige permiso sobre la cuenta. | Edición de la cuenta propia con `200` en prueba aislada. |
| Eliminación y cambio de rol | Reservar ambas operaciones a administradores. | `DELETE /api/v1/usuarios/{id}` y `PATCH /api/v1/usuarios/{id}/rol` están protegidos por rol. | Pendiente verificar ambas operaciones con un administrador en la versión con JWT. |
| Audiencia del JWT | Emitir tokens para `sonidovivo-api` y comprobar `aud` antes de consultar el usuario. | El login incluye `aud` y el validador exige que contenga `sonidovivo-api`. | Emisión confirmada en Postman. Una prueba aislada confirmó la aceptación y el rechazo cuando falta `aud` o corresponde a otra API. |
| Documentación Swagger | Exponer el contrato de la API y el esquema Bearer. | Swagger UI y OpenAPI muestran las rutas de usuarios y autenticación. | `200` en Swagger UI y `/v3/api-docs`. Confirmadas las operaciones CRUD y el esquema Bearer. |
| Catálogo y pedidos | CRUD de catálogo y pedidos que validan el stock disponible, según el alcance de Sonido Vivo. | Pendiente. | Pendiente. |

### Arranque con Docker Compose

Docker Compose lee la clave JWT del archivo privado
`../sonidovivo-config/jwt.env`, en una carpeta hermana del repositorio.
La entrada se llama `JWT_SECRET` y contiene una clave aleatoria de 32 bytes
en Base64. Conserva la misma clave entre reinicios. Si configuraste otro
archivo privado, puedes indicar su ruta mediante `JWT_ENV_FILE`.


Compose sigue leyendo las variables de Oracle del archivo de entorno habitual.
Si usas un archivo de Oracle en otra ruta, indica esa ruta con `--env-file`.


### Autenticación y permisos

| Petición | Acceso |
|---|---|
| `POST /api/v1/usuarios` | Público. Registro de un cliente. |
| `POST /api/v1/auth/login` | Público. Recibe `correo` y `contrasena`. |
| `GET /api/v1/auth/me` | Usuario autenticado. El perfil se obtiene del JWT. |
| `GET` / `PUT /api/v1/usuarios/{id}` | Titular o administrador. |
| Listar, buscar por correo, eliminar o cambiar el rol | Administrador. |

El login responde con `accessToken`, `tipo` (Bearer), `expiraEnSegundos` (900),
`usuarioId` y `rol`. El campo `token` contiene el mismo valor para conservar
la compatibilidad de las pruebas existentes. En Postman, usa el valor en
**Authorization → Bearer Token** para llamar a `GET /api/v1/auth/me` y espera
`200`. Repite con **No Auth** y espera `401`. Usa el token de cliente en
`GET /api/v1/usuarios` y espera `403`.

El cliente descarta el token al cerrar sesión e inicia sesión de nuevo cuando
vence. Esta versión prescinde de endpoints de cierre y renovación.

El backend emite JWT para la audiencia `sonidovivo-api` y responde `401` en
rutas protegidas si el token carece de `aud` o está destinado a otra API. Al
desplegar esta validación, vuelve a iniciar sesión para obtener un token nuevo.

Verifica que la columna `USUARIOS.PASSWORD_HASH` admita al menos 60 caracteres
antes de registrar cuentas con BCrypt. La implementación usa las columnas
existentes de `USUARIOS`.



### Autenticación y permisos

| Petición | Acceso |
|---|---|
| `POST /api/v1/usuarios` | Público. Registro de un cliente. |
| `POST /api/v1/auth/login` | Público. Recibe `correo` y `contrasena`. |
| `GET /api/v1/auth/me` | Usuario autenticado. El perfil se obtiene del JWT. |
| `GET` / `PUT /api/v1/usuarios/{id}` | Titular o administrador. |
| Listar, buscar por correo, eliminar o cambiar el rol | Administrador. |

El login responde con `accessToken`, `tipo` (Bearer), `expiraEnSegundos` (900),
`usuarioId` y `rol`. El campo `token` contiene el mismo valor para conservar
la compatibilidad de las pruebas existentes. En Postman, usa el valor en
**Authorization → Bearer Token** para llamar a `GET /api/v1/auth/me` y espera
`200`. Repite con **No Auth** y espera `401`. Usa el token de cliente en
`GET /api/v1/usuarios` y espera `403`.

El cliente descarta el token al cerrar sesión e inicia sesión de nuevo cuando
vence. Esta versión prescinde de endpoints de cierre y renovación.

El backend emite JWT para la audiencia `sonidovivo-api` y responde `401` en
rutas protegidas si el token carece de `aud` o está destinado a otra API. Al
desplegar esta validación, vuelve a iniciar sesión para obtener un token nuevo.

Verifica que la columna `USUARIOS.PASSWORD_HASH` admita al menos 60 caracteres
antes de registrar cuentas con BCrypt. La implementación usa las columnas
existentes de `USUARIOS`.
