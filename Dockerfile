FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY ./build/libs/core-0.0.1-SNAPSHOT.jar /app/core.jar
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "core.jar"]