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

import com.mongodb.*;
import eu.openanalytics.containerproxy.model.spec.ContainerSpec;
import eu.openanalytics.containerproxy.model.spec.ProxyAccessControl;
import eu.openanalytics.containerproxy.model.spec.ProxySpec;
import eu.openanalytics.containerproxy.spec.IProxySpecProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "proxy.mongodb")
public class MongoDBSpecProvider implements IProxySpecProvider {

    @Inject
    private Environment environment;

    private MongoClient mongoClient;

    private List<ProxySpec> specList;

    private String collection;

    private String database;

    private String uri;

    @Override
    public List<ProxySpec> getSpecs() {
        try {
            DBCollection apps = getDBCollection();
            specList = apps.find().toArray().stream().map(MongoDBSpecProvider::transformToProxySpec).collect(Collectors.toList());
            return specList;
        } catch (UnknownHostException e) {
            return null;
        }
    }

    protected MongoClientURI getMongoURI(){
        String uri = getUri();
        if (uri != null)
            return new MongoClientURI(uri);
        else
            throw new RuntimeException("No Mongo URI specified");
    }

    protected static ProxySpec transformToProxySpec(DBObject obj){
        ProxySpec spec = new ProxySpec();
        if (obj == null)
            return null;

        Object id = obj.get("id");
        if (id != null)
            spec.setId(id.toString());

        Object displayName = obj.get("display-name");
        if (displayName != null)
            spec.setDisplayName(displayName.toString());

        Object description = obj.get("description");
        if (description != null)
            spec.setDescription(description.toString());

        ContainerSpec containerSpec = toContainerSpec(obj);
        spec.setContainerSpecs(Collections.singletonList(containerSpec));

        spec.setAccessControl(toAccessControl(obj));

        return spec;
    }

    private static ProxyAccessControl toAccessControl(DBObject obj) {
        ProxyAccessControl accessControl = new ProxyAccessControl();
        Object accessGroups = obj.get("access-groups");
        if(accessGroups != null){
            if (accessGroups instanceof BasicDBList){
                accessControl.setGroups(((BasicDBList) accessGroups).stream().map(Object::toString).collect(Collectors.toList()).toArray(new String[0]));
            }
            return accessControl;
        }

        return null;
    }

    private static ContainerSpec toContainerSpec(DBObject obj) {
        ContainerSpec cSpec = new ContainerSpec();
        Map<String, Integer> portMapping = new HashMap<>();
        portMapping.put("default", 3838);
        cSpec.setPortMapping(portMapping);
        Object cmd = obj.get("container-cmd");
        if (cmd != null) {
            if (cmd instanceof BasicDBList){
                BasicDBList cmdList = (BasicDBList) cmd;
                cSpec.setCmd(cmdList.stream().map(Object::toString).collect(Collectors.toList()).toArray(new String[0]));
            }
        }
        Object image = obj.get("container-image");
           if (image != null)
            cSpec.setImage(image.toString());

        Object network = obj.get("container-network");
        if(network != null)
            cSpec.setNetwork(network.toString());

        return cSpec;
    }

    @Override
    public ProxySpec getSpec(String id) {
        try {
            DBCollection apps = getDBCollection();
            ProxySpec proxySpec = transformToProxySpec(apps.findOne(new BasicDBObject().append("id", id)));
            logger.debug(proxySpec);
            return proxySpec;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected MongoClient getMongoClient() throws UnknownHostException {
        if (mongoClient == null){
            this.mongoClient = new MongoClient(getMongoURI());
        }
        return mongoClient;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    private DBCollection getDBCollection() throws UnknownHostException {
        MongoClient mongoClient = getMongoClient();
        String database = getDatabase();
        String collection = getCollection();
        return mongoClient.getDB(database).getCollection(collection);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
