@echo off
REM Compilar todos los microservicios Java (backend) de una sola vez

cd backend\config-service
call mvn clean package -DskipTests
cd ../..

cd backend\eureka-service
call mvn clean package -DskipTests
cd ../..

cd backend\gateway-service
call mvn clean package -DskipTests
cd ../..

for %%i in (m1 m2 m3 m4 m5 m6 m7) do (
    cd backend\%%i
    call mvn clean package -DskipTests
    cd ../..
)

echo Todos los JARs han sido generados.
pause