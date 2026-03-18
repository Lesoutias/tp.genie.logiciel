package com.groupe2.project;

public class Connexion {
	private String serveur;
    private String database;
    private String user;
    private String password;
    private int port;

    public Connexion(String serveur, String database, String user, String password, int port) {
        this.serveur = serveur;
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public String getServeur() { return serveur; }
    public String getDatabase() { return database; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public int getPort() { return port; }

	public String getHost() {
		// TODO Auto-generated method stub
		return null;
	}
}
