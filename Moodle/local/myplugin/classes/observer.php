<?php
namespace local_myplugin;

defined('MOODLE_INTERNAL') || die();

class observer {

    // Almacén para items procesados recientemente.
    private static $processedGradeItems = [];

    /**
     * Manejador del evento grade_item_updated.
     *
     * @param \core\event\base $event
     */


     public static function user_graded_handler($data) {
        error_log('Entrando en user_graded_handler');
        $userid = $data->userid;
        $courseid = $data->courseid;
        $grade = $data->other['finalgrade'] ?? "Desconocido";
        $gradeid = $data->objectid;
        error_log("Datos recibidos: userid=$userid, courseid=$courseid, grade=$grade, gradeid=$gradeid");
    
        $gradeObj = \grade_grade::fetch(['id' => $gradeid]); 
        $assignmentname = "Tarea no encontrada";
        if ($gradeObj) {
            $itemid = $gradeObj->itemid;
            $gradeItem = \grade_item::fetch(['id' => $itemid]);
            $assignmentname = $gradeItem ? $gradeItem->itemname : "Tarea no encontrada";
        }
        error_log("Nombre de la tarea: $assignmentname");
    
        $url = 'http://notifications-service-service:8082/notifications/updateGrade';
        $postData = json_encode([
            'userid' => $userid,
            'courseid' => $courseid,
            'grade' => $grade,
            'assignmentname' => strval($assignmentname) 
        ]);
        error_log("Enviando solicitud a: $url con datos: $postData");
    
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            'Content-Type: application/json',
            'Authorization: 123',
        ]);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $postData);
    
        $response = curl_exec($ch);
        if (curl_errno($ch)) {
            error_log('Error en cURL: ' . curl_error($ch));
        } else {
            error_log('Respuesta de la solicitud: ' . $response);
        }
        curl_close($ch);
    }


    /**
     * Programa la limpieza del item procesado.
     *
     * @param int $gradeItemId
     */
    private static function schedule_cleanup($gradeItemId) {
        $task = new \local_myplugin\task\clear_processed_grade_item_task();
        $task->set_custom_data(['gradeItemId' => $gradeItemId]);
        \core\task\manager::queue_adhoc_task($task);
    }
}
