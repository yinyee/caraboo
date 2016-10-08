package com.github.yinyee.caraboo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
	 * Returns the requested User object
	 * @param itemid
	 * @return
	 */
	@GET
	@Path("/users/user/journal/{timestamp}")
    public JournalEntry getItem(@PathParam("entry") String itemid) {
        return this;
    }

}
