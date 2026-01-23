package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.servo.MultiServoTuner;
import org.firstinspires.ftc.teamcode.util.servo.TunableServo;

@TeleOp(name = "Multi Servo Tuner")
public class servotunertest extends OpMode {

    MultiServoTuner tuner;

    @Override
    public void init() {
        TunableServo spinservo1 = new TunableServo(
                "spinservo1",
                hardwareMap.get(Servo.class, "claw"),
                0.5,
                0.01
        );

        TunableServo spinservo2 = new TunableServo(
                "spinservo2",
                hardwareMap.get(Servo.class, "wrist"),
                0.5,
                0.01
        );

        TunableServo arm = new TunableServo(
                "Arm",
                hardwareMap.get(Servo.class, "arm"),
                0.5,
                0.005
        );

        tuner = new MultiServoTuner(
                java.util.Arrays.asList(spinservo1, spinservo2, arm)
        );
    }

    @Override
    public void loop() {
        tuner.update(gamepad1);
        tuner.telemetry(this);
        telemetry.update();
    }
}
