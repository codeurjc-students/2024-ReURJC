# 2024-ReURJC

## Nombre de la aplicación web
- **MyURJC**

## Descripción de la aplicación web
- Aplicación para el personal docente, administrativo y estudiantil de la Universidad Rey Juan Carlos que sirve para acceder a los servicios ofertados por esta.

## Cómo construir las imágenes (para Docker Hub - Opcional)

1. Situarse en el directorio raíz de este repositorio.
2. Tener Docker abierto.
3. Ejecutar: `docker build -t <tuUsuario>/reurjc -f ./Docker/App/Dockerfile .`
4. Ejecutar: `docker push <tuUsuario>/reurjc`
5. Ejecutar: `docker build -t <tuUsuario>/mymoodle -f ./Docker/Moodle/Dockerfile .`
6. Ejecutar: `docker push <tuUsuario>/mymoodle`

**(Nota: Los demás microservicios siguen un patrón de construcción similar)**

## Cómo ejecutar la app (Entorno Local con Docker Compose)

1. Ejecutar Docker.
2. Situarse en el directorio raíz del repositorio y ejecutar: `cd Docker/App`
3. Ejecutar: `docker-compose -p myurjc up -d`

### Si deseas ejecutar la app en Android:

1. Asegurarse de tener instalado Android Studio.
2. Situarse en el directorio `Frontend` de este repositorio: `cd Frontend/`
3. Ejecutar: `ionic build`
4. Ejecutar: `npx cap sync`
5. Ejecutar: `npx cap copy android`
6. Ejecutar: `npx cap run android`

### Si deseas acceder al aula virtual (Moodle) en local:

1. Ejecutar `localhost`, se te redirigirá automáticamente.
2. Llevar a cabo los pasos de configuración cuando se entra por primera vez.
3. Ir a **Site Administration > Server > Web Services > Manage protocols**.
4. Activar todos los protocolos y guardar cambios.

**Nota:** La correspondencia entre cursos y usuarios entre la app y el aula virtual es su ID. Para la comunicación entre el plugin de Moodle y los microservicios en el entorno de Docker Compose, el archivo `observer.php` del plugin `myplugin` está configurado para apuntar al nombre del contenedor `notifications`. Para un despliegue en Kubernetes, esta URL tendría que ser modificada para apuntar al nombre del servicio de Kubernetes correspondiente.

## Prerrequisitos para el Despliegue en Kubernetes

Antes de aplicar los manifiestos de Kubernetes, es necesario construir todas las imágenes Docker de la aplicación y subirlas a Google Container Registry (GCR).

1.  **Habilitar servicios y configurar la autenticación (si es la primera vez):**
    Asegúrate de haberte autenticado y configurado tu proyecto de GCP:
    ```bash
    gcloud auth login
    gcloud config set project tfgurjc-e9e62 # Reemplaza con tu ID de Proyecto si es diferente
    ```
    Habilita las APIs necesarias:
    ```bash
    gcloud services enable container.googleapis.com containerregistry.googleapis.com
    ```

2.  **Configurar la Autenticación de Docker:**
    Este comando configura el cliente de Docker para que pueda autenticarse con GCR. Es un paso fundamental antes de poder subir imágenes.
    ```bash
    gcloud auth configure-docker gcr.io
    ```

3.  **Construir y Subir todas las imágenes a Google Container Registry (GCR):**
    A continuación, se muestran los comandos para construir cada imagen con la plataforma `linux/amd64`, etiquetarla correctamente para `gcr.io` y subirla. **Ejecuta estos comandos desde el directorio raíz del proyecto (`2024-ReURJC/`).**

    * **Aplicación Principal (ReURJC):**
        ```bash
        docker build --platform linux/amd64 -t gcr.io/tfgurjc-e9e62/reurjc:latest -f ./Docker/App/Dockerfile .
        docker push gcr.io/tfgurjc-e9e62/reurjc:latest
        ```

    * **Moodle:**
        ```bash
        docker build --platform linux/amd64 -t gcr.io/tfgurjc-e9e62/mymoodle:latest -f ./Docker/Moodle/Dockerfile .
        docker push gcr.io/tfgurjc-e9e62/mymoodle:latest
        ```

    * **Microservicio de Asistencias:**
        ```bash
        docker build --platform linux/amd64 -t gcr.io/tfgurjc-e9e62/attendance:latest -f ./Microservice/Asistance/Dockerfile .
        docker push gcr.io/tfgurjc-e9e62/attendance:latest
        ```

    * **Microservicio de Notificaciones:**
        ```bash
        docker build --platform linux/amd64 -t gcr.io/tfgurjc-e9e62/notifications:latest -f ./Microservice/Notifications/Dockerfile .
        docker push gcr.io/tfgurjc-e9e62/notifications:latest
        ```

