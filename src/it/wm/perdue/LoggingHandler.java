package it.wm.perdue;

import android.app.Activity;

import it.wm.perdue.businessLogic.LoginData;

public class LoggingHandler {
    
    private static OnLoggingHandlerListener listener = null;
    
    public interface OnLoggingHandlerListener{
        public void onDidLogin();
        public void onDidLogout();
    }
    
    public static void setListener(Activity l){
        listener = (OnLoggingHandlerListener) l;
    }
    
    //salva i dati di login
    public static void doLogin(LoginData l){
        Utils.setPreferenceString("userName",l.getNomeContatto());
        Utils.setPreferenceString("userSurname",l.getCognomeContatto());
        Utils.setPreferenceString("userMail",l.getEmail());
        Utils.setPreferenceString("userId",l.getIdCustomer()+"");
        listener.onDidLogin();
    }
    
    //recupera i dati di login
    public static LoginData getSavedLoginData(){
        return new LoginData(Utils.getPreferenceString("userName",""),
                Utils.getPreferenceString("userSurname",""),Utils.getPreferenceString("userMail",""),
                Integer.parseInt(Utils.getPreferenceString("userId","-1")));
    }
    
    public static void doLogout(){
        Utils.setPreferenceString("userName",null);
        Utils.setPreferenceString("userSurname",null);
        Utils.setPreferenceString("userMail",null);
        Utils.setPreferenceString("userId",-1+"");
        listener.onDidLogout();
    }
    
    public static boolean isLogged(){
        if(getSavedLoginData().getIdCustomer() != -1)
            return true;
        else return false;
    }
}
