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
- https://entrega-2-fbremberg.onrender.com
- Swagger UI: https://entrega-2-fbremberg.onrender.com/swagger-ui/index.html
- OpenAPI JSON: https://entrega-2-fbremberg.onrender.com/v3/api-docs

---

## Documentacion
- [Diagrama de clases](docs/clases.md)
- [Diagrama de arquitectura](docs/arquitectura.md)
- [Especificacion OpenAPI](docs/openapi.yaml)

---

## API expuesta

### Donaciones
- `POST /donaciones`
- `GET /donaciones`
- `GET /donaciones/{id}`
- `GET /donaciones?donadorID={donadorID}&fecha={yyyy-MM-dd}`
- `PATCH /donaciones/{id}/estado?estado={INGRESADA|ACEPTADA|CONQUEJA}`
- `POST /donaciones/{id}/quejas`

### Productos
- `POST /productos`
- `GET /productos`
- `GET /productos/{id}`

### Identificadores
- `POST /identificadores`
- `GET /identificadores`
- `GET /identificadores/{id}`

### Administracion
- `DELETE /admin/datos`

### Configuracion Entrega 3
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `DONADORES_Y_ENTIDADES_URL`
- `LOGISTICA_URL`

---

### Importante

**ARCHIVOS PROTEGIDOS:**

> Los archivos de las carpetas "/catedra" y ".github/" estan PROTEGIDOS, es decir, **NO PUEDEN MODIFICARLOS**.
Modificar estos archivos implica desaprobar inmediatamente la instancia de entrega del TPA.
