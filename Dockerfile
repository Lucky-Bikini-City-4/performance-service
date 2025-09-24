# Dockerfile
FROM openjdk:17-jdk-slim

# 빌드된 JAR 파일과 application.yml 파일을 컨테이너 내부로 복사
# GitLab CI 아티팩트가 job의 root directory에 다운로드된다고 가정
COPY performance-service-build.jar /app.jar
COPY application.yml /config/application.yml

# 컨테이너가 시작될 때 실행할 명령어 정의
ENTRYPOINT ["java", "-jar", "/app.jar"]