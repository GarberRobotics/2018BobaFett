package Auton;

import org.usfirst.frc.team5216.robot.Robot;

import edu.wpi.first.wpilibj.command.*;

public class RightSideForReal extends Command{
	public static double l, r = .7;
	public static double oppositeSide;
	public static double rightTurn, slightlyForward, slightlyBackward;
	public static boolean stepFiveLeft, stepSixLeft, stepSevenLeft, stepOneLeft, stepTwoLeft, stepThreeLeft, stepFourLeft, elevatorUp, correction = true;
	public static boolean bet = false;
	private AutonFunctions autonFunctions;
	
	public RightSideForReal(){

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
		if(Robot.frontRight.getSelectedSensorPosition(0) < 44200 && stepOneLeft){
			Robot.ElevatorMotor.set(0);
			autonFunctions.ArmDown();
			autonFunctions.SlowPID(Robot.autonZ);
			setSpeed();
			rightTurn = Robot.autonZ - 75;
		}else if(rightTurn < Robot.imu.getAngleZ() && stepTwoLeft){
			stepOneLeft = false;
			autonFunctions.TurnLeft(1.5, 2);
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
	
	public void RightScale(){
		if(stepOneLeft){
			autonFunctions.FastPID(Robot.autonZ);
			setSpeed();
			if(Robot.frontRight.getSelectedSensorPosition(0) > 57000){
				l = r = -.5;
				bet = true;
			}
			if(bet && Robot.frontRight.getSelectedSensorVelocity(0) < 0){
				l = r = 0;
				stepOneLeft = false;
			}
			Robot.ElevatorMotor.set(0);
			Robot.ArmMotor.set(0);
		}else if(Robot.topElevator.get() && stepTwoLeft){
			l = r = 0;
			autonFunctions.ArmDown();
			autonFunctions.ElevatorUp();
		}else if(Robot.imu.getAngleZ() > Robot.autonZ - 50 && stepThreeLeft){
			stepTwoLeft = false;
			autonFunctions.TurnLeft(1.5, 2);
			autonFunctions.ElevatorUp();
			setSpeed();
			slightlyForward = Robot.frontRight.getSelectedSensorPosition(0);
		}else if(Robot.frontRight.getSelectedSensorPosition(0) < slightlyForward + 4300 && stepFourLeft){
			stepThreeLeft = false;
			autonFunctions.ElevatorUp();
			l = r = .45;
		}else if(stepFiveLeft){
			stepFourLeft = false;
			autonFunctions.ElevatorUp();
			autonFunctions.OpenArms();
			r = l = 0;
			if(Robot.timer.get() >= 11.5)
				stepFiveLeft = false;
			slightlyBackward = Robot.frontRight.getSelectedSensorPosition(0);
		}else if(Robot.frontRight.getSelectedSensorPosition(0) >= slightlyBackward - 7000 && stepSixLeft){
			l = r = -.42;
		}else if(stepSevenLeft){
			stepSixLeft = false;
			autonFunctions.ArmUp();
			Robot.ElevatorMotor.set(0);
			l = r = 0;
			if(!Robot.bottomElevator.get())
				stepSevenLeft = false;
		}else if(Robot.imu.getAngleZ() <= Robot.autonZ){
			autonFunctions.TurnRight(1.5, 2);
			setSpeed();
		}else{
			l = r = 0;
		}
	}
	
	public void setSpeed(){
		l = autonFunctions.l;
		r = autonFunctions.r;
	}
}