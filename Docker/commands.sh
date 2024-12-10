#Situarte en el directorio con dockerfile
#Borrar la caché
docker builder prune --all --force

#Reconstruir la imagen
docker build -t jesussmariscal/reurjc .

#Subirla
docker push jesussmariscal/reurjc


#Construirla
 docker-compose -p myurjc up -d

#logs
docker logs myurjc     

#Para abrir el frontend en un dispositivo android
npx cap sync
npx cap copy android
npx cap open android


