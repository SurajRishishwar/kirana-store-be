FROM public.ecr.aws/amazoncorretto/amazoncorretto:17 AS builder
WORKDIR /app
RUN yum install -y tar gzip
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -q
COPY src/ src/
RUN ./mvnw package -DskipTests -q
RUN ls -la /app/target/

FROM public.ecr.aws/amazoncorretto/amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/target/backend-1.0.0.jar app.jar
RUN ls -la /app/
VOLUME /tmp
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
