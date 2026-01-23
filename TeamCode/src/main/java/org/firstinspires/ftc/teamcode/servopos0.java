package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.util.servo.ServoTuner;
@TeleOp(name="Servopos0")
public class servopos0 extends LinearOpMode {

    Servo spinservo1;
    Servo spinservo2;
    Servo spinservo3;
    @Override
    public void runOpMode() throws InterruptedException {
        spinservo1 = hardwareMap.get(Servo.class, "spinservo1");
        spinservo2 = hardwareMap.get(Servo.class, "spinservo2");
        spinservo3 = hardwareMap.get(Servo.class, "spinservo3");

        waitForStart();

        while (opModeIsActive()){

            spinservo1.setPosition(0);
            spinservo2.setPosition(0);
            spinservo3.setPosition(0);

            if (gamepad1.a){
                spinservo1.setPosition(0);
                spinservo2.setPosition(0);
                spinservo3.setPosition(0);
            }
            if (gamepad1.b){
                spinservo1.setPosition(0.2);
                spinservo2.setPosition(0.2);
                spinservo3.setPosition(0.2);
            }

            if(gamepad1.x){
                spinservo3.setPosition(0.4);
                spinservo2.setPosition(0.4);
                spinservo1.setPosition(0.4);
            }

            if(gamepad1.y){
                spinservo3.setPosition(0.6);
                spinservo2.setPosition(0.6);
                spinservo1.setPosition(0.6);
            }

        }
    }
}
