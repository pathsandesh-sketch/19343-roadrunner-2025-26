package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;

@TeleOp(name = "Spindexer_AprilTag")
public class spindexer_apriltag_logic extends LinearOpMode {

    // ================= HARDWARE =================
    Servo spinServo;
    ColorSensor[] colorSensors = new ColorSensor[3];
    DistanceSensor[] distanceSensors = new DistanceSensor[3];
    Limelight3A limelight;

    // ================= CONSTANTS =================
    final double SLOT_0 = 0.00; // 0°
    final double SLOT_1 = 0.381; // 120°
    final double SLOT_2 = 0.7619; // 240°

    final double[] SLOTS = { SLOT_0, SLOT_1, SLOT_2 };
    final double BALL_DISTANCE_CM = 6.0;

    // ================= COLOR =================
    enum BallColor { PURPLE, GREEN, UNKNOWN }

    // ================= SEQUENCE =================
    BallColor[] sequence = new BallColor[3];
    int sequenceIndex = 0;

    boolean yPrev = false;
    int lastSeenTag = -1;

    @Override
    public void runOpMode() {

        spinServo = hardwareMap.get(Servo.class, "spinservo");

        colorSensors[0] = hardwareMap.get(ColorSensor.class, "colorSensor");
        colorSensors[1] = hardwareMap.get(ColorSensor.class, "color1");
        colorSensors[2] = hardwareMap.get(ColorSensor.class, "color2");

        distanceSensors[0] = hardwareMap.get(DistanceSensor.class, "colorSensor");
        distanceSensors[1] = hardwareMap.get(DistanceSensor.class, "color1");
        distanceSensors[2] = hardwareMap.get(DistanceSensor.class, "color2");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();


        telemetry.addLine("Spindexer + AprilTags READY");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            updateSequenceFromAprilTag();

            boolean yPressed = gamepad1.y && !yPrev;

            if (yPressed && sequenceIndex < sequence.length) {
                handleStep(sequence[sequenceIndex]);
            }

            yPrev = gamepad1.y;

            telemetry.addData("AprilTag", lastSeenTag);
            telemetry.addData("Step", sequenceIndex);
            telemetry.update();
        }
    }

    // ================= APRILTAG LOGIC =================
    void updateSequenceFromAprilTag() {

        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) return;

        for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {

            int id = tag.getFiducialId();

            if (id == lastSeenTag) return; // already loaded

            if (id == 21) {
                sequence = new BallColor[]{
                        BallColor.GREEN,
                        BallColor.PURPLE,
                        BallColor.PURPLE
                };
            } else if (id == 22) {
                sequence = new BallColor[]{
                        BallColor.PURPLE,
                        BallColor.GREEN,
                        BallColor.PURPLE
                };
            } else if (id == 23) {
                sequence = new BallColor[]{
                        BallColor.PURPLE,
                        BallColor.PURPLE,
                        BallColor.GREEN
                };
            } else {
                return;
            }

            sequenceIndex = 0;
            lastSeenTag = id;
            telemetry.addLine("Loaded sequence from Tag " + id);
            return;
        }
    }

    // ================= CORE STEP =================
    void handleStep(BallColor targetColor) {

        int currentSlot = getCurrentSlot();
        BallColor[] logicalSlots = readLogicalSlots(currentSlot);

        if (logicalSlots[currentSlot] == targetColor) {
            launch();
            sequenceIndex++;
            return;
        }

        int forward = (currentSlot == 2) ? 0 : currentSlot + 1;
        int backward = (currentSlot == 0) ? 2 : currentSlot - 1;

        if (logicalSlots[forward] == targetColor) {
            spinServo.setPosition(SLOTS[forward]);
        } else if (logicalSlots[backward] == targetColor) {
            spinServo.setPosition(SLOTS[backward]);
        }
    }

    // ================= SLOT REMAP =================
    BallColor[] readLogicalSlots(int servoSlot) {

        BallColor s0 = readColor(0);
        BallColor s1 = readColor(1);
        BallColor s2 = readColor(2);

        BallColor[] logical = new BallColor[3];

        if (servoSlot == 0) {
            logical[0] = s0; logical[1] = s1; logical[2] = s2;
        } else if (servoSlot == 1) {
            logical[0] = s2; logical[1] = s0; logical[2] = s1;
        } else {
            logical[0] = s1; logical[1] = s2; logical[2] = s0;
        }

        return logical;
    }

    // ================= COLOR =================
    BallColor readColor(int i) {

        if (distanceSensors[i].getDistance(DistanceUnit.CM) > BALL_DISTANCE_CM)
            return BallColor.UNKNOWN;

        int r = colorSensors[i].red();
        int g = colorSensors[i].green();
        int b = colorSensors[i].blue();

        if (r > 100 && b > 100 && g < 80) return BallColor.PURPLE;
        if (g > r && g > b && g > 100) return BallColor.GREEN;

        return BallColor.UNKNOWN;
    }

    // ================= SERVO SLOT =================
    int getCurrentSlot() {
        double p = spinServo.getPosition();
        if (p == SLOT_0) return 0;
        if (p == SLOT_1) return 1;
        return 2;
    }

    // ================= LAUNCH =================
    void launch() {
        telemetry.addLine("LAUNCH!");
        telemetry.update();
        // shooter / pusher logic
    }
}
