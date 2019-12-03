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

package eu.openanalytics.shinyproxy.spec.provider;

import eu.openanalytics.containerproxy.spec.IProxySpecProvider;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
@Primary
public class SpecProviderFactory extends AbstractFactoryBean<IProxySpecProvider> {

    @Inject
    private Environment environment;

    @Inject
    private MongoDBSpecProvider mongoDBSpecProvider;

    @Inject
    private ShinyProxySpecProvider shinyProxySpecProvider;

    @Override
    public Class<?> getObjectType() {
        return IProxySpecProvider.class;
    }

    @Override
    protected IProxySpecProvider createInstance() throws Exception {
        IProxySpecProvider provider = null;

        String spec = environment.getProperty("proxy.spec-provider", "none");
        if (spec.equals("mongodb"))
            provider = mongoDBSpecProvider;
        else
            provider = shinyProxySpecProvider;

        return provider;

    }



}
