-- Drop tables if they exist
DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS project;

-- Create tables
CREATE TABLE project (
    project_id INT NOT NULL AUTO_INCREMENT,
    project_name VARCHAR(128) NOT NULL,
    estimated_hours DECIMAL(7, 2),
    actual_hours DECIMAL(7, 2),
    difficulty INT,
    notes TEXT,
    PRIMARY KEY (project_id)
);

CREATE TABLE category (
    category_id INT NOT NULL AUTO_INCREMENT,
    category_name VARCHAR(128) NOT NULL,
    PRIMARY KEY (category_id)
);

CREATE TABLE step (
    step_id INT NOT NULL AUTO_INCREMENT,
    project_id INT NOT NULL,
    step_text TEXT NOT NULL,
    step_order INT NOT NULL,
    PRIMARY KEY (step_id),
    FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE material (
    material_id INT NOT NULL AUTO_INCREMENT,
    project_id INT NOT NULL,
    material_name VARCHAR(128) NOT NULL,
    num_required INT,
    cost DECIMAL(7, 2),
    PRIMARY KEY (material_id),
    FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE project_category (
    project_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (project_id, category_id),
    FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE
);

-- Add project
INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes)
VALUES ('Hang a closet door', 0, 0, 0, '');

-- Add materials
INSERT INTO material (project_id, material_name, num_required, cost)
VALUES (1, 'Door hangers', 1, 0);
INSERT INTO material (project_id, material_name, num_required, cost)
VALUES (1, 'Screws', 20, 4.5);

-- Add steps
INSERT INTO step (project_id, step_text, step_order)
VALUES (1, 'Align hangers on opening side c and repeat for side b', 1);
INSERT INTO step (project_id, step_text, step_order)
VALUES (1, 'Screw hangers into frame', 2);

-- Add categories
INSERT INTO category (category_id, category_name)
VALUES (1, 'Doors and Windows');
INSERT INTO category (category_id, category_name)
VALUES (2, 'Repairs');
INSERT INTO category (category_id, category_name)
VALUES (3, 'Gardening');

-- Assign categories to project
INSERT INTO project_category (project_id, category_id)
VALUES (1, 1);
INSERT INTO project_category (project_id, category_id)
VALUES (1, 2);
