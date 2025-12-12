# 📘 API de Gestión de Prácticas Profesionales

Este proyecto corresponde a un sistema backend desarrollado para un colegio técnico profesional, con el objetivo de gestionar las prácticas profesionales de estudiantes egresados.
La API permite registrar, consultar, actualizar y desactivar prácticas según el perfil de usuario (estudiante o profesor).

---

## 🚀 Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- Lombok
- PostgreSQL
- Postman (para pruebas)
- VS Code
- ChatGPT y GitHub Copilot como herramientas de apoyo en depuración y redacción.

---

## 🧩 Funcionalidades principales
### 👩‍🎓 Rol Estudiante
- Crear una práctica propia
- Ver sus prácticas
- No puede modificar ni desactivar prácticas

### 👨‍🏫 Rol Profesor
- Crear prácticas para cualquier estudiante
- Actualizar cualquier práctica
- Desactivar (eliminar lógicamente) prácticas
- Consultar prácticas de cualquier estudiante

El rol se define mediante un **header** en cada request:

---

## 🗃️ Estructura de la Base de Datos

La base de datos contiene tres tablas principales:

### **Estudiante**
- id  
- nombre  
- carrera  
- email  

### **Profesor**
- id  
- nombre  
- email  
- telefono  

### **Practica**
- id  
- fechaini  
- fechafin  
- empresa  
- jefe_practica  
- descripcion  
- estudiante_id (FK)  
- profesor_id (FK)  
- activo (boolean) → para eliminación lógica  

Todas las relaciones se manejan mediante claves foráneas y JPA/Hibernate.

---

## 🧪 Pruebas con Postman  
Se preparó una colección con todos los endpoints.  
En Postman se envían los roles mediante headers para validar comportamiento, acceso y restricciones de permisos.

Pruebas realizadas:
- Crear prácticas (estudiante y profesor)
- Listar por rol
- Consultar práctica por ID
- Actualizar prácticas (sólo profesor)
- Eliminar lógicamente (sólo profesor)
- Validación de errores (IDs inexistentes, accesos no permitidos, etc.)

---

## 📂 Endpoints principales

### 🔍 **Listar prácticas**
- **GET /api/listar**  
  Responde según el rol (estudiante o profesor).

### 📄 **Obtener práctica por ID**
- **GET /api/listar/{id}**

### ➕ **Crear práctica**
- **POST /api/crear**

### ✏️ **Actualizar práctica (solo profesor)**
- **PUT /api/actualizar/{id}**

### 🗑️ **Eliminar lógica (solo profesor)**
- **DELETE /api/eliminar/{id}**

---

## 🗑️ Eliminación lógica

En lugar de borrar registros, la API marca la práctica como **inactiva** mediante el campo:

```java
private boolean activo = true;

```
---

## 🗄️ Base de datos

Las tablas principales utilizadas son:

- estudiantes
- profesores
- practicas

La columna `activo` en la tabla `practicas` permite implementar eliminación lógica.
