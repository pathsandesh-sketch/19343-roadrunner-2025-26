package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "motortest")
public class motortest extends LinearOpMode {

    Servo spinservo1;
    Servo spinservo2;
    double targetDegrees = 0;

    @Override
    public void runOpMode() {

        spinservo1 = hardwareMap.get(Servo.class, "spinservo1");
        spinservo2 = hardwareMap.get(Servo.class, "spinservo2");

        telemetry.addLine("INIT OK");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.a) targetDegrees = 0;
            if (gamepad1.b) targetDegrees = 180;// 0.5704
            if (gamepad1.x) targetDegrees = 60;//0.1905
            if (gamepad1.y) targetDegrees = 120;//0.381
            if (gamepad1.left_bumper) targetDegrees = 240;//0.7619
            if (gamepad1.right_bumper) targetDegrees = 300;//0.9524


            double servoPos = degreesToServo(targetDegrees);
            spinservo1.setPosition(servoPos);
            //spinservo2.setDirection(Servo.Direction.REVERSE);
            //spinservo2.setPosition(servoPos);

            telemetry.addData("Target Degrees", targetDegrees);
            telemetry.addData("Servo Position", servoPos);
            telemetry.update();
        }
    }

    // 🔹 Conversion function
    public double degreesToServo(double degrees) {
        degrees = Math.max(0, Math.min(315, degrees));
        return degrees / 315.0;
    }
}
