@echo off
echo Eliminando todos los contenedores detenidos...
docker container prune -f

echo Eliminando todos los contenedores (detenidos y corriendo)...
for /f "tokens=*" %%i in ('docker ps -aq') do docker rm -f %%i

echo Eliminando todas las imágenes...
for /f "tokens=*" %%i in ('docker images -q') do docker rmi -f %%i

echo Eliminando todos los volúmenes no usados...
docker volume prune -f

echo Limpieza de Docker completada.
pause