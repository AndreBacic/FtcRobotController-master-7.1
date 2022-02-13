package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="RED Duck side", group = "normal duck autos")
public class DuckSideAutoRed extends FreightFrenzy_BaseAutoSetup {

    @Override
    public void initOdometryCoordinates() {
        robot.odometer.x = -112;
        robot.odometer.y = 2;
    }

    @Override
    public void runRobotAuto() {
        // drive around TSE and score pre-loaded freight
        robot.intake.setPower(robot.INTAKE_ON_POWER);
        if (this.tse_position == TSE_Position.LEFT) { // bottom tray
            robot.motorTurnNoReset(0.8, robot.ARM_HINGE_UP_CLICKS*23/90, robot.armHinge);
            robot.odStrafe(-35, 1, -103, 20, 4, 1200, 0.025, true);
            robot.odometer.odSleep(60);
            robot.odStrafe(-40, 0.4, -101, 26, 1.5, 1500);

            robot.odometer.odSleep(800);
            robot.intake.setPower(robot.INTAKE_OUTPUT_POWER);
            robot.odometer.odSleep(1200);
            robot.intake.setPower(robot.INTAKE_ON_POWER/3);
            robot.odometer.odSleep(400);

            robot.odStrafe(-40, 1, -108, 16, 4, 1500, 0.025, false);

        } else if (this.tse_position == TSE_Position.CENTER) { // bottom tray
            robot.motorTurnNoReset(0.7, robot.ARM_HINGE_UP_CLICKS*47/90, robot.armHinge);
            robot.odStrafe(0, 1, -98, 19, 4, 1200, 0.025, true);
            robot.motorTurnNoReset(0.6, (int)robot.ARM_TURNSTILE_CLICKS_PER_DEG*-32, robot.armTurnstile);
            robot.odometer.odSleep(60);
            robot.odStrafe(0, 0.4, -96, 23, 1.5, 1000);

            robot.odometer.odSleep(200);
            robot.intake.setPower(robot.INTAKE_OUTPUT_POWER);
            robot.odometer.odSleep(1200);
            robot.intake.setPower(robot.INTAKE_ON_POWER/3);
            robot.odometer.odSleep(300);

            robot.odStrafe(0, 1, -95, 18, 4, 1500, 0.025, false);

        } else { // top tray
            robot.motorTurnNoReset(0.7, robot.ARM_HINGE_UP_CLICKS*77/90, robot.armHinge);
            robot.odStrafe(0, 1, -110, 20, 4, 1200, 0.025, false);
            robot.intakeFlap.setPosition(0);
            robot.motorTurnNoReset(0.6, (int)robot.ARM_TURNSTILE_CLICKS_PER_DEG*-97, robot.armTurnstile);
            robot.odStrafe(0, 1, -110, 39, 4, 1200, 0.025, true);
            robot.odometer.odSleep(50);
            robot.odStrafe(0, 0.4, -99, 42, 1.5, 1100);

            robot.motorTurnNoReset(0.8, robot.ARM_HINGE_UP_CLICKS * 67 / 90, robot.armHinge);
            robot.odometer.odSleep(700);
            robot.intake.setPower(robot.INTAKE_OUTPUT_POWER);
            robot.odometer.odSleep(1200);
            robot.motorTurnNoReset(0.8, robot.ARM_HINGE_UP_CLICKS * 80 / 90, robot.armHinge);
            robot.odometer.odSleep(800);

            robot.odStrafe(0, 1, -117, 42, 4, 1500, 0.025, false);
            robot.intakeFlap.setPosition(1);
            robot.odometer.odSleep(100);

        }
        robot.intake.setPower(0);
        robot.motorTurnNoReset(0.55, (int)robot.ARM_TURNSTILE_CLICKS_PER_DEG*-20, robot.armTurnstile);
        robot.motorTurnNoReset(0.7, robot.ARM_HINGE_UP_CLICKS*100/90, robot.armHinge);
        if (this.tse_position == TSE_Position.RIGHT) {
            robot.odStrafe(0, 0.95, -124, 23, 5, 3000);
        } else if (this.tse_position == TSE_Position.CENTER) {
            robot.odStrafe(0, 1, -121, 14, 4, 3000);
        } else {
            robot.odStrafe(-40, 1, -123, 13, 4, 3000);
        }
        robot.odTurn(-100, 0.8, 800, 0.01, false);
        robot.odStrafe(-100, 0.44, -133, 12, 3, 1000, 0.015, false);
        robot.odStrafe(-100, 0.35, -135, 10, 1.5, 1000, 0.02, true);
        robot.odometer.odSleep(400);

        // spin carousel
        robot.duckSpinner.setVelocity(1750);
        robot.motorTurnNoReset(0.6, 0, robot.armTurnstile);
        robot.motorTurnNoReset(0.5, 0, robot.armHinge);
        robot.odometer.odSleep(3200);
        robot.duckSpinner.setVelocity(0);


        this.AfterScorePreloadAndDeliver();
    }
    public void AfterScorePreloadAndDeliver() {

        // pick up duck TODO: make duck-finding vision software?
        robot.odStrafe(-105, 0.8, -110, 13, 4, 1300, 0.02, true);
        robot.intake.setPower(robot.INTAKE_ON_POWER);
        robot.odTurn(145, 1, 1100);
        robot.odStrafe(145, 0.7, -109, 16, 4, 1200, 0.02, false);

        // TODO: use distance sensor here?
        robot.odStrafe(145, 0.4, -124, 15.5, 3, 3200);
        robot.odTurn(60, 0.7, 1200);

        // score duck
        robot.odStrafe(150, 0.7, -123, 24, 4, 1500, 0.025, false);
        robot.odStrafe(180, 1, -129, 34, 4, 1500, 0.02, false);
        robot.motorTurnNoReset(0.5, robot.ARM_HINGE_UP_CLICKS*75/90, robot.armHinge);
        robot.intakeFlap.setPosition(0);
        robot.odStrafe(180, 1, -130, 45, 4, 1000, 0.02, false);
        robot.motorTurnNoReset(0.6, (int)robot.ARM_TURNSTILE_CLICKS_PER_DEG*185, robot.armTurnstile);
        robot.odTurn(90, 1, 900, 0.005, false);

        robot.odStrafe(90, 1, -113, 46, 4, 2000, 0.02, false);
        robot.intake.setPower(robot.INTAKE_ON_POWER/2);
        robot.odStrafe(90, 0.4, -104, 45, 1.5, 1000);

        robot.motorTurnNoReset(0.5, robot.ARM_HINGE_UP_CLICKS*64/90, robot.armHinge);
        robot.odometer.odSleep(500);
        robot.intake.setPower(robot.INTAKE_OUTPUT_POWER*1.2);
        robot.odometer.odSleep(1300);
        robot.motorTurnNoReset(0.5, robot.ARM_HINGE_UP_CLICKS*75/90, robot.armHinge);
        robot.odometer.odSleep(800);

        // park
        robot.motorTurnNoReset(1, (int)robot.ARM_TURNSTILE_CLICKS_PER_DEG*80, robot.armTurnstile);
        robot.intakeFlap.setPosition(1);
        robot.odStrafe(90, 1, -116, 53, 4, 2000, 0.02, false);
        robot.intake.setPower(0);
        robot.motorTurnNoReset(0.5, robot.ARM_HINGE_UP_CLICKS*45/90, robot.armHinge);
        robot.motorTurnNoReset(0.6, (int)robot.ARM_TURNSTILE_CLICKS_PER_DEG*5, robot.armTurnstile);
        robot.odTurn(180, 1, 600, 0.008, false);
        robot.motorTurnNoReset(0.5, robot.ARM_HINGE_UP_CLICKS*3/90, robot.armHinge);
        robot.odTurn(-90, 1, 800, 0.008, true);
        robot.odStrafe(-90, 1, -136, 40, 4, 1300, 0.025, false);
        robot.odStrafe(-90, 0.8, -140, 34, 2, 1300, 0.03, true);

    }
}
