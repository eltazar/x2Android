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
    
    public int getIdCustomer(){
        return idCustomer;
    }
    public String getNomeContatto(){
        return nomeContatto;
    }
    public String getCognomeContatto(){
        return cognomeContatto;
    }

}