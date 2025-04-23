# Tripot

![image](https://github.com/user-attachments/assets/9413c208-649b-4d67-90ab-0f8fc8eaefa2)                                      ![image](https://github.com/user-attachments/assets/c064bcbc-8f4e-4362-8039-410ec2145f0e)

> **개발기간** 2024.10 ~

<br/>

## 서비스 주소

https://apps.apple.com/kr/app/%ED%8A%B8%EB%A6%BD%ED%8C%9F/id6740468089

<br />

## 프로젝트 소개

사용자의 여행 기록을 다양한 사람들과 공유할 수 있는 서비스입니다. 자신의 여행을 기록하고, 추억하며, 다른 사람들과 이를 주고받으면서 좋은 여행지를 추천받을 수 있습니다. 지도를 보면서 여행지의 위치를 한눈에 확인해보세요.

<br />

## 기술 스택

<p><strong>Language</strong></p>
<div>
    <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white">
</div>
<p><strong>Environment</strong></p>
<div>
    <img src="https://img.shields.io/badge/IntelliJ-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
    <img src="https://img.shields.io/badge/git-F05033.svg?style=for-the-badge&logo=git&logoColor=white" />&nbsp
    <img src="https://img.shields.io/badge/github-181717.svg?style=for-the-badge&logo=github&logoColor=white" />&nbsp
</div>
<p><strong>Database</strong></p>
<div>
    <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=PostgreSQL&logoColor=white"> 
    <img src="https://img.shields.io/badge/Redis-FF4438?style=for-the-badge&logo=Redis&logoColor=white"> 
</div>
<p><strong>Framework</strong></p>
<div>
    <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white"> 
    <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
    <img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> 
    <img src="https://img.shields.io/badge/QueryDSL-4479A1?style=for-the-badge">
    <img src="https://img.shields.io/badge/Spring Security-4479A1?style=for-the-badge&logo=springsecurity&logoColor=white">
    
</div>
<p><strong>Test</strong></p>
<div>
  <img src="https://img.shields.io/badge/Postman-FF6C37.svg?style=for-the-badge&logo=Postman&logoColor=white">
  <img src="https://img.shields.io/badge/junit5-25A162.svg?style=for-the-badge&logo=Postman&logoColor=white">
</div>
<p><strong>Communication</strong></p>
<div>
    <img src="https://img.shields.io/badge/Notion-F3F3F3.svg?style=for-the-badge&logo=notion&logoColor=black">
    <img src="https://img.shields.io/badge/Discord-5865F2.svg?style=for-the-badge&logo=Discord&logoColor=white">  
</div>
<p><strong>CI/CD</strong></p>
<div>
      <img src="https://img.shields.io/badge/Amazon EC2-FF9900.svg?style=for-the-badge&logo=amazonec2&logoColor=white">
      <img src="https://img.shields.io/badge/docker-2496ED.svg?style=for-the-badge&logo=docker&logoColor=white">
      <img src="https://img.shields.io/badge/GitHub Actions-2088FF.svg?style=for-the-badge&logo=githubactions&logoColor=white">
      <img src="https://img.shields.io/badge/nginx-009639.svg?style=for-the-badge&logo=nginx&logoColor=white">
</div>
<br />

## 주요 기능

### ⭐ 한눈에 보는 여행일지

- 지도를 통해 사용자가 다녀온 여행지의 위치를 한눈에 알 수 있습니다.

### ⭐ 작성 스토리 조회

- 작성했던 스토리를 조회할 수 있습니다.

### ⭐ 소셜 로그인

- 카카오를 통한 소셜 로그인으로 복잡한 회원가입 절차 없이 해당 서비스를 이용할 수 있습니다.

### ⭐ JWT을 이용한 토큰방식 로그인

- JWT을 이용한 토큰방식으로 로그인을 구현 하였습니다.
- Refresh Token을 같이 사용하여 일반적인 JWT 방의 문제점을 보완했습니다.

<br />

## 아키텍처
![image](https://github.com/user-attachments/assets/c3e967d8-6af8-46f2-a9a2-ce7871c3471c)



<br />

## 화면 구성

|                                                    메인페이지                                                    |                                                   둘러보기                                                   |                                                     작성한글                                                     |
| :--------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------: |
|![image](https://github.com/user-attachments/assets/88be7d8a-213d-4648-94da-618963d369de)                         | ![image](https://github.com/user-attachments/assets/aa92d115-cc24-48b6-9e55-bfd29086c84a)                       | ![image](https://github.com/user-attachments/assets/4443a9b9-0bc1-4c52-9506-4718a35e2f69)                       |
|                                                      스토리 검색                                                   |                                                     회원가입                                                     |                                               스토리 작성 위치 선택                                             |
| ![image](https://github.com/user-attachments/assets/ca4c07e7-c1a2-4a98-a4a9-acc4abcf9faa)                        | ![image](https://github.com/user-attachments/assets/bf320508-4961-4ec4-b7ba-6a46ce7eb5fd)                       | ![image](https://github.com/user-attachments/assets/1456ac4a-1b45-4d5d-9ad0-33449ad187f0)|
|                                                    게시글 작성                                                    |                                               알림 페이지                                                        |                                                    상세페이지                                                    |
| ![image](https://github.com/user-attachments/assets/39038587-d1c3-489a-bfa7-f490e16447f4)                        | ![image](https://github.com/user-attachments/assets/f7e62c62-9ade-4834-8b74-a63c83878ce4)                      | ![image](https://github.com/user-attachments/assets/179ad866-3088-4943-884f-abd78f184fdc)|

<br />


## 웹개발팀

<table>
  <tr>
    <th style="width: 200px; text-align : center;">김지유</th>
    <th style="width: 200px; text-align : center;">박희성</th>
    <th style="width: 200px; text-align : center;">김창호</th>
  </tr>
  <tr style="border-bottom: 1px solid white;">
    <td>
        <img src="https://github.com/gugumo-service/gugumo_frontend/assets/96280450/d6716133-cc01-451c-af07-0da997725785">
    </td>
    <td style="border-left: 1px solid white;">
        <img src="https://github.com/gugumo-service/gugumo_frontend/assets/96280450/6c18d80c-5aed-48ec-90ad-f847437e83a0">
    </td>
    <td style="border-left: 1px solid white;">
        <img src="https://github.com/gugumo-service/gugumo_frontend/assets/96280450/412dbdcb-8dad-4bc9-8ae4-bc28fba73f6c">
    </td>
  </tr>
  <tr style="border-bottom: 1px solid white; text-align : center;">
    <td>FE</td>
    <td style="border-left: 1px solid white;">BE</td>
    <td style="border-left: 1px solid white;">BE</td>
  </tr>
</table>

<br />

## 디자인팀

<table>
  <tr>
    <th style="width: 200px; text-align: center">정하은</th>
  </tr>
  <tr style="border-bottom: 1px solid white;">
    <td>
        <img src="https://github.com/gugumo-service/gugumo_frontend/assets/96280450/757d814c-6dd6-4546-b950-3d9505b4e1b7">
    </td>
  </tr>
  <tr style="border-bottom: 1px solid white; text-align : center;">
    <td>
        디자이너
    </td>
  </tr>
</table>