## Despliegue en Kubernetes (Google Cloud Platform)

Una vez que todas las imágenes estén subidas a GCR, puedes proceder con el despliegue:

1.  **Crear el clúster de GKE (si no se ha hecho):**
    ```bash
    cd 2024-ReURJC/
    gcloud container clusters create myurjc-cluster \
        --num-nodes=2 \
        --machine-type=e2-medium \
        --zone=us-central1-a
    ```
2.  **Obtener las credenciales del clúster:**
    ```bash
    gcloud container clusters get-credentials myurjc-cluster --zone us-central1-a
    ```
3.  **Crear los secretos necesarios:** (Desde el directorio raíz del repositorio)
    ```bash
    kubectl create secret generic firebase-secret --from-file=firebase-service-account.json=./Backend/src/main/resources/firebase-service-account.json
    kubectl create secret generic keystore-secret --from-file=keystore.p12=./Backend/src/main/resources/keystore.p12
    ```
4.  **Aplicar los manifiestos de Kubernetes en orden:**
    ```bash
    cd k8s-manifests 

    # 1. Desplegar todas las bases de datos primero
    kubectl apply -f myurjc-db-deployment.yaml
    kubectl apply -f moodle-db-deployment.yaml
    kubectl apply -f attendance-db-deployment.yaml
    kubectl apply -f notifications-db-deployment.yaml

    # Esperar a que las bases de datos estén en estado 'Running'
    echo "Esperando a que las bases de datos se inicien..."
    kubectl get pods -w
    # (Pulsar Ctrl+C cuando todos los pods de las BBDD estén en estado 'Running')

    # 2. Desplegar los servicios dependientes
    kubectl apply -f moodle-app-deployment.yaml
    kubectl apply -f attendance-service-deployment.yaml
    kubectl apply -f notifications-service-deployment.yaml

    # Esperar a que estos servicios se inicien
    echo "Esperando a que los servicios intermedios se inicien..."
    kubectl get pods -w
    # (Pulsar Ctrl+C cuando los pods estén en estado 'Running')

    # 3. Desplegar la aplicación principal
    kubectl apply -f myurjc-app-deployment.yaml
    ```
