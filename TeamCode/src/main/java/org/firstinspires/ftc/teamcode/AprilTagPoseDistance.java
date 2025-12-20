package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp(name="AprilTag Pose Distance", group="Test")
public class AprilTagPoseDistance extends LinearOpMode {

    Limelight3A limelight;

    @Override
    public void runOpMode() {

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        limelight.pipelineSwitch(0);   // AprilTag pipeline
        limelight.start();

        waitForStart();

        while (opModeIsActive()) {

            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {

                Pose3D pose = result.getBotpose();

                double x = pose.getPosition().x; // forward (m)
                double y = pose.getPosition().y; // left (m)
                double z = pose.getPosition().z; // up (m)

                double distance = Math.sqrt(x*x + y*y + z*z);

                telemetry.addData("X forward (m)", x);
                telemetry.addData("Y left (m)", y);
                telemetry.addData("Z up (m)", z);
                telemetry.addData("Distance to tag (m)", distance);

            } else {
                telemetry.addData("AprilTag", "Not visible");
            }

            telemetry.update();
        }
    }
}
