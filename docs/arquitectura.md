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
    Controllers["Controllers REST\n/donaciones\n/productos\n/identificadores"]
    Fachada["FachadaDonaciones\nFachada"]
    Dominio["Dominio\nDonacion\nProducto\nIdentificador"]
    Repos["Repositorios in-memory\nDonaciones\nProductos\nIdentificadores"]
    Mappers["Mappers\nDTO a Dominio"]
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
  Fachada --> FDE
  Fachada --> FL
```

## Despliegue actual

En esta entrega no hay persistencia externa. La aplicacion se ejecuta como un servicio
Spring Boot y mantiene los datos en memoria, igual que en la entrega anterior.

```mermaid
flowchart TB
  User["Postman / navegador"] --> Internet["HTTPS"]
  Internet --> Render["Render Web Service"]
  Render --> Container["Contenedor Docker"]
  Container --> App["Spring Boot\nar.edu.utn.dds.k3003.Application"]
  App --> Memory["Listas en memoria"]

  App -. futura integracion .-> DonadoresURL["URL_DONADORES_Y_ENTIDADES"]
  App -. futura integracion .-> LogisticaURL["URL_LOGISTICA"]
```

## Notas de integracion

- Para esta entrega se agregaron adaptadores locales de Donadores y Logistica que permiten
  ejecutar el flujo basico sin tener los otros servicios desplegados.
- La integracion real entre componentes queda preparada por las interfaces de catedra:
  `FachadaDonadoresYEntidades` y `FachadaLogistica`.
- Los datos se guardan en memoria; no hay base de datos ni ORM en esta entrega.
