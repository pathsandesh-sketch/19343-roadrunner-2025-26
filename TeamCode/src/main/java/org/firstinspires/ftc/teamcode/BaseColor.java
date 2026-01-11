package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Color_Distance_CLEAN", group = "Sensor")
public class BaseColor extends LinearOpMode {

    private ColorSensor colorSensor;
    private DistanceSensor distanceSensor;

    // ---- Tunables ----
    private static final double DETECTION_DISTANCE_CM = 13.0;

    // Color ratio thresholds (much more stable than raw RGB)
    private static final double GREEN_DOMINANCE = 1.35;
    private static final double PURPLE_RG_SIMILARITY = 0.85;
    private static final double PURPLE_BLUE_DOMINANCE = 1.15;

    @Override
    public void runOpMode() {

        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "colorSensor");

        telemetry.addLine("Initialized. Waiting for start...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            double distance = distanceSensor.getDistance(DistanceUnit.CM);

            if (distance <= DETECTION_DISTANCE_CM) {

                int r = colorSensor.red();
                int g = colorSensor.green();
                int b = colorSensor.blue();

                // Normalize RGB
                double sum = r + g + b;
                if (sum == 0) sum = 1; // safety

                double rN = r / sum;
                double gN = g / sum;
                double bN = b / sum;

                String detectedColor = detectColor(rN, gN, bN);

                telemetry.addLine("Ball Detected");
                telemetry.addData("Color", detectedColor);
                telemetry.addData("R G B", "%d %d %d", r, g, b);
                telemetry.addData("R G B (norm)", "%.2f %.2f %.2f", rN, gN, bN);
                telemetry.addData("red",colorSensor.red());
                telemetry.addData("blue", colorSensor.blue());
                telemetry.addData("green", colorSensor.green());
                telemetry.addData("red", colorSensor.red());
                telemetry.addData("blue", colorSensor.blue());
                telemetry.addData("green", colorSensor.green());
                telemetry.addData("Distance (cm)", "%.2f", distance);

                telemetry.update();
            }
            else {
                telemetry.addLine("No Ball Detected");
            }


            telemetry.update();
        }
    }

    // ------------------ COLOR LOGIC ------------------

    private String detectColor(double r, double g, double b) {
        double sum = r + g + b;

        // GREEN: green dominates both red and blue
        if (g > r * GREEN_DOMINANCE && g > b * GREEN_DOMINANCE) {
            return "GREEN";
        }

        // PURPLE: red & blue similar, green suppressed
        else if ((b > 1900 || (b > 70 && b < 150 && b > g && r < b) || (b > g && b > r))) {

            return "PURPLE";
        } else if (sum < 300 && g < 100 && b < 100 && r < 75) {
            return "PURPLE 2";
        } else if (300 < sum && sum < 400 && g < 150 && g>100 && b < 100 && r < 75) {
            return "GREEN 2" ;

        }
        return "UNKNOWN";
    }
}