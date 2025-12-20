package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
@TeleOp(name = "AprilTag Shooter Control", group = "Limelight")
public class LimelightShooterController extends LinearOpMode {

    private Limelight3A limelight;
    private DcMotor shooter;

    @Override
    public void runOpMode() throws InterruptedException {

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        shooter = hardwareMap.get(DcMotor.class, "shooter"); // name in config

        telemetry.setMsTransmissionInterval(11);

        limelight.pipelineSwitch(0);
        limelight.start();

        telemetry.addLine("Press Play.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            LLResult result = limelight.getLatestResult();

            if (result.isValid()) {

                for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {

                    if (tag.getFiducialId() == 24) {

                        Pose3D camPose = tag.getTargetPoseCameraSpace();

                        double z = camPose.getPosition().z;   // forward (m)
                        double distance = z * 0.16;           // calibrated

                        // === SHOOTER CONTROL ===
                        double k = 2.0;        // tuning constant
                        double minPower = 0.2;
                        double maxPower = 1.0;

                        double shooterPower = k * distance;

                        // clamp
                        shooterPower = Math.max(minPower,
                                Math.min(maxPower, shooterPower));

                        shooter.setPower(shooterPower);

                        telemetry.addLine("Tag 24 FOUND");
                        telemetry.addData("Distance (m)", "%.3f", distance);
                        telemetry.addData("Shooter Power", "%.2f", shooterPower);

                        break;
                    }
                }
            } else {
                shooter.setPower(0);
                telemetry.addLine("No tag detected");
            }

            telemetry.update();
        }

        shooter.setPower(0);
        limelight.stop();
    }
}
