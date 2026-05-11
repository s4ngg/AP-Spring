[README.md](https://github.com/user-attachments/files/27564971/README.md)
# AllPick Backend

> Spring Boot 기반 쇼핑몰 플랫폼 백엔드 서버

---

## 목차

- [프로젝트 소개](#프로젝트-소개)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [시작하기](#시작하기)
- [환경 변수](#환경-변수)
- [팀원 역할 분담](#팀원-역할-분담)
- [ERD](#erd)
- [API 문서](#api-문서)
- [도메인별 API 목록](#도메인별-api-목록)
- [공통 응답 형식](#공통-응답-형식)
- [배포](#배포)

---

## 프로젝트 소개

AllPick은 일반 회원, 판매자(Seller), 관리자(Admin)가 함께 사용하는 쇼핑몰 플랫폼입니다.  
회원은 상품을 탐색하고 주문하며, 판매자는 상품을 등록하고 관리하고, 관리자는 판매자 및 상품 승인, CS 처리 등을 담당합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.5 |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security + JWT |
| Database | MySQL |
| Cache | Redis |
| Storage | AWS S3 |
| SMS | CoolSMS |
| OAuth2 | Spring OAuth2 Client (소셜 로그인) |
| API 문서 | Swagger (springdoc-openapi 2.8.8) |
| Logging | Log4j2 |
| Build | Gradle |

---

## 프로젝트 구조

```
src/main/java/co/kr/allpick/
├── AllPickApplication.java
├── domain/
│   ├── admin/          # 관리자 (어드민 계정, FAQ, 공지사항, 상품 승인, 판매자 승인, 문의/클레임 처리)
│   ├── cart/           # 장바구니
│   ├── coupon/         # 쿠폰
│   ├── member/         # 회원 (인증, 마이페이지, 멤버십, SMS 인증)
│   ├── order/          # 주문 및 배송지
│   ├── product/        # 상품 및 카테고리
│   ├── review/         # 리뷰
│   └── seller/         # 판매자 (인증, 판매자 신청)
└── global/
    ├── common/         # 공통 엔티티 (BaseEntity 등)
    ├── config/         # 설정 (Security, JWT, Swagger, CoolSMS 등)
    ├── exception/      # 예외 처리 (ErrorCode, BusinessException)
    ├── filter/         # JWT 인증 필터
    ├── response/       # 공통 응답 형식
    ├── service/        # 공통 서비스
    └── util/           # 유틸 (S3Uploader 등)
```

각 도메인은 아래 레이어 구조를 따릅니다.

```
controller/
  └── docs/       # Swagger 문서용 인터페이스
dto/
entity/
repository/
service/
  └── impl/
```

---

## 시작하기

### 사전 요구사항

- Java 21
- MySQL 8.x
- Redis
- AWS S3 버킷

### 실행 방법

```bash
# 1. 저장소 클론
git clone https://github.com/your-org/AP-Spring.git
cd AP-Spring

# 2. 환경 변수 설정 (.env 또는 시스템 환경 변수)
# 아래 환경 변수 섹션 참고

# 3. 빌드 및 실행
./gradlew bootRun
```

---

## 환경 변수

`application.properties`에서 아래 환경 변수를 참조합니다.  
실행 전 반드시 설정이 필요합니다.

| 변수명 | 설명 |
|--------|------|
| `DB_USERNAME` | MySQL 사용자 이름 |
| `DB_PASSWORD` | MySQL 비밀번호 |
| `DB_HOST` | MySQL 호스트 주소 |
| `JWT_SECRET` | JWT 서명 키 |
| `JWT_EXPIRATION_TIME` | JWT 만료 시간 (ms) |
| `AWS_ACCESS_KEY` | AWS Access Key (앱 실행용) |
| `AWS_SECRET_KEY` | AWS Secret Key (앱 실행용) |
| `AWS_ACCESS_KEY_ID` | AWS Access Key ID (GitHub Actions 배포용) |
| `AWS_SECRET_ACCESS_KEY` | AWS Secret Access Key (GitHub Actions 배포용) |
| `AWS_REGION` | AWS 리전 (예: ap-northeast-2) |
| `S3_IMAGE_BUCKET_NAME` | S3 이미지 버킷 이름 |
| `BACKEND_S3_BUCKET_NAME` | S3 배포용 JAR 버킷 이름 |
| `EC2_INSTANCE_ID` | EC2 인스턴스 ID (SSM 배포용) |
| `REDIS_HOST` | Redis 호스트 주소 |
| `COOLSMS_API_KEY` | CoolSMS API Key |
| `COOLSMS_API_SECRET` | CoolSMS API Secret |
| `COOLSMS_SENDER` | SMS 발신 번호 |
| `BUSINESS_API_KEY` | 사업자 번호 검증 API Key |

> Redis는 기본값 `localhost:6379`을 사용합니다. 별도 서버 사용 시 `spring.data.redis.host` 수정 필요.

---

## 팀원 역할 분담

| 이름 | 담당 기능 | 담당 도메인 |
|------|-----------|-------------|
| 김성원 | 상품 상세 페이지 및 리뷰, 장바구니, 찜(위시리스트) | 🛒 상품 · 리뷰 · 장바구니 |
| 김상우 | 대시보드(카테고리), 결제 화면, AI 상품 추천 | 📦 주문 · 회원 |
| 신보라 | 고객센터 (문의/환불 신청), 카테고리 관리, FAQ, 공지사항 | 🎧 문의 · 클레임 · 공지 · FAQ · 카테고리 |
| 이영훈 | 로그인 / 회원가입 (선호 카테고리 선택 포함) | 🏪 판매자 · 회원 |
| 이소정 | 관리자 페이지, 마이페이지 | 🔧 관리자 |
| 고유리 | 상품 리스트 | 🛍️ 상품 리스트 |

---

## ERD

> 👉 [ERDCloud에서 전체 보기](https://www.erdcloud.com/d/pqPaLT6pQmbfkQKt9)

---

## API 문서

서버 실행 후 아래 주소에서 Swagger UI를 통해 전체 API를 확인할 수 있습니다.

```
http://localhost:8080/swagger-ui/index.html
```

---

## 도메인별 API 목록

> **인증 범례**
> - `불필요` — 누구나 접근 가능
> - `ROLE_USER` — 로그인한 회원 또는 판매자 (JWT 토큰 필요)
> - `ROLE_SUPER_ADMIN` — 슈퍼 어드민 전용
> - `ROLE_CS_ADMIN` — CS 어드민 전용
> - `ROLE_SUPER_ADMIN / ROLE_CS_ADMIN` — 둘 중 하나

### 👤 회원

<details>
<summary>인증 (/api/auth)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/auth/signup` | 회원 가입 | 불필요 |
| POST | `/api/auth/login` | 로그인 | 불필요 |

</details>

<details>
<summary>회원 (/api/members)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/members/me` | 내 정보 조회 | ROLE_USER |
| GET | `/api/members/check-email` | 이메일 중복 확인 | 불필요 |
| PATCH | `/api/members/me` | 내 정보 수정 | ROLE_USER |
| PATCH | `/api/members/me/password` | 비밀번호 변경 | ROLE_USER |
| DELETE | `/api/members/me` | 회원 탈퇴 | ROLE_USER |
| GET | `/api/members/me/orders` | 내 주문 목록 조회 | ROLE_USER |

</details>

<details>
<summary>SMS 인증 (/api/sms)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/sms/send` | 인증번호 발송 | 불필요 |
| POST | `/api/sms/verify` | 인증번호 확인 | 불필요 |

</details>

<details>
<summary>이용약관 (/api/terms)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/terms` | 이용약관 목록 조회 | 불필요 |

</details>

<details>
<summary>멤버십 (/api/membership)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/membership/history/{memberId}` | 멤버십 이력 조회 | ROLE_USER |

</details>

---

### 🛒 쇼핑

<details>
<summary>상품 (/api/products)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/products` | 상품 목록 조회 | 불필요 |
| GET | `/api/products/{productId}` | 상품 상세 조회 | 불필요 |
| POST | `/api/products` | 상품 등록 | ROLE_USER |
| PATCH | `/api/products/{productId}` | 상품 수정 | ROLE_USER |
| DELETE | `/api/products/{productId}` | 상품 삭제 | ROLE_USER |
| GET | `/api/products/seller` | 판매자 상품 목록 조회 | ROLE_USER |

</details>

<details>
<summary>카테고리 (/api/categories)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/categories` | 부모 카테고리 목록 조회 | 불필요 |
| GET | `/api/categories/{slug}` | 부모 카테고리 단건 조회 | 불필요 |
| GET | `/api/categories/{parentCategoryId}/child-categories` | 자식 카테고리 목록 조회 | 불필요 |
| GET | `/api/categories/child-categories/{slug}` | 자식 카테고리 단건 조회 | 불필요 |

</details>

<details>
<summary>리뷰 (/api/reviews)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/reviews/{productId}` | 상품 리뷰 목록 조회 | 불필요 |
| POST | `/api/reviews` | 리뷰 작성 | ROLE_USER |
| PATCH | `/api/reviews/{reviewId}` | 리뷰 수정 | ROLE_USER |

</details>

<details>
<summary>장바구니 (/api/carts)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/carts` | 장바구니 상품 추가 | ROLE_USER |
| GET | `/api/carts/{memberId}` | 장바구니 목록 조회 | ROLE_USER |
| DELETE | `/api/carts/{cartItemId}` | 장바구니 상품 단건 삭제 | ROLE_USER |
| DELETE | `/api/carts/selected` | 장바구니 선택 상품 삭제 | ROLE_USER |
| DELETE | `/api/carts/clear` | 장바구니 전체 비우기 | ROLE_USER |

</details>

<details>
<summary>쿠폰 (/api/coupons)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/coupons` | 쿠폰 생성 | ROLE_USER |
| POST | `/api/coupons/{couponId}/members/{memberId}` | 쿠폰 발급 | ROLE_USER |
| GET | `/api/coupons/members/{memberId}` | 회원 쿠폰 목록 조회 | ROLE_USER |
| GET | `/api/coupons/members/{memberId}/unused` | 미사용 쿠폰 목록 조회 | ROLE_USER |
| GET | `/api/coupons/search` | 쿠폰 검색 | ROLE_USER |

</details>

<details>
<summary>주문 (/api/orders)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/orders` | 주문 생성 | ROLE_USER |
| GET | `/api/orders/{orderId}` | 주문 상세 조회 | ROLE_USER |
| GET | `/api/orders/{orderId}/payment` | 결제 정보 조회 | ROLE_USER |
| POST | `/api/orders/addresses` | 배송지 등록 | ROLE_USER |
| GET | `/api/orders/addresses` | 배송지 목록 조회 | ROLE_USER |
| PATCH | `/api/orders/addresses/{addressId}` | 배송지 수정 | ROLE_USER |
| DELETE | `/api/orders/addresses/{addressId}` | 배송지 삭제 | ROLE_USER |

</details>

---

### 🏪 판매자

<details>
<summary>판매자 인증 (/api/seller/auth)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/seller/auth/signup` | 판매자 회원가입 | 불필요 |
| POST | `/api/seller/auth/login` | 판매자 로그인 | 불필요 |
| PATCH | `/api/seller/auth/{sellerId}` | 판매자 정보 수정 | ROLE_USER |
| DELETE | `/api/seller/auth/{sellerId}` | 판매자 탈퇴 | ROLE_USER |

</details>

<details>
<summary>판매자 신청 (/api/sellers)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/sellers/apply` | 판매자 신청 | ROLE_USER |
| GET | `/api/sellers/apply/status` | 판매자 신청 상태 조회 | ROLE_USER |

</details>

---

### 🎧 CS

<details>
<summary>문의 (/api/inquiries)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/inquiries` | 문의 등록 | ROLE_USER |
| GET | `/api/inquiries/{inquiryId}` | 문의 단건 조회 | ROLE_USER |
| GET | `/api/inquiries/my` | 내 문의 목록 조회 | ROLE_USER |
| GET | `/api/inquiries/admin` | 어드민 문의 목록 조회 | ROLE_SUPER_ADMIN / ROLE_CS_ADMIN |
| GET | `/api/inquiries/seller` | 판매자 문의 목록 조회 | ROLE_USER |
| POST | `/api/inquiries/{inquiryId}/answers/admin` | 어드민 문의 답변 등록 | ROLE_SUPER_ADMIN / ROLE_CS_ADMIN |
| POST | `/api/inquiries/{inquiryId}/answers/seller` | 판매자 문의 답변 등록 | ROLE_USER |
| PATCH | `/api/inquiries/{inquiryId}/cancel` | 문의 취소 | ROLE_USER |

</details>

<details>
<summary>클레임 (/api/claims)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/claims` | 클레임 등록 | ROLE_USER |
| GET | `/api/claims/{claimId}` | 클레임 단건 조회 | ROLE_USER |
| GET | `/api/claims/my` | 내 클레임 목록 조회 | ROLE_USER |
| GET | `/api/claims/admin` | 어드민 클레임 목록 조회 | ROLE_USER |
| PATCH | `/api/claims/{claimId}/status` | 클레임 상태 변경 | ROLE_USER |
| PATCH | `/api/claims/{claimId}/reject` | 클레임 거절 | ROLE_USER |
| PATCH | `/api/claims/{claimId}/cancel` | 클레임 취소 | ROLE_USER |
| PATCH | `/api/claims/{claimId}/approve` | 클레임 승인 | ROLE_USER |
| PATCH | `/api/claims/{claimId}/seller-reject` | 판매자 클레임 거절 | ROLE_USER |

</details>

<details>
<summary>첨부파일 (/api/attachments)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/attachments/inquiry/{inquiryId}` | 문의 첨부파일 업로드 | ROLE_USER |
| POST | `/api/attachments/claim/{claimId}` | 클레임 첨부파일 업로드 | ROLE_USER |
| GET | `/api/attachments/inquiry/{inquiryId}` | 문의 첨부파일 목록 조회 | ROLE_USER |
| GET | `/api/attachments/claim/{claimId}` | 클레임 첨부파일 목록 조회 | ROLE_USER |
| DELETE | `/api/attachments/{attachmentId}` | 첨부파일 삭제 | ROLE_USER |

</details>

---

### 🔧 관리자

<details>
<summary>관리자 계정 (/api/admins)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | `/api/admins/login` | 어드민 로그인 | 불필요 |
| POST | `/api/admins` | 어드민 계정 생성 | ROLE_SUPER_ADMIN |
| PATCH | `/api/admins/{adminId}/status` | 어드민 상태 변경 | ROLE_SUPER_ADMIN |

</details>

<details>
<summary>상품 승인 (/api/admin/products)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| PATCH | `/api/admin/products/{productId}/approve` | 상품 승인 | ROLE_SUPER_ADMIN |
| PATCH | `/api/admin/products/{productId}/reject` | 상품 반려 | ROLE_SUPER_ADMIN |

</details>

<details>
<summary>판매자 승인 (/api/admin/sellers)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| PATCH | `/api/admin/sellers/{sellerId}/approve` | 판매자 승인 | ROLE_SUPER_ADMIN |
| PATCH | `/api/admin/sellers/{sellerId}/reject` | 판매자 반려 | ROLE_SUPER_ADMIN |

</details>

<details>
<summary>구매자 관리 (/api/admin/members)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/admin/members` | 구매자 목록 조회 | ROLE_SUPER_ADMIN |
| PATCH | `/api/admin/members/{memberId}/status` | 구매자 상태 변경 (활성/정지) | ROLE_SUPER_ADMIN |

</details>

<details>
<summary>FAQ (/api/faqs)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/faqs` | FAQ 목록 조회 | 불필요 |
| GET | `/api/faqs/category/{category}` | 카테고리별 FAQ 조회 | 불필요 |
| POST | `/api/faqs` | FAQ 등록 | ROLE_SUPER_ADMIN / ROLE_CS_ADMIN |
| PUT | `/api/faqs/{faqId}` | FAQ 수정 | ROLE_SUPER_ADMIN / ROLE_CS_ADMIN |
| PATCH | `/api/faqs/{faqId}/visibility` | FAQ 노출 여부 변경 | ROLE_SUPER_ADMIN / ROLE_CS_ADMIN |
| DELETE | `/api/faqs/{faqId}` | FAQ 삭제 | ROLE_SUPER_ADMIN / ROLE_CS_ADMIN |

</details>

<details>
<summary>공지사항 (/api/notices)</summary>

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | `/api/notices` | 공지사항 목록 조회 | 불필요 |
| GET | `/api/notices/{noticeId}` | 공지사항 단건 조회 | 불필요 |
| POST | `/api/notices` | 공지사항 등록 | ROLE_SUPER_ADMIN / ROLE_CS_ADMIN |
| PUT | `/api/notices/{noticeId}` | 공지사항 수정 | ROLE_SUPER_ADMIN / ROLE_CS_ADMIN |
| DELETE | `/api/notices/{noticeId}` | 공지사항 삭제 | ROLE_SUPER_ADMIN / ROLE_CS_ADMIN |

</details>

---

## 공통 응답 형식

모든 API는 아래 형식으로 응답합니다.

```json
{
  "success": true,
  "message": "요청이 처리되었습니다.",
  "data": { }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `success` | boolean | 요청 성공 여부 |
| `message` | String | 처리 결과 메시지 |
| `data` | Object / null | 응답 데이터 (없을 경우 null) |

실패 시 `success: false`와 함께 해당 HTTP 상태 코드를 반환합니다.

---

## 배포

`develop` 브랜치에 push되면 GitHub Actions를 통해 자동으로 EC2에 배포됩니다.

### 배포 흐름

```
develop 브랜치 push
→ GitHub Actions 트리거
→ Java 21 환경 설정
→ Gradle 빌드 (테스트 제외)
→ AWS 인증
→ 빌드된 JAR 파일을 S3에 업로드
→ SSM을 통해 EC2에서 배포 명령 실행
→ systemctl stop allpick
→ S3에서 JAR 다운로드
→ systemctl start allpick
```

### GitHub Actions Secrets 설정

아래 값들을 GitHub 저장소의 `Settings > Secrets and variables > Actions`에 등록해야 합니다.

| Secret 이름 | 설명 |
|-------------|------|
| `DB_USERNAME` | MySQL 사용자 이름 |
| `DB_PASSWORD` | MySQL 비밀번호 |
| `DB_HOST` | MySQL 호스트 주소 |
| `JWT_SECRET` | JWT 서명 키 |
| `JWT_EXPIRATION_TIME` | JWT 만료 시간 |
| `AWS_ACCESS_KEY` | AWS Access Key (앱 실행용) |
| `AWS_SECRET_KEY` | AWS Secret Key (앱 실행용) |
| `AWS_ACCESS_KEY_ID` | AWS Access Key ID (배포용) |
| `AWS_SECRET_ACCESS_KEY` | AWS Secret Access Key (배포용) |
| `AWS_REGION` | AWS 리전 |
| `S3_IMAGE_BUCKET_NAME` | S3 이미지 버킷 이름 |
| `BACKEND_S3_BUCKET_NAME` | S3 배포 JAR 버킷 이름 |
| `EC2_INSTANCE_ID` | EC2 인스턴스 ID |
| `REDIS_HOST` | Redis 호스트 주소 |
| `COOLSMS_API_KEY` | CoolSMS API Key |
| `COOLSMS_API_SECRET` | CoolSMS API Secret |
| `COOLSMS_SENDER` | SMS 발신 번호 |
| `BUSINESS_API_KEY` | 사업자 번호 검증 API Key |

> 배포 워크플로우 파일 위치: `.github/workflows/deploy.yml`
