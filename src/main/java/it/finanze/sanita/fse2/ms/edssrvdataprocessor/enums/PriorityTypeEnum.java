package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import lombok.Getter;

@Getter
public enum PriorityTypeEnum {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private final String description;

    PriorityTypeEnum(String description) {
        this.description = description;
    }
}
