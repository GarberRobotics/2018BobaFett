package Auton;

public class TurnDegreesAtRate {
	public double l, r = 0;
	public double deltaRate;
	public TurnDegreesAtRate(){
		
	}
	
	public void setLeftTurn(double speed){
		l = speed;
		r = -speed;
	}
	
	public void TurnRight(double Gyrorate, double minRate, double maxRate){
		if(Gyrorate < minRate){
			deltaRate = (minRate - Gyrorate) / 1000;
			l += deltaRate;
			r -= deltaRate;
		}
		if(Gyrorate > maxRate){
			deltaRate = (maxRate - Gyrorate) / 100000;
			l += deltaRate;
			r -= deltaRate;
		}
		
		if(l >= .65)
			l = .65;
		if(r <= -.65)
			r = -.65;
	}
}
