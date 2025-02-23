ionic build
npx cap sync
npx cap copy android
ionic capacitor run android -l --external



--

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




