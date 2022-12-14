<div align="center">
  <img width="250px;" src="./README/logo.png" />
<br/>  
<br/> 
</div>

# 🌎 수줍 [SOOJOOB]

- 서비스명 : 수줍 [SOOJOOB]

- 개발기간 : 2022.07.11 ~ 2022.8.19 (5주)

- 팀명 : 프리지아🌻
  
  - **프리**티한 **지**구가 **아**파요 🤎🌏😷 
    <br><br>

# 🍀 서비스 소개

### 1. 수줍(SOOJOOB)이란?

- 🏃‍♂️달리고 걸을 때 🍥쓰레기를 주워 💪나의 건강과 지구의 건강을 함께 지키는 앱
  
  ### 2. 서비스 기획 배경
  
  <div align="center">
  <img height="300px" width="350px;" src="./README/trash2.png" />
  <img height="300px"width="350px;" src="./README/sweeper.png" />
  </div>

- 길에 보이는 쓰레기들이 장마철에 바다로 흘러가는 현상

- 환경미화원의 업무량 과다 및 인력 부족

- 단순히 일정기간의 캠페인에서 그치는 것이 아니라 일상에서 언제나 참여할 수 있는 플로깅 플랫폼을 만들자!
  <br><br>

# 🌈 주요 기능

### 🏠 메인화면

<div align="center">
   <img src="./README/gif/home/homeStart.gif" width="30%">
</div>

### 👶 사용자 관리

> + ##### 회원가입, 로그인/로그아웃, 회원정보 수정, 회원탈퇴, 마이페이지
> + ##### jwt 토근 인증방식을 사용한 관리
> + ##### 나의 프로필을 볼 수 있고 개인 정보를 수정하는 페이지
> + ##### 프로필 사진, 닉네임, 손수 줍기 횟수, 활동 배지, 개인정보 수정
>   1. 회원가입

<div align="center">
     <img src="./README/gif/user/userSignUp.gif" width="30%">
</div>

    2. 로그인 / 로그아웃

<div align="center">
  <img src="./README/gif/user/userLogIn.gif" width="30%">
  <img src="./README/gif/user/userLogout.gif" width="30%">
</div>

    3. 회원정보 수정 및 비밀번호 변경

<div align="center">
   <img src="./README/gif/user/userUpdateInfo.gif" width="30%">
   <img src="./README/gif/user/userUpdatePassword.gif" width="30%">
</div>

    4. 회원탈퇴

<div align="center">
   <img src="./README/gif/user/userDelete.gif" width="30%">
</div>

    5. 마이페이지

<div align="center">
   <img src="./README/gif/myPage/myPage.gif" width="30%">
   <img src="./README/gif/board/myBoard.gif" width="30%">
</div>

<br><br>

### 🌺 플로깅을 기록하고 공유

> + ##### 지도 기반으로 Plogging을 시작할 수 있는 페이지
> + ##### Google Map api를 활용해 현재 위치 표시 및 주변에 있는 화장실이나 쓰레기통 클러스팅 표시
> + ##### 플로깅한 경로를 트래킹하고 시간, km를 계산하여 제공
>   1. 플로깅 기능
>      - 쓰레기를 주울 때 카운팅
>      - 주운 위치에 꽃이 피어나서 지도에 마킹
>      - 지나간 길은 폴리라인으로 경로 확인 가능

<div align="center">
   <img src="./README/gif/plogging/ploggingStart.gif" width="30%">
   <img src="./README/gif/plogging/ploggingEnd.gif" width="30%">
   <br><br>
   <img src="./README/gif/home/homeList.gif" width="30%">
   <img src="./README/gif/record/recordList.gif" width="30%">
</div>

    2. 주변 위치 정보 제공
      - 주변 쓰레기통 위치 정보 제공
      - 주변 화장실 위치 정보 제공

<div align="center">
   <img src="./README/gif/plogging/ploggingTrashcan.gif" width="30%">
   <img src="./README/gif/plogging/ploggingToilet.gif" width="30%">
</div>

    3. SNS 사진 공유
      - 기록에는 마킹과 폴리라인이 된 지도 사진이 기본 연동
      - 필요 시 유저가 커스텀한 사진으로 변경 가능
      - SNS 공유 기능

<div align="center">
   <img src="./README/gif/sns.gif" width="30%">
</div>

<br><br>

### 🏆 경혐치 & 업적 배지! 랭킹시스템까지! 참여율 UP UP!!

> + ##### 특정조건을 달성하면 배지 획득 가능
> + ##### 배지를 클릭하면 달성 조건을 확인할 수 있는 페이지
> + ##### 플로깅 결과에 따른 유저 경험치(온도), 랭킹을 확인할 수 있는 페이지
> + ##### 목표 달성을 통한 동기부여 제공
>   1. 온도 경험치 시스템
>      - 활동 기록에 따라 온도 경첨치 산정
>      - 36.5°C부터 100°C까지 열정이 불타요!

<div align="center">
  <img src="./README/home.png" width="30%">
