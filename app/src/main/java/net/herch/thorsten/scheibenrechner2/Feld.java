package net.herch.thorsten.scheibenrechner2;

/**
 * Created by Thorsten on 01.02.2017.
 */

public  class Feld {
    private int wert;
    private boolean schwarzesFeld;
    private boolean aktiviert;

    public Feld(int wert, boolean schwarzesFeld, boolean aktiviert) {
        this.wert = wert;
        this.schwarzesFeld = schwarzesFeld;
        this.aktiviert = aktiviert;
    }

    public void setAktiviert(boolean aktiviert) {
        this.aktiviert = aktiviert;
    }

    public boolean getAktiviert(){
        return this.aktiviert;
    }

    public int getWert() {
        return this.wert;
    }

    public void setWert(int value) {
        this.wert = value;
    }

    public void setSchwarzesFeld(boolean schwarz) {
        this.schwarzesFeld = schwarz;
    }

    public boolean getSchwarzesFeld() {
        return this.schwarzesFeld;
    }
}


