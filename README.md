# 👕 모으또 (Moeutto) 👕

> 옷장 관리, 분석 및 날씨 기반 착장 추천 서비스

**모으또 (Moeutto)** 옷장 관리 및 추천 서비스입니다.

옷장을 열어보지 않고 한눈에 파악할 수 있고, 안 입는 옷은 기부를 통해 옷장을 관리할 수 있는 기능을 제공합니다.

옷장 분석을 통해 어떤 종류의 옷이 많은지 등을 모으또 사용자와 비교해주는 기능을 제공합니다.

날씨에 맞는 착장을 추천하여 무엇을 입을지 고민할 필요가 없어 시간을 절약할 수 있습니다.

착장 검사를 통해 날씨에 맞는 착장인지 확인해주는 기능을 제공합니다.

<br/>

**모으또 (Moeutto)를 활용하여 옷장을 관리해보세요!!**
## 📙 소개 영상
![UCC](./UCC/moeutto.gif)

<br/>

## 👨‍💻 팀원 소개
| <a href="https://github.com/hannernos"><img src="https://avatars.githubusercontent.com/u/82031197?v=4?s=100" width="100px;" alt=""/><br /></a> | <a href="https://github.com/27kanghan"><img src="https://avatars.githubusercontent.com/u/58686114?v=4?s=100" width="100px;" alt=""/><br /></a> | <a href="https://github.com/pohangman"><img src="https://avatars.githubusercontent.com/u/90905159?v=4?s=100" width="100px;" alt=""/><br /></a> | <a href="https://github.com/psj98"><img src="https://avatars.githubusercontent.com/u/60167488?v=4?s=100" width="100px;" alt=""/><br /></a> | <a href="https://github.com/seoyoung81"><img src="https://avatars.githubusercontent.com/u/122508505?v=4?s=100" width="100px;" alt=""/><br /></a> | <a href="https://github.com/s01k1m618"><img src="https://avatars.githubusercontent.com/u/121751626?v=4?s=100" width="100px;" alt=""/><br /></a> |
| --- | --- | --- | --- | --- | --- |
| <p style="background-color: #AAAAAA; color: black"> **김동현** | **강한** | **권현우** | **박성준** | **고서영** | **김솔** |
| <p align="left">Backend <br> ML</p> | <p align="left">Backend</p> | <p align="left">Backend <br> Infra</p> | <p align="left">Backend</p> | <p align="left">Frontend</p> | <p align="left">Frontend</p> |
| 옷 배경 제거, 카테고리 분류, 색상 분류<br><br>옷 날씨, 계절 적합도 점수 산정, AI 코멘트 생성, 옷 추천 및 평가 | 옷장 분석, 캘린더, 친구 API<br><br>소셜 로그인, 튜토리얼 페이지 | Kakao OAuth를 활용한 회원 관리<br><br>옷 등록, 빈도, 활용도에 따른 옷장 분석<br>AI 착장 검사 API<br>S3 Bucket API<br><br>CI/CD 구축 | 옷장, AI 착장 추천, 방명록<br>카테고리, 친구 착장 추천 API<br><br>의류 이미지 크롤링 | 메인 페이지<br>사용자 위치에 따른 날씨 업데이트<br><br>카테고리별 옷장 조회<br><br>캘린더<br><br>착장 검사<br><br>친구 착장 추천 | 옷장 등록, 분석<br><br>착장 검사<br><br>캘린더 등록<br><br>친구 검색, 친구 옷장 조회, 방명록 작성 |



<br/>

## 🗺 아키텍처

![아키텍처](./imgs/architecture.png)

<br/>

## 📚 기술 스택

