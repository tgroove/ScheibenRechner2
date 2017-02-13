package net.herch.thorsten.scheibenrechner2;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by Thorsten on 31.01.2017.
 */

public class Scheibe {
    private  Feld[][] spielfeld;
    private  int ergebnis;
    private  int felderproseite;
    private  String name;
    private static ArrayList Ergebnisse = new ArrayList<Integer>();

    public void setFelderProSeite(int felder) {
        if(spielfeld != null) {
            if (felderproseite != felder) {
                Feld[][] neues_feld = new Feld[felder][felder];
                int wert;
                boolean schwarz;
                for (int i = 0; i < neues_feld.length; i++) {
                    for (int j = 0; j < neues_feld.length; j++) {
                        if ((i < spielfeld.length) && (j < spielfeld.length)) {
                            wert = spielfeld[j][i].getWert();
                            schwarz = spielfeld[j][i].getSchwarzesFeld();
                        } else {
                            wert = 0;
                            schwarz = spielfeld[j%2][i%2].getSchwarzesFeld();;
                        }
                        neues_feld[j][i] = new Feld(wert, schwarz, false);
                    }
                }
                spielfeld = neues_feld;
            }
        }
        felderproseite = felder;
    }

    public int getFelderProSeite() {
        return felderproseite;
    }

    public void setName(String ScheibenName) { name =ScheibenName; }

    public String getName() { return name; }

    public  void main() {
    /*ein quadratisches zweidimensionales Array mit 4 Feldern*/
        spielfeld = new Feld[felderproseite][felderproseite];

    /*initialisiere das Spielfeld mit den Werten oder random-zahlen*/
        int wert;
        for (int i = 0; i < spielfeld.length; i++) {
            for (int j = 0; j < spielfeld.length; j++) {
                wert = i * felderproseite + j + 1;
                spielfeld[j][i] = new Feld(wert, (wert%2)==1, false);
            }
        }
    /*Ausgabe des Spielfeldes, nur zum Testen*/
        /*
        for (int i = 0; i < spielfeld.length; i++) {
            for (int j = 0; j < spielfeld.length; j++) {
                System.out.print(spielfeld[i][j].getWert() + "-");
            }
            System.out.println();
        }
        */
    }

    public Feld feld(int x, int y){
        return spielfeld[x][y];
    }

    public int Summe(){
        int s = 0;

        for (int i = 0; i < spielfeld.length; i++) {
            for (int j = 0; j < spielfeld.length; j++) {
                if (spielfeld[i][j].getAktiviert())
                    s += spielfeld[i][j].getWert();
            }
        }

        return s;
    }


    public String getJSON(){
        int zeile, spalte;
        String jsonStr = "";
        JSONObject object = new JSONObject();
        JSONObject jsonFeld = null;
        JSONArray jsonArray = new JSONArray();
        try {
            object.put("Name", name);
            object.put("FelderProSeite", felderproseite);
            for(zeile=0; zeile<felderproseite; zeile++) {
                for(spalte=0; spalte<felderproseite; spalte++){
                    jsonFeld = new JSONObject();
                    jsonFeld.put("Zeile", zeile);
                    jsonFeld.put("Spalte", spalte);
                    jsonFeld.put("Wert", spielfeld[spalte][zeile].getWert());
                    jsonFeld.put("Schwarz", spielfeld[spalte][zeile].getSchwarzesFeld());
                    jsonFeld.put("Aktiviert", spielfeld[spalte][zeile].getAktiviert());
                    jsonArray.put(jsonFeld);
                }
            }
            object.put("Felder", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonStr = object.toString();
        System.out.println(jsonStr);

        return jsonStr;

    }


    public void setByJSON(String jsonStr){
        if (jsonStr != null) {
            System.out.println(jsonStr);
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                name = jsonObj.getString("Name");
                felderproseite = jsonObj.getInt("FelderProSeite");
                //System.out.println("FpS".concat(Integer.toString(felderproseite)));
                spielfeld = new Feld[felderproseite][felderproseite];


                // Getting JSON Array node
                JSONArray feld = jsonObj.getJSONArray("Felder");

                // looping through All Contacts
                for (int i = 0; i < feld.length(); i++) {
                    JSONObject c = feld.getJSONObject(i);
                    int spalte = c.getInt("Spalte");
                    int zeile = c.getInt("Zeile");
                    int wert = c.getInt("Wert");
                    boolean schwarz = c.getBoolean("Schwarz");
                    boolean aktiviert = c.getBoolean("Aktiviert");
//                    System.out.println("Z".concat(Integer.toString(zeile)).concat("S".concat(Integer.toString(spalte))).concat("W".concat(Integer.toString(wert))));
                    spielfeld[spalte][zeile] = new Feld(wert, schwarz, aktiviert);

                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());

            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
    }

    public void ClearActivate() {
        System.out.println("ClearActivate");
        int i, j;
        for(i=0; i<felderproseite; i++) {
            for(j=0; j<felderproseite; j++) {
                feld(i, j).setAktiviert(false);
            }
        }
    }


    public static void addErgebnis(int wert) {
        Ergebnisse.add(wert);
    }

    public static void clrErgebnise() {
        System.out.println("clrErgebnisse");
        Ergebnisse.clear();
    }

    public static ArrayList<Integer> getErgebnisse() {
        return Ergebnisse;
    }

}

