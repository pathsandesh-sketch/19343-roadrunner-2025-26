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

    Servo spinservo1;
    Servo spinservo2;
    Servo spinservo3;

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

    private static String detectColor(double r, double g, double b) {
        double sum = r + g + b;

        // GREEN: green dominates both red and blue
        if (g > r  && g > b && g> 1000) {
            return "GREEN";
        }

        // PURPLE: red & blue similar, green suppressed
        else if ((b > 1900 || (b > 70 && b < 150 && b > g && r < b) )) {
            return "PURPLE";
        }
        else if (sum < 300 && g < 100 && b < 100 && r < 75) {
            return "PURPLE 2";
        }
        else if ((200 < sum && sum < 400) && g < 200 && g > 100 && b <= 130 && r < 75) {
            return "GREEN 2";
        }

        return "UNKNOWN";
    }

    enum State { S300,wait1, S180,wait2, S60,wait3, part1DONE, pos240, shoot1, pos120, shoot2, pos0, shoot3 }

    State state = State.S300;
    double statenumber = 2;
    boolean lastA = false;

    @Override
    public void init() {
        spinservo1 = hardwareMap.get(Servo.class, "spinservo1");
        spinservo2 = hardwareMap.get(Servo.class, "spinservo2");
        spinservo3 = hardwareMap.get(Servo.class, "spinservo3");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "colorSensor");

        spinservo1.setPosition(POS_300);
        spinservo2.setPosition(POS_300);
        spinservo3.setPosition(POS_300);
    }

    @Override
    public void loop() {



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
                    telemetry.addData("Color value", content240);

                }
                break;

            case wait1:
                if(StateTimer.seconds()>= statenumber){
                    telemetry.addLine("SUCCESS!");
                    spinservo1.setPosition(POS_180);
                    spinservo2.setPosition(POS_180);
                    spinservo3.setPosition(POS_180);
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
                    telemetry.addData("Color value", content120);
                }
                break;

            case wait2:
                if(StateTimer.seconds()>= statenumber){
                    telemetry.addLine("SUCCESS!");
                    spinservo1.setPosition(POS_60);
                    spinservo2.setPosition(POS_60);
                    spinservo3.setPosition(POS_60);
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
                    telemetry.addData("Color value", content0);
                }
                break;

            case wait3:
                if(StateTimer.seconds()>= statenumber){
                    telemetry.addLine("SUCCESS!");
                    spinservo1.setPosition(POS_0);
                    spinservo2.setPosition(POS_0);
                    spinservo3.setPosition(POS_0);
                    StateTimer.reset();
                    state = State.part1DONE;

                }
                break;
            case part1DONE:
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
