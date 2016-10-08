package com.github.yinyee.caraboo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

@Path("/")
public class User {
	
	public String username;
	public ArrayList<Question> questions;
	public ArrayList<JournalEntry> journal;
	
	public User() {
		
		this.username = "Me";
		
		this.questions = new ArrayList<Question>();
		questions.add(new Question());
		
		this.journal = new ArrayList<JournalEntry>();
		journal.add(new JournalEntry());
		
	}
	
	/**
	 * Returns the requested User object
	 * @param itemid
	 * @return
	 */
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
	
	// Saves a new answer
	@POST
	@Path("/users/{userid}/questions/{qid}")
	@Consumes("application/json")
	public void newAnswer(@PathParam("userid") String itemid, @PathParam("qid") int qid, Question question) {
		questions.add(question);
	}
	
	// Saves a new journal entry
	@POST
	@Path("/users/{userid}/journal")
	@Consumes("application/json")
	public void newJournalEntry(@PathParam("userid") String itemid, JournalEntry entry) {
		journal.add(entry);
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
	
	@POST
	@Path("/users/{id}/journal/{journal}/notify")
	public Response getItem(@PathParam("id") String user, @PathParam("journal") long timestamp, @QueryParam("destination") String destination) {
		if (!destination.isEmpty()) {
			String journalEntry = "I was asked to speak at a panel as an expert.";
			// TODO read the journal entry back from the DynamoDB
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
