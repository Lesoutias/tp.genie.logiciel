package com.groupe2.project;

import java.sql.Connection;

public interface IConnexion {
    Connection initialise(Connexion connexion, ConnectionType type);
}

