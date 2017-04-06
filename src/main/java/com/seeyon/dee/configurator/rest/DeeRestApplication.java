package com.seeyon.dee.configurator.rest;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by Administrator on 2016-9-20.
 */
public class DeeRestApplication extends ResourceConfig {
    public DeeRestApplication() {

        packages("com.seeyon.v3x.dee.srv.rest.resources");
        //register(LoggingFilter.class);
    }
}
