package com.groupe2.project;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PersonneAdresse {

    private int id;
    private int idPersonne;
    private int idAdresse;
    private String avenue;
    private int numeroAvenue;

    public PersonneAdresse() {}

    public PersonneAdresse(int id, int idPersonne, int idAdresse, String avenue, int numeroAvenue) {
        this.id = id;
        this.idPersonne = idPersonne;
        this.idAdresse = idAdresse;
        this.avenue = avenue;
        this.numeroAvenue = numeroAvenue;
    }
    
    

    // ---------------- CRUD METHODS ----------------

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdPersonne() {
		return idPersonne;
	}

	public void setIdPersonne(int idPersonne) {
		this.idPersonne = idPersonne;
	}

	public int getIdAdresse() {
		return idAdresse;
	}

	public void setIdAdresse(int idAdresse) {
		this.idAdresse = idAdresse;
	}

	public String getAvenue() {
		return avenue;
	}

	public void setAvenue(String avenue) {
		this.avenue = avenue;
	}

	public int getNumeroAvenue() {
		return numeroAvenue;
	}

	public void setNumeroAvenue(int numeroAvenue) {
		this.numeroAvenue = numeroAvenue;
	}

	public void Enregistrer(PersonneAdresse pa) {
        String sql = "{CALL sp_insert_domicile(?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, pa.id);
            stmt.setInt(2, pa.idPersonne);
            stmt.setInt(3, pa.idAdresse);
            stmt.setString(4, pa.avenue);
            stmt.setInt(5, pa.numeroAvenue);

            stmt.execute();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du domicile", e);
        }
    }

    public void Supprimer(int id) {
        String sql = "{CALL sp_delete_domicile(?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, id);
            stmt.execute();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du domicile", e);
        }
    }
    
    public int getLastId() {
        String sql = "{CALL sp_last_personneAdresse_id()}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération du dernier ID", e);
        }

        return 0;
    }
    
    public List<PersonneAdresse> PersonnesAdresse() {
        List<PersonneAdresse> list = new ArrayList<>();
        String sql = "{CALL sp_select_personnes_adresses()}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PersonneAdresse pa = new PersonneAdresse(
                    rs.getInt("id"),
                    rs.getInt("id_personne"),
                    rs.getInt("id_adresse"),
                    rs.getString("avenue"),
                    rs.getInt("numero_avenue")
                );
                
                list.add(pa);
            }

        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement des adresses des personnes", e);
        }

        return list;
    }

}