</div>

    2. 업적 배지
    
      - 특정 조건이나 이스터에그 발견 시 얻을 수 있는 배지 시스템
      - 아직 획득하지 못한 배지를 노리고 도전하는 재미

<div align="center">
   <img src="./README/gif/myBadge/myBadge.gif" width="30%">
   <img src="./README/gif/myBadge/myBadgeUnearned.gif" width="30%">
</div>

    3. 랭킹
      - 랭킹확인

<div align="center">
   <img src="./README/gif/rank/rank.gif" width="30%">
</div>

### 📝 게시글 작성

    1. 사진, 게시글 작성

<div align="center">
   <img src="./README/gif/board/boardWrite.gif" width="30%">
</div>

    2. 최신순, 많은 순

<div align="center">
   <img src="./README/gif/board/boardList.gif" width="30%">
</div>

<br><br>

# 💡 '수줍'하면 일어나는 기대효과!

1. 성취감
2. 운동효과
3. 선한 영향력
   <br><br>

# 🏃 향후 계획

1. 쓰레기 분류 기능
2. 헬스 API
3. Wear OS 활용
   <br><br>

# 💻 기술 스택

> ### Front-End : Kotlin 1.8, Retrofit, Firebase, Google Map API
> 
> ### Back-End : Java 1.8, Spring Boot 2.6.1, JWT, Security, JPA
> 
> ### Server : Ubuntu 20.04, AWS, EC2, Nginx, SSL인증서
> 
> ### DB : MySQL 8.0.28
> 
> ### 기획 : Figma, Notion, JIRA, GitLab, ERD Cloud
> 
> <br><br>

# 👨‍👩‍👧‍👦 팀원 역할

| 팀원  | 역할  | 직무        | 담당 업무                                                                                                                                                                                                                      | 한 줄 소감                               |
| --- | --- | --------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------ |
| 공통  | -   | -         | **JIRA 관리, Android, Retrofit(활용), UI/UX(기본적인 틀), DB설계, 기능명세서**                                                                                                                                                             | -                                    |
| 이재영 | 팀장  | Front-End | **Retrofit 정리자료 공유, 회원정보 수정, 비밀번호 변경, 회원탈퇴 <br>기타 : JIRA 총괄, UCC 촬영, PPT 자료 구성 및 발표**                                                                                                                                      | 시간이 부족해서 기획했던 기능들을 마저 구현하지 못해 아쉽습니다. |
| 김다은 | 팀원  | Back-End  | **BE: Security(jwt), 회원관리, 레코드, 플로깅, 랭킹, 배지로직<br>FE: 구글로그인, 토큰관리, 배지, 랭킹, proguard기능<br>기타 : 포팅메뉴얼, PPT 자료 구성 및 발표, 야외 라이브 시연, UCC 촬영**                                                                                    | Spring과 Android와 많이 친해진거 같아요!        |
| 박민진 | 팀원  | Back-End  | **게시판, 날씨 API, 쓰레기통DB, 화장실DB**                                                                                                                                                                                             | 새로운 것을 배우면서 성장할 수 있는 경험이었습니다.        |
| 박찬석 | 팀원  | Front-End | **기획 : Figma 목업 제작 <br/>FE : NavBar Fragmnet로 구현, Retrofit 구성 및 구현,  <br/>페이지 제작 (홈화면, 마이페이지, 로그인, 회원가입), <br/>디자인 제작 (플로깅(시작,완료), 커뮤니티, 기록, 배지) <br/>기타 : 디자인 총괄 (모든 페이지), 팀 노션 페이지 관리,  야외 라이브 시연 및 기술지원, 기능별 영상 파일(mp4) 추출** | Kotlin과 친해질 수 있는 소중한 경험이었습니다.        |
| 박한훈 | 팀원  | Front-End | **GoogleMapAPI(마커, 폴리라인, 현위치, 주소변환, 화장실/쓰레기통 클러스팅, 위치권한 설정), 지도캡처 후 저장 및 전송<br>기타 : 야외 라이브 시연, UCC 촬영, gif 편집, 포팅메뉴얼, ReadMe.md 작성**                                                                                       | 모든 것이 처음이라 낯설었지만 후회없는 선택이었습니다.       |
| 홍석현 | 팀원  | Back-End  | **BE: Security(jwt), 배지, AWS-EC2 서버 배포 <br>FE:  SNS공유, 카메라 권한 설정, GoogleMapAPI(마커, 폴리라인, 현위치), 게시판, 지도캡처 후 저장 및 전송, 이미지 업로드, 플로깅 리스트, tts, 스플래시 화면, <br>기타 : 야외 라이브 시연, PPT 영상 제작, UCC 촬영 및 제작**                           | 다양한 기술스택을 경험할 수 있는 좋은 기회였습니다.        |

<br><br>

# 📚 산출물

#### Git Lab 내 exec 폴더 참조

#### [Notion] https://www.notion.so/d210/SSAFY-8d8771c733e7469e93c6bc9bb7c9efa3

# 
