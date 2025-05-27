package ru.practicum.stat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase(),
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleServletRequestParameterException(final MissingServletRequestParameterException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase(),
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase().toUpperCase(),
                "Unknown error",
                e.getMessage(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
    }
}
