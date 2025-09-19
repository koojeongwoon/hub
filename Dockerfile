# Java 21을 사용하는 기본 이미지
FROM openjdk:21-jdk-slim

# JAR 파일을 애플리케이션 안으로 복사
COPY build/libs/hub-0.0.1.jar app.jar

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]