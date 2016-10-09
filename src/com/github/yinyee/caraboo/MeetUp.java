package com.github.yinyee.caraboo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class MeetUp {
	
	public String uid;
	public int mid;
	public String value;
	
	public MeetUp() {
		
	}
	
	public MeetUp(String uid, int mid, String value) {
		this.uid = uid;
		this.mid = mid;
		this.value = value;
	}
	
	@GET
	@Path("/users/{userid}/meetups")
	@Produces(MediaType.APPLICATION_JSON)
    public List<MeetUp> getItems(@PathParam("userid") String userid) {
		
		Map<String, AttributeValue> key = new HashMap<String,AttributeValue>();
		key.put(":uid", new AttributeValue(userid));
		
		QueryRequest qr = new QueryRequest()
		.withTableName("caraboo-meetups")
		.withKeyConditionExpression("uid = :uid")
		.withExpressionAttributeValues(key);

		List<Map<String, AttributeValue>> items = Application.getDbClient().query(qr).getItems();

		List<MeetUp> meetups = new ArrayList<MeetUp>();
		
		for (Map<String, AttributeValue> item : items) {
			MeetUp meetup = new MeetUp();
			meetup.uid = userid;
			meetup.mid = Integer.parseInt(item.get("mid").getS());
			meetup.value = item.get("value").getS();
			meetups.add(meetup);
		}
		
        return meetups;
    }

}