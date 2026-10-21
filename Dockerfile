FROM gradle:8.7-jdk17 AS build
WORKDIR /app

# 캐시 최적화
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# 소스 복사 후 빌드
COPY . .

ARG BUILD_PROFILE=local
RUN gradle clean bootJar -x test -Pprofile=${BUILD_PROFILE} --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
