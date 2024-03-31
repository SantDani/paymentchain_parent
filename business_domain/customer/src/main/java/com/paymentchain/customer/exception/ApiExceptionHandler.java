/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.exception;

import com.paymentchain.customer.common.StandardizedApiExeptionResponse;
import java.net.URI;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 *
 * ApiExceptionHandler<br><br>
 *
 * Describes the five levels of <b>HTTP response codes</b> utilized in standard
 * HTTP communications, signifying various types of responses from the server to
 * the client. These response codes are organized into categories:
 * <ol>
    * <li><b>100-level (Informational)</b> - The server acknowledges that the
    * request was received and understood. This is a transient response indicating
    * the client should await the final response.</li>
    * <li><b>200-level (Success)</b> - Indicates that the server has successfully
    * processed the request as expected.</li>
    * <li><b>300-level (Redirection)</b> - Suggests the client needs to undertake
    * further actions to complete the request.</li>
    * <li><b>400-level (Client Error)</b> - Implies the request sent by the client
    * was invalid.</li>
    * <li><b>500-level (Server Error)</b> - The server failed to fulfill an
    * otherwise valid request due to an internal server error.</li>
 * </ol>
 * The aim of <b>exception handling</b> is to provide the client with an
 * appropriate response code along with additional, understandable information
 * to aid in troubleshooting the issue. The <b>message part</b> of the response
 * should be user-friendly, and if the client sends an "<b>Accept-Language</b>"
 * header (e.g., English or French), the title part should be translated into
 * the client's language if internationalization is supported. The <b>detail</b>
 * is primarily intended for the developer of the client application, so
 * translation is not necessary. If there is a need to report more than one
 * error, a list of errors can be returned in the response.
 *
 * @author SantiagoSRP
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<StandardizedApiExeptionResponse> handleUnknownHostException(UnknownHostException ex) {
        logger.error("UnknownHostException intercepted by ApiExceptionHandler", ex);

        StandardizedApiExeptionResponse response
                = new StandardizedApiExeptionResponse("Connection error", "erorr-1024", ex.getMessage());
        return new ResponseEntity(response, HttpStatus.PARTIAL_CONTENT);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<StandardizedApiExeptionResponse> handleBusinessTransaction(BusinessRuleException ex) {
        
        logger.error("BusinessRuleException intercepted by ApiExceptionHandler", ex);
        
        StandardizedApiExeptionResponse response
                = new StandardizedApiExeptionResponse("Validation error", ex.getCode(), ex.getMessage());
        return new ResponseEntity(response, HttpStatus.PARTIAL_CONTENT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardizedApiExeptionResponse> handleGeneralException(Exception ex) {
        logger.error("handleGeneralException intercepted by ApiExceptionHandler", ex);
        StandardizedApiExeptionResponse response
                = new StandardizedApiExeptionResponse("Internal Server Error", "error-500", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(WebClientResponseException.ServiceUnavailable.class)
    public ResponseEntity<StandardizedApiExeptionResponse> handleServiceUnavailableException(WebClientResponseException.ServiceUnavailable ex) {
        // Log the exception details, including the specific service that is unavailable, if possible
        logger.error("Service Unavailable: A service is temporarily unavailable.", ex);
        //TODO.. mirar de arreglar esto
    
        logger.error("Request Method: {}", ex.getRequest().getMethod());
        logger.error("Request URL: {}", ex.getRequest().getURI());
        // Create and return a standardized response entity indicating service unavailability
        StandardizedApiExeptionResponse response = new StandardizedApiExeptionResponse(
                "Service Unavailable",
                "SERVICE_UNAVAILABLE",
                "The service is temporarily unavailable. Please try again later. " + ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
