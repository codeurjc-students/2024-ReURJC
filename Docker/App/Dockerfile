# Etapa 1: Construcción del frontend
FROM node:20.12.2 AS frontend

WORKDIR /app

COPY ./Frontend ./

RUN npm install -g @angular/cli @ionic/cli 
RUN npm config set @capawesome-team:registry https://npm.registry.capawesome.io
RUN npm config set //npm.registry.capawesome.io/:_authToken POLAR-ED6832C2-542C-4C72-A029-7226BA937FC1
RUN npm install

RUN ionic build --prod

# Etapa 2: Construcción del backend
FROM maven:3.8.4-openjdk-17 AS backend

WORKDIR /app

ENV RUNNING_IN_DOCKER=true

COPY ./Backend/pom.xml ./
COPY ./Backend/src ./src

RUN mkdir -p /app/src/main/resources/public
COPY --from=frontend /app/www /app/src/main/resources/public

RUN mvn clean install -DskipTests

# Etapa 3: Imagen final
FROM amazoncorretto:17 AS final

ARG FCM_PRIVATE_KEY

WORKDIR /app

COPY --from=backend /app/target/*.jar /app/app.jar
COPY ./Backend/src/main/resources/keystore.p12 /app/certs/keystore.p12

RUN curl -LJO https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && chmod +x /app/wait-for-it.sh

ENTRYPOINT ["/bin/sh", "-c", "echo \"$FCM_PRIVATE_KEY\" > /app/firebase-service-account.json && /app/wait-for-it.sh moodle-app:80 -- java -jar /app/app.jar"]

# Exponer el puerto HTTPS
EXPOSE 8443
