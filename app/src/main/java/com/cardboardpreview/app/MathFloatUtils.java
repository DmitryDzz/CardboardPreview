package com.cardboardpreview.app;

public class MathFloatUtils {
    public static boolean equals(float a, float b, float epsilon) {
        return Math.copySign(a - b, 1.0f) <= epsilon
                // copySign(x, 1.0) is a branch-free version of abs(x), but with different NaN semantics
                || (a == b) // needed to ensure that infinities equal themselves
                || (Float.isNaN(a) && Float.isNaN(b));
    }

    public static int compare(float a, float b, float tolerance) {
        if (equals(a, b, tolerance)) {
            return 0;
        } else if (a < b) {
            return -1;
        } else if (a > b) {
            return 1;
        } else {
            return compareBooleans(Float.isNaN(a), Float.isNaN(b));
        }
    }

    private static int compareBooleans(boolean a, boolean b) {
        return (a == b) ? 0 : (a ? 1 : -1);
    }
}
