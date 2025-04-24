
# JUST - 익명 고민 상담

## 프로젝트 목적sd


# Just Mobile App

<p align="center">
주변 사람에게는 말 못할 고민을 털어 놓을 수 있는 공간, Just  <br>

## 🚪 프로젝트 소개
> **Just Application은 익명성을 강조한 SNS입니다. 다소 공격적인 댓글과 선정적인 성향의 SNS보다 사람들이 편하게 느끼고 그런 생태계를 만들고자 모든 사용자가 노력하기를 바랍니다. 해당 어플리케이션은 익명성을 지키면서 SNS를 재미있고 따듯하게 즐기수 있습니다!**

## 🔧 기술 스택
⭐️ *Language*<br><br>
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
  ![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)<br><br>
⭐️ *Library & Framework*<br><br>
  ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
  ![ElasticSearch](https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white)
  ![Flask](https://img.shields.io/badge/Flask-000000?style=for-the-badge&logo=flask&logoColor=white)<br><br>
⭐️ *Database* <br><br>
  ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)<br><br>
⭐️ *ORM* <br><br>
  ![JPA](https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)<br><br>
⭐️ *Deploy* <br><br>
  ![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)<br><br>
⭐️ *CI/CD* <br><br>
  ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)
  ![AWS CodeDeploy](https://img.shields.io/badge/AWS_CodeDeploy-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
  ![AWS S3](https://img.shields.io/badge/AWS_S3-569A31?style=for-the-badge&logo=amazon-s3&logoColor=white)


## 🗒️ 구현 기능
### 이성현
- 게시글 기능 부분을 담당 (무한스크롤, 신고자 글 제외, 게시글 랜덤 조회, 삭제, 수정 등등)
- CI/CD 아키텍쳐 설계
- 해시 태그 추가/삭제/조회 기능
- GPT API를 이용한 해시태그 추천 기능
### 문준호
- 댓글 작성/추가/삭제/대댓글 기능
- ES(ElasticSearch)를 이용한 검색 기능
- OAuth2.0을 이용한 로그인/로그아웃 기능
- 알림/신고 기능



## ⚙️ 아키텍쳐 설계 


### Application 아키텍쳐
<img width="1314" alt="스크린샷 2024-05-31 오후 3 13 48" src="https://github.com/inje-megabrain/JUST-be/assets/96710732/f4f05cb0-c8c5-4d6c-b9e9-603b10f16d82">

### CI/CD 아키텍쳐 
<img width="924" alt="스크린샷 2024-03-17 오후 4 09 44" src="https://github.com/inje-megabrain/JUST-be/assets/96710732/d7f5597e-e78b-458b-92f0-641371089963">



## 📦 ERD
![Copy of Just](https://github.com/inje-megabrain/JUST-be/assets/96710732/653e7f58-802c-465c-af1d-25ffb38f93e4)

## 화면 구성 📺
| 메인 페이지  |  댓글 페이지  |
| :-------------------------------------------: | :------------: |
|  <img width="350" alt="image" src="https://github.com/inje-megabrain/JUST-be/assets/96710732/847ce9a8-c58c-4413-940a-c53badedf7db">| <img width="350" alt="image" src="https://github.com/inje-megabrain/JUST-be/assets/96710732/3ae14c73-1605-45b9-8ce1-ee4c3fa4aaa4">|  
| 글 작성 페이지  |  태그 생성 페이지   |  
| <img width="350" alt="image" src="https://github.com/inje-megabrain/JUST-be/assets/96710732/79c941cc-9c0b-424e-b204-9d592e827978">  |  <img width="350" alt="image" src="https://github.com/inje-megabrain/JUST-be/assets/96710732/184ec1ff-9c8a-4148-ad90-4e3999eafdbd"> |

## 주요 기능 📦

### ⭐️ 게시글의 작성 및 무한스크롤 기능
- QueryDSL를 기반으로 게시글 무한스크롤(조회) 기능 제작
- ES(ElasticSearch)를 이용한 검색 기능

### ⭐️게시글의 자유로운 댓글 작성 및 해시태그 작성 기능
- OpenAI API를 이용한 태그 추천 기능 제작
- 댓글 작성/추가/삭제/대댓글 기능
- 1:N, N:1 관계를 활용한 태그 작성/삭제/수정 기능 제작

### ⭐️ AI를 활용한 이름/직업 등에 대한 필터링 기능
- 뽀로로 라이브러리를 활용한 이름/직업 등 개인적인 내용에 대한 필터링 기능 제공





## 🖥️ 기능 동작 영상 
[![](https://github.com/inje-megabrain/JUST-FE/raw/main/readme_images/ui.gif)](https://github.com/inje-megabrain/JUST-FE/blob/main/readme_images/ui.gif)
