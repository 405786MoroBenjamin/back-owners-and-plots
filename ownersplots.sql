
-- Tabla DniTypes
CREATE TABLE dni_types (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           description VARCHAR(255),
                           created_datetime DATETIME,
                           created_user INT,
                           last_updated_datetime DATETIME,
                           last_updated_user INT
);

-- Tabla Tax_Status
CREATE TABLE TaxStatus (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           description VARCHAR(255),
                           created_datetime DATETIME,
                           created_user INT,
                           last_updated_datetime DATETIME,
                           last_updated_user INT
);

-- Tabla OwnersTypes
CREATE TABLE OwnersTypes (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             description VARCHAR(255),
                             created_datetime DATETIME,
                             created_user INT,
                             last_updated_datetime DATETIME,
                             last_updated_user INT
);

-- Tabla Owners
CREATE TABLE Owners (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255),
                        lastname VARCHAR(255),
                        dni_type_id INT,
                        dni VARCHAR(255),
                        date_birth DATETIME,
                        tax_status_id INT,
                        owner_type_id INT,
                        business_name VARCHAR(255),
                        active BOOLEAN,
                        created_datetime DATETIME,
                        created_user INT,
                        last_updated_datetime DATETIME,
                        last_updated_user INT,
                        FOREIGN KEY (tax_status_id) REFERENCES TaxStatus(id),
                        FOREIGN KEY (owner_type_id) REFERENCES OwnersTypes(id),
                        FOREIGN KEY (dni_type_id) REFERENCES dni_types(id)
);

-- Tabla PlotStates
CREATE TABLE PlotStates (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(255),
                            created_datetime DATETIME,
                            created_user INT,
                            last_updated_datetime DATETIME,
                            last_updated_user INT
);

-- Tabla PlotTypes
CREATE TABLE PlotTypes (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(255),
                           created_datetime DATETIME,
                           created_user INT,
                           last_updated_datetime DATETIME,
                           last_updated_user INT
);

-- Tabla Plots
CREATE TABLE Plots (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       plot_number INT,
                       block_number INT,
                       plot_state_id INT,
                       plot_type_id INT,
                       total_area_in_m2 DECIMAL(10, 2),
                       built_area_in_m2 DECIMAL(10, 2),
                       created_datetime DATETIME,
                       created_user INT,
                       last_updated_datetime DATETIME,
                       last_updated_user INT,
                       FOREIGN KEY (plot_state_id) REFERENCES PlotStates(id),
                       FOREIGN KEY (plot_type_id) REFERENCES PlotTypes(id)
);

-- Tabla PlotOwners
CREATE TABLE PlotOwners (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            plot_id INT,
                            owner_id INT,
                            created_datetime DATETIME,
                            created_user INT,
                            last_updated_datetime DATETIME,
                            last_updated_user INT,
                            FOREIGN KEY (plot_id) REFERENCES Plots(id),
                            FOREIGN KEY (owner_id) REFERENCES Owners(id)
);


-- Tabla Files
CREATE TABLE Files (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       file_uuid VARCHAR(255),
                       name VARCHAR(255),
                       created_datetime DATETIME,
                       created_user INT,
                       last_updated_datetime DATETIME,
                       last_updated_user INT
);

-- Tabla Files_Plots
CREATE TABLE Files_Plots (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             file_id INT,
                             plot_id INT,
                             created_datetime DATETIME,
                             created_user INT,
                             last_updated_datetime DATETIME,
                             last_updated_user INT,
                             FOREIGN KEY (file_id) REFERENCES Files(id),
                             FOREIGN KEY (plot_id) REFERENCES Plots(id)
);

-- Tabla Files_Owners
CREATE TABLE Files_Owners (
                              id INT PRIMARY KEY AUTO_INCREMENT,
                              file_id INT,
                              owner_id INT,
                              created_datetime DATETIME,
                              created_user INT,
                              last_updated_datetime DATETIME,
                              last_updated_user INT,
                              FOREIGN KEY (file_id) REFERENCES Files(id),
                              FOREIGN KEY (owner_id) REFERENCES Owners(id)
);


