package org.firstinspires.ftc.teamcode.util.servo;

import com.qualcomm.robotcore.hardware.Gamepad;

public class ServoTuner {
    private final TunableServo servo;
    private final Gamepad gamepad;

    private boolean lastUp, lastDown;

    public ServoTuner(TunableServo servo, Gamepad gamepad) {
        this.servo = servo;
        this.gamepad = gamepad;
    }

    public void update() {
        boolean up = gamepad.dpad_up;
        boolean down = gamepad.dpad_down;

        if (up && !lastUp) servo.increase();
        if (down && !lastDown) servo.decrease();

        lastUp = up;
        lastDown = down;
    }

    public double getPosition() {
        return servo.get();
    }
}
