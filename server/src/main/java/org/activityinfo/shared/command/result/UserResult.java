package org.activityinfo.shared.command.result;

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

import org.activityinfo.shared.dto.UserPermissionDTO;

/**
 * @see org.activityinfo.shared.command.GetUsers
 * 
 * @author Alex Bertram
 */
public class UserResult extends PagingResult<UserPermissionDTO> {

    public UserResult() {
    }

    public UserResult(List<UserPermissionDTO> data) {
        super(data);
    }

    public UserResult(List<UserPermissionDTO> data, int offset, int totalCount) {
        super(data, offset, totalCount);
    }
}
