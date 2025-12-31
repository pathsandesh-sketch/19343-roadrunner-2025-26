package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Elev_Servo_Degree_Tuning")
public class elev_servo extends LinearOpMode {

    Servo spinservo;
    double targetDegrees = 0;

    @Override
    public void runOpMode() {

        spinservo = hardwareMap.get(Servo.class, "spinservo");
        spinservo.setDirection(Servo.Direction.REVERSE);
        telemetry.addLine("INIT OK");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.a) targetDegrees = 0;
            if (gamepad1.b) targetDegrees = 57;// 0.5704
            if (gamepad1.x) targetDegrees = 58;//0.1905
            if (gamepad1.y) targetDegrees = 59;//0.381
            if (gamepad1.left_bumper) targetDegrees = 45;//0.7619
            if (gamepad1.right_bumper) targetDegrees = 50;//0.9524


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
