# README – Navbar dinámico por rol

> Guía para que cualquier integrante del equipo agregue/edite elementos del menú **sin tocar código** y con permisos **alineados al ACL**.

---

## 0. Qué cubre esta guía

* Dónde y cómo **editar el menú** por rol (DynamoDB `perumar_ui_config`).
* Cómo **asignar permisos** a cada item (DynamoDB `perumar_access_control`).
* Convención de **nombres de permisos** (`TOKEN#op#FRONTEND`).
* Cómo **probar y auditar** (front + backend).
* Buenas prácticas (AWS Well‑Architected + OWASP).

> Arquitectura: **UI Config** (qué podría verse) separada de **ACL** (qué realmente puede hacer). El backend es la autoridad; el front solo oculta.

---

## 1) Menú por rol – `perumar_ui_config`

Tabla: `perumar_ui_config`  (PK/SK según estándar interno). Cada documento por **rol** contiene secciones e items del Navbar.

### 1.1 Ejemplo mínimo de config para un rol

```json
{
  "role": "DIRECTOR",
  "sections": [
    {
      "label": "Académico",
      "key": "academico",
      "items": [
        {
          "label": "Materias",
          "key": "materias",
          "path": "/materias",
          "icon": "book",
          "perm": "MATERIA_LIST#view#FRONTEND"
        },
        {
          "label": "Carreras",
          "key": "carreras",
          "path": "/carreras",
          "icon": "layers",
          "perm": "CARRERA_LIST#view#FRONTEND"
        }
      ]
    }
  ]
}
```

### 1.2 Reglas

* `label` se muestra en la UI (evitar tildes mal codificadas; ideal UTF‑8 correcto).
* `path` es la ruta del SPA (debe existir en React Router).
* `perm` vincula el item con el ACL (ver sección 2). Si se omite, el item se considera **público** (se recomienda evitarlo en módulos sensibles).
* Secciones sin items visibles por permisos **no se muestran**.

---

## 2) Permisos – `perumar_access_control`

Tabla: `perumar_access_control`

* **PK:** `role` (p. ej. `ADMIN`, `DIRECTOR`, `COORDINADOR`, o `user#<sub>` para reglas por usuario)
* **SK:** `resource_action_scope` (token de permiso)
* Atributo: `allow` (Boolean)

### 2.1 Convención de tokens

```
<MÓDULO>_<RECURSO>#<operación>#FRONTEND
```

**Ejemplos**:

* `MATERIA_LIST#view#FRONTEND`
* `CARRERA_FORM#create#FRONTEND`
* `DASHBOARD_PAGE#view#FRONTEND`

> **Importante:** el frontend solo evalúa permisos que terminan en `#FRONTEND`. Los permisos `#BACKEND` son para la API.

### 2.2 Inserción de permisos

**Registro allow=TRUE (rol puede ver item del menú):**

```json
{
  "role": {"S": "DIRECTOR"},
  "resource_action_scope": {"S": "MATERIA_LIST#view#FRONTEND"},
  "allow": {"BOOL": true}
}
```

**Registro allow=FALSE (explícitamente negado):**

```json
{
  "role": {"S": "COORDINADOR"},
  "resource_action_scope": {"S": "CARRERA_FORM#create#FRONTEND"},
  "allow": {"BOOL": false}
}
```

### 2.3 Reglas

* **Solo** `allow==true` se expone en `/api/v1/acl/me` para el front.
* Puedes combinar permisos por **rol** y por **usuario** (prefijo `user#<sub>` en `role`).
* Wildcards en **frontend**: el UI admite patrones con `*` en `ui_config` (`MATERIA_*#*#FRONTEND`), pero úsalo con mesura.

---

## 3) Flujo de evaluación en runtime

1. **Frontend** se autentica (Cognito) y obtiene `ui_config` por rol.
2. **Backend** (`/api/v1/acl/me`) devuelve `perms` fusionando JWT + Dynamo (solo `allow==true`, `#FRONTEND`).
3. **`useAcl()`** filtra los items: si `item.perm` ∈ `perms` → se muestra; caso contrario se oculta.
4. **Auditoría**: cada clic de menú envía `POST /api/v1/audit/menu-click`.

---

## 4) Cómo agregar un nuevo ítem (paso a paso)

1. **Definir ruta** en el front (React Router) y la pantalla correspondiente.
2. **Crear token** `TOKEN#op#FRONTEND` (sigue la convención de la sección 2.1).
3. **Cargar permiso** en `perumar_access_control` para el/los roles que deban verlo:

   * `role = "DIRECTOR"` (p. ej.)
   * `resource_action_scope = "TOKEN#op#FRONTEND"`
   * `allow = true`