-- Tabla de auditoría para Owners
CREATE TABLE Owners_audit (
                              version_id INT PRIMARY KEY AUTO_INCREMENT,
                              id INT,
                              version INT,
                              name VARCHAR(255),
                              lastname VARCHAR(255),
                              dni VARCHAR(255),
                              dni_type_id INT,
                              date_birth DATE,
                              tax_status_id INT,
                              owner_type_id INT,
                              business_name VARCHAR(255),
                              active BOOLEAN,
                              created_datetime DATETIME,
                              created_user INT,
                              last_updated_datetime DATETIME,
                              last_updated_user INT
);

-- Tabla de auditoría para PlotTypes
CREATE TABLE PlotTypes_audit (
                                 version_id INT PRIMARY KEY AUTO_INCREMENT,
                                 id INT,
                                 version INT,
                                 name VARCHAR(100),
                                 created_datetime DATETIME,
                                 created_user INT,
                                 last_updated_datetime DATETIME,
                                 last_updated_user INT
);

-- Tabla de auditoría para PlotStates
CREATE TABLE PlotStates_audit (
                                  version_id INT PRIMARY KEY AUTO_INCREMENT,
                                  id INT,
                                  version INT,
                                  name VARCHAR(100),
                                  created_datetime DATETIME,
                                  created_user INT,
                                  last_updated_datetime DATETIME,
                                  last_updated_user INT
);

-- Tabla de auditoría para Plots
CREATE TABLE Plots_audit (
                             version_id INT PRIMARY KEY AUTO_INCREMENT,
                             id INT,
                             version INT,
                             plot_number INT,
                             block_number INT,
                             plot_state_id INT,
                             plot_type_id INT,
                             total_area_in_m2 DECIMAL(10, 2),
                             built_area_in_m2 DECIMAL(10, 2),
                             created_datetime DATETIME,
                             created_user INT,
                             last_updated_datetime DATETIME,
                             last_updated_user INT
);

-- Tabla de auditoría para PlotOwners
CREATE TABLE PlotOwners_audit (
                                  version_id INT PRIMARY KEY AUTO_INCREMENT,
                                  id INT,
                                  version INT,
                                  plot_id INT,
                                  owner_id INT,
                                  created_datetime DATETIME,
                                  created_user INT,
                                  last_updated_datetime DATETIME,
                                  last_updated_user INT
);

-- Tabla de auditoría para Files
CREATE TABLE Files_audit (
                             version_id INT PRIMARY KEY AUTO_INCREMENT,
                             id INT,
                             version INT,
                             file_uuid VARCHAR(255),
                             name VARCHAR(255),
                             created_datetime DATETIME,
                             created_user INT,
                             last_updated_datetime DATETIME,
                             last_updated_user INT
);

-- Tabla de auditoría para Files_Plots
CREATE TABLE Files_Plots_audit (
                                   version_id INT PRIMARY KEY AUTO_INCREMENT,
                                   id INT,
                                   version INT,
                                   file_id INT,
                                   plot_id INT,
                                   created_datetime DATETIME,
                                   created_user INT,
                                   last_updated_datetime DATETIME,
                                   last_updated_user INT
);

-- Tabla de auditoría para Files_Owners
CREATE TABLE Files_Owners_audit (
                                    version_id INT PRIMARY KEY AUTO_INCREMENT,
                                    id INT,
                                    version INT,
                                    file_id INT,
                                    owner_id INT,
                                    created_datetime DATETIME,
                                    created_user INT,
                                    last_updated_datetime DATETIME,
                                    last_updated_user INT
);

CREATE TABLE dni_types_audit (
                                 version_id INT PRIMARY KEY AUTO_INCREMENT,
                                 id INT,
                                 version INT,
                                 description VARCHAR(255),
                                 created_datetime DATETIME,
                                 created_user INT,
                                 last_updated_datetime DATETIME,
                                 last_updated_user INT
);


-- Tabla de auditoría para TaxStatus
CREATE TABLE TaxStatus_audit (
                                 version_id INT PRIMARY KEY AUTO_INCREMENT,
                                 id INT,
                                 version INT,
                                 description VARCHAR(255),
                                 created_datetime DATETIME,
                                 created_user INT,
                                 last_updated_datetime DATETIME,
                                 last_updated_user INT
);

