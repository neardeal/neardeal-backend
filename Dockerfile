# (기존 빌드 단계 삭제)
# 바로 실행 환경(JRE)으로 시작
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# GitHub Actions가 빌드한 파일만 복사 (경로 주의)
COPY build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]