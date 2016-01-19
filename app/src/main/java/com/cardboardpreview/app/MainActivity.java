package com.cardboardpreview.app;

import android.os.Bundle;
import android.util.Log;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

public class MainActivity extends CardboardActivity {

    private VrStereoRenderer mStereoRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);

        mStereoRenderer = new VrStereoRenderer(this, cardboardView);

        // Associate a CardboardView.StereoRenderer with cardboardView.
        cardboardView.setRenderer(mStereoRenderer);
        // Associate the cardboardView with this activity.
        setCardboardView(cardboardView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("++++", "onPause");
        mStereoRenderer.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("++++", "onResume");
        mStereoRenderer.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d("++++ MainActivity ++++", "onWindowFocusChanged(hasFocus=" + hasFocus + ")");
        super.onWindowFocusChanged(hasFocus);
    }
}
