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

        
        // 2. Obtener información adicional si es necesario
        $userid = $data->userid;
        $courseid = $data->courseid;
        $grade = $data->other['finalgrade'] ?? "Desconocido";
        $gradeid = $data->objectid;
    
        // Obtener el objeto grade_grade usando el ID
        $gradeObj = \grade_grade::fetch(['id' => $gradeid]); 
        
        // Obtener el nombre de la tarea si se encontró el objeto grade_grade
        if ($gradeObj) {
            // Obtener el ID del item de calificación (itemid)
            $itemid = $gradeObj->itemid;
    
            // Obtener el objeto grade_item usando el itemid
            $gradeItem = \grade_item::fetch(['id' => $itemid]);
            
    
            if ($gradeItem) {
                $assignmentname = $gradeItem->itemname;
            } else {
                $assignmentname = "Tarea no encontrada";
            }
        } else {
            $assignmentname = "Tarea no encontrada";
        }
        
    
        // 3. Construir la solicitud POST
        $url = 'http://notifications:8082/notifications/updateGrade';
        $postData = json_encode([
            'userid' => $userid,
            'courseid' => $courseid,
            'grade' => $grade,
            'assignmentname' => strval($assignmentname) 
        ]);
    
        // 4. Enviar la solicitud al endpoint
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
    
        
        curl_close($ch);
    
        
    }
    
    public static function grade_item_updated_handler($event) {
        $data = $event->get_data();

        // Obtener el ID del item de calificación.
        $gradeItemId = $data['objectid'];

        // Ignorar si ya se procesó recientemente.
        if (isset(self::$processedGradeItems[$gradeItemId])) {
            return;
        }

        // Verificar que el evento sea de creación (CRUD = 'c').
        if ($data['crud'] !== 'c') {
            return;
        }

        // Marcar como procesado.
        self::$processedGradeItems[$gradeItemId] = time();

        // Extraer datos relevantes para enviar al backend.
        $userid = $data['userid'] ?? null;
        $courseid = $data['courseid'] ?? $data['contextinstanceid'];

        if ($userid && $courseid) {
            self::send_grade_data($userid, $courseid);
        }

        // Programar limpieza del almacenamiento temporal.
        self::schedule_cleanup($gradeItemId);
    }

    /**
     * Envía los datos de la calificación al backend.
     *
     * @param int $userid
     * @param int $courseid
     */
    private static function send_grade_data($userid, $courseid) {
        $url = 'http://myurjc:8080/v1/moodle/newGradeItem';

        $postData = json_encode([
            'userid' => $userid,
            'courseid' => $courseid,
        ]);

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
            error_log('cURL error: ' . curl_error($ch));
        }

        curl_close($ch);

        if ($response === false) {
            throw new \moodle_exception('errorpostrequest', 'local_myplugin');
        }
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
