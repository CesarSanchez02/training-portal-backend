**EduMaster System – Backend (Spring Boot)
Descripción**

El backend de EduMaster System gestiona la lógica de negocio, seguridad, persistencia de datos y envío de notificaciones del portal de capacitaciones.
Fue desarrollado con Spring Boot 3, utilizando JWT para autenticación, MySQL como base de datos y SendGrid para correos automáticos.

**Características principales**

Autenticación con JWT (registro y login de usuarios).

CRUD de cursos y usuarios.

Envío de correos automáticos con SendGrid.

Roles y control de acceso (Admin / Usuario).

Historial de cursos completados.

Integración con MinIO para almacenamiento de archivos.

**Tecnologías**
Componente	Tecnología
Lenguaje	Java 24
Framework	Spring Boot 3.x
ORM	Spring Data JPA (Hibernate)
Base de datos	MySQL 8
Seguridad	JWT Authentication
Correo	SendGrid API
Almacenamiento	MinIO
Testing	JUnit, Mockito
Build	Maven

**Configuración del entorno**

Clonar el repositorio
```bash
git clone https://github.com/CesarSanchez02/training-portal-backend.git
cd training-portal-backend
```


**Configurar la base de datos**
Crear una base de datos en MySQL:


```bash
CREATE DATABASE training_portal;
```

Importar el archivo SQL del repositorio **training-portal-bd.**

Configurar **application.properties**

Ejecutar el servidor

```bash
mvn spring-boot:run
```


El backend estará disponible en:
```bash
http://localhost:8080
```
**Endpoints principales**

**Método	Endpoint	Descripción**
POST	/api/auth/login	Autenticación de usuario
POST	/api/users/register	Registro de usuario
GET	/api/courses	Listar cursos disponibles
POST	/api/courses	Crear nuevo curso (Admin)
GET	/api/users/{id}/progress	Consultar progreso e insignias

**Datos de prueba
Usuario	Rol	Correo	Contraseña**
admin	ROLE_ADMIN	cesar@empresa.comm 123456
user1	ROLE_USER	cesar1@banco.com 123456

**Autor**
César Andrés Sánchez
📧 cesar.ssanchez02@gmail.com

🌐 GitHub
