# Zighang_BE
## 개요
본 프로젝트는 직행 플랫폼 내 공고 탐색 과정의 불편함을 해결하기 위해 기획되었습니다.
고객이 더욱 빠르고 정확하게 자신에게 맞는 공고를 발견할 수 있도록 개인화 로직 개발과 간결한 온보딩 프로세스 수립을 핵심 목표로 삼았습니다.
특히, 로그인 유도를 통해 고객 데이터를 확보하고 이를 분석·활용함으로써, 고객 맞춤형 추천과 지속적인 개선이 가능한 기반을 마련하였습니다. 이를 위해 다음과 같은 주요 기능을 설계·구현했습니다.
## 주요 기능정의
• 나만의 공고 뽑기: 개인 맞춤형 공고 추천을 제공</br>
• 동문관: 신뢰도 높은 공고평·동문 정보를 통해 탐색 품질 강화</br>
• 북마크 대시보드: 사용자가 스크랩·저장한 공고를 한눈에 관리</br>
## 배포 Swagger API 링크
https://zighang4.o-r.kr/swagger-ui/index.html
## R&R
| 김영록 | 윤창현 |
| --- | --- |
|<img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/135b89f3-4f91-4a37-9273-dbc9a3b60ee3" />|<img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/938ee854-b0fb-49d1-8c8a-cad5319b6a2b" />|

|이름|역할|
| --- | --- |
|김영록|서버 배포 자동화 환경 구축, 온보딩 API 제작, 카드뽑기 API 제작, 스크랩/대시보드 API 제작(스크랩 수행 및 조회 위주로)|
|윤창현|소셜 로그인(카카오) 구현, LLM과 MQ연동 및 구축, 동문관 관련 API 제작, 공고 상세보기 페이지 API 제작, 스크랩/대시보드 API 제작(스크랩 한 공고에 포트폴리오, 이력서 첨부 기능 위주로), 메모 및 공고평 API 제작|

## 기술스택
### Languages
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white&style=flat-square)
### Frameworks
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white&style=flat-square)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity&logoColor=white&style=flat-square)
### Databases & Caches
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white&style=flat-square)
![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white&style=flat-square)
### Infra & DevOps
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white&style=flat-square)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions&logoColor=white&style=flat-square)
![NCP](https://img.shields.io/badge/Naver%20Cloud-03C75A?logo=naver&logoColor=white&style=flat-square)
![NGINX](https://img.shields.io/badge/NGINX-009639?logo=nginx&logoColor=white&style=flat-square)
![Google Cloud SQL](https://img.shields.io/badge/Google%20Cloud%20SQL-4285F4?logo=googlecloud&logoColor=white&style=flat-square)
### API & Docs
![OpenAPI/Swagger](https://img.shields.io/badge/OpenAPI%2FSwagger-85EA2D?logo=swagger&logoColor=black&style=flat-square)
### Messaging
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?logo=rabbitmq&logoColor=white&style=flat-square)
### AI
![Clova Studio](https://img.shields.io/badge/Clova%20Studio-03C75A?logo=naver&logoColor=white&style=flat-square)

## 아키텍처
<img width="2080" height="1158" alt="image" src="https://github.com/user-attachments/assets/59c420c0-f0ad-4252-a91f-ea1c6bb7d7ad" />
- 공고상세보기에서 우대사항, 자격요건 등과 같이 부가적인 정보들이 필요할 때가 있습니다. 그래서 해당 공고의 ocr데이터를 MQ로 전달해 clova chatbot으로 보내서 해당 공고의 우대사항과 자격요건 칼럼을 비동기적으로 채워서 사용자가 조회가능하게 합니다.</br>
- redis같은 경우에는 카드를 뽑을 때 스크랩 수 제한(다음에 또 뽑고 싶으면 현재 스크랩 수에서 3장이상 스크랩을 해야함)이 있어서 뽑을 당시에 스크랩 수가 얼마인지 기록할 필요가 있고, 카드를 뽑을 때 이미 뽑힌 카드는 더 이상 안 뽑혀야해서 사용자가 뽑은 카드 내역을 저장(계속 저장하면 나중에 더 이상 추천할 공고가 없을 수 있으니 TTL적용)
  하는 용도로 사용하며 사용자가 뽑은 카드를 페이지를 나간 후에도 보여줘야하기 때문에 지금 뽑은 카드 내역도 여기에 저장하고 있습니다.

## ERD
<img width="1300" height="822" alt="image" src="https://github.com/user-attachments/assets/731fce61-b3f7-4db4-8e51-78fde0840389" />

## Rabbit MQ & AI chatbot 흐름도
<img width="956" height="758" alt="image" src="https://github.com/user-attachments/assets/2c707fb7-5df1-4384-8023-abcac8bee9ab" />


