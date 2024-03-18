FROM maven:3.9.6-eclipse-temurin-21-alpine as dev-build
WORKDIR /App
ADD pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:resolve dependency:resolve-plugins
ADD src src
RUN --mount=type=cache,target=/root/.m2 mvn package -Dmaven.test.skip

FROM eclipse-temurin:21-jre-alpine as dev
WORKDIR /App
COPY --from=dev-build /App/target target
RUN adduser -D banker
USER banker
CMD [ "target/bin/banking-system" ]

FROM maven:3.9.6-eclipse-temurin-21-alpine as prod-build
WORKDIR /App
ADD pom.xml .
RUN mvn dependency:resolve dependency:resolve-plugins
ADD src src
RUN mvn package -Dmaven.test.skip

FROM eclipse-temurin:21-jre-alpine as prod
WORKDIR /App
COPY --from=prod-build /App/target .
RUN adduser -D banker
USER banker
CMD [ "bin/banking-system" ]