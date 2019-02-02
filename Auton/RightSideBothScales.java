package Auton;

import org.usfirst.frc.team5216.robot.Robot;

import edu.wpi.first.wpilibj.command.*;

public class RightSideBothScales extends Command{
	public static double l, r = .7;
	public static double autonZ, oppositeSide; //AutonZ used for between switch and Scale
	public static double leftTurn, rightTurn, opositeScale, elevatorTimer, slightlyForward, slightlyBackward;
	public static boolean stepFiveLeft, stepSixLeft, stepSevenLeft, stepOneLeft, stepTwoLeft, stepThreeLeft, stepFourLeft, elevatorUp, correction = true;
	public static boolean bet2, bet = false;
	private AutonFunctions autonFunctions;
	
	public RightSideBothScales(){
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
		if(stepOneLeft){
			autonFunctions.FastPID(Robot.autonZ);
			setSpeed();
			leftTurn = Robot.autonZ - 76;						
			if(Robot.frontRight.getSelectedSensorPosition(0) > 42100){
				l = r = -.5;
				bet = true;
			}
			if(bet && Robot.frontRight.getSelectedSensorVelocity(0) < 0){
				l = r = 0;
				stepOneLeft = false;
			}
			Robot.ElevatorMotor.set(0);
			Robot.ArmMotor.set(0);
		}else if(stepTwoLeft){
			if(Robot.imu.getAngleZ() > leftTurn){
				autonFunctions.TurnLeft(1.5, 2);
				setSpeed();
				autonFunctions.ArmUp();
			}else if(Robot.frontRight.getSelectedSensorVelocity(0) > 0){
				r = -.55;
				l = .55;
				Robot.ArmMotor.set(0);
			}else{
				l = r = 0;
				stepTwoLeft = false;
			}
			oppositeSide = Robot.frontRight.getSelectedSensorPosition(0);
			autonZ = Robot.autonZ - 86.5;
		}else if(stepThreeLeft){
			autonFunctions.FastPID(autonZ);
			setSpeed();
			autonFunctions.ArmUp();
			if(Robot.frontRight.getSelectedSensorPosition(0) > oppositeSide + 38000){
				l = r = -.5;
				bet2 = true;
			}
			if(bet2 && Robot.frontRight.getSelectedSensorVelocity(0) < 0){
				l = r = 0;
				stepThreeLeft = false;
			}
			leftTurn = Robot.autonZ;
		}else if(stepFourLeft){
			if(Robot.imu.getAngleZ() < leftTurn){
				autonFunctions.TurnRight(1.5, 2);
				setSpeed();
			}else{
				l = r = 0;
				autonFunctions.ArmDown();
				stepFourLeft = false;
			}
		}else if(stepFiveLeft){
			autonFunctions.ElevatorUp();
			if(!Robot.topElevator.get()){
				stepFiveLeft = false;
			}
			slightlyForward = Robot.frontRight.getSelectedSensorPosition(0);
		}else if(stepSixLeft){
			if(Robot.frontRight.getSelectedSensorPosition(0) < slightlyForward + 8600){
				l = r = .5;
			}else{
				l = r = 0;
				stepSixLeft = false;
			}
		}else{
			autonFunctions.OpenArms();
			l = r = 0;
		}
	}
	
	public void RightScale(){
		if(stepOneLeft){
			autonFunctions.FastPID(Robot.autonZ);
			setSpeed();
			rightTurn = Robot.autonZ + 65;
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