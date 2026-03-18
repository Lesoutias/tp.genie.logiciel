package com.groupe2.project;

import java.sql.Connection;

public class DatabaseConnection {

    private static Connection connection;

    private DatabaseConnection() { }

    public static synchronized Connection getInstance(
            Connexion config,
            ConnectionType type,
            IConnexion factory
    ) {
        if (connection == null) {
            connection = factory.initialise(config, type);
        }
        return connection;
    }
    
    public static Connection getInstance() {
        return connection;
    }
}
