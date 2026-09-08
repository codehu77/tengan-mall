package com.tengan.mall.devtools.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

/** 一次性內部工具，不用分自訂例外階層——把常見失敗原因轉成一句人看得懂的訊息回前端顯示就好。 */
@RestControllerAdvice
public class ApiExceptionHandler {

    public record ErrorBody(String message) {
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorBody> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorBody(e.getMessage()));
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorBody> handleRestClientResponse(RestClientResponseException e) {
        String message = "呼叫後端服務失敗（HTTP " + e.getStatusCode().value() + "）：" + e.getResponseBodyAsString();
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorBody(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleGeneric(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorBody(e.getClass().getSimpleName() + "：" + e.getMessage()));
    }
}
