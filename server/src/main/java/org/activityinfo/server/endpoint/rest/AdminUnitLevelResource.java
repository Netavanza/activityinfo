package org.activityinfo.server.endpoint.rest;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.activityinfo.server.database.hibernate.entity.AdminEntity;
import org.activityinfo.server.database.hibernate.entity.AdminLevel;
import org.activityinfo.server.endpoint.rest.model.NewAdminEntity;
import org.activityinfo.server.endpoint.rest.model.NewAdminLevel;
import org.activityinfo.server.endpoint.rest.model.UpdatedAdminLevel;
import org.activityinfo.server.util.blob.BlobNotFoundException;
import org.activityinfo.server.util.blob.BlobService;
import org.apache.log4j.Logger;

import com.google.common.io.ByteStreams;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.view.Viewable;

public class AdminUnitLevelResource {

    private static final Logger LOGGER = Logger.getLogger(AdminUnitLevelResource.class.getName());
    
    private Provider<EntityManager> entityManager;
    private AdminLevel level;
    private BlobService blobService;

    public AdminUnitLevelResource(Provider<EntityManager> entityManager, BlobService blobService, AdminLevel level) {
        super();
        this.entityManager = entityManager;
        this.blobService = blobService;
        this.level = level;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable get() {
        return new Viewable("/resource/AdminUnitLevel.ftl", level);
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(UpdatedAdminLevel updatedLevel) {
        level.setName(updatedLevel.getName());
  
        return Response.ok().build();
    }

    @GET
    @Path("/units")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<AdminEntity> getUnits() {
        return level.getEntities();
    }
    
    @PUT
    @Path("/units")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUnits(List<AdminUnit> units) {
        return Response.ok().build();
    }
    
    @POST
    @Path("/childLevel")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postNewLevel(NewAdminLevel newLevel) {
        AdminLevel child = new AdminLevel();
        child.setCountry(level.getCountry());
        child.setName(newLevel.getName());
        child.setParent(level);
        
        for(NewAdminEntity entity : newLevel.getEntities()) {
            AdminEntity childEntity = new AdminEntity();
            childEntity.setName(entity.getName());
            childEntity.setLevel(child);
            childEntity.setCode(entity.getCode());
            childEntity.setBounds(entity.getBounds());
            if(entity.getParentId() != null) {
                childEntity.setParent(entityManager.get().getReference(AdminEntity.class, entity.getParentId()));
            }
            child.getEntities().add(childEntity);
        }
        
        level.getChildLevels().add(child);
        
        entityManager.get().persist(child);
        
        return Response.ok().build();
    }
    
    @GET
    @Path("/geometry/wkb")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getWkbGeometry() throws IOException {
        try {
            LOGGER.info("Get");

            return Response
                .ok(ByteStreams.toByteArray(blobService.get(wkbKey())))
                .build();
            
        } catch (BlobNotFoundException e) {
            throw new NotFoundException();
        } 
    }
    
    @PUT
    @Path("/geometry/wkb")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public void putWkbGeometry(byte[] wkb) throws IOException {
        blobService.put(wkbKey(), ByteStreams.newInputStreamSupplier(wkb));
    }

    private String wkbKey() {
        return "/adminGeometry/" + level.getId() + ".wkb.gz";
    }
}
