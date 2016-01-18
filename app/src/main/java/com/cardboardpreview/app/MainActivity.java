package com.cardboardpreview.app;

import android.os.Bundle;
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
//        mStereoRenderer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mStereoRenderer.resume();
    }
}
