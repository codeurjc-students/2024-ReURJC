# 2024-ReURJC

## Nombre de la aplicación web
- **MyURJC**

## Descripción de la aplicación web
- Applicación para el personal docente, administrativo y estudiantil de la universidad Rey Juan Carlos que sirve para poder acceder a servicios ofertados por esta.

##Diagrama de base de datos
El diagrama se puede encontrar haciendo click [aquí]([https://github.com/codeurjc-students/2024-ReURJC/blob/main/DB-schema.svg?short_path=3f32352](https://github.com/codeurjc-students/2024-ReURJC/blob/main/DB-SCHEMA-tfgdb.svg))

###Entidades
- User: Esta clase representa los usuarios de la aplicacion y contiene toda su informacion relevante. Como los token de los dispositivos sincronizados, sus datos personales y su rol dentro de la universidad.
- Subject: Esta clase representa las asignaturas de la universidad. Se vale de tablas intermedias para aportar informacion relevante como las convocatorias, los usuarios que pertenecen a las asignaturas, el horario de las asignaturas, las notas de las diferentes tareas de estas...
- Convocatory: Las convocatorias a los exámenes finales de las asignaturas.
- Schedule: El horario en el que se impart euna asignatura
- Attendance: Entidad utilizada para crear los eventos de asistencia, tambien se vale de tabla sintermedias para saber los usuarios que han registrado la asistencia y la asignatura a ala que pertenece este evento.
- Notification: Entidad encargada de almacenar los datos relevantes de las notificaciones recibidas
- Event: entidad que representa los eventos temporales como la votación de delegados o la postulacion de delegados. Esta entidad abre la puerta a la flexibilidad de poder crear los eventos temporales que se deseen. No s elimita a la votacion de delegados
- Vote: entidad que represent aun voto emitido por un alumno hacia otro alumno
- SportReservation: Entidad que almacena las reservas realizadas por los usuarios para utilizar alguna pista deportiva de la universidad.
- Festive: Entidad que representa los días no laborables de la universidad, ideada para representa run día o una secuencia de días
- News: Entidad que representan las noticiás de la universidad


