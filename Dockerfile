# 사용할 기본 이미지 정의
FROM openjdk:17-jdk-slim

# 빌드된 JAR 파일의 경로 지정
ARG JAR_FILE=build/libs/performance-service-build.jar

# 빌드된 JAR 파일을 Docker 이미지 내부로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너가 시작될 때 실행할 명령어 정의
ENTRYPOINT ["java", "-jar", "/app.jar"]