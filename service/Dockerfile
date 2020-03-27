FROM adoptopenjdk/openjdk8:jdk8u242-b08-debian
RUN mkdir /opt/app
COPY target/service-0.0.1-SNAPSHOT.jar /opt/app/backend.jar
CMD ["java", "-jar", "/opt/app/backend.jar"]