@echo off
REM ---------------------------------------------------------------
REM  Copia el .jar del back end al repositorio local de este proyecto.
REM
REM  Antes: en NetBeans, en el proyecto biblioteca-backend, haz
REM         "Clean and Build" (genera target\biblioteca-backend-1.0.jar).
REM  Requisito: las carpetas biblioteca-backend y biblioteca-frontend-bd
REM         deben estar una junto a la otra.
REM  Despues: en NetBeans, clic derecho al proyecto del front end >
REM         "Reload POM" (o cierra y abre el proyecto) y haz Clean and Build.
REM ---------------------------------------------------------------
set ORIGEN=..\biblioteca-backend\target\biblioteca-backend-1.0.jar
set DESTINO=repo\com\biblioteca\biblioteca-backend\1.0\biblioteca-backend-1.0.jar

if not exist "%ORIGEN%" (
    echo No se encontro %ORIGEN%
    echo Primero haz Clean and Build en el proyecto biblioteca-backend.
    pause
    exit /b 1
)

copy /Y "%ORIGEN%" "%DESTINO%"

REM Maven guarda una copia en su cache; se borra para que tome la nueva
if exist "%USERPROFILE%\.m2\repository\com\biblioteca\biblioteca-backend" (
    rmdir /S /Q "%USERPROFILE%\.m2\repository\com\biblioteca\biblioteca-backend"
)

echo.
echo Listo: back end actualizado. Ahora haz Reload POM y Clean and Build en NetBeans.
pause
