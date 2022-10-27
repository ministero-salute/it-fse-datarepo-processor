package it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.mongo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import it.finanze.sanita.fse2.ms.edssrvdataprocessor.config.Constants;

/**
 * 
 * @author vincenzoingenito
 *
 *	Configuration for MongoDB.
 */
@Configuration
@EnableMongoRepositories(basePackages = Constants.ComponentScan.CONFIG_MONGO)
public class MongoDatabaseCFG {

	/**
	 * Mongo properties Configuration 
	 */
	@Autowired
	private MongoPropertiesCFG mongoPropertiesCFG;

	/**
	 * App Context 
	 */
    @Autowired
    private ApplicationContext appContext;
 
    final List<Converter<?, ?>> conversions = new ArrayList<>();

    /**
     * Database Factory 
     * @return MongoDatabaseFactory  MongoDatabaseFactory 
     */
    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(){
        return new SimpleMongoClientDatabaseFactory(mongoPropertiesCFG.getUri());
    }

    /**
     * Returns the Mongo Template 
     * 
     * @return MongoTemplate  Mongo Template 
     */
    @Bean
    @Primary
    public MongoTemplate mongoTemplate() {
        final MongoDatabaseFactory factory = mongoDatabaseFactory();

        final MongoMappingContext mongoMappingContext = new MongoMappingContext();
        mongoMappingContext.setApplicationContext(appContext);

        MappingMongoConverter converter =
                new MappingMongoConverter(new DefaultDbRefResolver(factory), mongoMappingContext);

        converter.setTypeMapper(new DefaultMongoTypeMapper(null));


        return new MongoTemplate(factory, converter);
    }
}