5.  **Obtener IPs externas y actualizar configuraciones:**
    ```bash
    # Esperar un poco a que los LoadBalancers asignen las IPs
    sleep 60 

    # Obtener IPs externas
    kubectl get services myurjc-app-service
    kubectl get svc moodle-app-service

    # NOTA MUY IMPORTANTE: Se deben actualizar manualmente los siguientes archivos con las IPs obtenidas y los nombres de servicio de Kubernetes:
    #   - Moodle: Actualizar la variable MOODLE_WWWROOT en `moodle-app-deployment.yaml` con la IP externa de `moodle-app-service` y reaplicar con `kubectl apply -f moodle-app-deployment.yaml`.
    #   - Notificaciones y WebSockets: Actualizar las URLs en los siguientes archivos para que apunten a los servicios de Kubernetes.
    #       - `observer.php` (en el plugin de Moodle).
    #       - `WebSocketConfig.java` (en el backend).
    #       - `web-socket-service.ts` (en el frontend).
    #     En el entorno de Kubernetes, estas URLs deben apuntar a los nombres de servicio de Kubernetes (ej. `http://notifications-service-service:8082`), mientras que para el entorno local de Docker Compose, apuntan a los nombres de contenedor (ej. `http://notifications:8082`).
    #
    #     Una vez actualizados, es necesario reconstruir y subir las imágenes Docker correspondientes y reiniciar los pods de Kubernetes para que tomen los cambios.
    ```

## Documentación API

Una vez inicializada la App, acceder al [siguiente enlace](http://localhost:8080/swagger-ui/index.html)

## Usuarios creados

A continuación, se detallan los usuarios que se crean por defecto y sus roles correspondientes:

| Nombre      | Apellido | Segundo Apellido |         DNI         |             Email             | Contraseña (sin cifrar) |    Roles     |
| :---------- | :------- | :-------------- | :-----------------: | :---------------------------: | :---------------------: | :----------: |
| John        | Doe      | Smith           |      12345678A      | mariscalalonso16@icloud.com  |           123           |     USER     |
| Jane        | Doe      | Smith           |      87654321B      | yaovi@icloud.com             |           123           |     USER     |
| Lolazo      | Doe      | Smith           |      87654321B      | yaovi123@icloud.com          |           123           |   TEACHER    |
| Lolazo123   | Doe      | Smith           |      87654321B      | yaovi1234@icloud.com         |           123           |    ADMIN     |

## Diagrama de base de datos

El diagrama se puede encontrar haciendo clic [aquí](https://github.com/codeurjc-students/2024-ReURJC/blob/main/DB_Schema.pdf).

### Entidades

- **User**: Esta clase representa a los usuarios de la aplicación y contiene toda su información relevante, como los tokens de los dispositivos sincronizados, sus datos personales y su rol dentro de la universidad.
- **Subject**: Esta clase representa las asignaturas de la universidad. Utiliza tablas intermedias para aportar información relevante como las convocatorias, los usuarios que pertenecen a las asignaturas, el horario y las notas de las diferentes tareas.
- **Convocatory**: Representa las convocatorias de los exámenes finales de las asignaturas.
- **Schedule**: El horario en el que se imparte una asignatura.
- **Attendance**: Entidad utilizada para crear los eventos de asistencia. También emplea tablas intermedias para registrar los usuarios que han asistido y la asignatura a la que pertenece el evento.
- **Notification**: Entidad encargada de almacenar los datos relevantes de las notificaciones recibidas.
- **Event**: Entidad que representa eventos temporales, como la votación de delegados o la postulación de delegados. Esta entidad permite flexibilidad para crear otros eventos temporales.
- **Vote**: Entidad que representa un voto emitido por un alumno hacia otro alumno.
- **SportReservation**: Entidad que almacena las reservas realizadas por los usuarios para utilizar alguna pista deportiva de la universidad.
- **Festive**: Entidad que representa los días no laborables de la universidad, diseñada para representar un día o una secuencia de días.
- **News**: Entidad que representa las noticias de la universidad.

### Usuarios

- **Anónimo**
- **Estudiante**
- **Personal docente**
- **Personal administrativo**

### Permisos de usuario

- **Anónimo**:
  - Ver las noticias.
  - Ver el calendario académico.

- **Estudiante**:
  - Todos los permisos de Anónimo.
  - Ver sus calificaciones finales.
  - Ver su carnet de estudiante.
  - Ver su horario académico.
  - Ver la información de los exámenes finales.
  - Ver sus alertas.
  - Reservar pistas deportivas.
  - Postularse como delegado.
  - Votar a un delegado.
  - Registrar asistencia.

- **Personal docente**:
  - Todos los permisos de Anónimo.
  - Todos los permisos de Estudiante menos:
    - Ver sus calificaciones finales.
    - Ver la información de exámenes finales.
    - Ver sus alertas.
    - Postularse y votar delegados.
    - Registrar asistencia.
  - Crear asistencias.

- **Personal administrativo**:
  - Todos los permisos de Anónimo.
  - Ver las reservas activas.
  - Ver el historial y los resultados de los eventos de votación de delegados.

## Diagrama de navegación

El diagrama representa la secuencia de pantallas disponibles, teniendo en cuenta los permisos de los usuarios. Las flechas verdes indican pantallas disponibles únicamente para los estudiantes, las grises para todos los usuarios (incluyendo anónimos), las moradas para cualquier usuario perteneciente a alumnos o profesores y las rojas para los administradores. La pantalla de logueo aparece al intentar acceder a funcionalidades restringidas sin estar logueado o al pulsar sobre el botón login al acceder a la app.

El diagrama se puede encontrar haciendo clic [aquí](https://github.com/codeurjc-students/2024-ReURJC/blob/main/NAVEGACION-SCHEMA.pdf).

### Descripción de las pantallas

- **Pantalla "Servicios"**: Permite a los usuarios acceder a los distintos servicios de la URJC:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/Servicios.png)

  - **Pantalla "Reserva de cancha"**: Permite al usuario reservar pistas deportivas seleccionando el tipo de pista, fecha y hora. Solo se permiten reservas con al menos 24 horas de antelación. Si un horario ya está reservado, no aparece en la lista. Si el usuario tiene una reserva activa, esta se muestra junto con la opción de cancelarla. Al cancelar, el horario se libera para otros usuarios:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/ReservaCancha.png)

  - **Pantalla "Votar delegados"**: Permite votar a los candidatos en el proceso de elección. Solo está disponible cuando el evento está activo:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/VotarDelegados.png)

  - **Pantalla "Postularse como delegado"**: Permite a los usuarios postularse como delegados. Solo está disponible cuando el evento está activo:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/PostularDelegados.png)

  - **Pantalla "Gestión de asistencias"**: Los profesores pueden crear nuevas asistencias, generando un código válido durante 5 minutos. También pueden consultar las últimas 10 asistencias creadas. Los alumnos, por su parte, solo pueden introducir códigos. Si el código es correcto y se introduce a tiempo, se muestra una notificación de éxito. En caso contrario, se muestra un error:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/Asistencia.png)

  - **Pantalla "Eventos"**: En esta pantalla los administradores pueden consultar el historial de eventos y los resultados de las votaciones:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/Eventos.png)

  - **Pantalla "Historial de reserva de pistas"**: En esta pantalla los administradores pueden consultar las reservas activas de todos los usuarios:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/HistorialReservas.png)

- **Pantalla "Alertas"**: Muestra al usuario en tiempo real los cambios realizados por los profesores en el aula virtual sobre sus notas:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/Alertas.png)

- **Pantalla "Noticias"**: Permite al usuario leer las noticias de la universidad:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/Noticias.png)

- **Pantalla "Tiempos"**: Incluye funcionalidades relacionadas con los tiempos de la universidad:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/Tiempos.png)

  - **Pantalla "Calendario"**: Muestra el calendario académico con colores que indican días lectivos, fines de semana y días no lectivos:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/Calendario.png)

  - **Pantalla "Exámenes finales"**: Muestra información sobre los exámenes finales de cada asignatura:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/ExamenesFinales.png)

  - **Pantalla "Horario semanal"**: Muestra el horario semanal con información sobre las asignaturas del usuario:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/Horario.png)

- **Pantalla "Perfil"**: Permite consultar las calificaciones finales y acceder al carnet de estudiante. Al seleccionar el carnet, se activa el NFC para compartir datos con dispositivos compatibles:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/Perfil.png)

  - **Pantalla "Calificaciones finales"**: Muestra las calificaciones obtenidas en cada asignatura, desglosadas por tareas:

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Assets/CalificacionesFinales.png)

## Esquema del Backend

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/Backend_Schema.png)

## Esquema del Frontend

![image](https://raw.githubusercontent.com/codeurjc-students/2024-ReURJC/main/frontend_schema.png)

## Vídeos de funcionalidad:

- **Usuarios Anónimos**: [Click aquí](https://youtu.be/fJMaB_9Y7fs)
- **Usuarios Estudiantes**: [Click aquí](https://youtu.be/dEGvcdUk6g8)
- **Usuarios Profesores**: [Click aquí](https://youtu.be/nyHwXWa0Ikg)
- **Usuarios Administrador**: [Click aquí](https://youtu.be/PX_lZXzBQG4)
- **Funcionalidades móvil**: [Click aquí](https://youtu.be/MEm8OAh4PgI)