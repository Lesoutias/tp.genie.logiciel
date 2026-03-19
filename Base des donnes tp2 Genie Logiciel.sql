CREATE DATABASE gestion_personne

USE gestion_personne

CREATE TABLE personne (
    id INT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    postnom VARCHAR(50),
    prenom VARCHAR(50),
    sexe CHAR(1) NOT NULL DEFAULT 'M',
    CONSTRAINT uk_personne UNIQUE (nom, postnom, prenom)
);

CREATE TABLE telephone (
    id INT PRIMARY KEY,
    id_proprietaire INT NOT NULL,
    initial VARCHAR(4) NOT NULL,
    numero VARCHAR(9) NOT NULL,
    CONSTRAINT fk_personne_telephone
        FOREIGN KEY (id_proprietaire) REFERENCES personne(id)
);

CREATE TABLE adresse (
    id INT PRIMARY KEY,
    quartier VARCHAR(50),
    commune VARCHAR(50),
    ville VARCHAR(50),
    pays VARCHAR(50) NOT NULL
);

CREATE TABLE domicile (
    id INT PRIMARY KEY,
    id_personne INT NOT NULL,
    id_adresse INT NOT NULL,
    avenue VARCHAR(50),
    numero_avenue INT,
    CONSTRAINT fk_personne_domicile
        FOREIGN KEY (id_personne) REFERENCES personne(id),
    CONSTRAINT fk_adresse_domicile
        FOREIGN KEY (id_adresse) REFERENCES adresse(id)
);

DELIMITER $$

CREATE PROCEDURE sp_insert_personne(
    IN p_id INT,
    IN p_nom VARCHAR(50),
    IN p_postnom VARCHAR(50),
    IN p_prenom VARCHAR(50),
    IN p_sexe CHAR(1)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM personne WHERE id = p_id) THEN
        INSERT INTO personne(id, nom, postnom, prenom, sexe)
        VALUES (p_id, p_nom, p_postnom, p_prenom, p_sexe);
    ELSE
        UPDATE personne
        SET nom = p_nom,
            postnom = p_postnom,
            prenom = p_prenom,
            sexe = p_sexe
        WHERE id = p_id;
    END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_delete_personne(IN p_id INT)
BEGIN
    DELETE FROM personne WHERE id = p_id;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_select_personnes()
BEGIN
    SELECT id, nom, postnom, prenom, sexe
    FROM personne
    ORDER BY nom ASC;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_select_personne(IN p_id INT)
BEGIN
    SELECT id, nom, postnom, prenom, sexe
    FROM personne
    WHERE id = p_id;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_insert_telephone(
    IN p_id INT,
    IN p_id_proprietaire INT,
    IN p_initial VARCHAR(4),
    IN p_numero VARCHAR(9)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM telephone WHERE id = p_id) THEN
        INSERT INTO telephone(id, id_proprietaire, initial, numero)
        VALUES (p_id, p_id_proprietaire, p_initial, p_numero);
    ELSE
        UPDATE telephone
        SET id_proprietaire = p_id_proprietaire,
            initial = p_initial,
            numero = p_numero
        WHERE id = p_id;
    END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_delete_telephone(IN p_id INT)
BEGIN
    DELETE FROM telephone WHERE id = p_id;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_select_telephones()
BEGIN
    SELECT id, id_proprietaire, initial, numero
    FROM telephone
    ORDER BY numero ASC;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_select_telephones_personne(IN p_id_personne INT)
BEGIN
    SELECT id, id_proprietaire, initial, numero
    FROM telephone
    WHERE id_proprietaire = p_id_personne
    ORDER BY numero ASC;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_select_telephone(IN p_id INT)
BEGIN
    SELECT id, id_proprietaire, initial, numero
    FROM telephone
    WHERE id = p_id;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_insert_adresse(
    IN p_id INT,
    IN p_quartier VARCHAR(50),
    IN p_commune VARCHAR(50),
    IN p_ville VARCHAR(50),
    IN p_pays VARCHAR(50)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM adresse WHERE id = p_id) THEN
        INSERT INTO adresse(id, quartier, commune, ville, pays)
        VALUES (p_id, p_quartier, p_commune, p_ville, p_pays);
    ELSE
        UPDATE adresse
        SET quartier = p_quartier,
            commune = p_commune,
            ville = p_ville,
            pays = p_pays
        WHERE id = p_id;
    END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_delete_adresse(IN p_id INT)
BEGIN
    DELETE FROM adresse WHERE id = p_id;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_select_adresses()
BEGIN
    SELECT id, quartier, commune, ville, pays
    FROM adresse
    ORDER BY quartier ASC;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_select_adresse(IN p_id INT)
BEGIN
    SELECT id, quartier, commune, ville, pays
    FROM adresse
    WHERE id = p_id;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_insert_domicile(
    IN p_id INT,
    IN p_id_personne INT,
    IN p_id_adresse INT,
    IN p_avenue VARCHAR(50),
    IN p_numero_avenue INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM domicile WHERE id = p_id) THEN
        INSERT INTO domicile(id, id_personne, id_adresse, avenue, numero_avenue)
        VALUES (p_id, p_id_personne, p_id_adresse, p_avenue, p_numero_avenue);
    ELSE
        UPDATE domicile
        SET id_personne = p_id_personne,
            id_adresse = p_id_adresse,
            avenue = p_avenue,
            numero_avenue = p_numero_avenue
        WHERE id = p_id;
    END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_delete_domicile(IN p_id INT)
BEGIN
    DELETE FROM domicile WHERE id = p_id;
END $$

DELIMITER ;

USE gestion_personne

select * from adresse

DROP PROCEDURE IF EXISTS sp_last_adresse_id;

DELIMITER $$

CREATE PROCEDURE sp_last_adresse_id()
BEGIN
    SELECT IFNULL(MAX(id), 0) AS last_id FROM adresse;
END $$

DELIMITER ;

DROP PROCEDURE IF EXISTS sp_last_personne_id;

DELIMITER $$

CREATE PROCEDURE sp_last_personne_id()
BEGIN
    SELECT IFNULL(MAX(id), 0) AS last_id FROM personne;
END $$

DELIMITER ;

DROP PROCEDURE IF EXISTS sp_last_personneAdresse_id;

DELIMITER $$

CREATE PROCEDURE sp_last_personneAdresse_id()
BEGIN
    SELECT IFNULL(MAX(id), 0) AS last_id FROM domicile;
END $$

DELIMITER ;

select * from domicile


DELIMITER $$

CREATE PROCEDURE sp_select_personnes_adresses()
BEGIN
   SELECT * FROM domicile;
END $$

DELIMITER ;

DROP PROCEDURE IF EXISTS sp_rapport_personnes;

DELIMITER $$

CREATE PROCEDURE sp_rapport_personnes()
BEGIN
    SELECT 
        p.id,
        CONCAT(p.nom, ' ', p.postnom, ' ', p.prenom) AS nomComplet,

        -- Liste des numéros
        (SELECT GROUP_CONCAT(CONCAT(t.initial, t.numero) SEPARATOR ', ')
         FROM telephone t
         WHERE t.id_proprietaire = p.id) AS numeros,

        -- Liste des adresses
        (SELECT GROUP_CONCAT(CONCAT(a.quartier, ' ', a.commune, ' ', a.ville) SEPARATOR ', ')
         FROM domicile pa
         JOIN adresse a ON pa.id_adresse = a.id
         WHERE pa.id_personne = p.id) AS adresses

    FROM personne p;
END $$

DELIMITER ;



USE gestion_personne











