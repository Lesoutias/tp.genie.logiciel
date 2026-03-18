package com.groupe2.project;

import java.sql.Connection;
import java.sql.SQLException;

public class Main_Gestion_Personne {

	public static void main(String[] args) {
		Connexion config = new Connexion(
			    "localhost",
			    "gestion_personne",
			    "root",
			    "Lesoutils@1907",
			    3306
			);

			IConnexion factory = new ImplementeConnexion();

			// This returns java.sql.Connection
			Connection conn = factory.initialise(config, ConnectionType.MYSQL);
			
			try {
				if (conn != null && !conn.isClosed()) {
					System.out.println("Connexion à la base de données réussie !");
				} else {
					System.out.println("Connexion échouée !");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			DatabaseConnection.getInstance(config, ConnectionType.MYSQL, factory);

	}
	
	public static void testPersonneInsert() {
	    Personne p = new Personne(
	            1,
	            "Yves",
	            "Metre",
	            "Junior",
	            Sexe.MASCULIN
	    );

	    p.Enregistrer(p);

	    System.out.println("Personne enregistrée avec succès !");
	}
}
