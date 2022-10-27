# multi_module
메이븐 멀티모듈 + 스프링 시큐리티(jwt) + 마이바티스 + 스프링 배치 + 게시판 기능 + 회원 기능

![image](https://user-images.githubusercontent.com/67456294/198409485-ed9be61d-170d-42d0-9953-1232ee0cced3.png)


- 관리자 API 모듈, 스케쥴러 API 모듈, 서비스 모듈을 구성해야 함

  - 관리자 API 모듈 : 게시판 CRUD / 회원 CRUD / 로그인 담당 (controller)

  - 서비스 모듈 : service, mapper 등 

  - 스케쥴러 API : 스프링 배치 
