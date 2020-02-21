/**
 * ShinyProxy
 *
 * Copyright (C) 2016-2019 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */

package eu.openanalytics.shinyproxy;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.inject.Inject;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    @Inject
    Environment environment;

    private static final String STATIC_FILEPATH_KEY = "static.filepath";
    private static final String STATIC_TARGET_URI_KEY = "static.target-uri";


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (environment.getProperty(STATIC_FILEPATH_KEY) != null)
            registry.addResourceHandler("/pdf/**")
                    .addResourceLocations("file:"+environment.getProperty(STATIC_FILEPATH_KEY));
        if (environment.getProperty(STATIC_TARGET_URI_KEY) != null)
            registry.addResourceHandler("/static-file/**")
                    .addResourceLocations(environment.getProperty(STATIC_TARGET_URI_KEY));
    }


}
