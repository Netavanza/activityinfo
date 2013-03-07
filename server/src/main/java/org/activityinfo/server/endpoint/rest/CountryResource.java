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

import java.util.List;
import java.util.Set;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.activityinfo.server.database.hibernate.entity.AdminLevel;
import org.activityinfo.server.database.hibernate.entity.Country;

import com.google.inject.Inject;
import com.sun.jersey.api.view.Viewable;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/country")
@Produces(MediaType.APPLICATION_JSON)
public class CountryResource {



    private Country country;

    public CountryResource(Country country) {
        this.country = country;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getPage() {
        return new Viewable("/resource/Country.ftl", country);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Country getJson() {
        return country;
    }

    
    @GET
    @Path("adminLevels")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<AdminLevel> getAdminLevels() {
        return country.getAdminLevels();
    }

}
