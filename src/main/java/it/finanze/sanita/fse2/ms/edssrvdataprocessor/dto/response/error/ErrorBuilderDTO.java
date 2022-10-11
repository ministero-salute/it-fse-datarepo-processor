package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.error.base.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.DocumentNotFoundException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.EmptyIdentifierException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import lombok.Builder;
import lombok.Data;

import static org.apache.http.HttpStatus.*;

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
     * @return  The error response 
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


    /**
     * Creates a Unsupported Operation Error Response 
     * 
     * @param trace  The LogTraceInfo DTO 
     * @param ex  Exception 
     * @return  The error response 
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
     * @return  The error response 
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
    

    /**
     * Creates a Document Not Found Error Response 
     * 
     * @param trace  The LogTraceInfo DTO 
     * @param ex  Exception 
     * @return  The error response 
     */
    public static ErrorResponseDTO createDocumentNotFoundError(LogTraceInfoDTO trace, DocumentNotFoundException ex) {
        return new ErrorResponseDTO(
            trace,
            ErrorType.RESOURCE.getType(),
            ErrorType.RESOURCE.getTitle(),
            ex.getMessage(),
            SC_NOT_FOUND,
            ErrorType.RESOURCE.toInstance(ErrorInstance.Resource.NOT_FOUND)
        );
    } 
    

    /**
     * Creates a Empty Identifier Error Response 
     * 
     * @param trace  The LogTraceInfo DTO 
     * @param ex  Exception 
     * @return  The error response 
     */
    public static ErrorResponseDTO createEmptyIdentifierError(LogTraceInfoDTO trace, EmptyIdentifierException ex) {
            return new ErrorResponseDTO(
                trace,
                ErrorType.RESOURCE.getType(),
                ErrorType.RESOURCE.getTitle(),
                ex.getMessage(),
                SC_NOT_FOUND,
                ErrorType.RESOURCE.toInstance(ErrorInstance.Resource.EMPTY)
            );           
    }
}
