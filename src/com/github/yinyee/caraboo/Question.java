package com.github.yinyee.caraboo;

import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class Question {
	
	public int answer;
	public long lastAnswered;
	
	public Question() {
		this.answer = 2;
		this.lastAnswered = System.currentTimeMillis();
	}
	
	public void setAnswer(int answer) {
		this.answer = answer;
		this.lastAnswered = System.currentTimeMillis();
	}
	
	@GET
	@Path("/users/user/questions/{qid}")
    public Question getItem(@PathParam("question") String itemid) {
        return this;
    }
	
}