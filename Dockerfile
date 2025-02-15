# OpenJDK 17 이미지 사용
FROM openjdk:17-jdk-slim

# JAR 파일을 컨테이너로 복사
COPY build/libs/anyonesolveit-0.0.1-SNAPSHOT.jar /anyonesolveit-0.0.1-SNAPSHOT.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/anyonesolveit-0.0.1-SNAPSHOT.jar"]