package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

@TeleOp(name = "AprilTag 24 Distance", group = "Limelight")
public class LimelightAprilTagDetection extends LinearOpMode {

    private Limelight3A limelight;

    @Override
    public void runOpMode() throws InterruptedException {

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        telemetry.setMsTransmissionInterval(11);

        limelight.pipelineSwitch(0); // AprilTag pipeline
        limelight.start();

        telemetry.addLine("Ready. Press Play.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            LLResult result = limelight.getLatestResult();

            if (result.isValid()) {

                for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {

                    if (tag.getFiducialId() == 24) {

                        // === THIS IS WHERE YOUR CALIBRATION GOES ===
                        Pose3D camPose = tag.getTargetPoseCameraSpace();

                        double z = camPose.getPosition().z; // forward (m)

                        double distanceMeters = z * 0.16; // calibration

                        telemetry.addLine("Tag 24 FOUND");
                        telemetry.addData("Raw Z (m)", "%.3f", z);
                        telemetry.addData("Distance (m)", "%.3f", distanceMeters);
                        telemetry.addData("Distance (mm)", "%.0f", distanceMeters * 1000);

                        break; // only need one tag
                    }
                }
            } else {
                telemetry.addLine("No valid Limelight data");
            }

            telemetry.update();


        }

        limelight.stop();
    }
}
