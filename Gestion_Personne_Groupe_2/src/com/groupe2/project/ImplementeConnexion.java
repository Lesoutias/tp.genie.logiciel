package com.groupe2.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ImplementeConnexion implements IConnexion {

    @Override
    public Connection initialise(Connexion config, ConnectionType type) {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Build JDBC URL
            String url = "jdbc:mysql://" + config.getServeur() + ":" + config.getPort()
                    + "/" + config.getDatabase() + "?useSSL=false&serverTimezone=UTC";

            // Create connection
            return DriverManager.getConnection(url, config.getUser(), config.getPassword());

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            return null;

        } catch (SQLException e) {
            System.err.println("Database connection failed.");
            e.printStackTrace();
            return null;
        }
    }
}

