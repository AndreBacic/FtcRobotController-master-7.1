package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

/**
 * FTC Robot for the Freight Frenzy season
 *
 * For autonomous code, odometry (x,y) is (0,0) for the back corner of the robot's alliance's warehouse.
 */
class FreightFrenzyRobot {
    public BNO055IMU imu;
    public DcMotor wheel1;
    public DcMotor wheel2;
    public DcMotor wheel3;
    public DcMotor wheel4;
    public DcMotor intake;
    public DcMotor armTurnstile;
    public DcMotor armHinge;
    public DcMotorEx duckSpinner;
    public Servo intakeFlap;
    public Servo xOdoPodLift;
    public Servo yOdoPodLift;
    // TSET = Team Shipping Element Turret
    public Servo TSET_Turnstile;
    public Servo TSET_Pivot;
    /**
     * A continuously rotating servo that extends and retracts the tape measure that scores the Team Shipping Element.
     */
    public Servo TSET_Extender1;
    public Servo TSET_Extender2;
    public Servo TSET_Extender3;
    /**
     * detects if anything is in the intake hand
     */
    public Rev2mDistanceSensor distanceSensor;

    public Orientation angles; // used to get info from BNO055IMU

    public Odometer odometer;

    public final double MIN_DRIVE_BASE_TURN_POWER = 0.18;

    public final double ARM_TURNSTILE_CLICKS_PER_DEG = 1530 / 180.0;
    public final double ARM_TURNSTILE_MAX_DEG = 200;
    /**
     * When it is turned 90 degrees up. It is capable of turning much farther, almost to -900 clicks.
     */
    public final int ARM_HINGE_UP_CLICKS = -590;
    public final int ARM_HINGE_DOWN_CLICKS = 0;

    public final double X_ODO_POD_DOWN_POSITION = 1.0;
    public final double X_ODO_POD_UP_POSITION = 0.0;
    public final double Y_ODO_POD_DOWN_POSITION = 0.78;
    public final double Y_ODO_POD_UP_POSITION = 0.4;

    /**
     * How many cm (give or take ~1cm) this.distanceSensor normally
     * detects from it to the bottom of the intake hand.
     */
    public final double MIN_CM_FOR_NO_FREIGHT = 10.0;

    /**
     * index of zero is true if distanceSensor detected the robot holding freight last frame
     * same for the rest of the array, but each index is one frame farther in the past
     */
    private boolean[] heldFreightLastFrames = {false,false,false,false};

    public final int DUCK_SPINNER_VELOCITY = 8000;

    public final double INTAKE_ON_POWER = 1;
    public final double INTAKE_OUTPUT_POWER = -0.43;
    public final double INTAKE_SHOOT_POWER = -0.8;

    private LinearOpMode program; // the program using this module.  Robot requires access to the program to know when the program is trying to stop.

    public void init(HardwareMap hardwareMap, LinearOpMode program) {
        // SET UP IMU AS BNO055IMU:
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        wheel1 = hardwareMap.get(DcMotor.class, "wheel1");
        wheel2 = hardwareMap.get(DcMotor.class, "wheel2");
        wheel3 = hardwareMap.get(DcMotor.class, "wheel3");
        wheel4 = hardwareMap.get(DcMotor.class, "wheel4");
        intake = hardwareMap.get(DcMotor.class, "intake");
        armTurnstile = hardwareMap.get(DcMotor.class, "armTurnstile");
        armHinge = hardwareMap.get(DcMotor.class, "armHinge");
        duckSpinner = hardwareMap.get(DcMotorEx.class, "duckSpinner");

        intakeFlap = hardwareMap.get(Servo.class, "intakeFlap");
        xOdoPodLift = hardwareMap.get(Servo.class, "xOdoPodLift");
        yOdoPodLift = hardwareMap.get(Servo.class, "yOdoPodLift");
        TSET_Turnstile = hardwareMap.get(Servo.class, "TSET_Turnstile");
        TSET_Pivot = hardwareMap.get(Servo.class, "TSET_Pivot");
        TSET_Extender1 = hardwareMap.get(Servo.class, "TSET_Extender1");
        TSET_Extender2 = hardwareMap.get(Servo.class, "TSET_Extender2");
        TSET_Extender3 = hardwareMap.get(Servo.class, "TSET_Extender3");

        distanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "distanceSensor");

        setDriveBaseZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        wheel4.setDirection(DcMotorSimple.Direction.REVERSE);
        wheel2.setDirection(DcMotorSimple.Direction.REVERSE);

        armTurnstile.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armTurnstile.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armTurnstile.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armTurnstile.setDirection(DcMotorSimple.Direction.REVERSE);
        armHinge.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armHinge.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armHinge.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        duckSpinner.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        duckSpinner.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        duckSpinner.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        odometer = new Odometer(0, 0, 0);
        odometer.init(imu, wheel1, wheel2);
        odometer.READ_Y_REVERSE = true;
        odometer.READ_X_REVERSE = true;

        distanceSensor.initialize(); // TODO: Learn if and when initialization is necessary

        this.program = program;
    }


    public void setDriveBaseRunMode(DcMotor.RunMode runMode) {
        wheel1.setMode(runMode);
        wheel2.setMode(runMode);
        wheel3.setMode(runMode);
        wheel4.setMode(runMode);
    }

    public void setDriveBaseZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        wheel1.setZeroPowerBehavior(behavior);
        wheel2.setZeroPowerBehavior(behavior);
        wheel3.setZeroPowerBehavior(behavior);
        wheel4.setZeroPowerBehavior(behavior);
    }

    public void setOdoPodsDown() {
        xOdoPodLift.setPosition(X_ODO_POD_DOWN_POSITION);
        yOdoPodLift.setPosition(Y_ODO_POD_DOWN_POSITION);
    }

    public void setOdoPodsUp() {
        xOdoPodLift.setPosition(X_ODO_POD_UP_POSITION);
        yOdoPodLift.setPosition(Y_ODO_POD_UP_POSITION);
    }

    public void setTSETtoInitPosition() {
        TSET_Turnstile.setPosition(1);
        TSET_Pivot.setPosition(0.75);
        TSET_Extender1.setPosition(0.5);
        TSET_Extender2.setPosition(0.5);
    }

    /**
     * Makes the drivetrain forcefully stop.
     * After the method is over the drivetrain ZeroPowerBehavior is left on BRAKE
     * @param ms How long to spend forcing the robot to stop.
     */
    public void brakeRobot(int ms) {
        this.setDriveBaseZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wheel1.setPower(0);
        wheel2.setPower(0);
        wheel3.setPower(0);
        wheel4.setPower(0);
        odometer.odSleep(ms);
    }

    /**
     * Has robot turn at speed power to heading degrees. degrees should be 180 >= degrees >= -180.
     * millis is a time limit on the method in milliseconds.
     * @param degrees Field-relative heading the robot is to turn to (0 is towards the carousels, 90 is towards the red side, -90 is towards the blue side)
     * @param speed motor power the robot is to turn at.
     * @param millis time limit in milliseconds for this method. If time runs out, the method just ends.
     */
    public void odTurn(double degrees, double speed, int millis) {
        odTurn(degrees, speed, millis, 0.008, true);
    }
    /**
     * Has robot turn at speed power to heading degrees. degrees should be 180 >= degrees >= -180.
     * millis is a time limit on the method in milliseconds.
     * @param degrees Field-relative heading the robot is to turn to (0 is towards the carousels, 90 is towards the red side, -90 is towards the blue side)
     * @param speed motor power the robot is to turn at.
     * @param millis time limit in milliseconds for this method. If time runs out, the method just ends.
     * @param adjustPower basically, how harshly the robot settles into the correct heading. adjustPower of > 0.05 is probably a LOT. Default is 0.008.
     * @param breakAtEnd If true, the robot sets its drive motors all to zero power;
     *                   if false, the robot exits the function with the last set drive powers still holding
     */
    public void odTurn(double degrees, double speed, int millis, double adjustPower, boolean breakAtEnd) {
        double difference, sign, correct, final_speed, start = (int)System.currentTimeMillis();

        this.setDriveBaseRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        double angle;
        OdometryPosition position = odometer.getCurrentPosition();
        angle = position.angle;

        while (angle != degrees && !this.program.isStopRequested()) {
            position = odometer.getCurrentPosition();
            angle = position.angle;
            difference = angle-degrees;
            if (difference < 0) { sign = -1;
            } else { sign = 1; }

            // make sure that the robot turn the correct direction
            if (Math.abs(difference) > 180) {
                difference = sign * (360-Math.abs(difference));
                correct = -difference*adjustPower;
                if (Math.abs(correct) > 1) { correct = -sign; } // make sure that we don't correct so much that we give the motors greater power than speed.
            } else {
                correct = difference*adjustPower;
                if (Math.abs(correct) > 1) { correct = sign; } // make sure that we don't correct so much that we give the motors greater power than speed.
            }
            final_speed = speed*correct;
            if (Math.abs(final_speed) < MIN_DRIVE_BASE_TURN_POWER) {
                if (final_speed > 0) {final_speed = MIN_DRIVE_BASE_TURN_POWER;}
                if (final_speed < 0) {final_speed = -MIN_DRIVE_BASE_TURN_POWER;}
            }
            wheel2.setPower(-final_speed);
            wheel4.setPower(-final_speed);
            wheel1.setPower(final_speed);
            wheel3.setPower(final_speed);
            if (start+millis < (int)System.currentTimeMillis()) {
                break;
            }
        }
        if (breakAtEnd) {
            setDriveBaseZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            wheel2.setPower(0);
            wheel4.setPower(0);
            wheel1.setPower(0);
            wheel3.setPower(0);
        }
    }

    /**
     * Robot travels facing heading going at speed to the point x, y (x and y are in inches).
     *
     * @param heading Field-relative angle (in degrees) that the robot is to remain facing as it moves
     * @param speed Power for motors (from 0-1)
     * @param x X coordinate on the field where the robot is to go
     * @param y Y coordinate on the field where the robot is to go
     */
    public void odStrafe(double heading, double speed, double x, double y) {
        odStrafe(heading, speed, x, y, 2);
    }
    /**
     * Robot travels facing heading going at speed to the point x, y (x and y are in inches).
     *
     * @param heading Field-relative angle (in degrees) that the robot is to remain facing as it moves
     * @param speed Power for motors (from 0-1)
     * @param x X coordinate on the field where the robot is to go
     * @param y Y coordinate on the field where the robot is to go
     * @param buffer How close to (x, y) in inches the robot must get before the loop exits
     */
    public void odStrafe(double heading, double speed, double x, double y, double buffer) {
        odStrafe(heading, speed, x, y, buffer, 120000);
    }
    /**
     * Robot travels facing heading going at speed to the point x, y (x and y are in inches).
     *
     * @param heading Field-relative angle (in degrees) that the robot is to remain facing as it moves
     * @param speed Power for motors (from 0-1)
     * @param x X coordinate on the field where the robot is to go
     * @param y Y coordinate on the field where the robot is to go
     * @param buffer How close to (x, y) in inches the robot must get before the loop exits
     * @param millis Maximum runtime this method is allowed (in milliseconds)
     */
    public void odStrafe(double heading, double speed, double x, double y, double buffer, int millis) {
        odStrafe(heading, speed, x, y, buffer, millis, 0.02, true);
    }
    /**
     * Robot travels facing heading going at speed to the point x, y (x and y are in inches).
     *
     * @param heading Field-relative angle (in degrees) that the robot is to remain facing as it moves
     * @param speed Power for motors (from 0-1)
     * @param x X coordinate on the field where the robot is to go
     * @param y Y coordinate on the field where the robot is to go
     * @param buffer How close to (x, y) in inches the robot must get before the loop exits
     * @param millis Maximum runtime this method is allowed (in milliseconds)
     * @param adjustPower How harshly the robot should correct its heading when it veers off
     *                    (0.02 is medium; 0.1 is pretty harsh; 0.004 is very little)
     * @param breakAtEnd If true, the robot sets its drive motors all to zero power;
     *                   if false, the robot exits the function with the last set drive powers still holding
     */
    public void odStrafe(double heading, double speed, double x, double y, double buffer, int millis, double adjustPower, boolean breakAtEnd) {
        odStrafe(heading, speed, x, y, buffer, millis, adjustPower, breakAtEnd, false);
    }
    /**
     * Robot travels facing heading going at speed to the point x, y (x and y are in inches).
     *
     * @param heading Field-relative angle (in degrees) that the robot is to remain facing as it moves
     * @param speed Power for motors (from 0-1)
     * @param x X coordinate on the field where the robot is to go
     * @param y Y coordinate on the field where the robot is to go
     * @param buffer How close to (x, y) in inches the robot must get before the loop exits
     * @param millis Maximum runtime this method is allowed (in milliseconds)
     * @param adjustPower How harshly the robot should correct its heading when it veers off
     *                    (0.02 is medium; 0.1 is pretty harsh; 0.004 is very little)
     * @param breakAtEnd If true, the robot sets its drive motors all to zero power;
     *                   if false, the robot exits the function with the last set drive powers still holding
     * @param stopIfHoldingFreight Tracks to see if the intake possesses freight, and if so the loop exits.
     */
    public void odStrafe(double heading, double speed, double x, double y, double buffer, int millis, double adjustPower, boolean breakAtEnd, boolean stopIfHoldingFreight) {

        setDriveBaseRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        boolean offTarget = true;
        double start = (int)System.currentTimeMillis();

        while (offTarget && !this.program.isStopRequested() &&
                (!stopIfHoldingFreight || this.isHoldingFrieght())) {

            OdometryPosition position = odometer.getCurrentPosition();
            double angle = position.angle;

            double xdis = x - position.x;
            double ydis = y - position.y;
            if (pythagoreanTheorem(xdis, ydis) <= Math.abs(buffer)) {
                offTarget = false;
                continue;
            }
            double radians = Math.atan2(ydis, xdis);  // the direction the robot is supposed to go towards

            double theta = ((heading/180)*Math.PI) - radians + Math.PI*3/4;

            double x_vector = Math.cos(theta);  // calculate ratio of x distance to y distance.
            double y_vector = Math.sin(theta);

            // make sure minimum value of larger vector is 1 while maintaining the ratio
            double increase = Math.abs(1 / (0.0000000001 + Math.max(Math.abs(x_vector), Math.abs(y_vector))));
            x_vector*=increase;
            y_vector*=increase;

            // complicated logic to make the robot correct if it turns a bit off heading.
            double sign;
            double difference = angle-heading;
            if (Math.abs(difference) > 180) {
                if (difference < 0) { sign = -1;
                } else { sign = 1; }
                difference = sign * (360-Math.abs(difference));
                difference = -difference*adjustPower;
            } else {
                difference = difference*adjustPower;
            }
            wheel1.setPower((speed*y_vector)+difference);
            wheel2.setPower((speed*x_vector)-difference);
            wheel3.setPower((speed*x_vector)+difference);
            wheel4.setPower((speed*y_vector)-difference);
            if (start+millis < (int)System.currentTimeMillis()) {
                break;
            }
            this.program.telemetry.addData("x", position.x);
            this.program.telemetry.addData("y", position.y);
            this.program.telemetry.addData("xdis", xdis);
            this.program.telemetry.addData("ydis", ydis);
            this.program.telemetry.addData("theta", theta*180/Math.PI);
            this.program.telemetry.addData("degrees", radians*180/Math.PI);
            this.program.telemetry.addData("x_vector", x_vector);
            this.program.telemetry.addData("y_vector", y_vector);
            this.program.telemetry.update();
        }
        if (breakAtEnd) {
            setDriveBaseZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            wheel2.setPower(0);
            wheel4.setPower(0);
            wheel1.setPower(0);
            wheel3.setPower(0);
        }
    }
    private boolean isHoldingFrieght() {
        for (int i = heldFreightLastFrames.length-1; i > 0; i--) {
            heldFreightLastFrames[i] = heldFreightLastFrames[i-1];
        }
        heldFreightLastFrames[0] = (distanceSensor.getDistance(DistanceUnit.CM) < MIN_CM_FOR_NO_FREIGHT);

        for (boolean f: heldFreightLastFrames) {
            if (!f) {
                return false;
            }
        }
        return true;
    }
    public void resetFreightHoldingTacking() {
        heldFreightLastFrames = new boolean[]{false, false, false, false};
    }

    public void motorTurnNoReset(double speed, int clicks, DcMotor motor) {
        // has motor turn clicks at speed. Not very complicated.
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setTargetPosition(clicks);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(speed);
    }
    public void motorTurn(double speed, int clicks, DcMotor motor) {
        // has motor turn clicks at speed. Not very complicated.
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition(clicks);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(speed);
        while (motor.isBusy() && !this.program.isStopRequested()) {}
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private double pythagoreanTheorem(double a, double b) {
        return Math.sqrt(a*a + b*b);
    }
}
