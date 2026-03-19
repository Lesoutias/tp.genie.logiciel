GESTION DES PERSONNES

Projet académique – Génie Logiciel – Groupe 2

Ce projet est une application JavaFX permettant de gérer plusieurs entités liées :

👤 Personnes

📞 Téléphones

🏠 Adresses

🏡 Domiciles (Personne–Adresse)

📊 Rapport global (Personne + Téléphones + Adresses)

L’application utilise une architecture JavaFX + JDBC + MySQL avec des procédures stockées pour toutes les opérations CRUD.

📂 1. Fonctionnalités principales
✔️ Gestion des Personnes
Ajouter, modifier, supprimer une personne

Afficher la liste complète

Afficher automatiquement les téléphones liés à la personne sélectionnée

✔️ Gestion des Téléphones
Associer un téléphone à une personne

CRUD complet

Affichage dynamique

✔️ Gestion des Adresses
CRUD complet

Liste triée par quartier

✔️ Gestion des Domiciles (Personne–Adresse)
Associer une personne à une adresse

CRUD complet

Affichage des domiciles existants

✔️ Rapport global
Génération d’un tableau regroupant :

Nom complet

Liste des numéros (GROUP_CONCAT)

Liste des adresses (GROUP_CONCAT)

🏗️ 2. Architecture du projet
Code
src/

 └── com.groupe2.project/
 
      ├── GestionPersonneUI.java        # Interface principale JavaFX
	  
      ├── Personne.java                 # Modèle + CRUD via procédures stockées
	  
      ├── Telephone.java
	  
      ├── Adresse.java
	  
      ├── PersonneAdresse.java
	  
      ├── RapportPersonne.java
	  
      ├── Connexion.java# Configuration de connexion
	  
      ├── DatabaseConnection.java       # Singleton JDBC
	  
      └── enums / interfaces …
🗄️ 3. Base de données MySQL

📌 Création de la base

CREATE DATABASE gestion_personne;

USE gestion_personne;

📌 Tables

Table personne

CREATE TABLE personne (

    id INT PRIMARY KEY,
	
    nom VARCHAR(50) NOT NULL,
	
    postnom VARCHAR(50),
	
    prenom VARCHAR(50),
	
    sexe CHAR(1) NOT NULL DEFAULT 'M',
	
    CONSTRAINT uk_personne UNIQUE (nom, postnom, prenom)
	
);

Table telephone

CREATE TABLE telephone (

    id INT PRIMARY KEY,
	
    id_proprietaire INT NOT NULL,
	
    initial VARCHAR(4) NOT NULL,
	
    numero VARCHAR(9) NOT NULL,
	
    CONSTRAINT fk_personne_telephone
	
        FOREIGN KEY (id_proprietaire) REFERENCES personne(id)
		
);

Table adresse

CREATE TABLE adresse (

    id INT PRIMARY KEY,
	
    quartier VARCHAR(50),
	
    commune VARCHAR(50),
	
    ville VARCHAR(50),
	
    pays VARCHAR(50) NOT NULL
	
);

Table domicile

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
⚙️ 4. Procédures stockées
Le projet utilise exclusivement des procédures stockées pour :

Insérer / mettre à jour

Supprimer

Sélectionner

Obtenir le dernier ID

Générer le rapport

Exemple : insertion / mise à jour d’une personne

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
	
END;

Exemple : rapport global

CREATE PROCEDURE sp_rapport_personnes()

BEGIN

    SELECT 
	
        p.id,
        CONCAT(p.nom, ' ', p.postnom, ' ', p.prenom) AS nomComplet,
		
        (SELECT GROUP_CONCAT(CONCAT(t.initial, t.numero) SEPARATOR ', ')
		
         FROM telephone t WHERE t.id_proprietaire = p.id) AS numeros,
		 
        (SELECT GROUP_CONCAT(CONCAT(a.quartier, ' ', a.commune, ' ', a.ville) SEPARATOR ', ')
		
         FROM domicile pa JOIN adresse a ON pa.id_adresse = a.id
		 
         WHERE pa.id_personne = p.id) AS adresses
		 
    FROM personne p;
	
END;

🖥️ 5. Interface utilisateur (JavaFX)

L’interface est construite avec :

BorderPane pour la structure principale

MenuBar pour la navigation

TableView pour l’affichage des données

HBox / VBox pour les formulaires

ComboBox pour les relations (Personne → Téléphone, Personne → Adresse)

Chaque module est construit dans une méthode dédiée :

buildPersonneUI()

buildTelephoneUI()

buildAdresseUI()

buildPersonneAdresseUI()

buildRapportUI()

🔌 6. Connexion à MySQL

La connexion utilise un singleton :

java

DatabaseConnection.getInstance(

    new Connexion("localhost", "gestion_personne", "root", "motdepasse", 3306),
	
    ConnectionType.MYSQL,
	
    new ImplementeConnexion()
	
);

▶️ 7. Lancement du projet

Prérequis

Java 17+

JavaFX 17+

MySQL 8+

JDBC Connector MySQL

Exécution

javac --module-path /path/to/javafx --add-modules javafx.controls,javafx.fxml *.java

java --module-path /path/to/javafx --add-modules javafx.controls,javafx.fxml com.groupe2.project.GestionPersonneUI

📦 8. Améliorations possibles

Ajout d’un thème CSS moderne

Export PDF/Excel du rapport

Pagination des tables

Validation avancée des champs

Authentification utilisateur

👥 9. Auteurs

Groupe 2 – Génie Logiciel

ISIG / GOMA

📜 Licence

Projet académique – libre d’utilisation pour l’apprentissage.
