package Auton;
import edu.wpi.first.wpilibj.command.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team5216.robot.Robot;

import edu.wpi.cscore.AxisCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

public class CenterNoVision  extends Command{
	double l, r;
	public static double dist, angleZ;
	public static boolean stepOne, stepTwo, stepThree, stepFour = true;
	Timer endTime = new Timer();
	
	public CenterNoVision(){
		
	}
	
	protected void initialize(){
		
	}
	
	protected void execute(){
		Robot.c.stop();
		if(Robot.gameData.equals("")){
			
		}
		else if(Robot.gameData.charAt(0) == 'L')
			Left();
		else if(Robot.gameData.charAt(0) == 'R')
			Right(); 
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
	
	public void Right(){
		if(Robot.frontRight.getSelectedSensorPosition(0) <= 5000 && stepOne){
			l = r = .6;
			ArmsDown();
			RaiseElevator(1);
		}else if(Robot.imu.getAngleZ() <= Robot.autonZ + 65 && stepTwo){
			stepOne = false;
			RaiseElevator(.7);
			l = .6;
			r = -.6;
			dist = Robot.frontRight.getSelectedSensorPosition(0);
		}else if(Robot.frontRight.getSelectedSensorPosition(0) <= dist + 7000 && stepThree){
			stepTwo = false;
			RaiseElevator(.2);
			l = r = .6;
		}else if(Robot.imu.getAngleZ() >= Robot.autonZ + 12 && stepFour){
			HoldElevator();
			stepThree = false;
			l = -.6;
			r = .6;
		}else{
			stepFour = false;
			l = r = .6;
		}
		if(Robot.timer.get() >= 9.5){
			OpenArms();
		}
		Robot.drive.tankDrive(l, r);
	}
	
	public void Left(){
		if(Robot.frontRight.getSelectedSensorPosition(0) <= 5000 && stepOne){
			l = .6;
			r = .6;
			ArmsDown();
			RaiseElevator(1);
		}else if(Robot.imu.getAngleZ() >= Robot.autonZ - 60 && stepTwo){
			stepOne = false;
			RaiseElevator(.7);
			l = -.6;
			r = .6;
			dist = Robot.frontRight.getSelectedSensorPosition(0);
		}else if(Robot.frontRight.getSelectedSensorPosition(0) <= dist + 10000 && stepThree){
			stepTwo = false;
			l = r = .6;
		}else if(Robot.imu.getAngleZ() <= Robot.autonZ - 13 && stepFour){
			stepThree = false;
			l = .6;
			r = -.6;
			HoldElevator();
		}else{
			stepFour = false;
			l = r = .6;
		}
		if(Robot.timer.get() >= 8.5){
			OpenArms();
		}
		Robot.drive.tankDrive(l, r);
	}
		
	public static void ArmsDown(){
		if(!Robot.armDown.get()){
			Robot.ArmMotor.set(0.0);
		}else{
			Robot.ArmMotor.set(0.3);
		}
	}
	
	public static void ArmsUp(){
		if(!Robot.armUp.get()){
			Robot.ArmMotor.set(-0.08);
		}else{
			Robot.ArmMotor.set(-0.7);
		}
	}
	
	public static void RaiseElevator(double speed){
		Robot.ElevatorMotor.set(speed);
		if(!Robot.topElevator.get()){
			Robot.ElevatorMotor.set(0.13);
		}
	}
	
	public static void HoldElevator(){
		Robot.ElevatorMotor.set(.15);
	}
	
	public static void OpenArms(){
		Robot.S1.set(DoubleSolenoid.Value.kForward);
		Robot.S2.set(DoubleSolenoid.Value.kForward);
	}
}
