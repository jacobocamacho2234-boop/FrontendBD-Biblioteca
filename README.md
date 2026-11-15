# biblioteca-frontend-bd

Front end (Swing) y base de datos de la Biblioteca Inteligente (Programación Orientada a Eventos).
La lógica del sistema está en el repositorio `biblioteca-backend` y se usa como librería (`lib/biblioteca-backend.jar`).

Proyecto **Maven** (Java 21). Se abre en NetBeans con **File > Open Project**.

## Contenido

```
basedatos/
 ├─ biblioteca.sql        Crea la base de datos y las tablas
 └─ datos_prueba.sql      Libros, usuarios y préstamos de ejemplo
lib/
 └─ biblioteca-backend.jar   Back end ya empaquetado
src/main/java/vista/
 ├─ VentanaPrincipal.java    Ventana con pestañas (Catálogo, Préstamos, Alertas y multas, Usuarios)
 └─ DialogoLibro.java        Diálogo para crear / editar libros
conexion.properties.ejemplo  plantilla de conexión (copiar como conexion.properties)
```

## Cómo ejecutarlo

1. **Base de datos:** en MariaDB/HeidiSQL ejecutar `basedatos/biblioteca.sql` y después `basedatos/datos_prueba.sql`.
2. Copiar `conexion.properties.ejemplo` como `conexion.properties` y poner usuario y contraseña de MariaDB
   (ese archivo no se sube a GitHub).
3. NetBeans: **File > Open Project** > esta carpeta. Maven descarga el driver de MariaDB.
4. Clic derecho al proyecto > **Run** (abre `vista.VentanaPrincipal`).

## Actualizar el back end

Cuando el back end cambie: en `biblioteca-backend` hacer **Clean and Build**, copiar
`target/biblioteca-backend-1.0.jar` aquí como `lib/biblioteca-backend.jar` (reemplazando el anterior)
y subir el cambio.

## Integrantes

(Nombres del equipo)
