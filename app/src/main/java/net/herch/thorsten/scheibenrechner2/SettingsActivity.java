package net.herch.thorsten.scheibenrechner2;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static final String STATE_CONFIG_SCHEIBE = "net.herch.thorsten.scheibenrechner2.ConfigScheibe";

    //static Scheibe config_scheibe;
    Scheibe config_scheibe;
    DrawingView dv;
    boolean aenerungen_uebernehmen = true;
    boolean changed = false;
    private Bundle b = new Bundle();
    String jsonOriginal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Spinner element
        Spinner spinnerLayout = (Spinner) findViewById(R.id.spinner_scheiben_layout);
        Spinner spinnerFelder = (Spinner) findViewById(R.id.spinner_felder);
        // Spinner click listener
        spinnerLayout.setOnItemSelectedListener(this);
        spinnerFelder.setOnItemSelectedListener(this);

        config_scheibe = new Scheibe();

        String jsonStr;
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b!=null)
        {
            jsonOriginal =(String) b.get("JSON_DATA");
            System.out.println(jsonOriginal);
            config_scheibe.setByJSON(jsonOriginal);
        }
        else {
            System.out.println("B==null");
        }
        if(config_scheibe.feld(0,0).getSchwarzesFeld()) {
            spinnerLayout.setSelection(1);
        }
        else if(config_scheibe.feld(1,0).getSchwarzesFeld()) {
            spinnerLayout.setSelection(2);
        }
        else {
            spinnerLayout.setSelection(0);
        }

        spinnerFelder.setSelection(config_scheibe.getFelderProSeite()-5);

        if (savedInstanceState != null) {
            config_scheibe.setByJSON(savedInstanceState.getString(STATE_CONFIG_SCHEIBE));
        }

        dv =  (DrawingView)findViewById(R.id.drawing_config_scheibe);
        dv.setScheibe(config_scheibe);
        dv.setEditMode(true);

        System.out.print("Name config scheibe: ".concat(config_scheibe.getName()));
        System.out.println();
        int x, y;
        int FelderProSeite = config_scheibe.getFelderProSeite();
        for(x=0; x<FelderProSeite; x++) {
            for(y=0; y<FelderProSeite; y++) {
                config_scheibe.feld(x, y).setAktiviert(false);
            }
        }
    }


    @Override
    public void finish(){
        String jsonStr;

        if(aenerungen_uebernehmen) {

            jsonStr = config_scheibe.getJSON();

            Intent data = new Intent();
            data.putExtra("JSON_DATA", jsonStr);
            setResult(RESULT_OK, data);
        }
        super.finish();
    }


    @Override
    public void onBackPressed() {
        if(jsonOriginal.equals(config_scheibe.getJSON())) {
            finish();
            return;
        }
        else {
            System.out.println("Org: ".concat(jsonOriginal));
            System.out.println("New: ".concat(config_scheibe.getJSON()));
        }

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        aenerungen_uebernehmen = true;
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        aenerungen_uebernehmen = false;
                        finish();
                        break;
                }
            }

        };


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.do_you_really_want_to_exit)).setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int x, y;
        boolean bw;

        if(parent.getId() == R.id.spinner_scheiben_layout) {
            int FelderProSeite = config_scheibe.getFelderProSeite();
            switch (position) {
                case 0:
                    // Weiß
                    for(x=0; x<FelderProSeite; x++) {
                        for(y=0; y<FelderProSeite; y++) {
                            config_scheibe.feld(x, y).setSchwarzesFeld(false);
                        }
                    }

                    break;
                case 1:
                    // Schwarz/Weiß
                    bw = false;
                    for(x=0; x<FelderProSeite; x++) {
                        if(x>0) bw = config_scheibe.feld(0,x-1).getSchwarzesFeld();
                        for(y=0; y<FelderProSeite; y++) {
                            bw = !bw;
                            config_scheibe.feld(y, x).setSchwarzesFeld(bw);
                        }
                    }
                    break;
                case 2:
                    // Weiß/Schwarz
                    bw = true;
                    for(x=0; x<FelderProSeite; x++) {
                        if(x>0) bw = config_scheibe.feld(0,x-1).getSchwarzesFeld();
                        for(y=0; y<FelderProSeite; y++) {
                            bw = !bw;
                            config_scheibe.feld(y, x).setSchwarzesFeld(bw);
                        }
                    }
            }
        }
        else {
            System.out.println("AdapterView != parent");
            config_scheibe.setFelderProSeite(position+5);
            //config_scheibe.main();
        }
        dv.invalidate();
    }


    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("My Title");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putString(STATE_CONFIG_SCHEIBE, config_scheibe.getJSON());
    }

}


