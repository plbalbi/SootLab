FROM adoptopenjdk:8u242-b08-jre-hotspot
RUN mkdir /opt/app
COPY target/service-0.0.1-SNAPSHOT.jar /opt/app/backend.jar
CMD ["java", "-jar", "/opt/app/backend.jar"]