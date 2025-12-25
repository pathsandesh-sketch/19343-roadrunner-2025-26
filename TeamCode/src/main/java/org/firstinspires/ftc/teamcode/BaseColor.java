package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Color_WHYNOTDISTANCE", group = "Sensor")
public class BaseColor extends LinearOpMode {

    ColorSensor colorSensor;
    DistanceSensor distanceSensor;

    int GREEN_MIN = 100;
    int GREEN_MAX = 255;
    int PURPLE_MIN = 100;
    int PURPLE_MAX = 255;
    int COLOR_THRESHOLD = 30;

    @Override
    public void runOpMode() {

        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "colorSensor");

        waitForStart();

        while (opModeIsActive()) {

            double distance = distanceSensor.getDistance(DistanceUnit.CM);

            if (distance <= 6.0) {

                int red = colorSensor.red();
                int green = colorSensor.green();
                int blue = colorSensor.blue();

                String detectedColor ="Unknown";

                if (green >= GREEN_MIN && green <= GREEN_MAX &&
                        red < green - COLOR_THRESHOLD &&
                        blue < green - COLOR_THRESHOLD) {

                    detectedColor = "GREEN";

                } else if (red >= PURPLE_MIN && red <= PURPLE_MAX &&
                        blue >= PURPLE_MIN && blue <= PURPLE_MAX &&
                        green < Math.max(red, blue) - COLOR_THRESHOLD) {

                    detectedColor = "PURPLE";
                }

                telemetry.addData("Detection", "Ball Detected");
                telemetry.addData("Red", red);
                telemetry.addData("Green", green);
                telemetry.addData("Blue", blue);
                telemetry.addData("Detected Color", detectedColor);
                telemetry.addData("Distance (cm)", distance);

            } else {

                telemetry.addData("Detection", "Ball Not Detected");
                telemetry.addData("Distance (cm)", distance);
            }

            telemetry.update();
        }
    }
}
