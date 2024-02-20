package com.todev.pdv.web.handlers;

import com.todev.pdv.common.dtos.ErrorResponse;
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
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                         HttpServletRequest request) {
        var error = new ErrorResponse(
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
    protected ResponseEntity<ErrorResponse> handleModelNotFound(ModelNotFoundException exception,
                                                                HttpServletRequest request) {
        var error = new ErrorResponse(
                LocalDateTime.now(),
                404,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler(ConstraintConflictException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintConflict(ConstraintConflictException exception,
                                                                     HttpServletRequest request) {
        var error = new ErrorResponse(
                LocalDateTime.now(),
                409,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, CONFLICT);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    protected ResponseEntity<ErrorResponse> handlePermissionDenied(PermissionDeniedException exception,
                                                                   HttpServletRequest request) {
        var error = new ErrorResponse(
                LocalDateTime.now(),
                500,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(DependencyInUseException.class)
    protected ResponseEntity<ErrorResponse> handleDependencyInUse(DependencyInUseException exception,
                                                                  HttpServletRequest request) {
        var error = new ErrorResponse(
                LocalDateTime.now(),
                500,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(DuplicatedItemException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicatedItems(DuplicatedItemException exception,
                                                                  HttpServletRequest request) {
        var error = new ErrorResponse(
                LocalDateTime.now(),
                400,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(NotEnoughStockException.class)
    protected ResponseEntity<ErrorResponse> handleNotEnoughStock(NotEnoughStockException exception,
                                                                 HttpServletRequest request) {
        var error = new ErrorResponse(
                LocalDateTime.now(),
                400,
                exception.getMessage(),
                request.getServletPath(),
                Set.of()
        );

        return new ResponseEntity<>(error, BAD_REQUEST);
    }
}
