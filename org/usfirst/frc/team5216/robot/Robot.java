/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5216.robot;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.can.*;

import Auton.CenterNoVision;
import Auton.LeftAuton;
import Auton.LeftAuton2;
import Auton.LeftRightScaleAuton;
import Auton.RightSideBothScales;
import Auton.RightSideForReal;
import Controller.Xbox;
import FileSystem.DataTxt;
import IMU.ADIS16448_IMU;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	//Robot drive
	public static WPI_TalonSRX frontLeft, frontRight, backLeft, backRight, ElevatorMotor, ArmMotor; //Initiates talons
	public static SpeedControllerGroup right, left; //Initiates SpeedControllerGroup
	public static DifferentialDrive drive;  //Initiates Drive
	
	//Controllers
	public static Xbox stick = new Xbox(0);
	public static Xbox stick2 = new Xbox(1);
	
	//Auton boolean
	public static boolean[] step = new boolean[9];
	public static boolean[] stop = new boolean[2];
	
	public DataTxt dataTxt;
	
	//Sensors
	public static ADIS16448_IMU imu = new ADIS16448_IMU();
	
	//Timers
	public static Timer timer;
	
	//Doubles
	public static double autonZ;
	
	//Booleans
	public static boolean rightAutoAngle1, rightAutoAngle2, leftAutoAngle1, leftAutoAngle2;
	public static boolean rightElevator, leftElevator, rightArm, leftArm;
	
	//Strings
	public static String gameData; //Read in from DriverStation for auton
	
	//Vision
	public static final int IMG_WIDTH = 320;
	public static final int IMG_HEIGHT = 240;
	
	//PWM ports - Limit Switches
	public static DigitalInput topElevator, bottomElevator, armDown, armUp;
	
	//Initiates compressor
	public static Compressor c;
	
	//Initiates solenoids
	public static DoubleSolenoid S1, S2;
	
	//Used for command based auton
	Command autoChooser;
	SendableChooser chooser;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit(){
		//Sets where solenoids where plugged in
		S1 = new DoubleSolenoid(0, 1);
		S2 = new DoubleSolenoid(2, 3);
		//Starts the compressor
		c = new Compressor(0);
		c.start();
		dataTxt = new DataTxt();
		//Sets DIO ports for LimitSwitches
		bottomElevator = new DigitalInput(0);
		topElevator = new DigitalInput(4);
		armDown = new DigitalInput(2);
		armUp = new DigitalInput(3);
		
		//sets Controller to given location
		stick = new Xbox(0);
		stick2 = new Xbox(1);
		
		timer = new Timer();
		
		//gives value to talons
		frontLeft = new WPI_TalonSRX(46);
		frontRight = new WPI_TalonSRX(47);
		backLeft = new WPI_TalonSRX(44);
		backRight = new WPI_TalonSRX(49);
		ElevatorMotor = new WPI_TalonSRX(45);
		ArmMotor = new WPI_TalonSRX(48);
		ArmMotor.setInverted(false); //Inverts voltage
		frontRight.setSelectedSensorPosition(0, 0, 0);
		
		//Creates a speedControllerGroup for either side of the robots drive train
		right = new SpeedControllerGroup(frontRight, backRight);
		left = new SpeedControllerGroup(frontLeft, backLeft);
		
		//Creates an instance for drive using SpeedControllerGroup
		drive = new DifferentialDrive(left, right);
		
		//Sets default values to booleans
		rightElevator = false;
		leftArm = false;
		leftElevator = true;
		rightArm = true;
		
		//pushes cameras image to smartDashBoard
		CameraServer.getInstance().startAutomaticCapture(0);
		CameraServer.getInstance().startAutomaticCapture(1);
	
		//Gets a value from a radiobutton for auton
		chooser = new SendableChooser();
		
		chooser.addDefault("CenterNoVision", new CenterNoVision());
		chooser.addObject("LeftAuton", new LeftAuton());
		chooser.addObject("RightSideForReal", new RightSideForReal());
		chooser.addObject("LeftBothScales", new LeftRightScaleAuton());
		chooser.addObject("RightSideBothScales", new RightSideBothScales());
		
		SmartDashboard.putData("Auton Select", chooser);
		
		SmartDashboard.putData(Scheduler.getInstance());
		
		//Gets the starting angle of the robot
		autonZ = imu.getAngleZ(); //Gets the current angle z of the robot
		
		//Resets encoders positions to 0
		frontRight.setSelectedSensorPosition(0, 0, 0);
		frontLeft.setSelectedSensorPosition(0, 0, 0);
		
		//Sets auton Defaults
		AutonInitiation();
		timer.start();
	}

	
	@Override
	public void autonomousInit() {
		timer.reset(); //Sets timer equal to zero
		timer.start(); //Starts the timers accumulation 
		c.stop();
		autonZ = imu.getAngleZ(); //Gets the current angle z of the robot
		
		//Resets encoder values to zero
		frontRight.setSelectedSensorPosition(0, 0, 0);
		frontLeft.setSelectedSensorPosition(0, 0, 0);
		
		//Sets auton Defaults
		AutonInitiation();
		dataTxt.Createfile();
		
		gameData = DriverStation.getInstance().getGameSpecificMessage(); //Grabs data from the driverstation for light positioning
		autoChooser = (Command) chooser.getSelected(); //Allows for use of class build auton
		autoChooser.start(); //Runs auton class
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		Scheduler.getInstance().run();
		PlaceIMUINFO();
		dataTxt.WriteToFile();
		SmartDashOut();
		//file.WriteToFile();
	}

		public void teleopInit(){
			frontRight.setSelectedSensorPosition(0, 0, 0);
			timer.reset();
			timer.start();
			dataTxt.Createfile();
			c.start();
			c.setClosedLoopControl(true); //Allows compressor to turn off with pressure switch
		}
	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
			dataTxt.WriteToFile();
			
		//Drive train
		if(DriverStation.getInstance().getJoystickIsXbox(0)){ //Nathan Drive
			drive.arcadeDrive(stick.leftStick.getY(), stick.leftStick.getX());
		}else if(DriverStation.getInstance().getJoystickIsXbox(1)){ //Wasseem and Carson Drive
			drive.arcadeDrive(stick2.leftStick.getY(), stick2.leftStick.getX());
		}
		
		SmartDashboard.putBoolean("topLimit", topElevator.get());
		SmartDashboard.putBoolean("bottomLimit", bottomElevator.get());
		SmartDashboard.putBoolean("armUp", armUp.get());
		SmartDashboard.putBoolean("armDown", armDown.get());
		
		//Controls the compressor
		UserCompControl();
		
		//Controls open and close on arms
		OpenCloseArm();
		
		//controls the elevator
		ElevatorMotor.set(stick.rightStick.getY());
		
		//Controls ArmMotor
		ArmMotorControl();		
		
		//Controls the elevator
		ElevatorMotorControl();
		
		//Everything that pushes to SmartDashboard
		SmartDashOut();
		
		//Pushes IMU info to SmartDashboard
		PlaceIMUINFO();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
		
	}
	
	public void ElevatorMotorControl(){
		if(DriverStation.getInstance().getJoystickIsXbox(0)){ //Nathan
			ElevatorMotor.set(stick.rightStick.getY()); //Elevator control up and down
		}else if(DriverStation.getInstance().getJoystickIsXbox(1)){ //Carson and Wasseem
			ElevatorMotor.set(stick2.rightStick.getY()); //Elevator control up and down
		}else{
			ElevatorMotor.set(0); //Sets ElevatorMotor off
		}
		if(!topElevator.get() && ElevatorMotor.get() > 0){
			ElevatorMotor.set(.15);
		}else if(!bottomElevator.get() && ElevatorMotor.get() < 0){
			ElevatorMotor.set(0);
		}else if(ElevatorMotor.get() == 0 && bottomElevator.get()){
			ElevatorMotor.set(.15);
		}
	}
	
	public void UserCompControl(){
		//Nathan
		if(stick.start.get() || stick.a.get()){ //Turns on the compressor
			c.start();
		}else if(stick.back.get() || stick.b.get()){ //Turns off the compressor
			c.stop();
		}
		//Carson and Wasseem
		if(stick2.start.get() || stick2.a.get()){ //Turns on the compressor
			c.start();
		}else if(stick2.back.get() || stick2.b.get()){ //Turns off the compressor
			c.stop();
		}
	}
	
	public void SmartDashOut(){
		SmartDashboard.putNumber("frontRight", frontRight.getSelectedSensorPosition(0));
		SmartDashboard.putBoolean("Elevaotr0", topElevator.get());
	}
	
	public void ArmMotorControl(){
		if(stick.dPad.down.get()){//Arm Flip Down
			ArmMotor.set(0.3);
		}else if(stick.dPad.up.get()){ //Arm Flip Up
			ArmMotor.set(-0.8);
		}
		if(stick2.lt.get()){//Arm Flip Down
			ArmMotor.set(0.3);
		}else if(stick2.lb.get()){ //Arm Flip Up
			ArmMotor.set(-0.8);
		}
		
		if(!armUp.get() && ArmMotor.get() < 0){ //When Motor Up hold
			ArmMotor.set(-.08);
		}else if(!armDown.get() && ArmMotor.get() > 0){ //When Motor Down Stop
			ArmMotor.set(0);
		}
	}
	
	public void OpenCloseArm(){
		//Carson and Wasseem
		if(stick2.rt.get()){ //close arms
			S1.set(DoubleSolenoid.Value.kReverse);
			S2.set(DoubleSolenoid.Value.kReverse);
		}
		else if(stick2.rb.get()){ //open arms
			S1.set(DoubleSolenoid.Value.kForward);
			S2.set(DoubleSolenoid.Value.kForward);
		}
		//Nathan
		if(stick.rb.get()){ //close arms
			S1.set(DoubleSolenoid.Value.kReverse);
			S2.set(DoubleSolenoid.Value.kReverse);
		}
		else if(stick.lb.get()){ //open arms
			S1.set(DoubleSolenoid.Value.kForward);
			S2.set(DoubleSolenoid.Value.kForward);
		}
		if(stick.lt.get()){
			S1.set(DoubleSolenoid.Value.kForward);
		}else if(stick.rt.get()){
			S2.set(DoubleSolenoid.Value.kForward);
		}
	}
	
	public void AutonInitiation(){
		//Initiates defaults for auton classes
		RightSideForReal.stepFiveLeft = RightSideForReal.stepSixLeft = RightSideForReal.stepSevenLeft = RightSideForReal.stepFourLeft = RightSideForReal.stepThreeLeft = RightSideForReal.stepOneLeft = RightSideForReal.stepTwoLeft = RightSideForReal.elevatorUp = RightSideForReal.correction = true;
		RightSideBothScales.stepFiveLeft = RightSideBothScales.stepSixLeft = RightSideBothScales.stepSevenLeft = RightSideBothScales.stepFourLeft = RightSideBothScales.stepThreeLeft = RightSideBothScales.stepOneLeft = RightSideBothScales.stepTwoLeft = RightSideBothScales.elevatorUp = RightSideBothScales.correction = true;
		LeftAuton2.bet = RightSideBothScales.bet2 = RightSideBothScales.bet = LeftRightScaleAuton.bet = LeftRightScaleAuton.bet2 = LeftRightScaleAuton.bet = LeftAuton.bet = RightSideForReal.bet = false;
		LeftAuton.stepFiveLeft = LeftAuton.stepSixLeft = LeftAuton.stepSevenLeft = LeftAuton.stepFourLeft = LeftAuton.stepThreeLeft = LeftAuton.stepTwoLeft = LeftAuton.elevatorUp = LeftAuton.correction = true;
		LeftAuton2.stepFiveLeft = LeftAuton2.stepSixLeft = LeftAuton2.stepSevenLeft = LeftAuton2.stepFourLeft = LeftAuton2.stepThreeLeft = LeftAuton2.stepOneLeft = LeftAuton2.stepTwoLeft = LeftAuton2.elevatorUp = LeftAuton2.correction = false;
		LeftAuton2.stepOneLeft = true;
		CenterNoVision.stepFour = CenterNoVision.stepOne = CenterNoVision.stepTwo = CenterNoVision.stepThree = true;
		CenterNoVision.angleZ = imu.getAngleZ();
		LeftRightScaleAuton.stepFiveLeft = LeftRightScaleAuton.stepFourLeft = LeftRightScaleAuton.stepOneLeft = LeftRightScaleAuton.stepSevenLeft = LeftRightScaleAuton.stepSixLeft = LeftRightScaleAuton.stepThreeLeft = LeftRightScaleAuton.stepTwoLeft = true;
		leftAutoAngle2 = leftAutoAngle1 = rightAutoAngle1  = rightAutoAngle2 = true;
	}
	
	public void PlaceIMUINFO(){
		//SmartDashboard.putData("IMU", imu);
		SmartDashboard.putNumber("AccelX", imu.getAccelX());
		SmartDashboard.putNumber("AccelY", imu.getAccelY());
		SmartDashboard.putNumber("AccelZ", imu.getAccelZ());
		SmartDashboard.putNumber("getAngle", imu.getAngle());
		SmartDashboard.putNumber("AngleX", imu.getAngleX());
		SmartDashboard.putNumber("AngleY", imu.getAngleY());
		SmartDashboard.putNumber("AngleZ", imu.getAngleZ());
		SmartDashboard.putNumber("BarometricPressure", imu.getBarometricPressure());
		SmartDashboard.putNumber("LastSampleTime", imu.getLastSampleTime());
		SmartDashboard.putNumber("MagX", imu.getMagX());
		SmartDashboard.putNumber("MagY", imu.getMagY());
		SmartDashboard.putNumber("MagZ", imu.getMagZ());
		SmartDashboard.putNumber("Pitch", imu.getPitch());
		SmartDashboard.putNumber("getQuaternionW", imu.getQuaternionW());
		SmartDashboard.putNumber("getQuaternionX", imu.getQuaternionX());
		SmartDashboard.putNumber("getQuaternionY", imu.getQuaternionY());
		SmartDashboard.putNumber("getQuaternionZ", imu.getQuaternionZ());
		SmartDashboard.putNumber("getRate", imu.getRate());
		SmartDashboard.putNumber("getRateX", imu.getRateX());
		SmartDashboard.putNumber("getRateY", imu.getRateY());
		SmartDashboard.putNumber("getRateZ", imu.getRateZ());
		SmartDashboard.putNumber("getRole", imu.getRoll());
		SmartDashboard.putNumber("Temperature", imu.getTemperature());
		SmartDashboard.putNumber("Yaw", imu.getYaw());
		SmartDashboard.putNumber("getRateY", imu.getRateY());
		SmartDashboard.putNumber("pidGit", imu.pidGet());
	}
}
