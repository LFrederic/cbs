package com.example.cbs.cbs.Models;

public class Contact {

    String nom;
    String numero;


    public Contact(String nom, String numero) {
        this.nom = nom;
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public String getNumero() {
        return numero;
    }
}
