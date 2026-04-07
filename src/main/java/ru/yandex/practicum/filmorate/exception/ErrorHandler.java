package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, WebRequest request) {
        log.warn("Ресурс не найден: {} | {}", ex.getMessage(), getPath(request));
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex, WebRequest request) {
        log.warn("Ошибка валидации: {} | {}", ex.getMessage(), getPath(request));
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(AlreadyExistsException ex, WebRequest request) {
        log.warn("Конфликт данных: {} | {}", ex.getMessage(), getPath(request));
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    // обработка ошибок @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Ошибка валидации");

        log.warn("Ошибка валидации: {} | {}", errorMessage, getPath(request));

        return buildErrorResponse(
                new ValidationException(errorMessage),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    // общий
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        log.error("Неожиданная ошибка: {} | {}", getPath(request), ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // сборка ответа
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex,
            HttpStatus status,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                getPath(request)
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    // получение пути
    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}