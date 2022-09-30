package it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.mongo.impl;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.IDocumentRepo;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.repository.entity.DocumentReferenceETY;
import it.finanze.sanita.fse2.ms.edssrvdataprocessor.utility.ProfileUtility;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;


@Repository
public class DocumentRepo implements IDocumentRepo, Serializable {

	/**
	 * Serial version UID 
	 */
	private static final long serialVersionUID = -8230326857671732231L;

	
	@Autowired
	private transient MongoTemplate mongoTemplate; 
	
	@Autowired
	private transient ProfileUtility profileUtility;
		
	String collection = Constants.ComponentScan.INGESTION_STAGING;

	
	@Override
	public DocumentReferenceETY insert(DocumentReferenceETY ety) throws OperationException {
		try {
			return mongoTemplate.insert(ety, getCollectionName()); 
		} catch(MongoException ex) {
			throw new OperationException(Constants.Logs.ERROR_MONGO_INSERT, ex); 
		}
	} 
	
	@Override
	public void deleteByIdentifier(String identifier) {
		Query query = Query.query(Criteria.where(Constants.App.IDENTIFIER).is(identifier)); 
		mongoTemplate.remove(query, getCollectionName()); 	
	}
	
	@Override
	public DocumentReferenceETY findById(String id) {
		DocumentReferenceETY ety = mongoTemplate.findById(id, DocumentReferenceETY.class, getCollectionName()); 
		return ObjectUtils.isEmpty(ety) ? null : ety;
	} 
	
	@Override
	public List<DocumentReferenceETY> findAll() {
		return mongoTemplate.findAll(DocumentReferenceETY.class, getCollectionName()); 
	}

	public String getCollectionName() {
		return profileUtility.isTestProfile() ?  Constants.Profile.TEST_PREFIX + '-' + Constants.ComponentScan.INGESTION_STAGING : Constants.ComponentScan.INGESTION_STAGING;
	}
}
