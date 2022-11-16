/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error.base.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OutOfRangeException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.MiscUtility;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import static it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants.Logs.ERR_VAL_UNABLE_CONVERT;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;

/**
 * Used to build an error response from a given DTO 
 *
 */
@Data
@Builder
public final class ErrorBuilderDTO {

    /**
     * Private constructor to disallow to access from other classes
     */
    private ErrorBuilderDTO() {}

    public static ErrorResponseDTO createConstraintError(LogTraceInfoDTO trace, ConstraintViolationException ex) {
        // Retrieve the first constraint error
        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();
        String field = MiscUtility.extractKeyFromPath(violation.getPropertyPath());
        // Return associated information
        return new ErrorResponseDTO(
            trace,
            ErrorType.VALIDATION.getType(),
            ErrorType.VALIDATION.getTitle(),
            violation.getMessage(),
            SC_BAD_REQUEST,
            ErrorType.VALIDATION.toInstance(ErrorInstance.Validation.CONSTRAINT_FIELD, field)
        );
    }

    /**
     * Creates a Generic Error Response 
     * 
     * @param trace  The LogTraceInfo DTO 
     * @param ex  Exception 
     * @return  The error response 
     */
    public static ErrorResponseDTO createGenericError(LogTraceInfoDTO trace, Exception ex) {
        return new ErrorResponseDTO(
            trace,
            ErrorType.SERVER.getType(),
            ErrorType.SERVER.getTitle(),
            ex.getMessage(),
            SC_INTERNAL_SERVER_ERROR,
            ErrorType.SERVER.toInstance(ErrorInstance.Server.INTERNAL)
        );
    }


    /**
     * Creates a Operation Error Response 
     * 
     * @param trace  The LogTraceInfo DTO 
     * @param ex  Exception 
     * @return ErrorResponseDTO  The error response 
     */
    public static ErrorResponseDTO createOperationError(LogTraceInfoDTO trace, OperationException ex) {
        return new ErrorResponseDTO(
            trace,
            ErrorType.SERVER.getType(),
            ErrorType.SERVER.getTitle(),
            ex.getMessage(),
            SC_INTERNAL_SERVER_ERROR,
            ErrorType.SERVER.toInstance(ErrorInstance.Server.INTERNAL)
        );
    }

    public static ErrorResponseDTO createArgumentMismatchError(LogTraceInfoDTO trace, MethodArgumentTypeMismatchException ex) {
        return new ErrorResponseDTO(
            trace,
            ErrorType.VALIDATION.getType(),
            ErrorType.VALIDATION.getTitle(),
            String.format(
                ERR_VAL_UNABLE_CONVERT,
                ex.getName(),
                ex.getParameter().getParameter().getType().getSimpleName()
            ),
            SC_BAD_REQUEST,
            ErrorType.VALIDATION.toInstance(ErrorInstance.Validation.CONSTRAINT_FIELD, ex.getName())
        );
    }

    public static ErrorResponseDTO createOutOfRangeError(LogTraceInfoDTO trace, OutOfRangeException ex) {
        return new ErrorResponseDTO(
            trace,
            ErrorType.VALIDATION.getType(),
            ErrorType.VALIDATION.getTitle(),
            ex.getMessage(),
            SC_BAD_REQUEST,
            ErrorType.VALIDATION.toInstance(ErrorInstance.Validation.CONSTRAINT_FIELD, ex.getField())
        );
    }


    /**
     * Creates a Unsupported Operation Error Response 
     * 
     * @param trace  The LogTraceInfo DTO 
     * @param ex  Exception 
     * @return ErrorResponseDTO  The error response 
     */
    public static ErrorResponseDTO createUnsupportedOperationError(LogTraceInfoDTO trace, UnsupportedOperationException ex) {
        return new ErrorResponseDTO(
                trace,
                ErrorType.CLIENT.getType(),
                ErrorType.CLIENT.getTitle(),
                ex.getMessage(),
                SC_BAD_REQUEST,
                ErrorType.CLIENT.toInstance(ErrorInstance.Client.UNSUPPORTED)
        );
    }


    /**
     * Creates a Connection Refused Error Response 
     * 
     * @param trace  The LogTraceInfo DTO 
     * @param ex  Exception 
     * @return ErrorResponseDTO  The error response 
     */
    public static ErrorResponseDTO createConnectionRefusedError(LogTraceInfoDTO trace, ConnectionRefusedException ex) {
        return new ErrorResponseDTO(
            trace,
            ErrorType.SERVER.getType(),
            ErrorType.SERVER.getTitle(),
            ex.getMessage(),
            SC_INTERNAL_SERVER_ERROR,
            ErrorType.SERVER.toInstance(ErrorInstance.Server.INTERNAL)
        );
    } 

}
