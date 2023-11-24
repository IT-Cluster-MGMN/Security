FROM openjdk:17-oracle

WORKDIR /app

COPY target/Security-Service-0.0.1-SNAPSHOT.jar /app/Security-Service-0.0.1-SNAPSHOT.jar

EXPOSE 8088

CMD ["java", "-jar" ,"./Security-Service-0.0.1-SNAPSHOT.jar"]
