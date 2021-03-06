package org.activityinfo.server.command.handler;

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

import java.util.Date;

import javax.persistence.EntityManager;

import org.activityinfo.server.database.hibernate.entity.Project;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.database.hibernate.entity.UserDatabase;
import org.activityinfo.shared.command.AddProject;
import org.activityinfo.shared.command.result.CommandResult;
import org.activityinfo.shared.command.result.CreateResult;
import org.activityinfo.shared.dto.ProjectDTO;
import org.activityinfo.shared.exception.CommandException;

import com.google.inject.Inject;

/*
 * Adds given Project to the database
 */
public class AddProjectHandler implements CommandHandler<AddProject> {

    private final EntityManager em;

    @Inject
    public AddProjectHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public CommandResult execute(AddProject cmd, User user)
        throws CommandException {

        UserDatabase db = em.find(UserDatabase.class, cmd.getDatabaseId());

        ProjectDTO from = cmd.getProjectDTO();
        Project project = new Project();
        project.setName(from.getName());
        project.setDescription(from.getDescription());
        project.setUserDatabase(db);

        db.setLastSchemaUpdate(new Date());

        em.persist(project);
        em.persist(db);
        db.getProjects().add(project);

        return new CreateResult(project.getId());
    }
}