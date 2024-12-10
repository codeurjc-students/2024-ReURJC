FROM node:20.12.2 as frontend

WORKDIR /MyURJC

# Copiar los archivos necesarios para instalar dependencias
COPY /MyURJC/package.json /MyURJC/package-lock.json /MyURJC/angular.json /MyURJC/tsconfig.app.json /MyURJC/tsconfig.json /MyURJC/tsconfig.spec.json /MyURJC/

# Instalar dependencias y CLI de Angular e Ionic
RUN npm install -g @angular/cli @ionic/cli 

RUN npm config set @capawesome-team:registry https://npm.registry.capawesome.io
RUN npm config set //npm.registry.capawesome.io/:_authToken POLAR-ED6832C2-542C-4C72-A029-7226BA937FC1

RUN npm install 

ENTRYPOINT [“ionic”]
CMD [“serve”, “8100”]

# Copiar los archivos fuente del proyecto Ionic
COPY MyURJC/src /MyURJC/src

# Construir la aplicación Ionic
RUN ionic init MyURJC -y --name MyURJC
# El flag `-y` omite las preguntas interactivas y utiliza los valores predeterminados.

RUN ionic build --prod

# Copiar los archivos generados del frontend
COPY MyURJC/www /MyURJC/dist

# Etapa de construcción del backend
FROM maven:3.8.4-openjdk-17 as build

WORKDIR /app

ENV RUNNING_IN_DOCKER=true

COPY pom.xml /app/
COPY src/ /app/src

# Crear la carpeta "public"
RUN mkdir -p /app/src/main/resources/public 

# Copiar los archivos del frontend al backend
COPY --from=frontend /MyURJC/www /app/src/main/resources/public

# Construir la aplicación Spring Boot
RUN mvn clean install -DskipTests

# Etapa final del contenedor
FROM amazoncorretto:17

WORKDIR /app

# Agregar el archivo JAR generado al contenedor
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app

# Descargar e incluir el script wait-for-it.sh
RUN curl -LJO https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && chmod +x /app/wait-for-it.sh
