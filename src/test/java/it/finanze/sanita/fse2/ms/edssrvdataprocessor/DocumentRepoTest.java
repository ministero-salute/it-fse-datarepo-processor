/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.edssrvdataprocessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.enums.ProcessorOperationEnum;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.IngestionStagingETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.IDocumentRepo;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EmbeddedKafka
class DocumentRepoTest {

	@Autowired
	private IDocumentRepo documentRepo;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private static final String wii = "WII_TEST";
	
	@BeforeEach
	void initialize() throws OperationException {
		IngestionStagingETY etyPublish = new IngestionStagingETY();
		etyPublish.setIdentifier("IDENTIFIER");
		etyPublish.setOperation(ProcessorOperationEnum.PUBLISH);
		etyPublish.setWorkflowInstanceId(wii);
		
		IngestionStagingETY etyReplace = new IngestionStagingETY();
		etyReplace.setIdentifier("IDENTIFIER");
		etyReplace.setOperation(ProcessorOperationEnum.REPLACE);
		etyReplace.setWorkflowInstanceId(wii);
		
		documentRepo.insert(etyPublish);
		documentRepo.insert(etyReplace);
		
	}
	
	
	@Test
	void testDelete() throws OperationException {
		assertEquals(2L, count());
		assertTrue(documentRepo.deleteById(wii, ProcessorOperationEnum.PUBLISH));
		List<IngestionStagingETY> list = findOperation();
		assertEquals(1, list.size());
		assertEquals(ProcessorOperationEnum.REPLACE, list.get(0).getOperation());
		assertTrue(documentRepo.deleteById(wii, ProcessorOperationEnum.REPLACE));
		assertEquals(0, count());
	}
	
	private long count() {
		Query query = new Query();
		query.addCriteria(Criteria.where("workflow_instance_id").is(wii));
		return mongoTemplate.count(query, IngestionStagingETY.class);
	}
	
	private List<IngestionStagingETY> findOperation() {
		Query query = new Query();
		query.addCriteria(Criteria.where("workflow_instance_id").is(wii));
		return mongoTemplate.find(query, IngestionStagingETY.class);
	}
}