### ML
![Pytorch](https://img.shields.io/badge/Pytorch-EE4C2C?style=for-the-badge&logoColor=white)
![OpenAI](https://img.shields.io/badge/openai-412991.svg?&style=for-the-badge&logo=openai&logoColor=white)

### Back-end

![Java](https://img.shields.io/badge/Java-yellow.svg?&style=for-the-badge&logo=java&logoColor=#3776AB)
![Spring](https://img.shields.io/badge/Spring-6DB33F.svg?&style=for-the-badge&logo=Spring&logoColor=white)
![Go](https://img.shields.io/badge/go-00ADD8.svg?&style=for-the-badge&logo=go&logoColor=white)
![Python](https://img.shields.io/badge/python-3776AB.svg?&style=for-the-badge&logo=python&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F.svg?&style=for-the-badge&logo=Spring%20Boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F.svg?&style=for-the-badge&logo=Spring%20Security&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-6DB33F.svg?&style=for-the-badge&logoColor=white)
![FastAPI](https://img.shields.io/badge/fastapi-009688.svg?&logo=fastapi&style=for-the-badge&logoColor=white)

### Front-end
![React](https://img.shields.io/badge/React-61DAFB.svg?&style=for-the-badge&logo=React&logoColor=blue)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6.svg?&style=for-the-badge&logo=Typescript&logoColor=white)
![Redux](https://img.shields.io/badge/Redux-764ABC.svg?&style=for-the-badge&logo=Redux&logoColor=white)
![Tailwind](https://img.shields.io/badge/Tailwind%20CSS-06B6D4.svg?&style=for-the-badge&logo=Tailwind%20CSS&logoColor=white)

### DataBase

![MySQL](https://img.shields.io/badge/MySQL-4479A1.svg?&style=for-the-badge&logo=MySQL&logoColor=white)
![ChromaDB](https://img.shields.io/badge/ChromaDB-412991.svg?&style=for-the-badge&logoColor=white)
### Storage

![Amazon S3](https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
### AWS Server
![Amazon EC2](https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)

### CI/CD & Infra Tools

![Jenkins](https://img.shields.io/badge/Jenkins-D24939.svg?&style=for-the-badge&logo=Jenkins&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED.svg?&style=for-the-badge&logo=Docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639.svg?&style=for-the-badge&logo=Nginx&logoColor=white)
### Collaboration Tools

![Notion](https://img.shields.io/badge/Notion-000000.svg?&style=for-the-badge&logo=Notion&logoColor=로고색상)
![Mattermost](https://img.shields.io/badge/Mattermost-0058CC.svg?&style=for-the-badge&logo=Mattermost&logoColor=로고색상)
### Issue & Configuration Management

![GitLab](https://img.shields.io/badge/Gitlab-FC6D26.svg?&style=for-the-badge&logo=Gitlab&logoColor=#FC6D26)
![Jira](https://img.shields.io/badge/Jira-0052CC.svg?&style=for-the-badge&logo=Jira&logoColor=Blue)

---

<br/>

## ❗ 주요 기능

**🛒 옷장 관리**
- 카테고리, 색상, 계절, 두께, 소재 등을 설정하여 옷을 **등록**할 수 있습니다.
- 이전에 등록된 옷을 **조회**할 수 있습니다.

**📝 옷장 분석**
- 모으또 사용자와 비교하여 옷장을 **분석**해줍니다.
- 분석한 결과를 **시각화**하여 보여줍니다.
  - 색상 : 옷장에 어떤 색의 옷이 많은지 알려줍니다.
  - 계절 : 옷장에 어떤 계절의 옷이 많은지 알려줍니다.
  - 빈도 : 어떤 옷을 많이, 적게 입는지 알려줍니다.
  - 가격 : 옷에 지출한 가격을 알려줍니다.
  - 양 : 옷을 얼마나 많이 가지고 있는지 알려줍니다.
  - 활용도 : 옷을 얼마나 활용하고 있는지 알려줍니다.

**🌞 착장 추천**
- 날씨 데이터를 기반으로 **3일 간**의 착장을 **추천**해줍니다.

**📊 착장 검사**
- 입고 싶은 옷을 선택하여 착장을 검사할 수 있습니다.
- 검사 시, 날씨, 색상 조합 등을 판단하고, 검사 결과를 **시각화**하여 알려줍니다.

<br/>

## ❗ 세부 기능

|구분|기능|설명|비고|
|---|---|---|---|
|1|옷장 등록|소유한 옷 등록 및 관리||
|2|옷장 조회|옷 세부사항 조회 및 최근 입은 날짜 조회||
|3|옷장 분석|색상, 계절, 빈도, 가격, 양, 활용도를 기준으로 옷장 분석||
|4|착장 추천|날씨 데이터를 기반으로 3일간의 착장 추천||
|5|착장 검사|날씨, 색상 조합 등을 고려하여 착장 검사 및 검사 결과 시각화||
|6|착장 등록|캘린더에 착장 등록 및 이전 착장 확인||
|7|착장 평가|착장 피드백 (좋아요, 싫어요)||
|8|친구 검색|사용자 검색||
|9|팔로우 / 언팔로우|사용자 팔로우 / 언팔로우||
|10|친구 옷장 조회|친구가 소유한 옷장 조회||
|11|방명록 작성|친구 옷장에 방명록 작성||

<br/>




<br/>

## 📅 번다운 차트
![1week](./imgs/burndown-charts/1.png)
![2week](./imgs/burndown-charts/2.png)
![3week](./imgs/burndown-charts/3.png)
![4week](./imgs/burndown-charts/4.png)
![5week](./imgs/burndown-charts/5.png)
![6week](./imgs/burndown-charts/6.png)
<br/>

## ⚙ 설정

1. [포팅 메뉴얼](https://github.com/hannernos/Moeutto/blob/develop/exec/%EB%B2%B5%EA%B0%88%ED%98%B8%EB%9E%91%EC%9D%B4_%ED%8F%AC%ED%8C%85%EB%A7%A4%EB%89%B4%EC%96%BC.pdf)

2. [데이터베이스 덤프 파일](https://github.com/hannernos/Moeutto/blob/develop/exec/%EB%B2%B5%EA%B0%88%ED%98%B8%EB%9E%91%EC%9D%B4_%EB%8D%A4%ED%94%84%ED%8C%8C%EC%9D%BC.sql)
