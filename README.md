# 📘 PERÚ MAR – Backend (Spring WebFlux)

## 📌 Descripción  
Backend modular en **Spring WebFlux**, desplegado en **AWS Elastic Beanstalk**, con persistencia en **DynamoDB** y almacenamiento de archivos en **S3**.  
Se organiza en **módulos de dominio (feature-based)**, lo que facilita una futura migración a microservicios.  

---

## 📂 Estructura de módulos

```
src/main/java/pe/edu/perumar/
 ├── maestras/       # Catálogos básicos (Sexo, TipoDoc, Ubigeo, Conceptos de costo)
 ├── academico/
 │    ├── materias/  # CRUD de Materias
 │    ├── carreras/  # CRUD de Carreras (usa Materias)
 │    └── ciclos/    # CRUD de Ciclos (usa Carreras)
 ├── usuarios/       # Autenticación y autorización con Cognito
 ├── matricula/      # Alumno + Inscripciones (flujo principal de matrícula)
 ├── archivos/       # Manejo de archivos en S3 (presigned URLs)
 ├── pdfs/           # Generación de PDFs (Ficha de matrícula)
 ├── pagos/          # Pagos de alumno particular
 ├── plantillas/     # Plantillas de documentos (post-MVP)
 ├── patrocinadores/ # Empresas patrocinadoras + facturación (post-MVP)
 └── auditoria/      # Logging estructurado (mínimo en MVP)
```

---

## 🔑 Dependencias entre módulos

```
[Maestras]
    ↓
[Materias] → [Carreras] → [Ciclos]
                           ↓
                        [Matrícula]
                           ↓ ↓ ↓ ↓
             [Archivos]  [PDFs]  [Pagos]  [Plantillas]
                                    ↓
                              [Patrocinadores]

(Auditoría) → transversal a todos los módulos
```

---

## 🚀 Flujo de despliegue
- **Build:**  
  ```bash
  mvn clean package -DskipTests
  ```
- **Deploy:**  
  ```bash
  eb deploy
  ```
- **Configuración:**  
  Variables AWS (`DynamoDB`, `S3`, `Cognito`) en `application.properties` o parámetros de entorno en Elastic Beanstalk.  

---

## ✅ Estado actual (Fase 1)
- CRUD de **Materias** completo (con tests).  
- **Carreras** y **Ciclos** en progreso.  
- Seguridad con JWT (**Cognito**) implementada.  
- **DynamoDB** y **S3** configurados.  
