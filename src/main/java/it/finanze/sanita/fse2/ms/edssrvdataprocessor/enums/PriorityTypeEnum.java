package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import lombok.Getter;

/**
 * Enum that describes the priority type 
 *
 */
@Getter
public enum PriorityTypeEnum {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

	/**
	 * Priority Description 
	 */
    private final String description;

    PriorityTypeEnum(String description) {
        this.description = description;
    }
}
