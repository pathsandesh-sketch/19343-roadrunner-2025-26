package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "Spindexer_Base_TeleOp")
public class SpindexerHalflTeleOp extends LinearOpMode {

    // ================= HARDWARE =================
    Servo spinServo;

    // ================= CONSTANTS =================
    static final int NUM_SLOTS = 3;
    static final double SERVO_RANGE = 300.0;

    // ================= SLOT MODEL =================
    double[] slotAngle = {0, 120, 240};   // 🔧 TUNE THESE
    char[] slotColor = {'U', 'U', 'U'};   // P / G / U
    boolean[] slotFilled = {false, false, false};

    // ================= SYSTEM STATE =================
    double currentAngle = 0;
    double launchAngle = 90;              // 🔧 TUNE THIS

    char[] sequence = {'P', 'G', 'P'};
    int sequenceIndex = 0;

    // ================= TELEOP =================
    @Override
    public void runOpMode() {

        spinServo = hardwareMap.get(Servo.class, "spinservo");

        telemetry.addLine("Spindexer Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // 🔹 SIMULATION INPUT (REMOVE LATER)
            if (gamepad1.a) { slotFilled[0] = true; slotColor[0] = 'P'; }
            if (gamepad1.b) { slotFilled[1] = true; slotColor[1] = 'G'; }
            if (gamepad1.x) { slotFilled[2] = true; slotColor[2] = 'P'; }

            // 🔹 STEP SEQUENCE
            if (gamepad1.y && sequenceIndex < sequence.length) {

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

            // ================= TELEMETRY =================
            telemetry.addLine("===== SPINDEXER STATUS =====");
            telemetry.addData("Current Angle", "%.1f°", currentAngle);
            telemetry.addData("Launch Angle", "%.1f°", launchAngle);

            telemetry.addLine("----- Slots -----");
            for (int i = 0; i < NUM_SLOTS; i++) {
                telemetry.addData(
                        "Slot " + i,
                        "Angle=%.0f | Filled=%s | Color=%c",
                        slotAngle[i],
                        slotFilled[i],
                        slotColor[i]
                );
            }

            telemetry.addLine("----- Sequence -----");
            telemetry.addData("Step", "%d / %d", sequenceIndex, sequence.length);
            if (sequenceIndex < sequence.length) {
                telemetry.addData("Target Color", sequence[sequenceIndex]);
            } else {
                telemetry.addLine("Sequence Complete");
            }

            telemetry.addLine("Controls:");
            telemetry.addLine("A/B/X = Load P/G/P (SIM)");
            telemetry.addLine("Y = Advance Sequence");

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
}
