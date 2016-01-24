package com.cardboardpreview.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CameraPreviewSizes {
    private ArrayList<CameraPreviewSize> mSizes = new ArrayList<CameraPreviewSize>();

    public int getCount() {
        return mSizes.size();
    }

    public CameraPreviewSize get(final int index) {
        return mSizes.get(index);
    }

    public void clear() {
        mSizes.clear();
    }

    public void add(final int width, final int height) {
        mSizes.add(new CameraPreviewSize(width, height));
    }

    public void sortSquare() {
        Collections.sort(mSizes, mComparatorSquare);
    }

    public void sortWidth() {
        Collections.sort(mSizes, mComparatorWidth);
    }

    public CameraPreviewSize getBestSize(final float eyeRatio) {
        if (getCount() == 0) {
            return null;
        }

        sortWidth();

        // Remove most heavy resolutions:
        if (getCount() > 10) {
            mSizes.remove(0);
            mSizes.remove(0);
        }

        // Find the best 4:3 resolution:
        for (CameraPreviewSize size : mSizes) {
            if (MathFloatUtils.equals(size.getRatio(), 4f / 3f, 0.01f)) {
                return size;
            }
        }

        // If there's no 4:3 then return the best one:
        return get(0);
    }

    private static final Comparator<CameraPreviewSize> mComparatorSquare = new Comparator<CameraPreviewSize>() {
        @Override
        public int compare(CameraPreviewSize o1, CameraPreviewSize o2) {
            return -MathFloatUtils.compare(o1.getSquare(), o2.getSquare(), 0.000001f);
        }
    };

    private static final Comparator<CameraPreviewSize> mComparatorWidth = new Comparator<CameraPreviewSize>() {
        @Override
        public int compare(CameraPreviewSize o1, CameraPreviewSize o2) {
            return -MathFloatUtils.compare(o1.getWidth(), o2.getWidth(), 0.000001f);
        }
    };

    public static class CameraPreviewSize {
        private int width;
        private int height;
        private float ratio;
        private long square;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public float getRatio() {
            return ratio;
        }

        public long getSquare() {
            return square;
        }

        public CameraPreviewSize(final int width, final int height) {
            this.width = width;
            this.height = height;
            this.ratio = (float) width / height;
            this.square = (long) width * (long ) height;
        }
    }
}
