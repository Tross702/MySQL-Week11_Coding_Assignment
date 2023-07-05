package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
    private ProjectDao projectDao = new ProjectDao();

    // This method calls the DAO class to insert a project row.
    public Project addProject(Project project) {
        return projectDao.insertProject(project);
    }

    // This method fetches all projects by calling the DAO class.
    public List<Project> fetchAllProjects() {
        return projectDao.fetchAllProjects();
    }

    // This method fetches a project by its ID by calling the DAO class.
    // If the project does not exist, it throws a NoSuchElementException.
    public Project fetchProjectById(Integer projectId) {
        return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
                "Project with project ID=" + projectId + " does not exist."));
    }

    // This method modifies the details of a project by calling the DAO class.
    // If the project does not exist, it throws a DbException.
    public void modifyProjectDetails(Project project) {
        if (!projectDao.modifyProjectDetails(project)) {
            throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
        }
    }
    
    // This method deletes a project by its ID by calling the DAO class.
    // If the project does not exist, it throws a DbException.
    	public void deleteProject(Integer projectId) {
		if (!projectDao.deleteProject(projectId)) {
            throw new DbException("Project with ID =" + projectId + " does not exist.");
		}
	}
}
