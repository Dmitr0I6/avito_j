# Используем базовый образ с JDK 17 (или другой версией, которую вы используете)
FROM eclipse-temurin:17-jdk-jammy as builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем исходный код и файлы Gradle
COPY src ./src
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle ./gradle
COPY .env .

# Собираем приложение с помощью Gradle
RUN ./gradlew build -x test --no-daemon
# Используем базовый образ для запуска приложения
FROM eclipse-temurin:17-jre-jammy
# Устанавливаем рабочую директорию
WORKDIR /app
COPY --from=builder /app/.env .

ENV CLOUDINARY_URL=cloudinary://698542969869454:I2VxQWHL7Q69fDDRepaPeueRCnw@df2oddste
# Копируем собранный JAR-файл из предыдущего этапа
COPY --from=builder /app/build/libs/bulletin*.jar app.jar

# Открываем порт, на котором работает приложение
EXPOSE 9000

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
