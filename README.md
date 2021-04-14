# onion-market

### 🥕당근마켓 클론 프로젝트

> 이미 나와 있는 서비스를 직접 구현해보는 <u><B>클론 코딩</B></u> 프로젝트를 진행
>
> <u>Kotlin</u>을 사용한 Android 개발 / <u>Node.js</u>를 이용한 서버 개발  

<br>

### 1. 회원가입 / 로그인

<p align="center">
<img src="https://user-images.githubusercontent.com/42233535/114555025-47501e00-9ca2-11eb-99b6-f669b22030e8.gif" height=500>      <img src="https://user-images.githubusercontent.com/42233535/114552695-d4de3e80-9c9f-11eb-98b8-928c5afd3eb7.gif" height=500>
</p>  


- Email / Password를 이용한 회원가입 지원

  - 회원가입시 이미 존재하는 사용자인지 확인 후 존재하지 않는 사용자라면 가입 진행

  - 데이터베이스에 Email / Password를 저장하며 이 때, 암호화 모듈인 `Bcrypt의 암호 해싱 기능`을 이용하여 암호화된 Password를 저장  

<br>

- Email / Password를 이용한 Login 지원

  - Passport를 이용하여 기능 구현
  - 로그인을 시도하면 `Local 인증`을 통해 유효한 사용자인지 검증하고 유효한 사용자라면 `JWT token을 발급`
    - 애플리케이션 내에서 사용자 로그인 여부가 필요할 경우 이 token을 사용

<br>

<br>

### 2. 게시글 등록 / 수정 / 삭제

<p align="center">
<img src="https://user-images.githubusercontent.com/42233535/114670714-466cca00-9d3e-11eb-9890-94b404d71f25.gif" height=500>      <img src="https://user-images.githubusercontent.com/42233535/114675092-d745a480-9d42-11eb-9f74-cf81dfd4b251.gif" height=500>
</p>  



- 게시글 등록 & 수정
  - 동일 Activity에서 이루어지며 판매글 수정의 경우 intent를 통해 전달받은 `Parcelable` 데이터로 View의 내용을 채움
  - 사진을 첨부할 경우 사진과 등록된 순서를 데이터베이스에 같이 저장하여 판매글에서 첨부한 순서대로 사진이 보일 수 있도록 함

- 게시글 삭제
  - `BottomSheetDialog`의 메뉴 중 선택할 수 있으며 삭제 전 확인을 거친 후 동작하도록 개발

<br>

<br>

### 3. 채팅

<img src="https://user-images.githubusercontent.com/42233535/114682359-fa278700-9d49-11eb-9607-2595e6276dc9.gif" height=500>

- `Socket.IO`를 이용한 채팅 구현
  - 이벤트
    - subscribe : 채팅을 시작하면 채팅방 id로 room을 만듦
    - newMessage : 새로운 메시지 전송
    - updateChat : 해당 채팅방 id를 가진 room에만 이벤트가 전달됨
- 채팅 메시지들은 데이터베이스에 별도로 저장

<br>

<br>

### 4. 알림

<img src="https://user-images.githubusercontent.com/42233535/114682456-11ff0b00-9d4a-11eb-9d6c-588b1301b106.gif" height=500>

- `Firebase Cloud Messaging (FCM)`을 이용한 채팅 알림 구현
  - Firebase에서 제공하는 `Firebase Admin SDK`를 사용
  - 로그인하게되면 해당 User의 Firebase Token을 데이터베이스에 저장하고 이 Token을 알림에서 사용함
  - 메세지 수신시 `FirebaseMessagingService`를 확장하여 `onMessageReceived()` 콜백을 재정의