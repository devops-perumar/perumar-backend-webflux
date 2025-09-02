# 🤝 Contribuir al Proyecto PERÚ MAR

Gracias por tu interés en contribuir al sistema de gestión de matrículas PERÚ MAR.  
Este documento resume las **reglas y estándares de contribución** que todo desarrollador debe seguir.  

---

## 📂 Estructura de Repositorios
- **Backend**: `perumar-backend` (Java Spring WebFlux)  
- **Frontend**: `perumar-frontend-react` (React Vite + Bootstrap)  

Cada repositorio mantiene su propio ciclo de versionado y despliegue.  

---

## 🌳 Rama Principal
- La rama principal es **`main`**.  
- Está **protegida** y requiere:  
  - Pull Request antes de merge.  
  - Al menos **1 aprobación**.  
  - Status checks (CI/CD) exitosos antes de merge.  

---

## 🏷️ Versionado (SemVer)
Usamos **Semantic Versioning (SemVer)**:  
```
MAJOR.MINOR.PATCH
```
- **MAJOR**: cambios incompatibles.  
- **MINOR**: nuevas funcionalidades compatibles.  
- **PATCH**: correcciones o mejoras menores.  

Ejemplo: `v0.1.0`  

Cada versión liberada debe estar **taggeada**:  
```bash
git tag -a vX.Y.Z -m "Descripción"
git push origin vX.Y.Z
```

---

## 📝 Convención de Commits
Formato:  
```
<tipo>(alcance opcional): descripción corta
```

### Tipos permitidos
- `feat`: nueva funcionalidad  
- `fix`: corrección de errores  
- `chore`: tareas internas  
- `docs`: cambios en documentación  
- `refactor`: cambios que no alteran funcionalidad  
- `test`: añadir o modificar pruebas  

### Ejemplos
```
feat(alumno): agregar validación de DNI
fix(carrera): corregir error en listado de materias
chore(ci): añadir pipeline básico en GitHub Actions
```

---

## 🔄 Flujo de Trabajo
1. Crear ramas `feature/` para nuevas funcionalidades.  
   Ejemplo: `feature/carreras-crud`  
2. Crear ramas `fix/` para correcciones.  
   Ejemplo: `fix/carrera-listado-null`  
3. Abrir un **Pull Request (PR)** hacia `main` cuando el cambio esté probado.  
4. Esperar al menos **1 aprobación** antes de merge.  
5. Eliminar ramas después de hacer merge.  

---

## ✅ Buenas Prácticas
- Asegúrate de que los tests pasen antes de abrir PR.  
- Documenta cualquier cambio en **README.md** o en la wiki del proyecto.  
- Sigue la estructura modular definida (feature-based).  

---

### Breadcrumbs (UI)

- Componente: `src/app/navigation/Breadcrumbs.tsx`.
- Fuente de verdad: `src/routes/breadcrumbs.ts` (cada ruta define `{ label, perm? }`).
- ACL: Los ítems intermedios solo son link si `useAcl.can(perm)` es `true`. El último ítem NUNCA es link.
- Integración: Se renderiza globalmente en `src/app/layout/Layout.tsx`, debajo del navbar.
- Accesibilidad: `aria-label="breadcrumb"` y `aria-current="page"` para el último ítem.
- Fallback: Si una ruta no está mapeada, se capitalizan los segmentos (`/foo/bar` → `Home > Foo > Bar`).
- Tests: `src/app/navigation/__tests__/Breadcrumbs.*.test.tsx`. Ejecutar `pnpm test` o `npx vitest run src/app/navigation/__tests__/Breadcrumbs.*`.
