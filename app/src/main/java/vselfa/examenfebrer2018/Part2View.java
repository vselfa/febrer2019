package vselfa.examenfebrer2018;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Part2View extends SurfaceView implements SurfaceHolder.Callback{

    // The thread
    private Part2Thread part2Thread = null;

    // Speed
    private int xDirection = 10;
    // Desplaçament de l'asteroide
    //private int yDirection = 10;
    private static int asteroidColor = Color.RED;
    private static int backgroundColor = Color.WHITE;

    // L'asteroide
    private float asteroidX, asteroidY;
    // El bitmaps
    private Bitmap asteroid, fonsAsteroides, nauEspacial;
    private Rect fonsPantalla;

    // Nivell: per anar baixant
    private float alt = 40, ample = 150, nivell = alt;

    // Paints
    Paint asteroidPaint = new Paint();
    Paint background = new Paint();

    private float mLastTouchX, mLastTouchY;

    public Part2View(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // Els bitmaps
        asteroid = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
        fonsAsteroides = BitmapFactory.decodeResource(getResources(), R.drawable.fons_asteroides);
        nauEspacial = BitmapFactory.decodeResource(getResources(), R.drawable.nau_espacial);
        // Situació inicial asteroid
        asteroidX =0; asteroidY = 0;
        // Llancem el thread
        if (part2Thread != null) return;
        part2Thread = new Part2Thread(getHolder());
        part2Thread.start();
    }

    public void newDraw(Canvas canvas) {
        // El tauler
        background.setColor(backgroundColor);
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);
        // Canviar el fons de pantalla
        // fonsPantalla = new Rect(0, 0, getWidth(), getHeight());
        // canvas.drawBitmap(fonsAsteroides, fonsPantalla,  fonsPantalla, background);
        // L'asteroid
        asteroidPaint.setColor(Color.RED);
        // canvas.drawRect(asteroidX, asteroidY, asteroidX + ample, asteroidY + alt, asteroidPaint);
        canvas.drawBitmap(asteroid, asteroidX, asteroidY, asteroidPaint );
        Log.d ("examen", "px = " + asteroidX + "  py = " + asteroidY );
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopThread();
    }

    public void stopThread() {
        if (part2Thread != null) part2Thread.stop = true;
    }

    // The thread -----------------------------------------------------------
    private class Part2Thread extends Thread {

        public boolean stop = false;
        private SurfaceHolder surfaceHolder;

        public Part2Thread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void run() {
            while (!stop) {
                // El moviment de l'asteroid: Sols es desplaça  en horitzontal
                asteroidX += xDirection;
                // Arriba a la dreta
                if (asteroidX + ample > getWidth()) {
                    // Apareix per l'altre costat
                    asteroidX = 0;
                    // Baixa un nivell
                    asteroidY += nivell;
                }
                // Control del xoc amb el meteorit en un futur
                Canvas c = null;
                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        newDraw(c);
                    }
                } finally {
                    if (c != null) surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}
