# API 엔드포인트

## 인증 (Authentication)

- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인

## 상품 (Product)

- `POST /api/products/category/add` - 카테고리 등록
- `GET /api/products/categories` - 카테고리 목록 조회
- `PUT /api/products/category/{categoryId}` - 카테고리 수정
- `DELETE /api/products/category/{categoryId}` - 카테고리 삭제
- `POST /api/products/add` - 상품 등록
- `GET /api/products/list` - 상품 목록 조회
- `GET /api/products/{productId}` - 상품 상세 조회
- `PUT /api/products/{productId}` - 상품 수정 (관리자)
- `DELETE /api/products/{productId}` - 상품 삭제 (관리자)

## 주문 (Order)

- `POST /api/orders/cart/add` - 장바구니 담기 (인증 필요)
- `GET /api/orders/cart` - 장바구니 조회 (인증 필요)
- `PUT /api/orders/cart/update/{cartItemId}` - 장바구니 항목 수량 변경 (인증 필요)
- `DELETE /api/orders/cart/delete/{cartItemId}` - 장바구니 항목 삭제 (인증 필요)
- `DELETE /api/orders/cart/clear` - 장바구니 전체 비우기 (인증 필요)
- `POST /api/orders/create` - 주문하기 (인증 필요)
- `GET /api/orders/list` - 주문 내역 조회 (인증 필요)
- `GET /api/orders/{orderId}/detail` - 주문 상세 조회 (인증 필요)
- `POST /api/orders/{orderId}/cancel` - 주문 취소 (인증 필요)
- `PATCH /api/orders/{orderId}/status` - 주문 상태 및 배송 정보 변경 (관리자, 인증 필요)

