package org.firstinspires.ftc.teamcode.util.math.interp;

import org.firstinspires.ftc.teamcode.util.math.interp.Interpolation;

import java.util.Arrays;

public class Interp1D implements Interpolation {

    private final double[] xs;
    private final double[] ys;

    public Interp1D(double[] xs, double[] ys) {
        this.xs = xs;
        this.ys = ys;
        sort();
    }

    @Override
    public double get(double... in) {
        double x = in[0];

        if (x <= xs[0]) return ys[0];
        if (x >= xs[xs.length - 1]) return ys[ys.length - 1];

        for (int i = 0; i < xs.length - 1; i++) {
            if (x >= xs[i] && x <= xs[i + 1]) {
                double t = (x - xs[i]) / (xs[i + 1] - xs[i]);
                return ys[i] + t * (ys[i + 1] - ys[i]);
            }
        }
        return ys[ys.length - 1];
    }

    private void sort() {
        for (int i = 0; i < xs.length - 1; i++) {
            for (int j = i + 1; j < xs.length; j++) {
                if (xs[j] < xs[i]) {
                    double tx = xs[i]; xs[i] = xs[j]; xs[j] = tx;
                    double ty = ys[i]; ys[i] = ys[j]; ys[j] = ty;
                }
            }
        }
    }
}
