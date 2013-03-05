package it.wm.perdue.businessLogic;

public class CreditCard {
    private String owner;
    private String number;
    private String cvv;
    private int month;
    private int year;
    
    public String getOwner(){
        return owner;
    }
    public String getNumber(){
        return number;
    }
    public String getCvv(){
        return cvv;
    }
    public int getMonth(){
        return month;
    }
    public int getYear(){
        return year;
    }
    
    public void setOwner(String o){
        owner = o;
    }
    public void setNumber(String n){
        number = n;
    }
    public void setCvv(String c){
        cvv = c;
    }
    public void setMonth(int m){
        month = m;
    }
    public void setYear(int y){
        year = y;
    }
}
