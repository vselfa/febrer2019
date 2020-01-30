package vselfa.examenfebrer2018;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Part3Activity extends MainMenu {

    public static Part3View part3View;
    // El so
    public static MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        part3View = new Part3View(this);
        // El so
        mp = MediaPlayer.create(this, R.raw.explosion);
        setContentView(part3View);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("SurfaceView", "onPause");
        // Per evitar l'error a l'eixir de l'aplicació
        if (part3View != null) part3View.stopThread();
    }
}
