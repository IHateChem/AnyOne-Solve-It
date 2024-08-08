# 이문제 어때요? BE

# 이문제 어때요? - 알고리즘 스터디 

## 배포 주소
 https://anyone-solve.pe.kr

## 프로젝트 소개

이 문제 푸셨나요?는 알고리즘 스터디를 진행하며 가장 불편했던 점인 문제를 정할때 스터디원의 문제 중복 여부를 일일이 물어봐야 하는 것을 해결하기 위해 기획, 제작한 프로젝트입니다.

## 시작 가이드
### Requirements
For building and running the application you need:

- [Springboot 3.2.0]
- [JDK 17]

### Installation
``` bash
$ git clone https://github.com/IHateChem/AnyOne-Solve-It
$ cd AnyOne-Solve-It
```

### application.yml 작성
달러표시($)는 상황에 맞게 추가
``` bash
$ vi src/main/resources/application.properties
```
```
spring:

  autoconfigure:
    exclude: org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration

  # DB 연결
  datasource:
    url: ${db_url}
    driver-class-name: org.mariadb.jdbc.Driver
    username: ${db_name}
    password: ${db_pw}
  jpa:
    hibernate:
      ddl-auto: update #create update none
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    show-sql: true
  google:
    client_id: ${google_id}
    client_secret: ${google_secret}
  naver:
    client_id: ${naver_id}
    client_secret: ${naver_secrete}
  kakao:
    client_id: ${kakao_id}
    client_secret: ${kakao_secrete}
    redirect_uri: "${redirect}"
  github:
    client_id:${github_id}
    client_secret:${github_secrete}

  data:
    redis:
      host: ${redis_host}
      port: ${redis_post}
jwt:
  secret: ${jwt_secret}

anyone:
  page: 5
  maxTitle: 15
  maxDescription: 40
```

#### Backend Build & Run
```
$ ./gradlew build
$ java -jar build/libs/anyonesolveit-0.0.1-SNAPSHOT.jar
```

#### Frontend
https://github.com/coddingyun/DoUSolveThis

---

## Stacks

### Environment
![IntelliJ](https://img.shields.io/badge/Intellij%20Idea-000?logo=intellij-idea&style=for-the-badge)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white)
![Github](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white)                   

### Development
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=Spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

### OS
![Ubuntu](https://img.shields.io/badge/-Linux-grey?logo=linux)

### Infra
[NHN Cloud]
<img width="72" alt="스크린샷 2024-03-16 오후 9 29 15" src="https://github.com/IHateChem/AnyOne-Solve-It/assets/83485983/67ddebef-6577-4ae9-a2b6-77af408b91e4">


### DB
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![mariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)

### Communication
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=Slack&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)

---

## DB 구조
<img width="902" alt="스크린샷 2024-03-16 오후 9 36 56" src="https://github.com/IHateChem/AnyOne-Solve-It/assets/83485983/39c477ab-16eb-4b89-96d3-d6d8da17fd73">

## API 
https://app.swaggerhub.com/apis/SYLEELSW_1/Anyone_Solved_It/1.0.0#/

## 인프라 구조
![image](https://github.com/IHateChem/AnyOne-Solve-It/assets/83485983/54eb391c-23e7-42e5-9da9-8a2cef6f38c0)


---
## 디자인 
https://www.figma.com/file/wvpWf47oJwQwOlh4y4mtkv/%5B%EA%B3%B5%EC%9C%A0%EC%9A%A9%5D-WEB_%EC%9D%B4-%EB%AC%B8%EC%A0%9C-%ED%91%B8%EC%85%A8%EB%82%98%EC%9A%94%3F?type=design&node-id=1-2&mode=design
