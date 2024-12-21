<?php
defined('MOODLE_INTERNAL') || die();

$observers = [
    [
        'eventname' => '\core\event\user_graded',
        'callback' => 'local_myplugin\observer::user_graded_handler',
        'includefile' => '/local/myplugin/classes/observer.php',
        'internal' => false,
    ],
    [
        'eventname' => '\core\event\grade_item_updated',
        'callback' => 'local_myplugin\observer::grade_item_updated_handler',
        'includefile' => '/local/myplugin/classes/observer.php',
        'internal' => false,
    ],
];
