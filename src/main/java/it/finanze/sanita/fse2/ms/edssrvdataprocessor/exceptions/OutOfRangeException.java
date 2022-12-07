/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions;

import lombok.Getter;

/**
 * Represent an invalid index upon a sortable, indexable data
 */
public class OutOfRangeException extends Exception {

    @Getter
    private final String field;

    /**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public OutOfRangeException(final String msg, final String field) {
        super(msg);
        this.field = field;
    }
}