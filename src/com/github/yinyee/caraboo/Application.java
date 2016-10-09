package com.github.yinyee.caraboo;

import org.glassfish.jersey.server.ResourceConfig;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

public class Application extends ResourceConfig {	
	private static AmazonDynamoDBClient sDbClient;
	private static AmazonSimpleEmailServiceClient sSESClient;
	private static SentimentAnalyzer sSentimentAnalyzer;
	
	public synchronized static AmazonDynamoDBClient getDbClient() {
		if (sDbClient == null) {
			AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient();
			dbClient.withRegion(Regions.EU_WEST_1);
			sDbClient = dbClient;
		}
		return sDbClient;
	}
	
	public synchronized static AmazonSimpleEmailServiceClient getSESClient() {
		if (sSESClient == null) {
			AmazonSimpleEmailServiceClient sesClient = new AmazonSimpleEmailServiceClient();
			sesClient.withRegion(Regions.EU_WEST_1);
			sSESClient = sesClient;
		}
		return sSESClient;
	}
	
	public synchronized static SentimentAnalyzer getSentimentAnalyzer() {
		if (sSentimentAnalyzer == null) {
			sSentimentAnalyzer = new SentimentAnalyzer();
		}
		return sSentimentAnalyzer;
	}

	public Application() {
		packages(getClass().getCanonicalName());
        	register(User.class);
        	register(Question.class);
        	register(JournalEntry.class);
	}

}
