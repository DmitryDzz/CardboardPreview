package com.cardboardpreview.app;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.os.Handler;
import android.util.Log;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.egl.EGLConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * Created by dmitrydzz on 18.01.16.
 */
public class VrStereoRenderer implements CardboardView.StereoRenderer {
    private static final String TAG = "++++"; //todo: Change ++++ to regular name
    private final static int FLOAT_SIZE_BYTES = 4;

//    private enum State { STOPPED, PREPARING, RENDERING };
//    private State mState = State.STOPPED;
    private volatile boolean mIsReady;

    private final Context mContext;
    private final CardboardView mCardboardView;

    private final Handler mHandler = new Handler();

    @SuppressWarnings("deprecation")
    private Camera mCamera;

    private int mGLProgram;
    private int mTexHandle;
    private int mTexCoordHandle;
    private int mTriangleVerticesHandle;
    private int mTransformHandle;
    private int mRotateHandle;
    private int mTextureName;
    private SurfaceTexture mSurfaceTexture = null;
    private final FloatBuffer mTextureVertices;
    private final FloatBuffer mQuadVertices;
    private final float[] mTransformMatrix;
    private final float[] mRotateMatrix;

    public VrStereoRenderer(final Context context, final CardboardView cardboardView) {
        mContext = context;
        mCardboardView = cardboardView;

        final float[] textureVertices = { 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f };
        mTextureVertices = ByteBuffer.allocateDirect(textureVertices.length *
                FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureVertices.put(textureVertices).position(0);

        final float[] quadVertices = { 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f };
        mQuadVertices = ByteBuffer.allocateDirect(quadVertices.length *
                FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mQuadVertices.put(quadVertices).position(0);

        mTransformMatrix = new float[16];
        mRotateMatrix = new float[16];
    }

    public synchronized void start() {
        if (mIsReady) {
            return;
        }

        @SuppressWarnings("deprecation")
        final Camera camera = openFacingBackCamera();
        if (camera == null) {
            return;
        }
        mCamera = camera;
        Log.d(TAG, "Camera.open");

        mGLProgram = createProgram(R.raw.vertex, R.raw.fragment);
        mTexHandle = GLES20.glGetUniformLocation(mGLProgram, "s_texture");
        mTexCoordHandle = GLES20.glGetAttribLocation(mGLProgram, "a_texCoord");
        mTriangleVerticesHandle = GLES20.glGetAttribLocation(mGLProgram, "vPosition");
        mTransformHandle = GLES20.glGetUniformLocation(mGLProgram, "u_xform");
        mRotateHandle = GLES20.glGetUniformLocation(mGLProgram, "u_rotation");
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mTextureName = textures[0];
        GLES20.glUseProgram(mGLProgram);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT,
                false, 0, mTextureVertices);
        GLES20.glVertexAttribPointer(mTriangleVerticesHandle, 2, GLES20.GL_FLOAT,
                false, 0, mQuadVertices);
        checkGlError("initialization");

//        Gdx.gl.glViewport(0, 0, width, height);  ?????????????

//        changeCameraParameters();

        SurfaceTexture oldSurfaceTexture = mSurfaceTexture;
        mSurfaceTexture = new SurfaceTexture(mTextureName);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                //todo: !!!!
//                mCameraFrameCount.incrementAndGet();
                if (mCardboardView != null) {
                    Log.d(TAG, "onFrameAvailable");
                    mCardboardView.requestRender();
                }
            }
        });
        if (oldSurfaceTexture != null) {
            oldSurfaceTexture.release();
        }

        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();

        mIsReady = true;
    }

    @SuppressWarnings("deprecation")
    private Camera openFacingBackCamera() {
        final int cameraCount = Camera.getNumberOfCameras();
        final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return Camera.open(cameraIndex);
            }
        }
        return null;
    }

    public synchronized void stop() {
        if (!mIsReady) {
            return;
        }

        mIsReady = false;
        mSurfaceTexture.release();
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

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
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
        // Doesn't work :(
    }

    private String readRawTextFile(int resId) {
        InputStream inputStream = mContext.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int loadShader(final int shaderType, final int shaderResId) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            final String source = readRawTextFile(shaderResId);
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private void checkGlError(String op) {
        int error;
        //noinspection LoopStatementThatDoesntLoop
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private int createProgram(final int vertexShaderResId, final int fragmentShaderResId) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderResId);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderResId);
        if (pixelShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
}
