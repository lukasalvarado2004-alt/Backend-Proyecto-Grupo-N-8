## Módulo de usuarios

El backend usa Oracle Cloud mediante el wallet y escucha en el puerto 8081. El alta
de usuarios guarda un hash BCrypt y asigna el rol CLIENTE. Las cuentas con
contraseñas anteriores en texto plano necesitan restablecerlas mediante un proceso
autorizado. Usa una cuenta ficticia recién registrada para comprobar el login.

### Arranque con Docker Compose

Docker Compose lee la clave JWT del archivo privado
`../sonidovivo-config/jwt.env`, en una carpeta hermana del repositorio.
La entrada se llama `JWT_SECRET` y contiene una clave aleatoria de 32 bytes
en Base64. Conserva la misma clave entre reinicios. Si configuraste otro
archivo privado, puedes indicar su ruta mediante `JWT_ENV_FILE`.

Desde la raíz del proyecto, crea ese archivo una vez si aún no lo tienes y
arranca el backend. Si ya tienes una clave JWT, reutilízala.

```bash
install -d -m 700 ../sonidovivo-config
if [ ! -e ../sonidovivo-config/jwt.env ]; then
    jwt_clave=$(openssl rand -base64 32) &&
        (umask 077 && printf 'JWT_SECRET=%s\n' "$jwt_clave" > ../sonidovivo-config/jwt.env)
    unset jwt_clave
fi
sudo docker compose up --build
```

Compose sigue leyendo las variables de Oracle del archivo de entorno habitual.
Si usas un archivo de Oracle en otra ruta, indica esa ruta con `--env-file`.

Swagger para el entorno local está en
[localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html).
Usa HTTPS o un túnel SSH para enviar credenciales o tokens a un servidor remoto.

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
