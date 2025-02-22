ionic build
npx cap sync
npx cap copy android
ionic capacitor run android -l --external



--

Nueva verison:

cd 2024-reurjc 
docker build -t jesussmariscal/reurjc -f ./Docker/App/Dockerfile .
docker push jesussmariscal/reurjc
docker build -t jesussmariscal/mymoodle -f ./Docker/Moodle/Dockerfile .
docker push jesussmariscal/mymoodle
cd Microservice/Asistance
docker build -t jesussmariscal/attendance -f ./Microservice/Asistance/Dockerfile  .  

Construirla:
cd ../../
 cd Docker/App 
 docker-compose -p myurjc up -d

 Si moodle deja de responder:

 docker exec -it moodle bash
chown -R www-data:www-data /var/www/html/moodledata
chmod -R 0770 /var/www/html/moodledata