-- Tabla de auditoría para TaxStatus
CREATE TABLE OwnersTypes_audit (
                                   version_id INT PRIMARY KEY AUTO_INCREMENT,
                                   id INT,
                                   version INT,
                                   description VARCHAR(255),
                                   created_datetime DATETIME,
                                   created_user INT,
                                   last_updated_datetime DATETIME,
                                   last_updated_user INT
);


DELIMITER $$

CREATE TRIGGER trg_taxstatus_insert
    AFTER INSERT ON TaxStatus
    FOR EACH ROW
BEGIN
    INSERT INTO TaxStatus_audit
    (id, version, description, created_datetime, created_user, last_updated_datetime, last_updated_user)
    VALUES
        (NEW.id, 1, NEW.description, NEW.created_datetime, NEW.created_user, NEW.last_updated_datetime, NEW.last_updated_user);
    END$$

    DELIMITER ;

DELIMITER $$

    CREATE TRIGGER trg_dnitypes_insert
        AFTER INSERT ON dni_types
        FOR EACH ROW
    BEGIN
        INSERT INTO dni_types_audit
        (id, version, description, created_datetime, created_user, last_updated_datetime, last_updated_user)
        VALUES
            (NEW.id, 1, NEW.description, NEW.created_datetime, NEW.created_user, NEW.last_updated_datetime, NEW.last_updated_user);
        END$$

        CREATE TRIGGER trg_dni_type_update
            AFTER UPDATE ON dni_types
            FOR EACH ROW
        BEGIN
            DECLARE latest_version INT;
            SELECT MAX(version) INTO latest_version FROM dni_types_audit WHERE id = NEW.id;
            SET latest_version = IFNULL(latest_version, 0) + 1;

            INSERT INTO dni_types_audit (id, version, description, created_datetime, last_updated_datetime, created_user, last_updated_user)
            VALUES (NEW.id, latest_version, NEW.description, NEW.created_datetime, NEW.last_updated_datetime, NEW.created_user, NEW.last_updated_user);
        END $$

DELIMITER ;

        DELIMITER $$

        CREATE TRIGGER trg_taxstatus_update
            AFTER UPDATE ON TaxStatus
            FOR EACH ROW
        BEGIN
            DECLARE version_number INT;

            SELECT IFNULL(MAX(version), 0) + 1 INTO version_number FROM TaxStatus_audit WHERE id = OLD.id;

            INSERT INTO TaxStatus_audit
            (id, version, description, created_datetime, created_user, last_updated_datetime, last_updated_user)
            VALUES
                (OLD.id, version_number, OLD.description, OLD.created_datetime, OLD.created_user, OLD.last_updated_datetime, OLD.last_updated_user);
            END$$

            DELIMITER ;

DELIMITER $$

            CREATE TRIGGER trg_owners_insert
                AFTER INSERT ON Owners
                FOR EACH ROW
            BEGIN
                INSERT INTO Owners_audit
                (id, version, name, lastname, dni, dni_type_id, date_birth, tax_status_id, owner_type_id, business_name, active, created_datetime, created_user, last_updated_datetime, last_updated_user)
                VALUES
                    (NEW.id, 1, NEW.name, NEW.lastname, NEW.dni,NEW.dni_type_id, NEW.date_birth, NEW.tax_status_id, NEW.owner_type_id, NEW.business_name, NEW.active, NEW.created_datetime, NEW.created_user, NEW.last_updated_datetime, NEW.last_updated_user);
                END$$

                DELIMITER ;

