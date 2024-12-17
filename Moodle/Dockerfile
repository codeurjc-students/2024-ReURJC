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

# Copy Moodle files from the root directory
COPY . /var/www/html

# Create moodledata directory and set permissions
RUN mkdir -p /var/www/html/moodledata && \
    chown -R www-data:www-data /var/www/html/moodledata && \
    chmod -R 0770 /var/www/html/moodledata

# Adjust permissions for Moodle
RUN chown -R www-data:www-data /var/www/html \
    && chmod -R 755 /var/www/html

# Copiar el archivo de configuración de OPcache
COPY opcache.ini /usr/local/etc/php/conf.d/opcache.ini

# Copiar el archivo de configuración de PHP (php.ini)
COPY php.ini /usr/local/etc/php/conf.d/php.ini

# Enable Apache modules and configure virtual host
RUN a2enmod rewrite
COPY moodle.conf /etc/apache2/sites-available/moodle.conf
RUN a2ensite moodle.conf

# Expose port 80 for web traffic
EXPOSE 80

# Default command to start Apache
CMD ["apache2-foreground"]