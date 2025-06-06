ionic build
npx cap sync
npx cap copy android
ionic capacitor run android -l --external

--
Subir imagenes al repo de gcp: 

docker build --platform linux/amd64 -t gcr.io/tfgurjc-e9e62/reurjc:latest -f ./Docker/App/Dockerfile .
docker push gcr.io/tfgurjc-e9e62/reurjc:latest

Nueva verison:



docker build -t jesussmariscal/reurjc -f ./Docker/App/Dockerfile .
docker push jesussmariscal/reurjc
docker build -t jesussmariscal/mymoodle -f ./Docker/Moodle/Dockerfile .
docker push jesussmariscal/mymoodle
cd Microservice/Asistance
docker build -t jesussmariscal/attendance -f ./Dockerfile  .
docker push jesussmariscal/attendance
cd ../../
cd Microservice/Notifications
docker build -t jesussmariscal/notifications -f ./Dockerfile  . 
docker push jesussmariscal/notifications

Construirla:
cd ../../
 cd Docker/App 
 docker-compose -p myurjc up -d

 Si moodle deja de responder:

 docker exec -it moodle bash
chown -R www-data:www-data /var/www/html/moodledata
chmod -R 0770 /var/www/html/moodledata

--

Comandos Kubernetes:

gcloud auth login
cd 2024-ReURJC/
gcloud container clusters create myurjc-cluster \
    --num-nodes=2 \
    --machine-type=e2-medium \
    --zone=us-central1-a
gcloud container clusters get-credentials myurjc-cluster --zone us-central1-a

kubectl create secret generic firebase-secret --from-file=firebase-service-account.json=./Backend/src/main/resources/firebase-service-account.json
kubectl create secret generic keystore-secret --from-file=keystore.p12=./Backend/src/main/resources/keystore.p12
cd k8s-manifests 
kubectl apply -f myurjc-db-deployment.yaml
kubectl apply -f moodle-db-deployment.yaml
kubectl apply -f attendance-db-deployment.yaml
kubectl apply -f notifications-db-deployment.yaml

- Esperar a que se pongan en running
kubectl get pods

sleep 50
kubectl apply -f moodle-app-deployment.yaml
kubectl apply -f attendance-service-deployment.yaml
kubectl apply -f notifications-service-deployment.yaml
sleep 50

- esperar a que se pongan en running:
kubectl get pods

kubectl apply -f myurjc-app-deployment.yaml


- obtenedlasr la ip de la app: 
sleep 50
kubectl get services myurjc-app-service
kubectl get svc moodle-app-service

- Actualiza WWWROOT De moodle con la ip externa

kubectl apply -f moodle-app-deployment.yaml

- Actualiza el WebSocketconfig.Java y el web-socket-service.js con la ip pública de myurjc
cd ../
docker build --platform linux/amd64 -t gcr.io/tfgurjc-e9e62/reurjc:latest -f ./Docker/App/Dockerfile .
docker push gcr.io/tfgurjc-e9e62/reurjc:latest

- Borrar el pod de MyURJC

- Borrar todo: 

kubectl delete deployment --all -n default
kubectl delete pods --all -n default





