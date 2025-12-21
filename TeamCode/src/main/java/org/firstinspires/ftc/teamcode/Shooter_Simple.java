package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp(name = "Shooter_Vision_Control")
public class Shooter_Simple extends LinearOpMode {

    DcMotor rightMotor;
    DcMotor leftMotor;
    Servo angleServo;
    Limelight3A limelight;

    double servoPos = 0.5;
    double servoIncrement = 0.05;

    boolean xPrev = false;
    boolean yPrev = false;

    @Override
    public void runOpMode() {

        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");
        leftMotor  = hardwareMap.get(DcMotor.class, "leftMotor");

        limelight  = hardwareMap.get(Limelight3A.class, "limelight");

        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        limelight.pipelineSwitch(0); // AprilTag pipeline
        limelight.start();

        telemetry.addLine("Ready. Press Play.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            /* ===== SERVO MANUAL CONTROL =====
            if (gamepad1.x && !xPrev) servoPos += servoIncrement;
            if (gamepad1.y && !yPrev) servoPos -= servoIncrement;

            xPrev = gamepad1.x;
            yPrev = gamepad1.y;

            servoPos = Math.min(1.0, Math.max(0.0, servoPos));
            angleServo.setPosition(servoPos);*/


            double shooterPower = 0.0;

            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {

                for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {

                    if (tag.getFiducialId() == 24) {

                        Pose3D camPose = tag.getTargetPoseCameraSpace();
                        double z = camPose.getPosition().z;   // forward (m)

                        // ✔️ Calibrated distance (you verified this)
                        double distanceMeters = z * 0.16;

                        /* ===== DISTANCE → SHOOTER POWER ===== */
                        double k = 2.0;        // tune this
                        double minPower = 0.8; //140 mm
                        double maxPower = 1.0;

                        shooterPower = k * distanceMeters;
                        shooterPower = Math.max(minPower,
                                Math.min(maxPower, shooterPower));

                        telemetry.addLine("Tag 24 FOUND");
                        telemetry.addData("Distance (mm)", "%.0f", distanceMeters * 1000);
                        telemetry.addData("Shooter Power", "%.2f", shooterPower);
                        break;
                    }
                }
            } else {
                telemetry.addLine("No tag detected");
            }


            rightMotor.setPower(shooterPower);
            leftMotor.setPower(shooterPower);

            telemetry.addData("Servo Position", "%.2f", servoPos);
            telemetry.update();
        }

        rightMotor.setPower(0);
        leftMotor.setPower(0);
        limelight.stop();
    }
}
