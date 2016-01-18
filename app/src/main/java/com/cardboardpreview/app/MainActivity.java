package com.cardboardpreview.app;

import android.os.Bundle;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

public class MainActivity extends CardboardActivity {

    private final VrStereoRenderer mStereoRenderer = new VrStereoRenderer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        // Associate a CardboardView.StereoRenderer with cardboardView.
        cardboardView.setRenderer(mStereoRenderer);
        // Associate the cardboardView with this activity.
        setCardboardView(cardboardView);
    }
}
