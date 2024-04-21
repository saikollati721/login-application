package com.dbs.assessment.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = InvalidRequestException.class)
    public ResponseEntity invalidStoreException(InvalidRequestException exception) {
        Map<String, List<String>> errorMessagesMap = new HashMap<>();
        errorMessagesMap.put("errorMessages", List.of(exception.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessagesMap);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity userNotFoundException(Exception exception) {
        Map<String, List<String>> errorMessagesMap = new HashMap<>();
        errorMessagesMap.put("errorMessages", List.of(exception.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessagesMap);
    }

    @ExceptionHandler(value = AccountLockedException.class)
    public ResponseEntity accountLockedException(AccountLockedException exception) {
        Map<String, List<String>> errorMessagesMap = new HashMap<>();
        errorMessagesMap.put("errorMessages", List.of(exception.getMessage()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessagesMap);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity invalidRequest(MethodArgumentNotValidException exception) {
        List<String> errorMessagesList = exception.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        Map<String, List<String>> errorMessagesMap = new HashMap<>();
        errorMessagesMap.put("errorMessages", errorMessagesList);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessagesMap);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity constraintViolation(ConstraintViolationException exception) {
        List<String> templateMessages = new ArrayList<>();
        exception.getConstraintViolations().forEach(violation -> templateMessages.add(violation.getMessageTemplate()));
        Map<String, List<String>> errorMessagesMap = new HashMap<>();
        errorMessagesMap.put("errorMessages", templateMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessagesMap);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity nonReadableException(HttpMessageNotReadableException exception) {
        String exceptionMessage = exception.getMessage();
        if (StringUtils.hasText(exceptionMessage) && isEnumValidationError(exceptionMessage)) {
            return buildEnumValidationErrorResponse(exceptionMessage);
        }
        Map<String, List<String>> errorMessagesMap = new HashMap<>();
        errorMessagesMap.put("errorMessages", List.of(exceptionMessage));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessagesMap);
    }

    private ResponseEntity buildEnumValidationErrorResponse(String exceptionMessage) {
        String enumClass = getEnumClassFromErrorMessage(exceptionMessage);
        String validEnumValues = getValidEnumValuesFromErrorMessage(exceptionMessage);
        log.error("Not a valid value for enum {} . Valid validEnumValues are {} ", enumClass, validEnumValues);
        String errorMessage =
                String.format("Invalid enum value for %s. Valid validEnumValues are %s ", enumClass, validEnumValues);
        Map<String, List<String>> errorMessagesMap = new HashMap<>();
        errorMessagesMap.put("errorMessages", List.of(errorMessage));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessagesMap);
    }

    private String getEnumClassFromErrorMessage(String exceptionMessage) {
        int firstIndex = exceptionMessage.indexOf("`");
        int lastIndex = exceptionMessage.lastIndexOf("`");
        if (firstIndex == -1 || lastIndex == -1) {
            return "";
        }
        String enumClassWithPackageName = exceptionMessage.substring(firstIndex + 1, lastIndex);

        return StringUtils.hasText(enumClassWithPackageName)
                ? getEnumClassNameWithoutPackageName(enumClassWithPackageName)
                : "";
    }

    private String getEnumClassNameWithoutPackageName(String enumClassWithPackageName) {
        int lastIndexOf = enumClassWithPackageName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return enumClassWithPackageName.substring(lastIndexOf + 1);
    }

    private String getValidEnumValuesFromErrorMessage(String exceptionMessage) {
        int indexOfOpeningBracket = exceptionMessage.indexOf("[");
        int indexOfClosingBracket = exceptionMessage.indexOf("]");

        if (indexOfOpeningBracket == -1 || indexOfClosingBracket == -1) {
            return "";
        }
        return exceptionMessage.substring(indexOfOpeningBracket + 1, indexOfClosingBracket);
    }

    private boolean isEnumValidationError(String exceptionMessage) {
        return exceptionMessage.contains("not one of the values accepted for Enum class");
    }
}
