/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor.client.base;

@FunctionalInterface
public interface MultiClientCallback<K> {
    void request(K key, K value) throws Exception;
}
