/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.controller.handler;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error.base.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OutOfRangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error.ErrorBuilderDTO.*;

/**
 *	Exceptions handler
 */
@ControllerAdvice
@Slf4j
public class ExceptionCTL extends ResponseEntityExceptionHandler {

    /**
     * Tracker log.
     */
    @Autowired
    private Tracer tracer;

    /**
     * Handles Connection Refused Exception 
     * @param ex  Exception 
     * @return ErrorResponseDTO  A DTO representing the error response 
     */
    @ExceptionHandler(ConnectionRefusedException.class)
    protected ResponseEntity<ErrorResponseDTO> handleConnectionRefusedException(ConnectionRefusedException ex) {
        // Log me
        log.warn(Constants.Logs.ERROR_CONNECTION_REFUSED);
        log.error(Constants.Logs.ERROR_CONNECTION_REFUSED, ex);
        // Create error DTO
        ErrorResponseDTO out = createConnectionRefusedError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    } 
    
    
    /**
     * Handles Generic Exception 
     * 
     * @param ex  Exception 
     * @return ErrorResponseDTO  A DTO representing the error response 
     */
    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        // Log me
        log.warn(Constants.Logs.ERROR_HANDLER_GENERIC_EXCEPTION);
        log.error(Constants.Logs.ERROR_HANDLER_GENERIC_EXCEPTION, ex);
        // Create error DTO
        ErrorResponseDTO out = createGenericError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle operation exception.
     *
     * @param ex		exception
     * @return ErrorResponseDTO  The Exception to be returned
     */
    @ExceptionHandler(OperationException.class)
    protected ResponseEntity<ErrorResponseDTO> handleOperationException(OperationException ex) {
        // Log me
        log.error("HANDLER handleOperationException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createOperationError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles an invalid index reference
     *
     * @param ex exception
     */
    @ExceptionHandler(OutOfRangeException.class)
    protected ResponseEntity<ErrorResponseDTO> handleOutOfRangeException(OutOfRangeException ex) {
        // Log me
        log.error("HANDLER handleOutOfRangeException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createOutOfRangeError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles exceptions thrown by the validation check performed on the request submitted by the user.
     *
     * @param ex exception
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        // Log me
        log.error("HANDLER handleConstraintViolationException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createConstraintError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles Unsupported Operation Exception 
     * 
     * @param ex  Exception 
     * @return ErrorResponseDTO  A DTO representing the error response 
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    protected ResponseEntity<ErrorResponseDTO> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        // Log me
        log.warn(Constants.Logs.ERROR_HANDLER_UNSUPPORTED_OPERATION_EXCEPTION);
        log.error(Constants.Logs.ERROR_HANDLER_UNSUPPORTED_OPERATION_EXCEPTION, ex);
        // Create error DTO
        ErrorResponseDTO out = createUnsupportedOperationError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Generate a new {@link LogTraceInfoDTO} instance
     * @return The new instance
     */
    private LogTraceInfoDTO getLogTraceInfo() {
        // Create instance
        LogTraceInfoDTO out = new LogTraceInfoDTO(null, null);
        // Verify if context is available
        if (tracer.currentSpan() != null) {
            out = new LogTraceInfoDTO(
                tracer.currentSpan().context().spanIdString(),
                tracer.currentSpan().context().traceIdString());
        }
        // Return the log trace
        return out;
    }
}
