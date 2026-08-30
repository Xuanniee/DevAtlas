package com.xuannie.devatlas.common.adapter.advice;

import com.xuannie.devatlas.common.error.ConflictException;
import com.xuannie.devatlas.common.error.ErrorResponseFactory;
import com.xuannie.devatlas.common.error.NotFoundException;
import com.xuannie.devatlas.workspace.common.exceptions.WorkspaceAlreadyExistsException;
import com.xuannie.devatlas.workspace.common.exceptions.WorkspaceNotFoundException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler to catch any exception thrown in the controller, so that if
 * any exception thrown is one we recognise, we do not return a generic error response
 * but a specific one.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
//    // Handle when any request failed to be validated with @Valid in any Controller
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException exception) {
//        // Store all errors in a hashmap
//        Map<String, String> errors = new HashMap<>();
//        // Extract the main reason the exception was thrown and the DTO fields that caused it
//        exception.getBindingResult().getFieldErrors().forEach((error -> {
//            // Store the error message as the value for each field that had an issue in @Valid DTO Validation
//            errors.put(error.getField(), error.getDefaultMessage());
//        }));
//
//        return ResponseEntity.badRequest().body(errors);
//    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        ProblemDetail pd = ErrorResponseFactory.of(HttpStatus.BAD_REQUEST, "Validation failed");
        Map<String, String> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage(), (a, b) -> a));
        pd.setProperty("errors", fieldErrors);
        return ResponseEntity.status(status).body(pd);
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException exception) {
        // Build an Error Response with the Factory
        return ErrorResponseFactory.of(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException exception) {
        return ErrorResponseFactory.of(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception exception) {
        // log it — this is the "something we didn't anticipate" bucket
        return ErrorResponseFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error");
    }
}
