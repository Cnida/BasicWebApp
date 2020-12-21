package com.github.cnida.basicwebapp;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;

@Component
public class RestRepositoryConfigurator implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        // Expose the ID's so that they can be used by the client.
        config.exposeIdsFor(Todo.class);
    }


}
