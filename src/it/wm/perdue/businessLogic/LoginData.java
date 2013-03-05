package it.wm.perdue.businessLogic;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LoginData implements Serializable {

    @SerializedName("idcustomer")
    private int    idCustomer           = -1;
    @SerializedName("nome_contatto")
    private String    nomeContatto           = null;
    @SerializedName("cognome_contatto")
    private String    cognomeContatto           = null;
    private String    email           = null;
    
    public LoginData(String name,String surname, String email, int id){
        idCustomer = id;
        nomeContatto = name;
        cognomeContatto = surname;
        this.email = email;
    }
    
    public int getIdCustomer(){
        return idCustomer;
    }
    public String getNomeContatto(){
        return nomeContatto;
    }
    public String getCognomeContatto(){
        return cognomeContatto;
    }
    public String getEmail(){
        return email;
    }
    
    public void setEmail(String e){
        email = e;
    }
    
    public String toString(){
        return "LOGIN DATA = "+nomeContatto+" "+cognomeContatto+" "+email+" "+idCustomer;
    }

}