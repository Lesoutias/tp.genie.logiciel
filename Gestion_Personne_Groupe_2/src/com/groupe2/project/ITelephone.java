package com.groupe2.project;

import java.util.List;

public interface ITelephone {
	 int getId();
	    int getIdProprietaire();
	    String getInitial();
	    String getNumero();
	    String getNumeroComplet();

	    ITelephone Nouveau();
	    void Enregistrer(ITelephone telephone);
	    void Supprimer(int id);
	    List<ITelephone> Telephones();
	    ITelephone OneTelephone(int id);
	    List<ITelephone> TelephonePersonne(int idPersonne);
}
