package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FhirOperationDTO {
    private String masterIdentifier;
    private String jsonString;
}
