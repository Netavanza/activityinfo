package org.activityinfo.server.database.hibernate.dao;

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

/**
 * Defines common methods of Data Access Objects (DAOs)
 * 
 * @param <T>
 *            The entity type (should be annotated with @Entity)
 * @param <K>
 *            The type of the entity's @Id property
 * 
 * @author Alex Bertram
 */
public interface DAO<T, K> {

    /**
     * Persists the entity to the datastore.
     * 
     * Same as enitityManager.persist(entity)
     * 
     * @param entity
     */
    void persist(T entity);

    /**
     * @param primaryKey
     * @return the entity with the specified key or null if no such entity
     *         exists
     */
    T findById(K primaryKey);
}
