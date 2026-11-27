# API Pedido — Sonido Vivo

Copiar `api-pedido` a la raíz del backend y agregar el contenido de `compose-servicio.txt` dentro de `services:` del docker-compose existente. No reemplazar los demás servicios.

Usa Spring Data JPA/Hibernate con entidades, JpaRepository y ddl-auto=validate; no modifica automáticamente el esquema. Producto y Usuario son entidades parciales de las tablas compartidas: no se crean ni eliminan desde esta API. Conserva el secreto, emisor y audiencia del JWT de api-usuario. Todas las operaciones requieren JWT de un usuario válido; solo accede a sus propios pedidos.

## Requisitos de BD por verificar
- ID_PEDIDO e ID_DETALLE se mapean con GenerationType.IDENTITY; confirmar que sean columnas identity. Si se usan secuencias, adaptar @GeneratedValue y @SequenceGenerator a las secuencias reales.
- El estado inicial PENDIENTE debe estar permitido por cualquier CHECK existente; verificar sus valores.
- Se descuenta stock al crear y se devuelve al eliminar un pedido pendiente. El borrado es físico y transaccional. No hay endpoint de modificación.
- Las restricciones de Oracle no se modifican. Verificar nombres reales y restricciones con USER_TAB_COLUMNS y USER_CONSTRAINTS.

## Ejecutar
`docker compose up -d --build --no-deps api-pedido`

`docker compose logs -f api-pedido`

Swagger: http://localhost:8083/swagger-ui/index.html

POST /api/v1/pedidos con Authorization: Bearer JWT:
```json
{"direccionEnvio":"Av. Central 123, Santiago", "detalles":[{"idProducto":1,"cantidad":2}]}
```

El producto debe existir, estar activo y tener stock. El precio se obtiene de Oracle. GET lista solo los pedidos propios. DELETE elimina solo pendientes propios. Probar también sin token (401), con otro usuario (404) y stock insuficiente (409).

No se ha ejecutado la compilación ni probado contra Oracle en este entorno. La fecha se guarda en UTC como TIMESTAMP sin zona.
