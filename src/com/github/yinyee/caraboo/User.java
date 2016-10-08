package com.github.yinyee.caraboo;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
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
		this.username = "Me";
	}
	
	@GET
	@Path("/users/{id}")
	@Produces(MediaType.APPLICATION_JSON)
    public User getItem(@PathParam("id") String userId) {
		Map<String, AttributeValue> key = new HashMap<String,AttributeValue>();
		key.put("id", new AttributeValue(userId));
		Map<String, AttributeValue> value = Application.getDbClient().getItem(new GetItemRequest().withTableName("caraboo-users").withKey(key)).getItem();
		// TODO use value
        return this;
    }

	@PUT
	@Path("/users/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    public Response putItem(@PathParam("id") String userId, User user) {
		Map<String, AttributeValue> item = new HashMap<String,AttributeValue>();
		item.put("id", new AttributeValue(userId));
		Application.getDbClient().putItem(new PutItemRequest().withTableName("caraboo-users").withItem(item));
        return Response.noContent().build();
    }
}
