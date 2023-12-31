package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

// Class uses JDBC to perform CRUD operations with project tables.
public class ProjectDao extends DaoBase {
    private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    @SuppressWarnings("unused")
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "step";

    // Inserts a project into the database and returns the inserted project with its generated ID.
    public Project insertProject(Project project) {
        String sql = "INSERT INTO " + PROJECT_TABLE +
                     " (project_name, estimated_hours, actual_hours, difficulty, notes) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);

                stmt.executeUpdate();

                Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
                commitTransaction(conn);

                project.setProjectId(projectId);
                return project;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    // Fetches all projects from the database.
    public List<Project> fetchAllProjects() {
        String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    List<Project> projects = new LinkedList<>();

                    while (rs.next()) {
                        projects.add(extract(rs, Project.class));
                    }

                    return projects;
                }
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    // Fetches a project by its ID from the database.
    public Optional<Project> fetchProjectById(Integer projectId) {
        String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try {
                Project project = null;

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    setParameter(stmt, 1, projectId, Integer.class);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            project = extract(rs, Project.class);
                        }
                    }
                }

                if (Objects.nonNull(project)) {
                    project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
                    project.getSteps().addAll(fetchStepsForProject(conn, projectId));
                    project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
                }

                commitTransaction(conn);
                return Optional.ofNullable(project);
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    // Fetches the categories associated with a project from the database.
    private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
        String sql = ""
            + "SELECT c.* FROM " + CATEGORY_TABLE + " C "
            + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
            + "WHERE project_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1, projectId, Integer.class);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Category> categories = new LinkedList<>();

                while (rs.next()) {
                    categories.add(extract(rs, Category.class));
                }

                return categories;
            }
        }
    }

    // Fetches the steps associated with a project from the database.
    private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
        String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1, projectId, Integer.class);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Step> steps = new LinkedList<>();

                while (rs.next()) {
                    steps.add(extract(rs, Step.class));
                }

                return steps;
            }
        }
    }

    // Fetches the materials associated with a project from the database.
    private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
        String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1, projectId, Integer.class);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Material> materials = new LinkedList<>();

                while (rs.next()) {
                    Material material = extract(rs, Material.class);
                    materials.add(material);
                }

                return materials;
            }
        }
    }

    // Modifies the details of a project in the database.
    public boolean modifyProjectDetails(Project project) {
        String sql = ""
            + "UPDATE " + PROJECT_TABLE + " SET "
            + "project_name = ?, "
            + "estimated_hours = ?, "
            + "actual_hours = ?, "
            + "difficulty = ?, "
            + "notes = ? "
            + "WHERE project_id = ?";

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);
                setParameter(stmt, 6, project.getProjectId(), Integer.class);

                boolean modified = stmt.executeUpdate() == 1;
                commitTransaction(conn);

                return modified;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    // Deletes a project from the database.
	public boolean deleteProject(Integer projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, projectId, Integer.class);

                boolean deleted = stmt.executeUpdate() == 1;

                commitTransaction(conn);
                return deleted;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
	}
}
