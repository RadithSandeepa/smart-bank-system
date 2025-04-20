FROM eclipse-temurin:17-jdk
ENV APP_FILE config-service-1.1-SNAPSHOT.jar
#ENV APP_HOME /app
#EXPOSE 8088
#COPY target/$APP_FILE $APP_HOME/
#WORKDIR $APP_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec java -jar $APP_FILE"]
ENV APP_HOME /app
EXPOSE 8088
# Copy any JAR in target/ to app.jar
COPY target/*.jar $APP_HOME/app.jar
WORKDIR $APP_HOME
ENTRYPOINT ["java","-jar","/app/app.jar"]
