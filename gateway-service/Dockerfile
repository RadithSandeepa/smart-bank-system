# gateway-service/Dockerfile
FROM eclipse-temurin:17-jre-alpine
ENV APP_FILE gateway-service-0.0.1-SNAPSHOT.jar
ENV APP_HOME /app
EXPOSE 8060
COPY target/$APP_FILE $APP_HOME/
WORKDIR $APP_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $APP_FILE"]