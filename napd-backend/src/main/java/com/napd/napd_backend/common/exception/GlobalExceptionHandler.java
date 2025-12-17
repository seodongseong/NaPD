package com.napd.napd_backend.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 비즈니스 로직 예외 (IllegalArgumentException)
    // 주로 서비스 레이어에서 throw한 권한 부족, 데이터 없음 등을 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("비즈니스 로직 예외 발생 : {}", e.getMessage());

        // 메시지 내용에 따라 상태 코드를 분기할 수 있습니다.
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if(e.getMessage().contains("권한") || e.getMessage().contains("작성자")) {
            status = HttpStatus.FORBIDDEN; // 403
        } else  if (e.getMessage().contains("존재하지 않")){
            status = HttpStatus.NOT_FOUND; // 404
        }
        return ResponseEntity.status(status).body("[오류] " + e.getMessage());
    }

    // 2. @Valid 검증 실패 시 (400 Bad Request)
    // DTO의 @NotBlank, @Size 등이 위반되었을 떄 발생
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e){
        String firstErrorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("입력값 검증 실패: {}", firstErrorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[검증 오류] " + firstErrorMessage);
    }

    // 3. JSON 파싱 에러 또는 바디 누락 (400 Bad Requset)
    // 'Body 없는 POST 요청' 같은 경우 처리합니다.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("JSON 파싱 에러 발생 : 요청 본문이 비어있거나 형식이 잘못되었습니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[형식 오류] 요청 바디 (JSON)를 확인해 주세요.");
    }

    // 4. URL 파라미터 타입 불일치 (400 Bad Request)
    // /api/contents/abc 처럼 숫자가 와야 할 곳에 문자가 올 때
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("타입 불일치 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("[경로 오류] 요청 주소의 형식이 잘못되었습니다.");
    }

    // 5. 그 외 모든 예외 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception e) {
        log.error("미처리 예외 발생!", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("[서버 에러] 관리자에게 문의하세요.");
    }



}