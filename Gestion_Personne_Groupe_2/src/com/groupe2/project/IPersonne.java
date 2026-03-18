package com.groupe2.project;

import java.util.List;

public interface IPersonne {
	int getId();
    String getNom();
    String getPostNom();
    String getPrenom();
    Sexe getSex();
    String getNomComplet();
    List<ITelephone> getTelephonePersonnes();

    void Enregistrer(Personne personne);
    void Supprimer(int id);
    List<IPersonne> Personnes();
    IPersonne OnePersonne(int id);
}
