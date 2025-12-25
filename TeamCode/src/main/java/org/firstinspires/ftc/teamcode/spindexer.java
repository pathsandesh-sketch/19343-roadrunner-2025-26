package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servo_Degree_Tuning")
public class spindexer extends LinearOpMode {

    Servo spinservo;
    double targetDegrees = 0;

    @Override
    public void runOpMode() {

        spinservo = hardwareMap.get(Servo.class, "spinservo");

        telemetry.addLine("INIT OK");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.a) targetDegrees = 0;
            if (gamepad1.b) targetDegrees = 180;
            if (gamepad1.x) targetDegrees = 60;
            if (gamepad1.y) targetDegrees = 120;
            if (gamepad1.left_bumper) targetDegrees = 240;
            if (gamepad1.right_bumper) targetDegrees = 300;


            double servoPos = degreesToServo(targetDegrees);
            spinservo.setPosition(servoPos);

            telemetry.addData("Target Degrees", targetDegrees);
            telemetry.addData("Servo Position", servoPos);
            telemetry.update();
        }
    }

    // 🔹 Conversion function
    public double degreesToServo(double degrees) {
        degrees = Math.max(0, Math.min(300, degrees));
        return degrees / 300.0;
    }
}
