FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package

FROM tomcat:10.1-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/gameservice.war /usr/local/tomcat/webapps/gameservice.war

# send the access log to stdout so "docker compose logs" shows requests
RUN sed -i -z 's#directory="logs"\n               prefix="localhost_access_log" suffix=".txt"#directory="/dev/stdout" prefix="" suffix="" rotatable="false"#' \
    /usr/local/tomcat/conf/server.xml

EXPOSE 8080
CMD ["catalina.sh", "run"]
