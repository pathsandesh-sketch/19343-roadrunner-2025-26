package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Shooter_Simple")
public class Shooter_Krishna extends LinearOpMode{

    DcMotor rightMotor;
    DcMotor leftMotor;
    Servo angleServo;

    double servoPos = 0;
    double shooterPower = 0;

    double servoIncrement = 0.05;
    double powerIncrement = 0.05;

    boolean xPrev = false;
    boolean yPrev = false;
    boolean aPrev = false;
    boolean bPrev = false;

    @Override
    public void runOpMode() {


        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");
        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        angleServo = hardwareMap.get(Servo.class, "angleServo");


        leftMotor.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {


            if (gamepad1.x && !xPrev) {
                servoPos += servoIncrement;
            }
            if (gamepad1.y && !yPrev) {
                servoPos -= servoIncrement;
            }


            if (gamepad1.a && !aPrev) {
                shooterPower += powerIncrement;
            }
            if (gamepad1.b && !bPrev) {
                shooterPower -= powerIncrement;
            }


            xPrev = gamepad1.x;
            yPrev = gamepad1.y;
            aPrev = gamepad1.a;
            bPrev = gamepad1.b;


            servoPos = Math.min(1, Math.max(0, servoPos));
            shooterPower = Math.min(1, Math.max(0, shooterPower));


            angleServo.setPosition(servoPos);
            rightMotor.setPower(shooterPower);
            leftMotor.setPower(shooterPower);


            telemetry.addData("Servo Position", servoPos);
            telemetry.addData("Shooter Power", shooterPower);
            telemetry.update();
        }
    }
}