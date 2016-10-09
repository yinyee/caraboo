package com.github.yinyee.caraboo;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

@Path("/")
public class User {
	
	public String username;
	
	public User() {
		
	}
	
	public User(String username) {
		this.username = username;
	}
	
	/**
	 * Returns the requested User object
	 * @param itemid
	 * @return
	 */
	@GET
	@Path("/users/{userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public User getItem(@PathParam("userid") String userId) {
		
		Map<String, AttributeValue> key = new HashMap<String,AttributeValue>();
		key.put("uid", new AttributeValue(userId));
		
		Map<String, AttributeValue> value = Application.getDbClient().getItem(new GetItemRequest().withTableName("caraboo-users").withKey(key)).getItem();
		this.username = value.get("username").getS();
		
		return this;
	}

	/**
	 * Posts a new user entry into the caraboo-users table
	 * @param userId
	 * @param user
	 * @return
	 */
	@POST
	@Path("/users/{userid}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putItem(@PathParam("userid") String userId, User user) {
		
		Map<String, AttributeValue> item = new HashMap<String,AttributeValue>();
		
		item.put("uid", new AttributeValue(userId));
		item.put("username", new AttributeValue(user.username));
		
		Application.getDbClient().putItem(new PutItemRequest().withTableName("caraboo-users").withItem(item));
		
		return Response.noContent().build();
	}
	
}
