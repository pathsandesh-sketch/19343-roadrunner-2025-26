package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "Spindexer_Final")
public class SpindexerFinal extends LinearOpMode {

    // ================= HARDWARE =================
    private Servo spinServo;
    private ColorSensor[] colorSensors = new ColorSensor[3];
    private DistanceSensor[] distanceSensors = new DistanceSensor[3];

    // ================= CONSTANTS =================
    static final int NUM_SLOTS = 3;
    static final double SERVO_RANGE = 300.0;
    static final double BALL_DISTANCE_CM = 6.0;

    // ---- COLOR LOGIC (FROM YOUR WORKING CODE) ----
    static final int GREEN_MIN = 100;
    static final int GREEN_MAX = 255;
    static final int PURPLE_MIN = 100;
    static final int PURPLE_MAX = 255;
    static final int COLOR_THRESHOLD = 30;

    // ================= SLOT MODEL =================
    double[] slotAngle = {0, 120, 240};     // 🔧 CALIBRATE
    char[] slotColor = {'U', 'U', 'U'};     // G / P / U
    boolean[] slotFilled = {false, false, false};

    // ================= SYSTEM STATE =================
    double currentAngle = 0;
    double launchAngle = 90;                // 🔧 CALIBRATE

    char[] sequence = {'P', 'G', 'P'};
    int sequenceIndex = 0;

    boolean yPrev = false;

    // ================= TELEOP =================
    @Override
    public void runOpMode() {

        spinServo = hardwareMap.get(Servo.class, "spinservo");

        // ---- SLOT SENSORS ----
        // Each color sensor ALSO acts as its distance sensor (same config name)
        colorSensors[0] = hardwareMap.get(ColorSensor.class, "colorSensor");
        colorSensors[1] = hardwareMap.get(ColorSensor.class, "color1");
        colorSensors[2] = hardwareMap.get(ColorSensor.class, "color2");

        distanceSensors[0] = hardwareMap.get(DistanceSensor.class, "colorSensor");
        distanceSensors[1] = hardwareMap.get(DistanceSensor.class, "color1");
        distanceSensors[2] = hardwareMap.get(DistanceSensor.class, "color2");

        telemetry.addLine("Spindexer READY (Color + Distance)");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // ================= UPDATE SLOT STATES =================
            for (int i = 0; i < NUM_SLOTS; i++) {

                double distance = distanceSensors[i].getDistance(DistanceUnit.CM);

                if (distance <= BALL_DISTANCE_CM) {
                    slotFilled[i] = true;
                    slotColor[i] = detectColor(colorSensors[i]);
                } else {
                    slotFilled[i] = false;
                    slotColor[i] = 'U';
                }
            }

            // ================= SEQUENCE ADVANCE (EDGE TRIGGER) =================
            boolean yPressed = gamepad1.y && !yPrev;

            if (yPressed && sequenceIndex < sequence.length) {

                char targetColor = sequence[sequenceIndex];
                int bestSlot = chooseBestSlot(targetColor);

                if (bestSlot != -1) {
                    double targetAngle = computeTargetAngle(bestSlot);
                    moveServoToAngle(targetAngle);

                    // Assume ball fired
                    slotFilled[bestSlot] = false;
                    slotColor[bestSlot] = 'U';

                    sequenceIndex++;
                }
            }

            yPrev = gamepad1.y;

            // ================= TELEMETRY =================
            telemetry.addLine("===== SPINDEXER STATUS =====");
            telemetry.addData("Current Angle", "%.1f°", currentAngle);
            telemetry.addData("Launch Angle", "%.1f°", launchAngle);

            telemetry.addLine("----- Slots -----");
            for (int i = 0; i < NUM_SLOTS; i++) {
                telemetry.addData(
                        "Slot " + i,
                        "Angle=%.0f | Filled=%s | Color=%c | Dist=%.1fcm",
                        slotAngle[i],
                        slotFilled[i],
                        slotColor[i],
                        distanceSensors[i].getDistance(DistanceUnit.CM)
                );
            }

            telemetry.addLine("----- Raw RGB -----");
            for (int i = 0; i < NUM_SLOTS; i++) {
                telemetry.addData(
                        "Sensor " + i,
                        "R:%d G:%d B:%d",
                        colorSensors[i].red(),
                        colorSensors[i].green(),
                        colorSensors[i].blue()
                );
            }

            telemetry.addLine("----- Sequence -----");
            telemetry.addData("Step", "%d / %d", sequenceIndex, sequence.length);
            if (sequenceIndex < sequence.length) {
                telemetry.addData("Target Color", sequence[sequenceIndex]);
            } else {
                telemetry.addLine("SEQUENCE COMPLETE");
            }

            telemetry.addLine("Press Y to advance");
            telemetry.update();
        }
    }

    // ================= LOGIC =================

    int chooseBestSlot(char targetColor) {

        List<Integer> candidates = new ArrayList<>();

        // Prefer matching color
        for (int i = 0; i < NUM_SLOTS; i++) {
            if (slotFilled[i] && slotColor[i] == targetColor) {
                candidates.add(i);
            }
        }

        // Fallback: any filled slot
        if (candidates.isEmpty()) {
            for (int i = 0; i < NUM_SLOTS; i++) {
                if (slotFilled[i]) {
                    candidates.add(i);
                }
            }
        }

        if (candidates.isEmpty()) return -1;

        double minRotation = 999;
        int bestSlot = -1;

        for (int i : candidates) {
            double rotation =
                    (launchAngle - slotAngle[i] - currentAngle + SERVO_RANGE) % SERVO_RANGE;

            if (rotation < minRotation) {
                minRotation = rotation;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    double computeTargetAngle(int slotIndex) {
        return (launchAngle - slotAngle[slotIndex] + SERVO_RANGE) % SERVO_RANGE;
    }

    void moveServoToAngle(double angle) {
        spinServo.setPosition(angle / SERVO_RANGE);
        currentAngle = angle;
    }

    // ================= COLOR DETECTION (YOUR LOGIC) =================
    char detectColor(ColorSensor sensor) {

        int red = sensor.red();
        int green = sensor.green();
        int blue = sensor.blue();

        // GREEN
        if (green >= GREEN_MIN && green <= GREEN_MAX &&
                red < green - COLOR_THRESHOLD &&
                blue < green - COLOR_THRESHOLD) {

            return 'G';
        }

        // PURPLE
        if (red >= PURPLE_MIN && red <= PURPLE_MAX &&
                blue >= PURPLE_MIN && blue <= PURPLE_MAX &&
                green < Math.max(red, blue) - COLOR_THRESHOLD) {

            return 'P';
        }

        return 'U';
    }
}
