package org.firstinspires.ftc.teamcode.util.math.interp;


public class PolyInterp implements Interpolation {

    private final double[] coeffs;

    public PolyInterp(double... coeffs) {
        this.coeffs = coeffs;
    }

    @Override
    public double get(double... in) {
        double x = in[0];
        double y = 0;
        double pow = 1;

        for (double c : coeffs) {
            y += c * pow;
            pow *= x;
        }
        return y;
    }
}
