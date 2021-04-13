# 양파마켓(🥕당근마켓 클론 프로젝트)

> 이미 나와 있는 서비스를 직접 구현해보는 <u><B>클론 코딩</B></u> 프로젝트를 진행
>
> <u>Kotlin</u>을 사용한 Android 개발 / <u>Node.js</u>를 이용한 서버 개발    



### 1. 회원가입 / 로그인

<img src="https://user-images.githubusercontent.com/42233535/114555025-47501e00-9ca2-11eb-99b6-f669b22030e8.gif" height=500>      <img src="https://user-images.githubusercontent.com/42233535/114552695-d4de3e80-9c9f-11eb-98b8-928c5afd3eb7.gif" height=500>



- Email / Password를 이용한 회원가입 지원

  - 회원가입시 이미 존재하는 사용자인지 확인 후 존재하지 않는 사용자라면 가입 진행

  - MySQL에 Email / Password를 저장하며 이 때, 암호화 모듈인 Bcrypt의 암호 해싱 기능을 이용하여 암호화된 Password를 저장    

    

- Email / Password를 이용한 Login 지원

  - Passport를 이용하여 기능 구현
  - 로그인을 시도하면 Local 인증을 통해 유효한 사용자인지 검증하고 유효한 사용자라면 JWT token을 발급
    - 애플리케이션 내에서 사용자 로그인 여부가 필요할 경우 이 token을 사용