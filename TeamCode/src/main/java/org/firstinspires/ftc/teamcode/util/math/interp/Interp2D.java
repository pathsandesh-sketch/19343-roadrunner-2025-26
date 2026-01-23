package org.firstinspires.ftc.teamcode.util.interp;

import org.firstinspires.ftc.teamcode.util.math.interp.Interpolation;

public class Interp2D implements Interpolation {

    private final double[] xs, ys;
    private final double[][] values;

    public Interp2D(double[] xs, double[] ys, double[][] values) {
        this.xs = xs;
        this.ys = ys;
        this.values = values;
    }

    @Override
    public double get(double... in) {
        double x = in[0];
        double y = in[1];

        int xi = index(xs, x);
        int yi = index(ys, y);

        double x1 = xs[xi], x2 = xs[xi + 1];
        double y1 = ys[yi], y2 = ys[yi + 1];

        double q11 = values[xi][yi];
        double q21 = values[xi + 1][yi];
        double q12 = values[xi][yi + 1];
        double q22 = values[xi + 1][yi + 1];

        double tx = (x - x1) / (x2 - x1);
        double ty = (y - y1) / (y2 - y1);

        return bilerp(q11, q21, q12, q22, tx, ty);
    }

    private int index(double[] arr, double v) {
        for (int i = 0; i < arr.length - 1; i++)
            if (v >= arr[i] && v <= arr[i + 1]) return i;
        return arr.length - 2;
    }

    private double bilerp(double q11, double q21, double q12, double q22,
                          double tx, double ty) {
        return q11 * (1 - tx) * (1 - ty)
                + q21 * tx * (1 - ty)
                + q12 * (1 - tx) * ty
                + q22 * tx * ty;
    }
}
