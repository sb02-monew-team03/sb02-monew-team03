# 1단계: 빌드용 이미지
FROM amazoncorretto:17 as builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 관련 파일 복사
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# 종속성 미리 캐싱
RUN ./gradlew dependencies

# 애플리케이션 소스 복사
COPY src src

# 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew clean build -x test

# 2단계: 실행용 이미지
FROM amazoncorretto:17

# 작업 디렉토리 설정
WORKDIR /app

# 환경변수 설정
ENV PROJECT_NAME=monew
ENV PROJECT_VERSION=1.0.0
ENV JVM_OPTS=""
ENV SPRING_PROFILES_ACTIVE=prod

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar /app/app.jar

# 포트 노출
EXPOSE 8080

# 실행 명령어 설정
ENTRYPOINT ["sh", "-c", "exec java $JVM_OPTS -jar /app/app.jar"]
