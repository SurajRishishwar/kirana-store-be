FROM public.ecr.aws/amazoncorretto/amazoncorretto:17 AS builder
WORKDIR /app
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -q
COPY src/ src/
RUN ./mvnw package -DskipTests -q

FROM public.ecr.aws/amazoncorretto/amazoncorretto:17
WORKDIR /app
VOLUME /tmp
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
