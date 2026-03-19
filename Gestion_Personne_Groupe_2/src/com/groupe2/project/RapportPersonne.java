package com.groupe2.project;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RapportPersonne {
    private String nomComplet;
    private String numeros;
    private String adresses;

    public RapportPersonne(String nomComplet, String numeros, String adresses) {
        this.nomComplet = nomComplet;
        this.numeros = numeros;
        this.adresses = adresses;
    }

    public String getNomComplet() { return nomComplet; }
    public String getNumeros() { return numeros; }
    public String getAdresses() { return adresses; }



}