DELIMITER $$

                CREATE TRIGGER trg_owners_update
                    AFTER UPDATE ON Owners
                    FOR EACH ROW
                BEGIN
                    DECLARE version_number INT;

                    SELECT IFNULL(MAX(version), 0) + 1 INTO version_number FROM Owners_audit WHERE id = OLD.id;

                    INSERT INTO Owners_audit
                    (id, version, name, lastname, dni, dni_type_id, date_birth, tax_status_id, owner_type_id, business_name, active, created_datetime, created_user, last_updated_datetime, last_updated_user)
                    VALUES
                        (OLD.id, version_number, OLD.name, OLD.lastname, OLD.dni, OLD.dni_type_id, OLD.date_birth, OLD.tax_status_id, OLD.owner_type_id, OLD.business_name, OLD.active, OLD.created_datetime, OLD.created_user, OLD.last_updated_datetime, OLD.last_updated_user);
                    END$$

                    DELIMITER ;

DELIMITER $$

                    CREATE TRIGGER trg_plotstates_insert
                        AFTER INSERT ON PlotStates
                        FOR EACH ROW
                    BEGIN
                        INSERT INTO PlotStates_audit
                        (id, version, name, created_datetime, created_user, last_updated_datetime, last_updated_user)
                        VALUES
                            (NEW.id, 1, NEW.name, NEW.created_datetime, NEW.created_user, NEW.last_updated_datetime, NEW.last_updated_user);
                        END$$

                        DELIMITER ;

DELIMITER $$

                        CREATE TRIGGER trg_plotstates_update
                            AFTER UPDATE ON PlotStates
                            FOR EACH ROW
                        BEGIN
                            DECLARE version_number INT;

                            SELECT IFNULL(MAX(version), 0) + 1 INTO version_number FROM PlotStates_audit WHERE id = OLD.id;

                            INSERT INTO PlotStates_audit
                            (id, version, name, created_datetime, created_user, last_updated_datetime, last_updated_user)
                            VALUES
                                (OLD.id, version_number, OLD.name, OLD.created_datetime, OLD.created_user, OLD.last_updated_datetime, OLD.last_updated_user);
                            END$$

                            DELIMITER ;

DELIMITER $$

                            CREATE TRIGGER trg_plottypes_insert
                                AFTER INSERT ON PlotTypes
                                FOR EACH ROW
                            BEGIN
                                INSERT INTO PlotTypes_audit
                                (id, version, name, created_datetime, created_user, last_updated_datetime, last_updated_user)
                                VALUES
                                    (NEW.id, 1, NEW.name, NEW.created_datetime, NEW.created_user, NEW.last_updated_datetime, NEW.last_updated_user);
                                END$$

                                DELIMITER ;

DELIMITER $$

                                CREATE TRIGGER trg_plottypes_update
                                    AFTER UPDATE ON PlotTypes
                                    FOR EACH ROW
                                BEGIN
                                    DECLARE version_number INT;

                                    SELECT IFNULL(MAX(version), 0) + 1 INTO version_number FROM PlotTypes_audit WHERE id = OLD.id;

                                    INSERT INTO PlotTypes_audit
                                    (id, version, name, created_datetime, created_user, last_updated_datetime, last_updated_user)
                                    VALUES
                                        (OLD.id, version_number, OLD.name, OLD.created_datetime, OLD.created_user, OLD.last_updated_datetime, OLD.last_updated_user);
                                    END$$

                                    DELIMITER ;

DELIMITER $$

                                    CREATE TRIGGER trg_plots_insert
                                        AFTER INSERT ON Plots
                                        FOR EACH ROW
                                    BEGIN
                                        INSERT INTO Plots_audit
                                        (id, version, plot_number, block_number, plot_state_id, plot_type_id, total_area_in_m2, built_area_in_m2, created_datetime, created_user, last_updated_datetime, last_updated_user)
                                        VALUES
                                            (NEW.id, 1, NEW.plot_number, NEW.block_number, NEW.plot_state_id, NEW.plot_type_id, NEW.total_area_in_m2, NEW.built_area_in_m2, NEW.created_datetime, NEW.created_user, NEW.last_updated_datetime, NEW.last_updated_user);
                                        END$$

                                        DELIMITER ;

