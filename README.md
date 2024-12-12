# 2024-ReURJC

## Nombre de la aplicación web
- **MyURJC**

## Descripción de la aplicación web
- Aplicación para el personal docente, administrativo y estudiantil de la Universidad Rey Juan Carlos que sirve para acceder a los servicios ofertados por esta.

## Cómo ejecutar la Aplicación

1. Instalar docker [aqui](https://www.docker.com/get-started/)
2. Una vez instalado, asegurase de que el docker engine se encuentra en ejecucion
3. Situarse en la raíz de este repositorio
4. Ejecutar el siguiente comando "docker-compose -p myurjc up -d"
5. La aplicacion se encontrará ejecutandose en el purto 8080 de su máquina. 

## Diagrama de base de datos
El diagrama se puede encontrar haciendo clic [aquí](https://github.com/codeurjc-students/2024-ReURJC/blob/main/DB_Schema.pdf).

### Entidades
- **User**: Esta clase representa a los usuarios de la aplicación y contiene toda su información relevante, como los tokens de los dispositivos sincronizados, sus datos personales y su rol dentro de la universidad.
- **Subject**: Esta clase representa las asignaturas de la universidad. Utiliza tablas intermedias para aportar información relevante como las convocatorias, los usuarios que pertenecen a las asignaturas, el horario, y las notas de las diferentes tareas.
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

- **Personal docente (por definir)**:
  - Todos los permisos de Anónimo.
  - Todos los permisos de estudiante menos registrar asistencia.
  - Crear asistencias.

- **Personal administrativo (por definir)**.

## Diagrama de navegación
El diagrama representa la secuencia de pantallas disponibles, teniendo en cuenta los permisos de los usuarios. Las flechas verdes indican pantallas disponibles únicamente para el personal docente, las grises para todos los usuarios (incluyendo anónimos) y las negras para cualquier usuario logueado. La pantalla de logueo aparece al intentar acceder a funcionalidades restringidas sin estar logueado.

El diagrama se puede encontrar haciendo clic [aquí](https://github.com/codeurjc-students/2024-ReURJC/blob/main/NAVEGACION-SCHEMA.pdf).

### Descripción de las pantallas
- **Pantalla "Servicios"**: Permite a los usuarios acceder a los distintos servicios de la URJC:
  - **Pantalla "Reserva de cancha"**: Permite al usuario reservar pistas deportivas seleccionando el tipo de pista, fecha y hora. Solo se permiten reservas con al menos 24 horas de antelación. Si un horario ya está reservado, no aparece en la lista. Si el usuario tiene una reserva activa, esta se muestra junto con la opción de cancelarla. Al cancelar, el horario se libera para otros usuarios.
  - **Pantalla "Votar delegados"**: Permite votar a los candidatos en el proceso de elección. Solo está disponible cuando el evento está activo.
  - **Pantalla "Postularse como delegado"**: Permite a los usuarios postularse como delegados. Solo está disponible cuando el evento está activo.
  - **Pantalla "Gestión de asistencias"**: Los profesores pueden crear nuevas asistencias, generando un código válido durante 5 minutos. También pueden consultar las últimas 10 asistencias creadas. Los alumnos, por su parte, solo pueden introducir códigos. Si el código es correcto y se introduce a tiempo, se muestra una notificación de éxito. En caso contrario, se muestra un error.

- **Pantalla "Alertas"**: Muestra al usuario en tiempo real los cambios realizados por los profesores en el aula virtual sobre sus notas.

- **Pantalla "Noticias"**: Permite al usuario leer las noticias de la universidad.

- **Pantalla "Tiempos"**: Incluye funcionalidades relacionadas con los tiempos de la universidad:
  - **Pantalla "Calendario"**: Muestra el calendario académico con colores que indican días lectivos, fines de semana y días no lectivos.
  - **Pantalla "Exámenes finales"**: Muestra información sobre los exámenes finales de cada asignatura.
  - **Pantalla "Horario semanal"**: Muestra el horario semanal con información sobre las asignaturas del usuario.

- **Pantalla "Perfil"**: Permite consultar las calificaciones finales y acceder al carnet de estudiante. Al seleccionar el carnet, se activa el NFC para compartir datos con dispositivos compatibles:
  - **Pantalla "Calificaciones finales"**: Muestra las calificaciones obtenidas en cada asignatura, desglosadas por tareas.
