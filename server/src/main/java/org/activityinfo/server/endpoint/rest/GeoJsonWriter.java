package org.activityinfo.server.endpoint.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.activityinfo.server.database.hibernate.entity.AdminEntity;
import org.codehaus.jackson.JsonGenerator;

public class GeoJsonWriter {

    private StringWriter writer = new StringWriter();
    private JsonGenerator json;
  
    public GeoJsonWriter() throws IOException {

        
    }
    
    public void write(List<AdminEntity> entities) throws IOException {
        json = Jackson.createJsonFactory(writer);
        json.writeStartObject();
        json.writeStringField("type", "FeatureCollection");
        json.writeArrayFieldStart("features");
        
        for(AdminEntity entity : entities) {
            json.writeStartObject();
            json.writeStringField("type", "Feature");
            json.writeNumberField("id", entity.getId());
        }
        
    }
}

