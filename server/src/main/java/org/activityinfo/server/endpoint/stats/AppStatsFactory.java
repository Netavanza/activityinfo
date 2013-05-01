package org.activityinfo.server.endpoint.stats;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.activityinfo.server.database.hibernate.entity.AppStats;
import org.activityinfo.server.util.date.DateCalc;
import org.activityinfo.server.util.date.DateFormatter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Provider;

public class AppStatsFactory {
    private final Provider<EntityManager> emp;

    @Inject
    public AppStatsFactory(Provider<EntityManager> emp) {
        this.emp = emp;
    }

    @SuppressWarnings("unchecked")
    public AppStats create() {
        Date now = new Date();

        JsonObject json = new JsonObject();
        json.addProperty("date", DateFormatter.formatDateTime(now));

        // totals
        json.addProperty("userTotal",
            (Long) emp.get().createQuery("select count(*) from User").getSingleResult());
        json.addProperty("databaseTotal",
            (Long) emp.get().createQuery("select count(*) from UserDatabase").getSingleResult());
        json.addProperty("siteTotal",
            (Long) emp.get().createQuery("select count(*) from Site").getSingleResult());
        json.addProperty("activityTotal",
            (Long) emp.get().createQuery("select count(*) from Activity").getSingleResult());
        json.addProperty("attributegroupTotal",
            (Long) emp.get().createQuery("select count(*) from AttributeGroup").getSingleResult());
        json.addProperty("attributevalueTotal",
            (Long) emp.get().createQuery("select count(*) from AttributeValue").getSingleResult());
        json.addProperty("indicatorTotal",
            (Long) emp.get().createQuery("select count(*) from Indicator").getSingleResult());
        json.addProperty("indicatorvalueTotal",
            (Long) emp.get().createQuery("select count(*) from IndicatorValue").getSingleResult());
        json.addProperty("reportTotal",
            (Long) emp.get().createQuery("select count(*) from ReportDefinition").getSingleResult());

        // site data
        // @formatter:off
        List<Object[]> siteRows = 
            emp.get().createNativeQuery(
                "select " +
                    "s.siteid siteId, " +
                    "(select count(*) from sitehistory h where h.siteid = s.siteid) histTotal, " +
                    "(select count(*) from sitehistory h where h.siteid = s.siteid and h.timecreated > ?1) histWeek " +
                "from site s " +
                "order by siteId")
            .setParameter(1, DateCalc.daysAgo(now, 7).getTime())
            .getResultList();
        // @formatter:on
        JsonArray sites = new JsonArray();
        for (Object[] siteRow : siteRows) {
            JsonObject site = new JsonObject();
            site.addProperty("id", String.valueOf(siteRow[0]));
            site.addProperty("at", String.valueOf(siteRow[1])); // total site activity
            site.addProperty("aw", String.valueOf(siteRow[2])); // activity since last week
            sites.add(site);
        }
        json.add("sites", sites);
        
        return new AppStats(now, json.toString());
    }
}
