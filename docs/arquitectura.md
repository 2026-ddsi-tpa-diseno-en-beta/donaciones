# Arquitectura - Servicio de Donaciones

## Componentes

El componente desarrollado es el Servicio de Donaciones. Expone una API HTTP y concentra
la orquestacion en `Fachada`, respetando la idea de controllers como capa de presentacion
y fachada como capa de servicio.

```mermaid
flowchart LR
  subgraph Cliente["Cliente externo"]
    Postman["Postman / consumidor HTTP"]
  end

  subgraph Donaciones["Servicio de Donaciones"]
    Controllers["Controllers REST\n/donaciones\n/productos\n/categorias\n/identificadores"]
    Fachada["FachadaDonaciones\nFachada"]
    Dominio["Dominio\nDonacion\nProducto\nCategoria\nIdentificador"]
    Repos["Repositorios JPA\nDonaciones\nProductos\nCategorias\nIdentificadores"]
    Mappers["Mappers\nDTO a Dominio"]
    Metrics["Micrometer / Actuator"]
  end

  subgraph Persistencia["Persistencia"]
    DB["PostgreSQL\nH2 local/test"]
  end

  subgraph Donadores["Servicio de Donadores y Entidades"]
    FDE["FachadaDonadoresYEntidades"]
  end

  subgraph Logistica["Servicio de Logistica"]
    FL["FachadaLogistica"]
  end

  Postman --> Controllers
  Controllers --> Fachada
  Fachada --> Dominio
  Fachada --> Repos
  Fachada --> Mappers
  Fachada --> Metrics
  Repos --> DB
  Fachada -->|HTTP| FDE
  Fachada -->|HTTP| FL
```

## Despliegue actual

La aplicacion se ejecuta como un servicio Spring Boot. En despliegue productivo se configura
con PostgreSQL mediante variables de entorno de Spring (`SPRING_DATASOURCE_URL`,
`SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`). Para ejecucion local sin
configuracion externa conserva H2 en memoria como fallback.

```mermaid
flowchart TB
  User["Postman / navegador"] --> Internet["HTTPS"]
  Internet --> Render["Render Web Service"]
  Render --> Container["Contenedor Docker"]
  Container --> App["Spring Boot\nar.edu.utn.dds.k3003.app.Application"]
  App --> DB["PostgreSQL"]
  App --> Metrics["Actuator / Micrometer"]

  App --> DonadoresURL["DONADORES_Y_ENTIDADES_URL"]
  App --> LogisticaURL["LOGISTICA_URL"]
```

## Notas de integracion

- La integracion con Donadores y Entidades se configura con `DONADORES_Y_ENTIDADES_URL`.
  El servicio consume `GET /donadores/{id}`, `GET /donadores/{id}/puede-donar` y
  `POST /donadores/{id}/quejas`.
- La integracion con Logistica se configura con `LOGISTICA_URL`. El servicio consume
  `POST /depositos/{id}/donacion` para informar la donacion ingresada.
- Si las URLs externas no estan configuradas, se usan adaptadores locales solo para facilitar
  desarrollo aislado.
- Se exponen metricas de altas y errores de integracion mediante Actuator/Micrometer.
- Las categorias se cargan previamente por `/categorias` y se validan antes de crear productos.
