@echo off
setlocal enabledelayedexpansion

REM Preguntar si se desea limpiar antes de desplegar
kubectl config use-context docker-desktop
set /p CLEAN="¿Deseas limpiar todos los recursos de Kubernetes antes de desplegar? (S/N): "
if /I "%CLEAN%"=="S" (
    call clean_k8s.bat
)

REM Aplica todos los YAML de deployment y servicios

cd deployment

REM ConfigMaps y Secrets
kubectl apply -f mysql-config-map.yaml
kubectl apply -f mysql-root-secret.yaml
kubectl apply -f mysql-user-secret.yaml

REM Bases de datos y PVCs
kubectl apply -f mysql-deployment.yaml
kubectl apply -f m1-db-deployment-service.yaml
kubectl apply -f m2-db-deployment-service.yaml
kubectl apply -f m3-db-deployment-service.yaml
kubectl apply -f m4-db-deployment-service.yaml
kubectl apply -f m5-db-deployment-service.yaml

REM Config Service
kubectl apply -f config-service-deployment-service.yaml

REM Esperar a que el config-service esté listo
echo Esperando a que config-service esté listo...
kubectl wait --for=condition=ready pod -l app=config-service-deployment --timeout=300s
if errorlevel 1 (
    echo Error: config-service no está listo después de 5 minutos.
    pause
    exit /b 1
)

REM Eureka Service
kubectl apply -f eureka-service-deployment-service.yaml

REM Esperar a que el eureka-service esté listo
echo Esperando a que eureka-service esté listo...
kubectl wait --for=condition=ready pod -l app=eureka-service-deployment --timeout=300s
if errorlevel 1 (
    echo Error: eureka-service no está listo después de 5 minutos.
    pause
    exit /b 1
)

REM Verificar si Eureka pudo conectarse al Config Service
echo Verificando conexión de eureka-service al Config Service...
set EUREKA_POD_NAME=
for /f "tokens=*" %%i in ('kubectl get pods -l app=eureka-service-deployment -o jsonpath="{.items[0].metadata.name}"') do set EUREKA_POD_NAME=%%i

kubectl logs %EUREKA_POD_NAME% | findstr "Connection refused"
if errorlevel 0 (
    echo Error: Eureka no pudo conectarse al Config Service.
    echo Reiniciando eureka-service...
    kubectl rollout restart deployment eureka-service-deployment
    echo Esperando a que eureka-service se reinicie...
    timeout /t 20 >nul
    kubectl wait --for=condition=ready pod -l app=eureka-service-deployment --timeout=300s
    if errorlevel 1 (
        echo Error: eureka-service sigue sin estar listo después del reinicio.
        pause
        exit /b 1
    )
)

REM Gateway Service
kubectl apply -f gateway-service-deployment-service.yaml

REM Microservicios funcionales
kubectl apply -f m1-deployment.yaml
kubectl apply -f m2-deployment.yaml
kubectl apply -f m3-deployment.yaml
kubectl apply -f m4-deployment.yaml
kubectl apply -f m5-deployment.yaml
kubectl apply -f m6-deployment.yaml
kubectl apply -f m7-deployment.yaml

REM Frontend
kubectl apply -f frotend-deployment.yaml

cd ..
echo Deployments aplicados.

REM =========================
REM Poblar automáticamente las bases de datos m1 a m5
REM =========================

REM Crear ConfigMaps con los scripts SQL
kubectl create configmap m1-db-sql --from-file=m1-db.mysql.sql=deployment/m1-db.mysql.sql --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap m2-db-sql --from-file=m2-db.mysql.sql=deployment/m2-db.mysql.sql --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap m3-db-sql --from-file=m3-db.mysql.sql=deployment/m3-db.mysql.sql --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap m4-db-sql --from-file=m4-db.mysql.sql=deployment/m4-db.mysql.sql --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap m5-db-sql --from-file=m5-db.mysql.sql=deployment/m5-db.mysql.sql --dry-run=client -o yaml | kubectl apply -f -

REM Aplicar los Jobs para poblar las bases
kubectl apply -f deployment/m1-db-populate-job.yaml
kubectl apply -f deployment/m2-db-populate-job.yaml
kubectl apply -f deployment/m3-db-populate-job.yaml
kubectl apply -f deployment/m4-db-populate-job.yaml
kubectl apply -f deployment/m5-db-populate-job.yaml

pause