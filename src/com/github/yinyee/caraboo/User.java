package com.github.yinyee.caraboo;

import java.util.ArrayList;

import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
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
	@Path("/users/{user}")
    public User getItem(@PathParam("user") String itemid) {
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

}