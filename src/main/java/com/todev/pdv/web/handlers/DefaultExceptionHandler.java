package com.todev.pdv.web.handlers;

import com.todev.pdv.common.dtos.ErrorResponseBody;
import com.todev.pdv.core.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponseBody> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                             HttpServletRequest request) {
        var error = new ErrorResponseBody(
                LocalDateTime.now(),
                400,
                "Os argumentos recebidos são inválidos ou estão ausentes!",
                request.getServletPath(),
                new HashSet<>()
        );

        exception.getAllErrors().forEach(requestError -> error.details().add(requestError.getDefaultMessage()));

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(ModelNotFoundException.class)
    protected ResponseEntity<ErrorResponseBody> handleModelNotFound(ModelNotFoundException exception,
                                                                    HttpServletRequest request) {
        var error = new ErrorResponseBody(
                LocalDateTime.now(),
                404,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler(ConstraintConflictException.class)
    protected ResponseEntity<ErrorResponseBody> handleConstraintConflict(ConstraintConflictException exception,
                                                                         HttpServletRequest request) {
        var error = new ErrorResponseBody(
                LocalDateTime.now(),
                409,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, CONFLICT);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    protected ResponseEntity<ErrorResponseBody> handlePermissionDenied(PermissionDeniedException exception,
                                                                       HttpServletRequest request) {
        var error = new ErrorResponseBody(
                LocalDateTime.now(),
                500,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(DependencyInUseException.class)
    protected ResponseEntity<ErrorResponseBody> handleDependencyInUse(DependencyInUseException exception,
                                                                      HttpServletRequest request) {
        var error = new ErrorResponseBody(
                LocalDateTime.now(),
                500,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(DuplicatedItemException.class)
    protected ResponseEntity<ErrorResponseBody> handleDuplicatedItems(DuplicatedItemException exception,
                                                                      HttpServletRequest request) {
        var error = new ErrorResponseBody(
                LocalDateTime.now(),
                400,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(NotEnoughStockException.class)
    protected ResponseEntity<ErrorResponseBody> handleNotEnoughStock(NotEnoughStockException exception,
                                                                     HttpServletRequest request) {
        var error = new ErrorResponseBody(
                LocalDateTime.now(),
                400,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, BAD_REQUEST);
    }
}
