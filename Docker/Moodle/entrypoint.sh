#!/bin/bash

# Ajusta los permisos de moodledata DESPUÉS de que el volumen se monte
chown -R www-data:www-data /var/www/html/moodledata
chmod -R 0770 /var/www/html/moodledata

# Ejecuta el comando principal (en este caso, Apache)
exec "$@"