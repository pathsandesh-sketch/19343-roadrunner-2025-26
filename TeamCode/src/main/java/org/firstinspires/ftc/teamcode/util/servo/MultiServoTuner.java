package org.firstinspires.ftc.teamcode.util.servo;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.List;

public class MultiServoTuner {

    private final List<TunableServo> servos;
    private int selectedIndex = 0;

    private boolean lastUp, lastDown, lastLeft, lastRight, lastA;

    public MultiServoTuner(List<TunableServo> servos) {
        this.servos = servos;
    }

    public void update(Gamepad gamepad) {
        boolean up = gamepad.dpad_up;
        boolean down = gamepad.dpad_down;
        boolean left = gamepad.dpad_left;
        boolean right = gamepad.dpad_right;
        boolean a = gamepad.a;

        if (up && !lastUp) current().increase();
        if (down && !lastDown) current().decrease();

        if (right && !lastRight) next();
        if (left && !lastLeft) previous();

        if (a && !lastA) current().set(0.5); // optional reset

        lastUp = up;
        lastDown = down;
        lastLeft = left;
        lastRight = right;
        lastA = a;
    }

    private TunableServo current() {
        return servos.get(selectedIndex);
    }

    private void next() {
        selectedIndex = (selectedIndex + 1) % servos.size();
    }

    private void previous() {
        selectedIndex = (selectedIndex - 1 + servos.size()) % servos.size();
    }

    public void telemetry(com.qualcomm.robotcore.eventloop.opmode.OpMode opMode) {
        for (int i = 0; i < servos.size(); i++) {
            TunableServo s = servos.get(i);
            String marker = (i == selectedIndex) ? ">> " : "   ";
            opMode.telemetry.addData(
                    marker + s.getName(),
                    "%.3f", s.get()
            );
        }
    }
}
