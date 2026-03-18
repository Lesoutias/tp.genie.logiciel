package com.groupe2.project;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Adresse {

    private int id;
    private String quartier;
    private String commune;
    private String ville;
    private String pays;

    public Adresse() {}

    public Adresse(int id, String quartier, String commune, String ville, String pays) {
        this.id = id;
        this.quartier = quartier;
        this.commune = commune;
        this.ville = ville;
        this.pays = pays;
    }
    
    public int getId() { return id; }
    public String getQuartier() { return quartier; }
    public String getCommune() { return commune; }
    public String getVille() { return ville; }
    public String getPays() { return pays; }


    // ---------------- CRUD METHODS ----------------

    public void Enregistrer(Adresse adresse) {
        String sql = "{CALL sp_insert_adresse(?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, adresse.id);
            stmt.setString(2, adresse.quartier);
            stmt.setString(3, adresse.commune);
            stmt.setString(4, adresse.ville);
            stmt.setString(5, adresse.pays);

            stmt.execute();

        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'enregistrement de l'adresse", e);
        }
    }

    public void Supprimer(int id) {
        String sql = "{CALL sp_delete_adresse(?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, id);
            stmt.execute();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de l'adresse", e);
        }
    }

    public List<Adresse> Adresses() {
        List<Adresse> list = new ArrayList<>();
        String sql = "{CALL sp_select_adresses()}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Adresse(
                        rs.getInt("id"),
                        rs.getString("quartier"),
                        rs.getString("commune"),
                        rs.getString("ville"),
                        rs.getString("pays")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement des adresses", e);
        }

        return list;
    }

    public Adresse OneAdresse(int id) {
        String sql = "{CALL sp_select_adresse(?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Adresse(
                            rs.getInt("id"),
                            rs.getString("quartier"),
                            rs.getString("commune"),
                            rs.getString("ville"),
                            rs.getString("pays")
                    );
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement de l'adresse", e);
        }

        return null;
    }
    
    public int getLastId() {
        String sql = "{CALL sp_last_adresse_id()}";

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

}

