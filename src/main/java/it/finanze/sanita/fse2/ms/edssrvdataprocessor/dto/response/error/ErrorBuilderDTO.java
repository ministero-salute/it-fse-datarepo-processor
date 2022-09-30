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

@Data
@Builder
public final class ErrorBuilderDTO {

    /**
     * Private constructor to disallow to access from other classes
     */
    private ErrorBuilderDTO() {}


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
