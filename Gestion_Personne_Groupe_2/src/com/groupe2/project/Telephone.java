package com.groupe2.project;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Telephone implements ITelephone{
	private int id;
    private int idProprietaire;
    private String initial;
    private String numero;

    public Telephone() { }

    public Telephone(int id, String initial, String numero, int idProprietaire) {
        this.id = id;
        this.idProprietaire = idProprietaire;
        this.initial = initial;
        this.numero = numero;
    }

    @Override
    public int getId() { return id; }

    @Override
    public int getIdProprietaire() { return idProprietaire; }

    @Override
    public String getInitial() { return initial; }

    @Override
    public String getNumero() { return numero; }

    @Override
    public String getNumeroComplet() {
        return initial + numero;
    }

    // ---------------- CRUD-LIKE METHODS ----------------

    @Override
    public ITelephone Nouveau() {
        return new Telephone();
    }

    @Override
    public void Enregistrer(ITelephone telephone) {
    	String sql = "{CALL sp_insert_telephone(?, ?, ?, ?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, telephone.getId());
            stmt.setInt(2, telephone.getIdProprietaire());
            stmt.setString(3, telephone.getInitial());
            stmt.setString(4, telephone.getNumero());

            stmt.execute();

        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'enregistrement du téléphone", e);
        }
    }

    @Override
    public void Supprimer(int id) {
        String sql = "{CALL sp_delete_telephone(?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, id);
            stmt.execute();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du téléphone", e);
        }
    }



    @Override
    public List<ITelephone> Telephones() {
        List<ITelephone> list = new ArrayList<>();
        String sql = "{CALL sp_select_telephones()}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Telephone t = new Telephone(
                    rs.getInt("id"),
                    rs.getString("initial"),
                    rs.getString("numero"),
                    rs.getInt("id_proprietaire")
                );
                list.add(t);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement des téléphones", e);
        }

        return list;
    }


    @Override
    public ITelephone OneTelephone(int id) {
        String sql = "{CALL sp_select_telephone(?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Telephone(
                    		rs.getInt("id"),
                            rs.getString("initial"),
                            rs.getString("numero"),
                            rs.getInt("id_proprietaire")
                    );
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement du téléphone", e);
        }

        return null;
    }


    @Override
    public List<ITelephone> TelephonePersonne(int idPersonne) {
        List<ITelephone> list = new ArrayList<>();
        String sql = "{CALL sp_select_telephones_personne(?)}";

        try (CallableStatement stmt = DatabaseConnection.getInstance().prepareCall(sql)) {

            stmt.setInt(1, idPersonne);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Telephone t = new Telephone(
                    		rs.getInt("id"),
                            rs.getString("initial"),
                            rs.getString("numero"),
                            rs.getInt("id_proprietaire")
                    );
                    list.add(t);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement des téléphones de la personne", e);
        }

        return list;
    }
    
    public int getLastId() {
        String sql = "{CALL sp_last_telephone_id()}";

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
