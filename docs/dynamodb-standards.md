# Estándares de Modelado – DynamoDB PERÚ MAR

## 1. Convenciones de Nombres

* **Tablas**: prefijo `perumar_` seguido del módulo funcional. Ejemplo: `perumar_materias`, `perumar_config`.
* **Atributos**: snake\_case. Ejemplo: `codigo_materia`, `estado`, `updated_at`.
* **Índices**: `<campo>-index`. Ejemplo: `estado-index`.
* **Valores Enum**: mayúsculas. Ejemplo: `ACTIVO`, `INACTIVO`, `CON_EXPERIENCIA`.

## 2. Estructura de Tablas

* Se utiliza **una tabla por agregado raíz** del dominio.
* Cada tabla contiene todos los atributos relacionados al agregado.
* Ejemplo:

  * `perumar_materias` → Materia
  * `perumar_carreras` → Carrera + materias\[]
  * `perumar_ciclos` → Ciclo + ubicación + materias\[]

## 3. Claves Primarias (PK/SK)

* Siempre se define una PK (obligatoria).
* Se usa SK si se necesita sub-agrupación o composición lógica.
* Evitar UUID como PK si puede usarse un identificador semántico.

Ejemplo:

```json
PK: "codigo"  // Ej: "MAT-001"
SK: opcional  // Ej: "seccion" para configuraciones
```

## 4. Índices Secundarios (GSI)

* Se crean solo cuando hay necesidad de consulta por otro campo distinto a la PK/SK.
* Prefijo: `<campo>-index`.

Ejemplo:

```json
GSI: "estado-index"  // PK: estado, SK: created_at
```

## 5. Atributos Comunes y Auditoría

* Todas las tablas deben incluir:

  * `estado`: `ACTIVO` o `INACTIVO`
  * `created_at`, `updated_at`: timestamps ISO 8601
  * `user_audit`: objeto con `created_by`, `updated_by`

Ejemplo:

```json
"user_audit": {
  "created_by": "admin@correo.com",
  "updated_by": "editor@correo.com"
}
```

## 6. Convenciones de Estados y Enum

* Todos los valores controlados deben estar en mayúsculas y ser validados desde el backend.
* Usar listas estáticas si aplica (ej. modalidad de carrera, sexo, tipo de documento).

## 7. Organización y Prefijos

* Evitar nombres genéricos como `config`, `data`, `info`.
* Usar nombres descriptivos por contexto.
* Agrupar por funcionalidad usando prefijos consistentes (`perumar_`).

## 8. Validación desde Backend

* Toda validación crítica se hace desde backend:

  * Validar roles y permisos antes de devolver datos.
  * No confiar en lo que el frontend solicita (ni siquiera en el rol enviado).
  * Verificar consistencia de atributos y relaciones (cuando existan).

## 9. Justificación de uso de DynamoDB

* PERÚ MAR es un MVP con:

  * Estructura simple y modular
  * Poca necesidad de relaciones complejas
  * Objetivo de bajo costo (Free Tier)
  * Infraestructura 100% serverless (DynamoDB, S3, Cognito, etc.)
* DynamoDB permite escalar sin costo de administración ni licencias.
* Las decisiones de modelado se basan en rendimiento, mantenimiento y simplicidad.
