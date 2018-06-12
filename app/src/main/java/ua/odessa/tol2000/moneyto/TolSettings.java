package ua.odessa.tol2000.moneyto;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Tolik on 18.06.2015.
 */

/**
 * start with initSettings(MainActivity)
 */

public class TolSettings {

    private static SharedPreferences fPref = null;

    public static void initSettings(Activity pAct) {
        if (fPref==null) fPref = pAct.getSharedPreferences(pAct.getPackageName() + ".preferences", pAct.MODE_PRIVATE);
    }

    public static SharedPreferences getPref() throws NullPointerException {
        if (fPref==null) throw new NullPointerException("Preferences was not initialized by initSettings()");
        return fPref;
    }

    public static void setPrefValue(String pPrefName, String pPrefVal) throws NullPointerException {
        SharedPreferences.Editor editor = getPref().edit();
        editor.putString(pPrefName, pPrefVal);
        editor.commit();
    }

    public static String getPrefValue(String pPrefName) throws NullPointerException {
        return getPref().getString(pPrefName,"");
    }

}
