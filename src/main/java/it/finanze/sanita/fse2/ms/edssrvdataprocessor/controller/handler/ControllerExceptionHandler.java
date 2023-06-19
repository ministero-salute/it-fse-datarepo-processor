/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

import java.util.Date;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error.ErrorBuilderDTO.*;

/**
 *	Exceptions handler
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

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
     * Handles exceptions thrown by the inability to convert a certain value from a type X to a type Y.
     * (e.g. {@link String} to {@link Date})
     *
     * @param ex exception
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        // Log me
        log.error("HANDLER MethodArgumentTypeMismatchException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createArgumentMismatchError(getLogTraceInfo(), ex);
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
