# biblioteca-frontend-bd

Front end (Swing) y base de datos de la Biblioteca Inteligente (Programación Orientada a Eventos).
La lógica del sistema está en el repositorio `biblioteca-backend` y se usa como librería
(un .jar guardado en la carpeta `repo/`, que funciona como repositorio local de Maven).

Proyecto **Maven** (Java 21). Se abre en NetBeans con **File > Open Project**.

## Contenido

```
basedatos/
 ├─ biblioteca.sql        Crea la base de datos y las tablas
 └─ datos_prueba.sql      Libros, usuarios y préstamos de ejemplo
repo/                        Repositorio local de Maven con el back end ya empaquetado
 └─ com/biblioteca/biblioteca-backend/1.0/biblioteca-backend-1.0.jar
actualizar-backend.bat       Copia el .jar nuevo del back end a repo/ (Windows)
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

Cuando el back end cambie:
1. En `biblioteca-backend`: **Clean and Build** (genera `target/biblioteca-backend-1.0.jar`).
2. Aquí: doble clic a `actualizar-backend.bat` (las dos carpetas deben estar una junto a la otra).
3. En NetBeans: **Reload POM** y **Clean and Build** en este proyecto.
4. Subir el cambio (el `.jar` de `repo/` va al repositorio).

## Integrantes

Leon Gamez Fernando
Camacho Guerra Jacobo
Garcia Rosales Karen Nicol
