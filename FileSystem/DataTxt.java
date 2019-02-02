package FileSystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.usfirst.frc.team5216.robot.Robot;


public class DataTxt {
	File f;
	BufferedWriter bw;
	BufferedWriter bwold;
	FileWriter fw;
	
	public DataTxt(){
	}
	
	public void Createfile(){
		try{
			f = new File("/home/lvuser/Rate1.txt");
			if(!f.exists()){
				f.createNewFile();
			}
			fw = new FileWriter(f, true);
		} catch (IOException e){
			e.printStackTrace();
		}
		bw = new BufferedWriter(fw);
	}
	
	public void WriteToFile(){
		try{
			fw = new FileWriter(f, true);
			bw = new BufferedWriter(fw);
			bw.write(Double.toString(Robot.timer.get()) + ", " + Double.toString(Robot.imu.getRate()));
			bw.newLine();
			bw.close();
			fw.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