DELIMITER $$

                                        CREATE TRIGGER trg_plots_update
                                            AFTER UPDATE ON Plots
                                            FOR EACH ROW
                                        BEGIN
                                            DECLARE version_number INT;

                                            SELECT IFNULL(MAX(version), 0) + 1 INTO version_number FROM Plots_audit WHERE id = OLD.id;

                                            INSERT INTO Plots_audit
                                            (id, version, plot_number, block_number, plot_state_id, plot_type_id, total_area_in_m2, built_area_in_m2, created_datetime, created_user, last_updated_datetime, last_updated_user)
                                            VALUES
                                                (OLD.id, version_number, OLD.plot_number, OLD.block_number, OLD.plot_state_id, OLD.plot_type_id, OLD.total_area_in_m2, OLD.built_area_in_m2, OLD.created_datetime, OLD.created_user, OLD.last_updated_datetime, OLD.last_updated_user);
                                            END$$

                                            DELIMITER ;

DELIMITER $$

                                            CREATE TRIGGER trg_plotowners_insert
                                                AFTER INSERT ON PlotOwners
                                                FOR EACH ROW
                                            BEGIN
                                                INSERT INTO PlotOwners_audit
                                                (id, version, plot_id, owner_id, created_datetime, created_user, last_updated_datetime, last_updated_user)
                                                VALUES
                                                    (NEW.id, 1, NEW.plot_id, NEW.owner_id, NEW.created_datetime, NEW.created_user, NEW.last_updated_datetime, NEW.last_updated_user);
                                                END$$

                                                DELIMITER ;

DELIMITER $$

                                                CREATE TRIGGER trg_plotowners_update
                                                    AFTER UPDATE ON PlotOwners
                                                    FOR EACH ROW
                                                BEGIN
                                                    DECLARE version_number INT;

                                                    SELECT IFNULL(MAX(version), 0) + 1 INTO version_number FROM PlotOwners_audit WHERE id = OLD.id;

                                                    INSERT INTO PlotOwners_audit
                                                    (id, version, plot_id, owner_id, created_datetime, created_user, last_updated_datetime, last_updated_user)
                                                    VALUES
                                                        (OLD.id, version_number, OLD.plot_id, OLD.owner_id, OLD.created_datetime, OLD.created_user, OLD.last_updated_datetime, OLD.last_updated_user);
                                                    END$$

                                                    DELIMITER ;





                                                    INSERT INTO TaxStatus (description, created_datetime, last_updated_datetime, created_user, last_updated_user)
                                                    VALUES
                                                        ('IVA Responsable inscripto', NOW(), NOW(), 1, 1),
                                                        ('IVA Responsable no inscripto', NOW(), NOW(), 1, 1),
                                                        ('IVA no Responsable', NOW(), NOW(), 1, 1),
                                                        ('IVA Sujeto Exento', NOW(), NOW(), 1, 1),
                                                        ('Monotributista', NOW(), NOW(), 1, 1);

                                                    INSERT INTO OwnersTypes (description, created_datetime, last_updated_datetime, created_user, last_updated_user)
                                                    VALUES
                                                        ('Persona Física' , NOW(), NOW(), 1, 1),
                                                        ('Persona Jurídica', NOW(), NOW(), 1, 1),
                                                        ('Otro', NOW(), NOW(), 1, 1);

                                                    INSERT INTO PlotStates (name, created_datetime, last_updated_datetime, created_user, last_updated_user)
                                                    VALUES
                                                        ('Disponible', NOW(), NOW(), 1, 1),
                                                        ('Habitado', NOW(), NOW(), 1, 1),
                                                        ('En construcción', NOW(), NOW(), 1, 1);

                                                    INSERT INTO PlotTypes (name, created_datetime, last_updated_datetime, created_user, last_updated_user)
                                                    VALUES
                                                        ('Comercial', NOW(), NOW(), 1, 1),
                                                        ('Residencial', NOW(), NOW(), 1, 1),
                                                        ('Baldío', NOW(), NOW(), 1, 1);

                                                    INSERT INTO dni_types (description, created_datetime, last_updated_datetime, created_user, last_updated_user)
                                                    VALUES
                                                        ('DNI', NOW(), NOW(), 1, 1),
                                                        ('Pasaporte', NOW(), NOW(), 1, 1),
                                                        ('CUIT/CUIL', NOW(), NOW(), 1, 1);