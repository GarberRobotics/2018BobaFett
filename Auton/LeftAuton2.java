package Auton;

import org.usfirst.frc.team5216.robot.Robot;

import edu.wpi.first.wpilibj.command.*;

public class LeftAuton2 extends Command{
	public static double l, r = .7;
	public static double oppositeSide;
	public static double rightTurn, slightlyForward, slightlyBackward;
	public static boolean stepFiveLeft, stepSixLeft, stepSevenLeft, stepOneLeft, stepTwoLeft, stepThreeLeft, stepFourLeft, elevatorUp, correction = true;
	public static boolean bet = false;
	private AutonFunctions autonFunctions;
	
	public LeftAuton2(){

	}
	
	protected void initialize(){
		autonFunctions = new AutonFunctions();
	}
	
	protected void execute(){
		if(Robot.gameData.equals("")){	
		}
		else if(Robot.gameData.charAt(1) == 'L'){
			LeftScale();
		}else if (Robot.gameData.charAt(1) == 'R'){
			RightScale();
		}
		Robot.c.stop();
		Robot.drive.tankDrive(l, r);
	}
	
	protected boolean isFinished(){
		if(Robot.timer.equals(15)){
			return true;
		}
		else return false;
	}
	
	protected void end(){
	}
	
	protected void interrupted(){
		//end();
	}
	
	public void LeftScale(){
		if(stepSevenLeft){
			autonFunctions.ArmUp();
			Robot.ElevatorMotor.set(0);
			if(!Robot.bottomElevator.get() && Robot.imu.getAngleZ() >= Robot.autonZ){
				autonFunctions.TurnLeft(1.5, 2);
				setSpeed();
			}else{
				l = r = 0;
			}
		}else if(stepSixLeft){
			if(Robot.frontRight.getSelectedSensorPosition(0) >= slightlyBackward - 6500){
				l = r = -.42;
			}else{
				stepSixLeft = false;
				stepSevenLeft = true;
			}
		}else if(stepFiveLeft){
			autonFunctions.ElevatorUp();
			autonFunctions.OpenArms();
			r = l = 0;
			if(Robot.timer.get() >= 12){
				stepFiveLeft = false;
				stepSixLeft = true;
			}
			slightlyBackward = Robot.frontRight.getSelectedSensorPosition(0);
		}else if(stepFourLeft){
			if(Robot.frontRight.getSelectedSensorPosition(0) < slightlyForward + 2300){
				autonFunctions.ElevatorUp();
				l = r = .42;
			}else{
				stepFourLeft = false;
				stepFiveLeft = true;
			}
		}else if(stepThreeLeft){
			if(Robot.imu.getAngleZ() < Robot.autonZ + 50){
				autonFunctions.TurnRight(1.5, 2);
				autonFunctions.ElevatorUp();
				setSpeed();
				slightlyForward = Robot.frontRight.getSelectedSensorPosition(0);
			}else{
				stepThreeLeft = false;
				stepFourLeft = true;
			}
		}else if(stepTwoLeft){
			if(Robot.topElevator.get()){
				l = r = 0;
				autonFunctions.ArmDown();
				autonFunctions.ElevatorUp();
			}else{
				stepTwoLeft = false;
				stepThreeLeft = true;
			}
		}else if(stepOneLeft){
			autonFunctions.FastPID(Robot.autonZ);
			setSpeed();
			if(Robot.frontRight.getSelectedSensorPosition(0) > 57000){
				l = r = -.5;
				bet = true;
			}
			if(bet && Robot.frontRight.getSelectedSensorVelocity(0) < 0){
				l = r = 0;
				stepOneLeft = false;
				stepTwoLeft = true;
			}
		}
	}
	
	public void RightScale(){
		if(Robot.frontRight.getSelectedSensorPosition(0) < 44200 && stepOneLeft){
			Robot.ElevatorMotor.set(0);
			autonFunctions.ArmDown();
			autonFunctions.SlowPID(Robot.autonZ);
			setSpeed();
			rightTurn = Robot.autonZ + 75;
		}else if(rightTurn > Robot.imu.getAngleZ() && stepTwoLeft){
			stepOneLeft = false;
			autonFunctions.TurnRight(1, 1.5);
			setSpeed();
			oppositeSide = Robot.frontRight.getSelectedSensorPosition(0);
		}else{
			stepTwoLeft = false;
			if(Robot.frontRight.getSelectedSensorPosition(0) < oppositeSide + 20000){
				l = r = .5;
			}else{
				l = r = 0;	
			}
		}
	}
	
	public void setSpeed(){
		l = autonFunctions.l;
		r = autonFunctions.r;
	}
}