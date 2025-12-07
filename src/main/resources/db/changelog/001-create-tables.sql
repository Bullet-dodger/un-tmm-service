-- src/main/resources/db/changelog/changes/001-create-tables.sql

--liquibase formatted sql

--changeset your-name:001-create-materials-table
CREATE TABLE materials (
                           id              BIGSERIAL       PRIMARY KEY,
                           formula         VARCHAR(50)     NOT NULL UNIQUE,
                           display_name    VARCHAR(255)    NOT NULL
);

CREATE INDEX idx_materials_formula ON materials(formula);

--changeset your-name:002-create-thermodynamic-coefficients-table
CREATE TABLE thermodynamic_coefficients (
                                            id                    BIGSERIAL       PRIMARY KEY,
                                            material_id           BIGINT          NOT NULL,
                                            state                 VARCHAR(20)     NOT NULL,
                                            t_min                 DECIMAL(10,2)   NOT NULL,
                                            t_max                 DECIMAL(10,2)   NOT NULL,
                                            calculation_enthalpy  DECIMAL(15,6),
                                            a                     DECIMAL(20,10),
                                            b                     DECIMAL(20,10),
                                            c                     DECIMAL(20,10),
                                            d                     DECIMAL(20,10),
                                            e                     DECIMAL(20,10),
                                            f                     DECIMAL(20,10),
                                            g                     DECIMAL(20,10),

                                            CONSTRAINT fk_coefficients_material
                                                FOREIGN KEY (material_id)
                                                    REFERENCES materials(id)
                                                    ON DELETE CASCADE
);

CREATE INDEX idx_coefficients_material_state ON thermodynamic_coefficients(material_id, state);