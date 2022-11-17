package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.tx;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response.LogTraceInfoDTO;
import lombok.Data;

import java.util.Date;

@Data
public class DeleteTxResDTO {
    private String traceID;
    private String spanID;
    private Date timestamp;
    private long deletedTransactions;

    public DeleteTxResDTO(LogTraceInfoDTO info, Date timestamp, long deletedTransactions) {
        this.traceID = info.getTraceID();
        this.spanID = info.getSpanID();
        this.timestamp = timestamp;
        this.deletedTransactions = deletedTransactions;
    }
}
