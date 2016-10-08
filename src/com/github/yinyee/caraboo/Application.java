package com.github.yinyee.caraboo;

import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {
	public Application() {
		packages(getClass().getCanonicalName());
        register(User.class);
	}

}
