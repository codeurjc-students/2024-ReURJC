# Etapa 1: Construcción del frontend
FROM node:20.12.2 AS frontend

WORKDIR /app

# Copiar los archivos necesarios para instalar dependencias
COPY ./Frontend ./

# Instalar dependencias y CLI de Angular e Ionic
RUN npm install -g @angular/cli @ionic/cli 
RUN npm config set @capawesome-team:registry https://npm.registry.capawesome.io
RUN npm config set //npm.registry.capawesome.io/:_authToken POLAR-ED6832C2-542C-4C72-A029-7226BA937FC1
RUN npm install

# Construir la aplicación Angular/Ionic
RUN ionic build --prod

# Etapa 2: Construcción del backend
FROM maven:3.8.4-openjdk-17 AS backend

WORKDIR /app

ENV RUNNING_IN_DOCKER=true

# Copiar archivos necesarios para la construcción
COPY ./Backend/pom.xml ./
COPY ./Backend/src ./src

# Copiar el keystore a una ubicación temporal
COPY ./Backend/src/main/resources/keystore.p12 /app/temp/keystore.p12

# Crear la carpeta "public" y copiar los archivos generados del frontend
RUN mkdir -p /app/src/main/resources/public
COPY --from=frontend /app/www /app/src/main/resources/public

# Construir la aplicación Spring Boot
RUN mvn clean install -DskipTests

# Etapa 3: Imagen final
FROM amazoncorretto:17 AS final

WORKDIR /app

# Copiar el archivo JAR generado al contenedor
COPY --from=backend /app/target/*.jar /app/app.jar

# Copiar el keystore desde la ubicación temporal a la ubicación final
COPY --from=backend /app/temp/keystore.p12 /app/certs/keystore.p12

# Descargar e incluir el script wait-for-it.sh
RUN curl -LJO https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && chmod +x /app/wait-for-it.sh

# Exponer el puerto HTTPS
EXPOSE 8443

ENTRYPOINT ["/app/wait-for-it.sh", "moodle-app:80", "--", "java", "-jar", "/app/app.jar"]