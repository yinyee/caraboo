package com.github.yinyee.caraboo;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class JournalEntry {
	
	public long timestamp;
	public String entry;
	public int rating;
	
	public JournalEntry() {
		this.timestamp = 1475953227226l;
		this.entry = "Smashed it today";
		this.rating = 5;
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
		
        return this;
    }
	
	@POST
	@Path("/users/{userid}/journal")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putItem(@PathParam("userid") String userid, JournalEntry entry) {
		
		Map<String, AttributeValue> key = new HashMap<String,AttributeValue>();
		
		key.put("uid", new AttributeValue(userid));
		key.put("timestamp", new AttributeValue().withN(String.valueOf(entry.timestamp)));
		key.put("entry", new AttributeValue(entry.entry));
		key.put("rating", new AttributeValue(String.valueOf(entry.rating)));
		
		Application.getDbClient().putItem(new PutItemRequest().withTableName("caraboo-journalentries").withItem(key));
		
		return Response.noContent().build();
	}

}
