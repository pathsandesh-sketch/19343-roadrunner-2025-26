package org.firstinspires.ftc.teamcode.util.servo;

import com.qualcomm.robotcore.hardware.Servo;

public class TunableServo {
    private final Servo servo;
    private final String name;
    private double position;
    private final double step;

    public TunableServo(String name, Servo servo, double startPosition, double step) {
        this.name = name;
        this.servo = servo;
        this.position = startPosition;
        this.step = step;
        servo.setPosition(position);
    }

    public void increase() {
        set(position + step);
    }

    public void decrease() {
        set(position - step);
    }

    public void set(double pos) {
        position = clip(pos);
        servo.setPosition(position);
    }

    public double get() {
        return position;
    }

    public String getName() {
        return name;
    }

    private double clip(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
