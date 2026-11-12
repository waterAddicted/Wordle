FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package

FROM quay.io/wildfly/wildfly:31.0.1.Final-jdk17
COPY --from=build /app/target/wordsdatabase.war /opt/jboss/wildfly/standalone/deployments/wordsdatabase.war

# send the access log to stdout so "docker compose logs" shows requests
# (access-log must be the last child of <host>, per the undertow schema;
# prefix/suffix can't be empty, so default filename + a symlink to /dev/stdout is used instead)
RUN sed -i 's#</host>#<access-log pattern="combined" rotate="false"/></host>#' \
    /opt/jboss/wildfly/standalone/configuration/standalone.xml \
    && mkdir -p /opt/jboss/wildfly/standalone/log \
    && ln -sf /dev/stdout /opt/jboss/wildfly/standalone/log/access_log.log

EXPOSE 8080
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
