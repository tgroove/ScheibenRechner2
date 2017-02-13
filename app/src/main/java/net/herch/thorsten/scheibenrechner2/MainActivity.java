package net.herch.thorsten.scheibenrechner2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {
    private  Scheibe scheibe = new Scheibe();
    private DrawingView dv;
    final Context c = this;
    public final static String EXTRA_MESSAGE = "net.herch.thorsten.scheibenrechner2.EXTRA";
    public final static String JSON_DATA = "net.herch.thorsten.scheibenrechner2.JSON_DATA";
    public final static String UPDATE_MESSAGE = "net.herch.thorsten.scheibenrechner2.UpdateSumme";
    private static final String STATE_SCHEIBE = "net.herch.thorsten.scheibenrechner2.Scheibe";
    public final int REQUEST_CODE = 1234;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int FelderProScheibe = 5;
        //scheibe = new Scheibe();
        //if(scheibe != null) System.out.println("Scheibe != null / ".concat(Integer.toString(scheibe.getFelderProSeite())));
        scheibe.setFelderProSeite(FelderProScheibe);
        scheibe.setName("Auswertung");
        scheibe.main();
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< onCreate >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        if (savedInstanceState != null) {
            scheibe.setByJSON(savedInstanceState.getString(STATE_SCHEIBE));
            System.out.println("Load State");
        }
        else {
            String load_filename = "current.ftg";
            String readString = new String();
            FileInputStream inputStream;
            File file = getBaseContext().getFileStreamPath(load_filename);
            if(file.exists()) {
                try {
                    System.out.println("Lade current.ftg");
                    InputStream iStream = openFileInput(load_filename);
                    //InputStream iStream = context.getResources().openRawResource(R.raw.scheibe1);
                    byte[] input = new byte[iStream.available()];
                    while (iStream.read(input) != -1) {
                    }
                    readString += new String(input);
                    iStream.close();
                    System.out.println("Load Data:");
                    System.out.println(readString);
                    scheibe.setByJSON(readString);
                    //scheibe.ClearActivate();
                    //scheibe.clrErgebnise();
                    //dv.invalidate();
                } catch (Exception e) {
                    Toast.makeText(this, "File load failed", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                    System.out.println( e.getMessage());
                }
            }
        }


        dv = (DrawingView)findViewById(R.id.drawing_scheibe);
        System.out.println("Kantenlänge: ".concat(Integer.toString(dv.getKantenlaenge())));
        System.out.println("Width: ".concat(Integer.toString(dv.getWidth())));

        dv.setEditMode(false);
        dv.setCustomEventListener(new DrawingView.OnCustomEventListener() {
            @Override
            public void onEvent(){
                //ArrayList Ergebnisse = new ArrayList<Integer>();
                Iterator<Integer> itr = scheibe.getErgebnisse().listIterator();
                String strErg = "";
                int sum =0;
                while (itr.hasNext()) {
                    Integer element = itr.next();
                    sum += element;
                    //System.out.println(element + " ");
                    strErg = strErg.concat(element.toString()).concat("+\u200B");
                }

                if(strErg.length()==0) {
                    strErg = "=".concat(Integer.toString(scheibe.Summe()));
                }
                else {
                    sum += scheibe.Summe();
                    strErg = strErg.concat(Integer.toString(scheibe.Summe())).concat("\u200B=").concat(Integer.toString(sum));
                }
                TextView tvSumme = (TextView)findViewById(R.id.textView_summe);
                tvSumme.setText(strErg);
            }
        });

        dv.setScheibe(scheibe);
        System.out.print("Name Auswert Scheibe: ".concat(scheibe.getName()));
        System.out.println();
    }


    public void goConfig (View view ) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        String jsonStr;
        jsonStr = scheibe.getJSON();
        intent.putExtra("JSON_DATA", jsonStr);
        startActivityForResult(intent, REQUEST_CODE);
    }


    public void doAdd(View view) {
        if(scheibe.Summe()>0) {
            scheibe.addErgebnis(scheibe.Summe());
            scheibe.ClearActivate();
            dv.invalidate();
        }
    }


    public void doClear(View view) {
        scheibe.ClearActivate();
        scheibe.clrErgebnise();
        dv.invalidate();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra("JSON_DATA")) {
                String jsonStr;
                jsonStr = data.getExtras().getString("JSON_DATA");
                System.out.println(jsonStr);
                scheibe.setByJSON(jsonStr);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String readString = new String();
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_load:
                String load_filename = "current.ftg";
                FileInputStream inputStream;

                try {
                    Context context = getApplicationContext();
                    InputStream iStream = context.getResources().openRawResource(R.raw.scheibe1);
                    byte[] input = new byte[iStream.available()];
                    while (iStream.read(input) != -1) {
                    }
                    readString += new String(input);
                    iStream.close();
                    System.out.println("Load Data:");
                    System.out.println(readString);
                    scheibe.clrErgebnise();
                    scheibe.setByJSON(readString);
                    dv.invalidate();
                } catch (Exception e) {
                    Toast.makeText(this, "File load failed", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }
                break;
            case R.id.action_load1:
                try {
                    Context context = getApplicationContext();
                    InputStream iStream = context.getResources().openRawResource(R.raw.scheibe1);
                    byte[] input = new byte[iStream.available()];
                    while (iStream.read(input) != -1) {
                    }
                    readString += new String(input);
                    iStream.close();
                    System.out.println("Load Data:");
                    System.out.println(readString);
                    scheibe.clrErgebnise();
                    scheibe.setByJSON(readString);
                    dv.invalidate();
                } catch (Exception e) {
                    Toast.makeText(this, "File load failed", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }
                break;
            case R.id.action_load2:
                try {
                    Context context = getApplicationContext();
                    InputStream iStream = context.getResources().openRawResource(R.raw.scheibe2);
                    byte[] input = new byte[iStream.available()];
                    while (iStream.read(input) != -1) {
                    }
                    readString += new String(input);
                    iStream.close();
                    System.out.println("Load Data:");
                    System.out.println(readString);
                    scheibe.clrErgebnise();
                    scheibe.setByJSON(readString);
                    dv.invalidate();
                } catch (Exception e) {
                    Toast.makeText(this, "File load failed", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }
                break;
            case R.id.action_save:
                String save_filename = "current.ftg";
                String jsonStr = scheibe.getJSON();
                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput(save_filename, Context.MODE_PRIVATE);
                    outputStream.write(jsonStr.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    Toast.makeText(this, "File save failed", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }
                break;
            case R.id.action_edit:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(EXTRA_MESSAGE, message);
                String jsonS;
                jsonS = scheibe.getJSON();
                intent.putExtra("JSON_DATA", jsonS);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = scheibe.getJSON();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        System.out.println("<<<<<<<<<<<<<<<<<<<< onSaveInstanceState >>>>>>>>>>>>>>>>>>>>>>>>>>");
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putString(STATE_SCHEIBE, scheibe.getJSON());
        System.out.println("Save System State");
    }


    @Override
    protected void onPause(){
        super.onPause();
        System.out.print("<<<<<<<<<<<<<<<<<<< onPause >>>>>>>>>>>>>>>>>>>>>>>>>>");
        String save_filename = "current.ftg";
        String jsonStr = scheibe.getJSON();
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(save_filename, Context.MODE_PRIVATE);
            outputStream.write(jsonStr.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(this, "File save failed", Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    protected void onDestroy(){
        System.out.println("onDestroy");
        super.onDestroy();
    }

}
