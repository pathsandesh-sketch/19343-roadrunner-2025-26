package org.firstinspires.ftc.teamcode.util.math.interp;

public class ExpoInterp implements Interpolation {

    private final double min, max, k;

    public ExpoInterp(double min, double max, double k) {
        this.min = min;
        this.max = max;
        this.k = k;
    }

    @Override
    public double get(double... in) {
        double x = in[0]; // assumed 0→1
        return min + (max - min) * (1 - Math.exp(-k * x));
    }
}
