FROM alpine/java:21-jdk AS builder
RUN apk add maven
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM alpine/java:21-jdk
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]