package com.systemsolution.commons.handler;


import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import static java.util.Objects.nonNull;


/**
 * 
 * @author Brandon Duarte
 * <p>RestException Handler is used when an exception is throwed in any part of code
 * 
 */

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RestControllerAdvice
@Slf4j
public class KodinguExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ABORT_EXCEPTION = "java.io.IOException: Se ha anulado una conexi�n establecida por el software en su equipo host.";
	private static final String ERROR2 = "Error {}";

	@Override
	   protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
	       String error = "Malformed JSON request";
	       log.error(ERROR2, ex);
	       return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
	   }

	   private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
	       return new ResponseEntity<>(apiError, apiError.getStatus());
	   }
	   
	   /**
	    * Handles RegisterFailedException and BadCredentials.
	    * @param ex
	    * @param request
	    * @return the api error object.
	    */
	   
	@ExceptionHandler({  BadCredentialsException.class, Exception.class,
			ExpiredJwtException.class })
	public ResponseEntity<Object> handleRegisterDeniedException(Exception ex, WebRequest request) {
		boolean isError = nonNull(ex) && nonNull(ex.getCause()) && ex.getCause().toString().contains(ABORT_EXCEPTION);
		if (isError) {
			return null;
		}
		log.error(ERROR2, ex);
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR: " + ex.getMessage(), ex));
	}
	   
	    /**
	     * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
	     *
	     * @param ex the ConstraintViolationException
	     * @return the ApiError object
	     */
	    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
	    protected ResponseEntity<Object> handleConstraintViolation(
	            javax.validation.ConstraintViolationException ex) {
	        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
	        apiError.setMessage("Validation error");
	        log.error(ERROR2, ex);
	        apiError.addValidationErrors(ex.getConstraintViolations());
	        return buildResponseEntity(apiError);
	    }
	    
	    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	    protected ResponseEntity<Object> accessDenied(
	    		org.springframework.security.access.AccessDeniedException ex) {
	        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
	        apiError.setMessage("Usted no tiene accesos para realizar esta accion.");
	        log.error(ERROR2, ex);
	        return buildResponseEntity(apiError);
	    }
	    
	    /**
	     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
	     *
	     * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
	     * @param headers HttpHeaders
	     * @param status  HttpStatus
	     * @param request WebRequest
	     * @return the ApiError object
	     */
	    @Override
	    protected ResponseEntity<Object> handleMethodArgumentNotValid(
	            MethodArgumentNotValidException ex,
	            HttpHeaders headers,
	            HttpStatus status,
	            WebRequest request) {
	    	log.error(ERROR2, ex);
	        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
	        apiError.setMessage("Validation error");
	        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
	        apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
	        return buildResponseEntity(apiError);
	    }
}	
