FROM eclipse-temurin:17-jdk
ENV APP_FILE account-service-1.0-SNAPSHOT.jar
ENV APP_HOME /app
EXPOSE 8088
COPY target/*.jar $APP_HOME/app.jar
WORKDIR $APP_HOME
ENTRYPOINT ["java","-jar","/app/app.jar"]
