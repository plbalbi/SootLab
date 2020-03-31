FROM adoptopenjdk/openjdk8:jdk8u242-b08-debian
COPY . /opt/sources
# TODO: This should be configured differently, it's horrible
RUN /opt/sources/compile.sh &&\
    /tmp/apache-maven-3.6.3/bin/mvn -f /opt/sources clean package -Dmaven.test.skip=true spring-boot:repackage

FROM adoptopenjdk/openjdk8:jdk8u242-b08-debian
COPY --from=0 /opt/sources/target/service-0.0.1-SNAPSHOT.jar /opt/app/backend.jar
CMD ["java", "-jar", "/opt/app/backend.jar"]