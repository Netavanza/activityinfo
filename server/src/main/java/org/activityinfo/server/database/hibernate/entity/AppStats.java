package org.activityinfo.server.database.hibernate.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "appstats")
public class AppStats {
    private int id;
    private long timeCreated;
    private String json;

    public AppStats() {
    }

    public AppStats(Date dateCreated, String json) {
        this.timeCreated = dateCreated.getTime();
        this.json = json;
    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return this.id;
    }

    public void setId(int siteId) {
        this.id = siteId;
    }

    @Basic
    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Lob
    public String getJson() {
        return this.json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
