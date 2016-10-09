package com.github.yinyee.caraboo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SentimentAnalyzer {
	public static String sApiKey;

	public SentimentAnalyzer() {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("hpehavenondemand.properties");
			if (is != null) {
				Properties props = new Properties();
				props.load(is);
				sApiKey = (String)props.get("apikey");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double analyze(String message) throws IOException {
		String url = "https://api.havenondemand.com/1/api/sync/analyzesentiment/v2?apikey=" + sApiKey + "&text=" + URLEncoder.encode(message, "UTF-8");
	    
	    org.apache.http.client.HttpClient client = new DefaultHttpClient();
	    HttpPost post = new HttpPost(url);
	    HttpResponse response = client.execute(post);
	    
	    JsonFactory factory = new JsonFactory();
	    JsonParser jp = factory.createParser(response.getEntity().getContent());
	    jp.setCodec(new ObjectMapper());
	    JsonNode actualObj = jp.readValueAsTree();

	    if (actualObj.get("sentiment_analysis") != null) {
			return actualObj.get("sentiment_analysis").get(0).get("aggregate").get("score").asDouble();	    	
	    } else {
	    	return 0d;
	    }
	}
}
