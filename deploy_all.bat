@echo off
REM Aplica todos los YAML de deployment y servicios

cd deployment

REM ConfigMaps y Secrets
kubectl apply -f mysql-config-map.yaml
kubectl apply -f mysql-user-secret.yaml
REM Si tienes root-secret, descomenta la siguiente línea
REM kubectl apply -f mysql-root-secret.yaml

REM Bases de datos y PVCs
kubectl apply -f mysql-deployment.yaml
kubectl apply -f m1-db-deployment-service.yaml
kubectl apply -f m2-db-deployment-service.yaml
kubectl apply -f m3-db-deployment-service.yaml
kubectl apply -f m4-db-deployment-service.yaml
kubectl apply -f m5-db-deployment-service.yaml

REM Config, Eureka, Gateway
kubectl apply -f config-service-deployment-service.yaml
kubectl apply -f eureka-service-deployment-service.yaml
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