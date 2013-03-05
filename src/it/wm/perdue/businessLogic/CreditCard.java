package it.wm.perdue.businessLogic;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CreditCard implements Parcelable{
    private String owner;
    private String number;
    private String cvv;
    private int month;
    private int year;
    private int institute;
    
    public int getInstitute(){
        return institute;
    }
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
    
    public void setInstitute(int i){
        institute = i;
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
    
    public String toString(){
        return "CARTA DI CREDITO: n = "+number+" cvv = "+cvv+" owner = "+owner+" month"+month
                +" year = "+year+" institute = "+institute;
    }
    
    public CreditCard(){
    }
    
    public CreditCard(Parcel in) {  
        Log.d("check","creo carta di credito parcelable");
        readFromParcel(in);  
    }  
    
    @Override  
    public void writeToParcel(Parcel out, int flags) {  
        Log.d("check","write to parcel");
        out.writeString(owner);  
        out.writeString(number);  
        out.writeString(cvv);
        out.writeInt(month);
        out.writeInt(year);
        out.writeInt(institute); 
    }  
    
    private void readFromParcel(Parcel in) {    
        Log.d("check","read from to parcel");

        owner = in.readString();  
        number = in.readString();  
        cvv = in.readString();  
        month = in.readInt();
        year = in.readInt();  
        institute = in.readInt();
    }  
    
    public static final Parcelable.Creator<CreditCard> CREATOR = new Parcelable.Creator<CreditCard>() {  
        
        public CreditCard createFromParcel(Parcel in) {  
            return new CreditCard(in);  
        }  
        
        public CreditCard[] newArray(int size) {  
            return new CreditCard[size];  
        }  
        
    };  
    
    @Override  
    public int describeContents() {  
        return 0;  
    } 
    
    
}
