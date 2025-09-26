FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

COPY gradle/ gradle/
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle.properties /root/.gradle/gradle.properties
RUN chmod +x gradlew


RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon --version

COPY src ./src


RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon clean bootJar -x test

############################
# 2) Runtime stage
############################
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app


# run as non-root
RUN useradd --no-create-home --system --uid 10001 appuser

# copy the bootable fat jar
COPY --from=build /app/build/libs/*.jar /app/app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50 -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8"
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 9090
USER appuser:appuser

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]