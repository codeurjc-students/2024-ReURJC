# Use an official PHP image with Apache as the base image
FROM php:8.2-apache

# Set working directory
WORKDIR /var/www/html

# Install necessary PHP extensions and dependencies for Moodle
RUN apt-get update && apt-get install -y \
    git \
    unzip \
    libzip-dev \
    libonig-dev \
    libxml2-dev \
    libpng-dev \
    libjpeg-dev \
    libfreetype6-dev \
    libicu-dev \
    && docker-php-ext-configure opcache --enable-opcache \
    && docker-php-ext-configure gd --with-freetype --with-jpeg \
    && docker-php-ext-install -j$(nproc) \
    pdo_mysql \
    zip \
    mbstring \
    exif \
    pcntl \
    soap \
    sockets \
    gd \
    mysqli \
    intl \
    opcache

# Copy Moodle files from the Moodle directory
COPY ./../Moodle /var/www/html/

# ELIMINA la creación de moodledata aquí

# Adjust permissions for Moodle (Esto se puede mantener o mover al entrypoint)
RUN chown -R www-data:www-data /var/www/html \
    && chmod -R 755 /var/www/html

# Copy OPcache configuration file
COPY ./../Moodle/opcache.ini /usr/local/etc/php/conf.d/opcache.ini

# Copy PHP configuration file (php.ini)
COPY ./../Moodle/php.ini /usr/local/etc/php/conf.d/php.ini

# Enable Apache modules and configure virtual host
RUN a2enmod rewrite
COPY ./../Moodle/moodle.conf /etc/apache2/sites-available/moodle.conf
RUN a2ensite moodle.conf

# Create entrypoint script
COPY ./entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Expose port 80 for web traffic
EXPOSE 80

# Use entrypoint script
ENTRYPOINT ["/entrypoint.sh"]

# Default command to start Apache
CMD ["apache2-foreground"]