package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Spindexer_FINAL")
public class Spindexer_Final extends LinearOpMode {

    // ================= HARDWARE =================
    Servo spinServo;
    ColorSensor[] colorSensors = new ColorSensor[3];
    DistanceSensor[] distanceSensors = new DistanceSensor[3];

    // ================= CONSTANTS =================
    final double SLOT_0 = 0.00; // 0°
    final double SLOT_1 = 0.381; // 120°
    final double SLOT_2 = 0.7619; // 240°

    final double[] SLOTS = { SLOT_0, SLOT_1, SLOT_2 };

    final double BALL_DISTANCE_CM = 6.0;

    // ================= COLOR =================
    enum BallColor { PURPLE, GREEN, UNKNOWN }

    // ================= SEQUENCE =================
    BallColor[] sequence = {
            BallColor.PURPLE,
            BallColor.GREEN,
            BallColor.PURPLE
    };
    int sequenceIndex = 0;

    boolean yPrev = false;

    @Override
    public void runOpMode() {

        spinServo = hardwareMap.get(Servo.class, "spinservo");

        colorSensors[0] = hardwareMap.get(ColorSensor.class, "colorSensor");
        colorSensors[1] = hardwareMap.get(ColorSensor.class, "color1");
        colorSensors[2] = hardwareMap.get(ColorSensor.class, "color2");

        distanceSensors[0] = hardwareMap.get(DistanceSensor.class, "colorSensor");
        distanceSensors[1] = hardwareMap.get(DistanceSensor.class, "color1");
        distanceSensors[2] = hardwareMap.get(DistanceSensor.class, "color2");

        telemetry.addLine("Spindexer Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            boolean yPressed = gamepad1.y && !yPrev;

            if (yPressed && sequenceIndex < sequence.length) {
                handleStep(sequence[sequenceIndex]);
            }

            yPrev = gamepad1.y;

            telemetry.addData("Current Slot", getCurrentSlot());
            telemetry.addData("Sequence Step", sequenceIndex);
            telemetry.update();
        }
    }

    // ================= CORE STEP LOGIC =================
    void handleStep(BallColor targetColor) {

        int currentSlot = getCurrentSlot();
        BallColor[] logicalSlots = readLogicalSlots(currentSlot);

        // 1️⃣ Already aligned → launch
        if (logicalSlots[currentSlot] == targetColor) {
            launch();
            sequenceIndex++;
            return;
        }

        // 2️⃣ Decide direction
        int forward = (currentSlot == 2) ? 0 : currentSlot + 1;
        int backward = (currentSlot == 0) ? 2 : currentSlot - 1;

        // 3️⃣ Move (prioritise forward)
        if (logicalSlots[forward] == targetColor) {
            spinServo.setPosition(SLOTS[forward]);
        } else if (logicalSlots[backward] == targetColor) {
            spinServo.setPosition(SLOTS[backward]);
        }
    }

    // ================= SENSOR → SLOT REMAP =================
    BallColor[] readLogicalSlots(int servoSlot) {

        BallColor s0 = readColor(0);
        BallColor s1 = readColor(1);
        BallColor s2 = readColor(2);

        BallColor[] logical = new BallColor[3];

        if (servoSlot == 0) {
            logical[0] = s0;
            logical[1] = s1;
            logical[2] = s2;
        } else if (servoSlot == 1) {
            logical[0] = s2;
            logical[1] = s0;
            logical[2] = s1;
        } else {
            logical[0] = s1;
            logical[1] = s2;
            logical[2] = s0;
        }

        return logical;
    }

    // ================= COLOR + DISTANCE =================
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
        // pusher / shooter logic
    }
}
