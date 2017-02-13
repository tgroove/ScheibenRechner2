package net.herch.thorsten.scheibenrechner2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;

//import static net.herch.thorsten.scheibenrechner2.MainActivity.scheibe;

/**
 * Created by Thorsten on 02.02.2017.
 */


public class DrawingView extends View {
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    int TouchX, TouchY;
    private int FelderProScheibe;
    private int kantenlaenge;
    private int kanteohnerand;
    private  int linienbreite;
    private TextView textView;
    private boolean edit_mode;
    private int rand = 40;
    private final Rect textBounds = new Rect();

    private Bitmap bmpKlein;

    Scheibe s;

    public  void setEditMode(boolean EditMode) { edit_mode = EditMode; }
    public  boolean getEditMode() { return edit_mode; }



    public void setScheibe(Scheibe scheibe){
        System.out.print("setScheibe:");

        System.out.println();

        s = scheibe;

    }

    public Scheibe getScheibe() {
        return s;
    }

    public  DrawingView(Context context) {
        super(context);
        canvasBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        this.mListener = null;
    }

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);

    }


    protected void _onSizeChanged(int w, int h, int oldw, int oldh) {
//view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

    }



    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int i;
        int p;

        if(s==null) {
            FelderProScheibe = 5;
        }
        else {
            FelderProScheibe = s.getFelderProSeite();
        }
        int x = getWidth();
        int y = getHeight();
        int radius;
        if (getWidth() < getHeight()) {
            kantenlaenge = getWidth();
        }
        else {
            kantenlaenge =  getHeight();
        }
        ViewGroup.LayoutParams params = this.getLayoutParams();

        if (params.width != kantenlaenge) {
            params.width = kantenlaenge;
            this.setLayoutParams(params);
        }
        else if (params.height != kantenlaenge) {
            params.height = kantenlaenge;
            this.setLayoutParams(params);
        }
        rand = kantenlaenge / 30;
        kanteohnerand = kantenlaenge - 2*rand;
        linienbreite = kantenlaenge / 180;
        Paint paint = new Paint();
        Paint lines_paint = new Paint();
        Paint inv_paint = new Paint();
        Paint black_paint = new Paint();
        Paint active_paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        // Use Color.parseColor to define HTML colors
        paint.setColor(Color.parseColor("#FAFCB6"));
        canvas.drawRect(0, 0, kantenlaenge, kantenlaenge, paint);

        lines_paint.setStyle(Paint.Style.STROKE);
        lines_paint.setStrokeWidth(linienbreite);
        lines_paint.setColor(Color.BLACK);
        canvas.drawRect(rand, rand, kantenlaenge-rand, kantenlaenge-rand, lines_paint);
        for(i=1; i<FelderProScheibe; i++) {
            p = rand + kanteohnerand * i / FelderProScheibe;
            canvas.drawLine(rand, p, kantenlaenge-rand, p, lines_paint);
            canvas.drawLine(p, rand, p, kantenlaenge-rand, lines_paint);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(kantenlaenge/19);
        paint.setTextAlign(Paint.Align.CENTER);
        inv_paint.set(paint);
        inv_paint.setColor(Color.WHITE);

        black_paint.setStyle(Paint.Style.FILL);
        black_paint.setColor(Color.BLACK);

        active_paint.setStyle(Paint.Style.FILL);
        active_paint.setColor(Color.RED);
        if(s == null) return;

        System.out.println();
        Paint paintFont = new Paint();

        for(x=0; x < FelderProScheibe; x++){
            for(y=0; y < FelderProScheibe; y++){
                if(s.feld(x, y).getSchwarzesFeld()) {
                    paintFont = inv_paint;
                    if(s.feld(x, y).getAktiviert()) {
                        canvas.drawRect(rand + x * kanteohnerand / FelderProScheibe,
                                rand + y * kanteohnerand / FelderProScheibe,
                                rand + (x + 1) * kanteohnerand / FelderProScheibe,
                                rand + (y + 1) * kanteohnerand / FelderProScheibe, active_paint);
                        canvas.drawRect(rand + x * kanteohnerand / FelderProScheibe,
                                rand + y * kanteohnerand / FelderProScheibe,
                                rand + (x + 1) * kanteohnerand / FelderProScheibe,
                                rand + (y + 1) * kanteohnerand / FelderProScheibe, lines_paint);
                        paintFont = paint;
                    }
                    else  {
                        canvas.drawRect(rand + x * kanteohnerand / FelderProScheibe,
                                rand + y * kanteohnerand / FelderProScheibe,
                                rand + (x + 1) * kanteohnerand / FelderProScheibe,
                                rand + (y + 1) * kanteohnerand / FelderProScheibe, black_paint);
                    }
                    paint.getTextBounds("0", 0, 1, textBounds);
                    canvas.drawText(Integer.toString(s.feld(x, y).getWert()),
                            rand + (int) ((float) (x + .5) * kanteohnerand / FelderProScheibe),
                            rand + (int) (((float) y + .5) * kanteohnerand / FelderProScheibe) - textBounds.exactCenterY(),
                            paintFont);
                }
                else {
                    paintFont = paint;
                    if(s.feld(x, y).getAktiviert()) {
                        canvas.drawRect(rand + x * kanteohnerand / FelderProScheibe,
                                rand + y * kanteohnerand / FelderProScheibe,
                                rand + (x + 1) * kanteohnerand / FelderProScheibe,
                                rand + (y + 1) * kanteohnerand / FelderProScheibe, active_paint);
                        canvas.drawRect(rand + x * kanteohnerand / FelderProScheibe,
                                rand + y * kanteohnerand / FelderProScheibe,
                                rand + (x + 1) * kanteohnerand / FelderProScheibe,
                                rand + (y + 1) * kanteohnerand / FelderProScheibe, lines_paint);
                        paintFont = paint;
                    }
                    paint.getTextBounds("0", 0, 1, textBounds);
                    canvas.drawText(Integer.toString(s.feld(x, y).getWert()),
                            rand + (int) ((float) (x + .5) * kanteohnerand / FelderProScheibe),
                            rand + (int) (((float) y + .5) * kanteohnerand / FelderProScheibe) - textBounds.exactCenterY(),
                            paintFont);
                }

            }
        }
        if(mListener != null) {
            mListener.onEvent();
        }
    }


    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {

            /*
            int FelderProScheibe = s.getFelderProSeite();
            TouchX = (int) (FelderProScheibe * e.getX() / kantenlaenge);
            TouchY = (int) (FelderProScheibe * e.getY() / kantenlaenge);

            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
            View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
            alertDialogBuilderUserInput.setView(mView);

            final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
            userInputDialogEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            alertDialogBuilderUserInput
                    .setCancelable(false)
                    .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                            // ToDo get user input here
                            scheibe.feld(TouchX, TouchY).setWert(Integer.parseInt(userInputDialogEditText.getText().toString()));
                            invalidate();
                        }
                    })

                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    dialogBox.cancel();
                                }
                            });
            AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
            alertDialogAndroid.show();
*/

        }

        public boolean onDown(MotionEvent e) {
            int FelderProScheibe = s.getFelderProSeite();

            if((e.getX()<kantenlaenge) && (e.getY()<kantenlaenge)) {
                if((e.getX()<rand) || (e.getX()>rand+kanteohnerand)) return true;
                if((e.getY()<rand) || (e.getY()>rand+kanteohnerand)) return true;
                TouchX = (int) (FelderProScheibe * (e.getX() - rand) / kanteohnerand);
                TouchY = (int) (FelderProScheibe * (e.getY() - rand) / kanteohnerand);
                if(edit_mode) {
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
                    View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
                    alertDialogBuilderUserInput.setView(mView);

                    final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                    userInputDialogEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    userInputDialogEditText.setText(Integer.toString(s.feld(TouchX, TouchY).getWert()));
                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    // ToDo get user input here
                                    if(!userInputDialogEditText.getText().toString().matches("")) {
                                        s.feld(TouchX, TouchY).setWert(Integer.parseInt(userInputDialogEditText.getText().toString()));
                                    }
                                    invalidate();
                                }
                            })

                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });
                    AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                    alertDialogAndroid.show();

                }
                else {
                    s.feld(TouchX, TouchY).setAktiviert(!s.feld(TouchX, TouchY).getAktiviert());
                    invalidate();
                }
            }

            return true;
        }
    });

    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    };


    public Bitmap getScheibeAsBitmap(int newWidth){
        Bitmap bmpScheibe;
        Buffer bufTmp;
        int width;
        bmpScheibe = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        //canvasBitmap.copy(bmpScheibe.getConfig(), true);
        //width = origBitmap.getWidth();
        width = kantenlaenge;
        bmpScheibe = getDrawingCache();
        bmpKlein = Bitmap.createScaledBitmap(bmpScheibe, 100, 100, false);
        return bmpScheibe;
    }


    public void test(Bitmap bmp) {
        System.out.println("Set bmpKlein");

        //Bitmap xbmpKlein = Bitmap.createScaledBitmap(bmp, 100, 100, false);

    }


    public interface OnCustomEventListener{
        public void onEvent();   //method, which can have parameters
    }

    private OnCustomEventListener mListener; //listener field

    //setting the listener
    public void setCustomEventListener(OnCustomEventListener eventListener) {
        this.mListener=eventListener;
    }

    public int getKantenlaenge() {
        return kantenlaenge;
    }

}

