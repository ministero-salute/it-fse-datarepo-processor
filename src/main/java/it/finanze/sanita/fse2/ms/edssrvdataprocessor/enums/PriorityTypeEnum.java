package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import lombok.Getter;

/**
 * Enum that describes the priority type 
 *
 */
@Getter
public enum PriorityTypeEnum {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH");

	/**
	 * Priority Description 
	 */
    private final String description;

    PriorityTypeEnum(String description) {
        this.description = description;
    }
}
