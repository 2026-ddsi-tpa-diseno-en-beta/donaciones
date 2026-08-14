[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/q5A4m_h4)

# 🧪 2026 - Trabajo Practico Anual

## 👤 Datos del Alumno
- **Nombre:** Federico
- **Apellido:** Bremberg

---

## 🧩 Componente Desarrollado
- **Servicio de Donaciones**

---

## ⚙️ Link al despliegue en Render
- https://donaciones-xouj.onrender.com
- Swagger UI: https://donaciones-xouj.onrender.com/swagger-ui/index.html
- OpenAPI JSON: https://donaciones-xouj.onrender.com/v3/api-docs

---

## Documentacion
- [Diagrama de clases](docs/clases.md)
- [Diagrama de arquitectura](docs/arquitectura.md)
- [Especificacion OpenAPI](docs/openapi.yaml)

---

## Como correrlo local

Las URLs de Donadores y Entidades y de Logistica son **obligatorias**: sin ellas el modulo usaria
fachadas de prueba que aceptan cualquier donador, asi que el arranque falla a proposito. Para
trabajar aislado esta el perfil `dev`, que habilita esas fachadas locales y H2 en memoria:

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

Contra los componentes reales:

```bash
export DONADORES_Y_ENTIDADES_URL=https://...
export LOGISTICA_URL=https://...
mvn spring-boot:run
```

Tests: `mvn test`. Requiere JDK 21.

---

## Variables de entorno

| Variable | Descripcion |
|---|---|
| `DONADORES_Y_ENTIDADES_URL` | Base URL de Donadores y Entidades |
| `LOGISTICA_URL` | Base URL de Logistica |
| `SPRING_DATASOURCE_URL` | JDBC de PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base |
| `SPRING_DATASOURCE_PASSWORD` | Password de la base |

El resto de la configuracion es opcional y esta documentada en
[docs/arquitectura.md](docs/arquitectura.md). `GET /admin/estado` muestra en que modo quedo cada
integracion.

---

## API expuesta

### Donaciones
| Metodo | Ruta | Descripcion |
|---|---|---|
| `POST` | `/donaciones` | Registra una donacion. Valida cantidad > 0, que el producto exista, y que el donador exista y pueda donar |
| `GET` | `/donaciones` | Lista todas, o filtra con `?donadorID=&fecha=yyyy-MM-dd` |
| `GET` | `/donaciones/{id}` | Busca por ID |
| `GET` | `/donaciones/search?donadorID=&fecha=` | Busqueda por donador desde una fecha |
| `GET` | `/donaciones/{id}/historial` | **Historial completo de cambios de estado** (trazabilidad y auditoria) |
| `PATCH` | `/donaciones/{id}/estado?estado=` | Cambia el estado validando la transicion |
| `POST` | `/donaciones/{id}/quejas` | Registra una queja (alias: `/queja`) |

### Productos
| Metodo | Ruta | Descripcion |
|---|---|---|
| `POST` | `/productos` | Alta. El `identificadorID` es **opcional**; `subcategoriaID` clasifica el producto |
| `GET` | `/productos` | Lista |
| `GET` | `/productos/{id}` | Busca por ID (incluye `subcategoriaID`) |
| `PUT` | `/productos/{id}` | **Modificacion**, revalidando el identificador |
| `DELETE` | `/productos/{id}` | **Baja**. 409 si tiene donaciones asociadas |

### Categorias
| Metodo | Ruta | Descripcion |
|---|---|---|
| `POST` | `/categorias` | Alta. Con `categoriaPadreID` la crea como subcategoria |
| `GET` | `/categorias` | Lista |
| `GET` | `/categorias/{id}` | Busca por ID |
| `GET` | `/categorias/{id}/subcategorias` | **Subcategorias** que la componen |
| `PUT` | `/categorias/{id}` | **Modificacion** |
| `DELETE` | `/categorias/{id}` | **Baja**. 409 si tiene subcategorias o productos |

### Identificadores
| Metodo | Ruta | Descripcion |
|---|---|---|
| `POST` | `/identificadores` | Alta |
| `GET` | `/identificadores` | Lista |
| `GET` | `/identificadores/{id}` | Busca por ID |
| `PUT` | `/identificadores/{id}` | **Modificacion**. 409 si invalidaria un producto existente |
| `DELETE` | `/identificadores/{id}` | **Baja**. 409 si algun producto lo usa |

### Administracion
| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/admin/estado` | Conteos del modulo y modo de cada integracion, sin mirar la base |
| `DELETE` | `/admin/datos` | Limpia toda la base |

### Codigos de error

| Codigo HTTP | `code` | Cuando |
|---|---|---|
| 400 | `BAD_REQUEST` | Pedido mal formado o dato invalido |
| 404 | `NOT_FOUND` | No existe el recurso |
| 409 | `RECURSO_EN_USO` | Se intento dar de baja algo referenciado |
| 422 | `DONACION_RECHAZADA` | El donador no existe o no esta habilitado para donar |
| 502 | `ERROR_INTEGRACION` | Fallo un componente del que dependemos (no es culpa del cliente) |

---

## Reglas de negocio implementadas

- **Validacion de identificadores**: codigo de barras valido si la descripcion tiene 3+ palabras;
  QR valido si el nombre tiene una cantidad par de letras (cuenta letras reales, ignora espacios y
  puntuacion). El identificador es opcional.
- **Maquina de estados**: `INGRESADA → ACEPTADA → CONQUEJA`. Toda transicion queda registrada con
  fecha y detalle, y el historial es consultable por HTTP.
- **Recepcion y rechazo**: antes de registrar se verifica contra Donadores y Entidades que el
  donador exista y que pueda donar. Un rechazo devuelve 422, no un error generico.
- **Categoria y subcategoria**: deben existir previamente. La subcategoria es la unidad minima de
  asignacion y se valida que pertenezca a la categoria declarada.
- **ABM completo** de productos, categorias e identificadores, con integridad referencial.
- **IDs numericos** asignados por este componente, desde una secuencia persistida.

---

### Importante

**ARCHIVOS PROTEGIDOS:**

> Los archivos de las carpetas "/catedra" y ".github/" estan PROTEGIDOS, es decir, **NO PUEDEN MODIFICARLOS**.
Modificar estos archivos implica desaprobar inmediatamente la instancia de entrega del TPA.

Este modulo **no modifico ningun archivo de `catedra/` ni de `.github/`**. Los datos que el
`ProductoDTO` y el `CategoriaDTO` de la catedra no contemplan (subcategoria, jerarquia de
categorias) viajan por records propios en `controllers/requests` y `controllers/responses`.
