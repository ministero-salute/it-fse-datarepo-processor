/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.UIDModeEnum;
import lombok.Getter;

/** 
 * The Enum containing the different UUID generation modes 
 *
 */
public enum UIDModeEnum {
    
    IP_UUID(1, "IP_UUID"),
    HOSTNAME_UUID(2, "HOSTNAME_UUID"),
    UUID_UUID(3, "UUID_UUID");

	/**
	 * The ID of the Enum 
	 */
    @Getter
    private Integer id;

    /**
     * The description of the Enum 
     */
    @Getter
    private String description;

    private UIDModeEnum(Integer inId, String inDescription) {
        id = inId;
        description = inDescription;
    }

    public static UIDModeEnum get(Integer inId) {
        UIDModeEnum out = null;
        for (UIDModeEnum v : UIDModeEnum.values()) {
            if (v.getId().equals(inId)) {
                out = v;
                break;
            }
        }
        return out;
    }
}