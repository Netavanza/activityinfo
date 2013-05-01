package org.activityinfo.server.endpoint.stats;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.activityinfo.server.database.hibernate.dao.Transactional;
import org.activityinfo.server.database.hibernate.entity.AppStats;

import com.google.inject.Provider;

@Path(AppStatsResource.ENDPOINT)
public class AppStatsResource {
    public static final String ENDPOINT = "/admin/appstats";

    private Provider<EntityManager> emp;
    private AppStatsFactory factory;

    @Inject
    public AppStatsResource(Provider<EntityManager> emp, AppStatsFactory factory) {
        super();
        this.emp = emp;
        this.factory = factory;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String serve() {
        return factory.create().getJson();
    }

    @GET
    @Path("/persist")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public String persist() {
        AppStats stats = factory.create();
        emp.get().persist(stats);
        return stats.getJson();
    }
}
