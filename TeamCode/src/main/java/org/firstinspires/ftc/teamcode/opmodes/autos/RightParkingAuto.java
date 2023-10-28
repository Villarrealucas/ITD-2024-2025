package org.firstinspires.ftc.teamcode.opmodes.autos;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Vision;
import org.firstinspires.ftc.teamcode.subsystems.drive.TankDrive;
import org.firstinspires.ftc.teamcode.util.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.util.PoseStorage;

@Autonomous
public class RightParkingAuto extends LinearOpMode {

    Pose2d startPose = new Pose2d(0, -62, Math.toRadians(0));
    TrajectoryVelocityConstraint vel = (v, pose2d, pose2d1, pose2d2) -> 20;
    TrajectoryAccelerationConstraint accel = (v, pose2d, pose2d1, pose2d2) -> 25;

    @Override
    public void runOpMode() {
        TankDrive drive = new TankDrive(gamepad1, hardwareMap);
        Vision vision = new Vision(hardwareMap, "Webcam 1", telemetry);

        Arm slide = new Arm(this);
        Claw claw = new Claw(this);

        drive.setPoseEstimate(startPose);
        while (!isStarted() && !isStopRequested()) {
            vision.updateTagOfInterest();
            vision.tagToTelemetry();
            telemetry.update();
        }

        waitForStart();

        TrajectorySequence start = drive.trajectorySequenceBuilder(startPose)
                .forward(5)
                .build();

        drive.followTrajectorySequence(start);

        switch (vision.getTag()) {
        case 1: {
            //
            drive.followTrajectorySequence(drive.trajectorySequenceBuilder(start.end())
                            .strafeRight(5)
                            .back(5)
                            .strafeLeft(5)
                    .build());
            break;
            }
        case 2: {

            drive.followTrajectorySequence(drive.trajectorySequenceBuilder(start.end())
                    .strafeRight(10)
                    .build());
            break;
            }
            case 3:
            default: {
            drive.followTrajectorySequence(drive.trajectorySequenceBuilder(start.end())
                    .strafeRight(20, vel, accel)
                    .build());
            break;
            }

        }
        PoseStorage.currentPose = drive.getPoseEstimate();
    }
}
