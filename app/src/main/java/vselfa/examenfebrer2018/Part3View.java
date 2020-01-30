package vselfa.examenfebrer2018;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Part3View extends SurfaceView implements SurfaceHolder.Callback{

    // Part 3: Mostra la  interacció entre paleta, pilota i asteroid
    // The thread
    private Part3Thread part3Thread = null;

    // The ball
    private int x, x0, y, y0;
    private static int radius = 20;
    Boolean dispara = false, bingo = false;
    int punts = 0 ;
    boolean fi = false;
    // Speed
    private int xDirection = 20;     private int yDirection = 20;
    private int xDirectionAsteroid = 10;

    // L'asteroide
    private int asteroidX, asteroidY;
    private Rect rectangle = new Rect();

    private Bitmap asteroid, fonsAsteroides, nauEspacial, asteroidExplosio;

    // El xoc
    MediaPlayer mp;

    // Nivell: per anar baixant
    private int altAsteroid = 40, ampleAsteroid = 150, nivell = altAsteroid;

    // La paleta
    private float paletaX, paletaY;   private float ample = 20, alt = 150;

    // Paints i colors
    private static int backgroundColor = Color.WHITE;
    private static int ballColor = Color.BLUE;
    private static int paletaColor = Color.RED;

    Paint text = new Paint ();
    Paint paleta = new Paint();
    Paint background = new Paint();
    Paint ball = new Paint();
    Paint asteroidPaint  = new Paint();

    Boolean xoc = false;
    private float mLastTouchX, mLastTouchY;

    public Part3View(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initGame();
        if (part3Thread != null) return;
        part3Thread = new Part3Thread(getHolder());
        part3Thread.start();
    }

    public void initGame () {
        // Els bitmaps
        asteroid = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
        nauEspacial = BitmapFactory.decodeResource(getResources(), R.drawable.nau_espacial);
        asteroidExplosio = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid_explosion);

        // Situació inicial pilota
        x = getWidth() / 2;
        y = (int) (getHeight() - alt);
        // Pilota dalt de la paleta per a cada vegada que disparem
        y0 = y;
        // Situació inicial asteroid
        asteroidX =0; asteroidY = 0;
        // El rectangle de l'asteroid per al xoc
        rectangle.set( asteroidX, asteroidY, asteroidX +  ampleAsteroid, asteroidY + altAsteroid );
        // Situació inicial paleta
        paletaX = getWidth() / 2;
        paletaY = getHeight() - alt;
        punts = 0;

    }


    public void newDraw(Canvas canvas) {
        // El tauler
        background.setColor(backgroundColor);
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);
         // La pilota
        if (dispara) {
            // La pilota
            ball.setColor(ballColor);
            canvas.drawCircle(x + 120, y, radius, ball);
        } else { // Amagem la pilota
            ball.setColor(backgroundColor);
        }
        // L'asteroid
        asteroidPaint.setColor(Color.RED);
        // canvas.drawRect(asteroidX, asteroidY, asteroidX + ampleAsteroid, asteroidY + altAsteroid, asteroidPaint);
        // El rectangle de l'asteroid per detectar quan la bola està dins i xoca
        rectangle.set(asteroidX - ampleAsteroid, asteroidY,  asteroidX + ampleAsteroid, asteroidY + altAsteroid );
        // Bitmap per a l'asteroid
        if (xoc) { // Explosio asteroid
            canvas.drawBitmap(asteroidExplosio, asteroidX, asteroidY, asteroidPaint);
            Part3Activity.mp.start();
        }
        else { // Asteroid normal
            canvas.drawBitmap(asteroid, asteroidX, asteroidY, asteroidPaint);
        }

        // La paleta
        paleta.setColor(Color.RED);
        // canvas.drawRect(paletaX, paletaY, paletaX + ample, paletaY + alt, paleta);

        // Bitmap per a la nau espacial
        canvas.drawBitmap(nauEspacial, paletaX, paletaY, asteroidPaint );

        if (punts > 0) {
            text.setTextSize(30);
            canvas.drawText("Bingo: " + punts + " punts!", 10, getHeight() - 50, text);
        }
        if (fi) {
            text.setTextSize(50);
            canvas.drawText("Has aconseguit: " + punts + " punts!", 100, getHeight() - 100, text);
            initGame();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}


    @Override
    public void surfaceDestroyed(SurfaceHolder holder)         {
        stopThread();
    }

    public void stopThread() {
        if (part3Thread != null) part3Thread.stop = true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastTouchX = ev.getX();
                mLastTouchY = ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // Calculate the distance moved
                final float x = ev.getX();
                final float y = ev.getY();
                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                // Sols moviment vertical de la paleta 1
                paletaX += dx;
                // Evitem que la paleta se'n isca pels estrems
                if (paletaX < 0) paletaX = 0;
                if (paletaX + ample > getWidth()) paletaX = getWidth() - ample;
                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                // La pilota apareixerà dalt de la paleta
                x = (int) paletaX;
                y = y0; // Dalt de la paleta
                // I eixirà disparada
                dispara = true;
                break;
            }

        }
        return true;// Fi  onTouchEvent
    }


    public void resumeThread() {
        Log.d ("fi", "Parat?: " + part3Thread.stop);
        if (part3Thread.stop) part3Thread.stop = false;
    }

    // The thread -----------------------------------------------------------
    private class Part3Thread extends Thread {

        public boolean stop = false;
        private SurfaceHolder surfaceHolder;

        public Part3Thread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void run() {
            while (!stop) {
                // 1.- El moviment de l'asteroid: Sols es desplaça  en horitzontal
                // asteroidX += xDirection;
                asteroidX += xDirectionAsteroid;
                // Arriba a la dreta
                if (asteroidX + ample > getWidth()) {
                    // Apareix per l'altre costat
                    asteroidX = 0;
                    // Baixa un nivell
                    asteroidY += 4*nivell;
                    // Anul·la l'efecte del possible xoc
                    xoc = false;
                }
                // 2.- El moviment de la bala
                if (dispara) {
                    // El moviment de la bala: Sols es desplaça cap amunt
                    // x += xDirection;
                    y -= yDirection;
                    // Rebot dalt
                    if (y < 0) {
                        // La bala  despareix
                        dispara = false;
                    }
                }
                // Control del xoc ab l'asteroid en un futur
                if (xoc(rectangle, x, y) ) {
                   // L'asteroid explota? No, per ara
                   punts ++;
                   // La pilota desapareix
                    x = getWidth() / 2; y = y0;
                    dispara = false;
                    xoc = true;
                }
                // Repintem
                // Asteroid arriba baix
                if (asteroidY > getHeight() - alt ) {
                    // Per mostrar el total de punts
                    fi = true;
                }
                Canvas c = null;
                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        newDraw(c);
                    }
                } finally {
                    if (c != null) surfaceHolder.unlockCanvasAndPost(c);
                }
                if (fi) {
                    // Dormim el thread DESPRÉS de mostrar els punts!
                    fi = false;
                    try {
                        part3Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }
    }

    public boolean xoc (Rect r, int x, int y) {
        if (r.contains(x, y)) {
            return true;
        }
        else {
            return false;
        }
    }
}
