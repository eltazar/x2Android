package it.wm.perdue;

import it.wm.perdue.businessLogic.LoginData;

public class LoggingHandler {
    
    //salva i dati di login
    public static void onDidLogin(LoginData l){
        Utils.setPreferenceString("userName",l.getNomeContatto());
        Utils.setPreferenceString("userSurname",l.getCognomeContatto());
        Utils.setPreferenceString("userMail",l.getEmail());
        Utils.setPreferenceString("userId",l.getIdCustomer()+"");
    }
    
    //recupera i dati di login
    public static LoginData getSavedLoginData(){
        return new LoginData(Utils.getPreferenceString("userName",""),
                Utils.getPreferenceString("userSurname",""),Utils.getPreferenceString("userMail",""),
                Integer.parseInt(Utils.getPreferenceString("userId","-1")));
    }
    
    public static void onDidLogout(){
        Utils.setPreferenceString("userName",null);
        Utils.setPreferenceString("userSurname",null);
        Utils.setPreferenceString("userMail",null);
        Utils.setPreferenceString("userId",-1+"");
    }
    
    public static boolean isLogged(){
        if(getSavedLoginData().getIdCustomer() != -1)
            return true;
        else return false;
    }
}
