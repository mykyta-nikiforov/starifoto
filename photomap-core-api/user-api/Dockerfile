FROM --platform=linux/x86_64/v8 openjdk:17-jdk-alpine
#FROM --platform=linux/arm64/v8 arm64v8/openjdk:17.0-jdk
VOLUME /tmp
EXPOSE 8082
ADD build/libs/user-api-1.0.0-beta.jar app.jar
ENTRYPOINT [ "sh", "-c", "java -jar /app.jar" ]