4. **Agregar item** al JSON del rol en `perumar_ui_config`:

   * `label`, `key`, `path`, `icon` (opcional), `perm = "TOKEN#op#FRONTEND"`
5. **Probar** (ver sección 6): recargar, verificar visibilidad según rol, y revisar auditoría.

> No necesitas deploy de front para cambios en `ui_config` y `access_control`; basta con actualizar Dynamo.

---

## 5) Ejemplos habituales

**A) Director ve Reportes**

* ACL:

```json
{
  "role": {"S":"DIRECTOR"},
  "resource_action_scope": {"S":"REPORTES_LIST#view#FRONTEND"},
  "allow": {"BOOL": true}
}
```

* UI config (`ui_config_director`), sección "Reportes":

```json
{
  "label": "Reportes",
  "key": "reportes",
  "items": [
    { "label": "Listado", "key": "reportes_list", "path": "/reportes", "perm": "REPORTES_LIST#view#FRONTEND" }
  ]
}
```

**B) Coordinador NO puede crear Materias**

* ACL deny explícito:

```json
{
  "role": {"S":"COORDINADOR"},
  "resource_action_scope": {"S":"MATERIA_FORM#create#FRONTEND"},
  "allow": {"BOOL": false}
}
```

* Resultado: el ítem “Nueva Materia” no se muestra.

---

## 6) Cómo probar

### 6.1 Ver permisos efectivos

```bash
curl -H "Authorization: Bearer <ACCESS_TOKEN>" \
  http://<host>/api/v1/acl/me
```

Debes ver `{"perms":["…#FRONTEND", …]}` con los tokens esperados para tu rol.

### 6.2 Validar menú en UI

* Inicia sesión con un usuario de **cada rol**.
* Verifica que se vea **solo** lo permitido.
* Usa DevTools → Network → throttling para ver el **Skeleton** de carga.

### 6.3 Auditoría de clics

* Haz clic en varios items.
* Valida en **CloudWatch/logs**: `audit.menu_click …`
* (Si está habilitado) revisa la tabla `perumar_audit_menu_click`.

---

## 7) Troubleshooting

* **El ítem no aparece:**

  * Confirma que `perm` del item **termina en `#FRONTEND`** y que está **allow=true** para el rol.
  * Verifica que el `path` existe en el Router y que el usuario está en el grupo correcto.
* **Sección vacía:** todos los items fueron filtrados por ACL; la sección se oculta (comportamiento esperado).
* **Tildes raras:** corrige el texto en el JSON de `ui_config` (UTF‑8). El front tiene `fixLatin1`, pero es preferible no depender del parche.
* **/acl/me 404/401:** revisa base‑path, mapping y que el front envíe `Authorization: Bearer <access_token>`.

---

## 8) Buenas prácticas

* **AWS Well‑Architected**

  * *Seguridad:* principio de menor privilegio; el backend decide; el front solo oculta.
  * *Fiabilidad:* fallbacks (`Skeleton`, navbar mínimo) si falla `ui_config` o ACL.
  * *Costos:* consultas por PK en Dynamo (barato); auditoría con on‑demand + TTL.
  * *Excelencia operativa:* auditoría estructurada y logs consistentes.
* **OWASP Secure SDLC**

  * Validación de entradas (regex en `path`, DTOs validados).
  * Gestión de secretos: el front no persiste tokens; backend exige Bearer JWT.
  * Revisiones: tests de allowed/denied y estados (`loading/error/empty`).

---

## 9) Glosario rápido

* **`ui_config`**: define estructura del menú por rol.
* **`perm`**: token de autorización del ítem (termina en `#FRONTEND`).
* **`/acl/me`**: endpoint que devuelve permisos efectivos (solo `allow==true` y `#FRONTEND`).
* **`useAcl()`**: hook del front que decide si mostrar/ocultar.
* **Auditoría**: `/audit/menu-click` registra interacciones de navegación.

---

## 10) Checklist para PRs

* [ ] Items nuevos con `perm` correcto (`#FRONTEND`).
* [ ] Entradas ACL creadas (rol/usuario) con `allow=true`.
* [ ] Ruta existe en el Router y pantalla lista.
* [ ] Pruebas manuales por rol + smoke de `/acl/me`.
* [ ] Auditoría verificada (log/tabla).
