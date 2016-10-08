package com.github.yinyee.caraboo;

import javax.websocket.server.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class User {
	public String username;
	
	public User() {
		this.username = "Me";
	}
	
	@GET
	@Path("/users/{user}")
    public User getItem(@PathParam("user") String itemid) {
        return this;
    }


}
