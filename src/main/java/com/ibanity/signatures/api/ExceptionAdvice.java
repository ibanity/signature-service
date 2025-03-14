package com.ibanity.signatures.api;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.ibanity.signatures.services.exception.EncryptionException;
import com.ibanity.signatures.services.exception.SignaturePartsException;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SignaturePartsException.class)
    public ResponseEntity<?> handleSignaturePartsException(SignaturePartsException e) {
        logger.error("# SignaturePartsException", e);

        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<?> handleEncryptionException(EncryptionException e) {
        logger.error("# EncryptionException", e);

        return ResponseEntity.internalServerError().build();
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null, request);

		return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(
			Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        logger.error("# " + ex.getClass(), ex);
        
		if (request instanceof ServletWebRequest servletWebRequest) {
			HttpServletResponse response = servletWebRequest.getResponse();
			if (response != null && response.isCommitted()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Response already committed. Ignoring: " + ex);
				}
				return null;
			}
		}

		if (body == null && ex instanceof ErrorResponse errorResponse) {
			body = errorResponse.updateAndGetBody(super.getMessageSource(), LocaleContextHolder.getLocale());
		}

		if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR) && body == null) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		}

		return createResponseEntity(body, headers, statusCode, request);
	}

}
