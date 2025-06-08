@echo off
REM Build y push de todas las imágenes Docker

REM Frontend
cd frontend
docker build -t duvanvergara/frontend:latest .
docker push duvanvergara/frontend:latest
cd ..

REM Config Service
cd backend\config-service
docker build -t duvanvergara/config-service:latest .
docker push duvanvergara/config-service:latest
cd ../..

REM Eureka Service
cd backend\eureka-service
docker build -t duvanvergara/eureka-service:latest .
docker push duvanvergara/eureka-service:latest
cd ../..

REM Gateway Service
cd backend\gateway-service
docker build -t duvanvergara/gateway-service:latest .
docker push duvanvergara/gateway-service:latest
cd ../..

REM Microservicios m1 a m7
for %%i in (m1 m2 m3 m4 m5 m6 m7) do (
    cd backend\%%i
    docker build -t duvanvergara/%%i:latest .
    docker push duvanvergara/%%i:latest
    cd ../..
)
echo Build y push completados.
pause