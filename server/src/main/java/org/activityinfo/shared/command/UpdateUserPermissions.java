package org.activityinfo.shared.command;

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

import org.activityinfo.shared.command.result.VoidResult;
import org.activityinfo.shared.dto.UserDatabaseDTO;
import org.activityinfo.shared.dto.UserPermissionDTO;

/**
 * Update the permissions of a user to access a given database.
 * 
 * The permissions are updated based on email address as the client will not
 * necessarily know whether a given person already has an account with
 * ActivityInfo. This means that a call to UpdateUserPermissions can result in
 * the creation of a new user account and all that entails, such as an email
 * message, etc.
 * 
 */
public class UpdateUserPermissions implements MutatingCommand<VoidResult> {

    private int databaseId;
    private UserPermissionDTO model;

    protected UpdateUserPermissions() {

    }

    public UpdateUserPermissions(UserDatabaseDTO db, UserPermissionDTO model) {
        this(db.getId(), model);
    }

    public UpdateUserPermissions(int databaseId, UserPermissionDTO model) {
        this.databaseId = databaseId;
        this.model = model;

    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public UserPermissionDTO getModel() {
        return model;
    }

    public void setModel(UserPermissionDTO model) {
        this.model = model;
    }
}
