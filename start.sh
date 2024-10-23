#!/bin/bash

echo "Определение переменных..."
COOKHUB_DOMAIN="" # Домен или IP-адрес для доступа к приложению CookHub
UPLOAD_PATH="" # Путь, где будут храниться загружаемые изображения
RECAPTCHA_KEY="" # Ключ для интеграции с reCAPTCHA
RECAPTCHA_SECRET="" # Секретный ключ для интеграции с reCAPTCHA
SPRING_MAIL_USERNAME="" # Адрес электронной почты для SMTP-сервера
SPRING_MAIL_PASSWORD="" # Пароль для аутентификации на SMTP-сервере

echo "Клонирование репозитория..."
git clone -b dev https://github.com/kovalenkojuls/otus-graduation-work.git
cd otus-graduation-work || exit 1

echo "Создание директории для логов, если она не существует..."
mkdir -p ../logs

echo "Запуск Docker контейнеров..."
docker-compose -f docker/docker-compose.yml up -d

echo "Замена значений в файлах application.properties..."
sed -i "s|cookhub.domain=http://localhost|cookhub.domain=http://$COOKHUB_DOMAIN|" cookhub-web-application/src/main/resources/application.properties
sed -i "s|upload.path=|upload.path=$UPLOAD_PATH|" cookhub-web-application/src/main/resources/application.properties
sed -i "s|recaptcha.key=|recaptcha.key=$RECAPTCHA_KEY|" cookhub-web-application/src/main/resources/application.properties
sed -i "s|recaptcha.secret=|recaptcha.secret=$RECAPTCHA_SECRET|" cookhub-web-application/src/main/resources/application.properties

sed -i "s|spring.mail.username=|spring.mail.username=$SPRING_MAIL_USERNAME|" cookhub-email-service/src/main/resources/application.properties
sed -i "s|spring.mail.password=|spring.mail.password=$SPRING_MAIL_PASSWORD|" cookhub-email-service/src/main/resources/application.properties

echo "Обеспечение прав на выполнение скриптов Gradle..."
chmod +x cookhub-web-application/gradlew
chmod +x cookhub-email-service/gradlew

echo "Сборка и запуск CookHub Web Application..."
cd cookhub-web-application || exit 1
./gradlew build
touch ../../logs/cookhub-web-application.log
./gradlew bootRun > ../../logs/cookhub-web-application.log 2>&1 &

echo "Сборка и запуск CookHub Email Service..."
cd ../cookhub-email-service/ || exit 1
./gradlew build
touch ../../logs/cookhub-email-service.log
./gradlew bootRun > ../../logs/cookhub-email-service.log 2>&1 &

echo "Ожидание завершения всех фонов процессов..."
wait




