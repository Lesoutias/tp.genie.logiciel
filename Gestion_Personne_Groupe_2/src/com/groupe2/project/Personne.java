package com.groupe2.project;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Personne implements IPersonne{
	private int id;
    private String nom;
    private String postNom;
    private String prenom;
    private Sexe sex;
    private List<ITelephone> telephonePersonnes = new ArrayList<>();

    public Personne() { }

    public Personne(int id, String nom, String postNom, String prenom, Sexe sex) {
        this.id = id;
        this.nom = nom;
        this.postNom = postNom;
        this.prenom = prenom;
        this.sex = sex;
    }

    // ---------------- PROPERTIES ----------------

    @Override
    public int getId() { return id; }

    @Override
    public String getNom() { return nom; }

    @Override
    public String getPostNom() { return postNom; }

    @Override
    public String getPrenom() { return prenom; }

    @Override
    public Sexe getSex() { return sex; }

    @Override
    public String getNomComplet() {
        return nom + " " + postNom + " " + prenom;
    }

    @Override
    public List<ITelephone> getTelephonePersonnes() {
        return telephonePersonnes;
    }

    // ---------------- METHODS ----------------

    public boolean validateName(String nom) {
        return nom != null && nom.length() >= 2;
    }

    @Override
    public String toString() {
        return getNomComplet() + " (" + sex + ")";
    }

    // ---------------- CRUD-LIKE METHODS ----------------

    @Override
    public void Enregistrer(Personne personne) {
    	String sql = "{CALL sp_insert_personne(?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, personne.getId());
            stmt.setString(2, personne.getNom());
            stmt.setString(3, personne.getPostNom());
            stmt.setString(4, personne.getPrenom());
            stmt.setString(5, personne.getSex().name().substring(0, 1)); 
            // Sex.MASCULIN → "M", Sex.FEMININ → "F"

            stmt.execute();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la personne", e);
        }
    }

    public void Supprimer(int id) {
        String sql = "{CALL sp_delete_personne(?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, id);
            stmt.execute();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de la personne", e);
        }
    }

    @Override
    public List<IPersonne> Personnes() {
        List<IPersonne> list = new ArrayList<>();
        String sql = "{CALL sp_select_personnes()}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Personne p = new Personne(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("postnom"),
                    rs.getString("prenom"),
                    rs.getString("sexe").equalsIgnoreCase("M") ? Sexe.MASCULIN : Sexe.FEMININ
                );

                // Load telephones for this person
                ITelephone tel = new Telephone();
                List<ITelephone> tels = tel.TelephonePersonne(p.getId());
                p.getTelephonePersonnes().addAll(tels);

                list.add(p);
            }

        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement des personnes", e);
        }

        return list;
    }


    @Override
    public IPersonne OnePersonne(int id) {
        String sql = "{CALL sp_select_personne(?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Personne p = new Personne(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("postnom"),
                        rs.getString("prenom"),
                        rs.getString("sexe").equalsIgnoreCase("M") ? Sexe.MASCULIN : Sexe.FEMININ
                    );

                    // Load telephones for this person
                    ITelephone tel = new Telephone();
                    List<ITelephone> tels = tel.TelephonePersonne(p.getId());
                    p.getTelephonePersonnes().addAll(tels);

                    return p;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement de la personne", e);
        }

        return null;
    }
    
    public int getLastId() {
        String sql = "{CALL sp_last_personne_id()}";

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
