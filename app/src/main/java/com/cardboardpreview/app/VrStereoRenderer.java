package com.cardboardpreview.app;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.egl.EGLConfig;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * Created by dmitrydzz on 18.01.16.
 */
public class VrStereoRenderer implements CardboardView.StereoRenderer {
    private static final String TAG = "++++";

    private final Handler mHandler = new Handler();

    private Camera mCamera;

    private AtomicBoolean mPreviewEnabled = new AtomicBoolean();

    public void pause() {
        stopPreviewAsync();
    }

    public void resume() {
        startPreviewAsync();
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
    }

    @Override
    public void onDrawEye(Eye eye) {

    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    @Override
    public void onRendererShutdown() {
    }

    private void startPreviewAsync() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                startPreview();
            }
        });
    }

    private void stopPreviewAsync() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                stopPreview();
            }
        });
    }

    private void startPreview() {
        if (mPreviewEnabled.get()) {
            return;
        }

        // Looking for facing back camera:
        final int cameraCount = Camera.getNumberOfCameras();
        final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCamera = Camera.open(cameraIndex);
                mPreviewEnabled.set(true);
                Log.d(TAG, "Camera.open");
                break;
            }
        }
    }

    private void stopPreview() {
        if (!mPreviewEnabled.get()) {
            return;
        }

        mPreviewEnabled.set(false);
//        mSurfaceTexture.release();
        try {
            mCamera.setPreviewTexture(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        Log.d(TAG, "Camera.release");
    }
}
