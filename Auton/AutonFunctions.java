package Auton;

import org.usfirst.frc.team5216.robot.Robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class AutonFunctions {
	public double l, r;
	
	public AutonFunctions(){
	}
	
	public void TurnLeft(double minRate, double maxRate){
		if(l >= -.45){
			l = -.45;
		}
		if(r <= .45){
			r = .45;
		}
		if(Robot.imu.getRate() > -minRate){
			l -= .0001;
			r += .0001;
		}
		if(Robot.imu.getRate() < -maxRate){
			l += .0001;
			r -= .0001;
		}
		if(l < -.9){
			l = -.9;
		}else if(r > .9){
			r = .9;
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
	
	public void TurnRight(double minRate, double maxRate){
		if(l <= .35){
			l = .55;
		}
		if(r >= -.35){
			r = -.55;
		}
		if(Robot.imu.getRate() < minRate){
			l += .00000000001;
			r -= .00000000001;
		}
		if(Robot.imu.getRate() > maxRate){
			l -= .0001;
			r += .0001;
		}
		if(l > .8){
			l = .8;
		}else if(r < -.8){
			r = -.8;
		}
	}
	
	public void CloseArms(){
		Robot.S1.set(DoubleSolenoid.Value.kReverse);
		Robot.S2.set(DoubleSolenoid.Value.kReverse);
	}
	
	public void OpenArms(){
		Robot.S1.set(DoubleSolenoid.Value.kForward);
		Robot.S2.set(DoubleSolenoid.Value.kForward);
	}
	
	public void ArmUp(){
		Robot.ArmMotor.set(-0.7);
		if(!Robot.armUp.get() && Robot.ArmMotor.get() < 0){
			Robot.ArmMotor.set(-0.05);
		}
	}
	
	public void ArmDown(){
		Robot.ArmMotor.set(0.2);
		if(!Robot.armDown.get() && Robot.ArmMotor.get() > 0){ //When Motor Down Stop
			Robot.ArmMotor.set(0);
		}
	}
	
	public void ElevatorUp(){
		if(Robot.topElevator.get()){
			Robot.ElevatorMotor.set(1);
		}else{
			Robot.ElevatorMotor.set(.13);
		}
	}
	
	public void SlowPID(double autonZ){
		l = r = .7;
		if(Robot.imu.getAngleZ() > autonZ + 5){
			l = .58;
		}else if(Robot.imu.getAngleZ() > autonZ + 2){
			l = .65;
		}else if(Robot.imu.getAngleZ() > autonZ + 1.5){
			l = .66;
		}else if(Robot.imu.getAngleZ() > autonZ + 1.3){
			l = .665;
		}else if(Robot.imu.getAngleZ() > autonZ + .5){
			l = .68;
		}else if(Robot.imu.getAngleZ() > autonZ + .2){
			l = .685;
		}else if(Robot.imu.getAngleZ() > autonZ + .12){
			l = .687;
		}else if(Robot.imu.getAngleZ() > autonZ + .1){
			l = .689;
		}else if(Robot.imu.getAngleZ() > autonZ + .05){
			l = .699;
		}
		
		if(Robot.imu.getAngleZ() < autonZ - 5){
			r = .58;
		}else if(Robot.imu.getAngleZ() < autonZ - 2){
			r = .65;
		}else if(Robot.imu.getAngleZ() < autonZ - 1.5){
			r = .66;
		}else if(Robot.imu.getAngleZ() < autonZ - 1.3){
			r = .665;
		}else if(Robot.imu.getAngleZ() < autonZ - .5){
			r = .68;
		}else if(Robot.imu.getAngleZ() < autonZ - .2){
			r = .685;
		}else if(Robot.imu.getAngleZ() < autonZ - .12){
			r = .687;
		}else if(Robot.imu.getAngleZ() < autonZ - .1){
			r = .689;
		}else if(Robot.imu.getAngleZ() < autonZ - .05){
			r = .699;
		}
		
	}

	public void FastPID(double autonZ){
		l = r = 1;
		if(Robot.imu.getAngleZ() > autonZ + 5){
			l = .88;
		}else if(Robot.imu.getAngleZ() > autonZ + 2){
			l = .95;
		}else if(Robot.imu.getAngleZ() > autonZ + 1.5){
			l = .96;
		}else if(Robot.imu.getAngleZ() > autonZ + 1.3){
			l = .965;
		}else if(Robot.imu.getAngleZ() > autonZ + .5){
			l = .98;
		}else if(Robot.imu.getAngleZ() > autonZ + .2){
			l = .985;
		}else if(Robot.imu.getAngleZ() > autonZ + .12){
			l = .987;
		}else if(Robot.imu.getAngleZ() > autonZ + .1){
			l = .989;
		}else if(Robot.imu.getAngleZ() > autonZ + 05){
			l = .999;
		}
		
		if(Robot.imu.getAngleZ() < autonZ - 5){
			r = .88;
		}else if(Robot.imu.getAngleZ() < autonZ - 2){
			r = .95;
		}else if(Robot.imu.getAngleZ() < autonZ - 1.5){
			r = .96;
		}else if(Robot.imu.getAngleZ() < autonZ - 1.3){
			r = .965;
		}else if(Robot.imu.getAngleZ() < autonZ - .5){
			r = .98;
		}else if(Robot.imu.getAngleZ() < autonZ - .2){
			r = .985;
		}else if(Robot.imu.getAngleZ() < autonZ - .12){
			r = .987;
		}else if(Robot.imu.getAngleZ() < autonZ - .1){
			r = .989;
		}else if(Robot.imu.getAngleZ() < autonZ - 05){
			r = .999;
		}
	}
}
