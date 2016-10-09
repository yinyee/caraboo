package com.github.yinyee.caraboo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class JournalEntry {
	
	public long timestamp;
	public String entry;
	public int rating;
	public double sentiment;
	
	public JournalEntry() {
		this.timestamp = 1475953227226l;
		this.entry = "Smashed it today";
		this.rating = 5;
		this.sentiment = 0d;
	}
	
	/**
	 * Returns the requested JournalEntry object
	 * @param itemid
	 * @return
	 */
	@GET
	@Path("/users/{userid}/journal/{timestamp}")
	@Produces(MediaType.APPLICATION_JSON)
    public JournalEntry getItem(@PathParam("userid") String userid, @PathParam("timestamp") String timestamp) {
		
		Map<String, AttributeValue> key = new HashMap<String,AttributeValue>();
		key.put("uid", new AttributeValue(userid));
		key.put("timestamp", new AttributeValue().withN(timestamp));
		
		Map<String, AttributeValue> value = Application.getDbClient().getItem(new GetItemRequest().withTableName("caraboo-journalentries").withKey(key)).getItem();
		this.timestamp = Long.parseLong(timestamp);
		this.entry = value.get("entry").getS();
		this.rating = Integer.parseInt(value.get("rating").getS());
		this.sentiment = Double.parseDouble(value.get("sentiment").getN());
		
        return this;
    }
	
	/**
	 * Returns all the journal entries available for the specified user id
	 * @param userid
	 * @return
	 */
	@GET
	@Path("/users/{userid}/journal")
	@Produces(MediaType.APPLICATION_JSON)
    public List<JournalEntry> getItems(@PathParam("userid") String userid) {
		
		Map<String, AttributeValue> key = new HashMap<String,AttributeValue>();
		key.put(":uid", new AttributeValue(userid));
		
		QueryRequest qr = new QueryRequest()
		.withTableName("caraboo-journalentries")
		.withKeyConditionExpression("uid = :uid")
		.withExpressionAttributeValues(key);

		List<Map<String, AttributeValue>> items = Application.getDbClient().query(qr).getItems();

		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		
		for (Map<String, AttributeValue> item : items) {
			JournalEntry entry = new JournalEntry();
			entry.timestamp = Long.parseLong(item.get("timestamp").getN());
			entry.entry = item.get("entry").getS();
			entry.rating = Integer.parseInt(item.get("rating").getS());
			entry.sentiment = Double.parseDouble(item.get("sentiment").getN());
			journalEntries.add(entry);
		}
		
        return journalEntries;
    }
	
	@POST
	@Path("/users/{userid}/journal")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JournalEntry putItem(@PathParam("userid") String userid, JournalEntry entry) {

		double sentiment = 0d;
		try {
			sentiment = Application.getSentimentAnalyzer().analyze(entry.entry);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, AttributeValue> key = new HashMap<String,AttributeValue>();
		entry.sentiment = sentiment;
		
		key.put("uid", new AttributeValue(userid));
		key.put("timestamp", new AttributeValue().withN(String.valueOf(entry.timestamp)));
		key.put("entry", new AttributeValue(entry.entry));
		key.put("rating", new AttributeValue(String.valueOf(entry.rating)));
		key.put("sentiment", new AttributeValue().withN(String.valueOf(sentiment)));
		
		Application.getDbClient().putItem(new PutItemRequest().withTableName("caraboo-journalentries").withItem(key));
		
		return entry;
	}
	
	@POST
	@Path("/users/{userid}/journal/{timestamp}/notify")
	public Response getItem(@PathParam("userid") String user, @PathParam("timestamp") String timestamp, @QueryParam("destination") String destination) {
		if (!destination.isEmpty()) {
			String journalEntry = getItem(user, timestamp).entry;
			
			String html = "<html>" +
					"<head>" +
					"<title>" + 
					"Kudos time!" +
					"</title>" + 
					"</head>" +
					"<body>" +
					"<h1>Kudos time!</h1>" +
					"<p>Anna has achieved something big today!</p>" +
					"<ul><li>" + journalEntry + "</li></ul>" +
					"<p>Help Anna celebrate her win! Do something for her today that will put an extra smile on her face!</p>" +
					"<p><button>I accept!</button>&nbsp;<button>Another time...</button></p>" +
					"</body>" +
					"</html>";

			SendEmailRequest ser = new SendEmailRequest()
			.withSource("notification@caraboo.uk.to")
			.withDestination(new Destination().withToAddresses(destination))
			.withMessage(new Message().withSubject(new Content().withData("Kudos time!")).withBody(new Body().withHtml(new Content(html))));

			Application.getSESClient().sendEmail(ser);
		}
		return Response.noContent().build();
	}

}