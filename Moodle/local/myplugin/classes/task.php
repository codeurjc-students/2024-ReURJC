<?php
namespace local_myplugin;

defined('MOODLE_INTERNAL') || die();

class clear_processed_grade_item_task extends \core\task\adhoc_task {

    /**
     * Ejecuta la limpieza del item procesado.
     */
    public function execute() {
        $data = $this->get_custom_data();
        $gradeItemId = $data->gradeItemId;

        // Acceder a la clase observer para eliminar el item procesado.
        unset(\local_myplugin\observer::$processedGradeItems[$gradeItemId]);
    }
}
