package com.github.yinyee.caraboo;

import org.glassfish.jersey.server.ResourceConfig;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class Application extends ResourceConfig {
	
	private static AmazonDynamoDBClient sDbClient;
	
	public synchronized static AmazonDynamoDBClient getDbClient() {
		if (sDbClient == null) {
			AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient();
			dbClient.withRegion(Regions.EU_WEST_1);
			sDbClient = dbClient;
		}
		return sDbClient;
	}
	
	public Application() {
		packages(getClass().getCanonicalName());
        	register(User.class);
        	register(Question.class);
        	register(JournalEntry.class);
	}

}
