# Используем образ Maven с OpenJDK 21 для сборки приложения
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Копируем pom.xml и загружаем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests

# Используем легковесный образ JDK для выполнения
FROM openjdk:21-jdk-slim
WORKDIR /app

# Копируем скомпилированный JAR из предыдущего этапа
COPY --from=build /app/target/*.jar app.jar

# Запускаем приложение
CMD ["java", "-jar", "app.jar"]
