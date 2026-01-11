package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.util.ElapsedTime;
@TeleOp(name = "Spindexp1")
public class spindexp1 extends OpMode {

    Servo servo;
    ColorSensor colorSensor;
    DistanceSensor distanceSensor;

    static final double POS_300 = 0.9524;
    static final double POS_180 = 0.5704;
    static final double POS_60  = 0.1905;
    static final double POS_0   = 0.0;
    ElapsedTime StateTimer = new ElapsedTime();
    static final double GREEN_DOMINANCE = 1.35;
    static final double DIST_CM = 6.0;

    double content240;
    double content120;
    double content0;
    enum State { S300,wait1, S180,wait2, S60,wait3, DONE }
    State state = State.S300;
    double statenumber = 2;
    boolean lastA = false;

    @Override
    public void init() {
        servo = hardwareMap.get(Servo.class, "spinservo");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "colorSensor");

        servo.setPosition(POS_300);
    }

    @Override
    public void loop() {

        boolean a = gamepad1.a;
        boolean aEdge = a && !lastA;
        lastA = a;

        double d = distanceSensor.getDistance(DistanceUnit.CM);
        int r = colorSensor.red();
        int g = colorSensor.green();
        int b = colorSensor.blue();

        boolean green =
                g > r * GREEN_DOMINANCE &&
                        g > b * GREEN_DOMINANCE;

        boolean purple =
                b > 1900 ||
                        (b > 70 && b < 150 && b > g && r < b) ||
                        (b > g && b > r);

        boolean valid =
                d <= DIST_CM &&
                        (green || purple);

        switch (state) {

            case S300:
                if (valid ) {

                    state = State.wait1;
                    StateTimer.reset();
                    if (green){
                        content240 = 1; // 1 is green

                    }
                    else {
                        content240=2; // 2 is purple or unknown
                    }

                }
                break;

            case wait1:
                if(StateTimer.seconds()>= statenumber){
                    telemetry.addLine("SUCCESS!");
                    servo.setPosition(POS_180);
                    StateTimer.reset();
                    state = State.S180;

                }
                break;

            case S180:
                if(valid){
                    state = State.wait2;
                    StateTimer.reset();
                    if (green){
                        content120 = 1; // 1 is green

                    }
                    else {
                        content120=2; // 2 is purple or unknown
                    }
                }
                break;

            case wait2:
                if(StateTimer.seconds()>= statenumber){
                    telemetry.addLine("SUCCESS!");
                    servo.setPosition(POS_60);
                    StateTimer.reset();
                    state = State.S60;

                }
                break;

            case S60:
                if(valid){
                    state = State.wait3;
                    StateTimer.reset();
                    if (green){
                        content0 = 1; // 1 is green

                    }
                    else {
                        content0=2; // 2 is purple or unknown
                    }
                }
                break;

            case wait3:
                if(StateTimer.seconds()>= statenumber){
                    telemetry.addLine("SUCCESS!");
                    servo.setPosition(POS_0);
                    StateTimer.reset();
                    state = State.DONE;

                }
                break;
            case DONE:
                telemetry.addLine("Done");
                break;
        }

        telemetry.addData("State", state);
        telemetry.addData("Dist", d);
        telemetry.addData("RGB", "%d %d %d", r, g, b);
        telemetry.addData("Green", green);
        telemetry.addData("Purple", purple);
        telemetry.addData("Valid", valid);
        telemetry.update();
    }
}
