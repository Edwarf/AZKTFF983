import java.util.ArrayList;
public class CriticalPoints 
{
	public class Point
	{
		double Xval;
		double Yval;
		double GetXval()
		{
			return Xval;
		}
		double GetYval()
		{
			return Yval;
		}
	}
	public class CriticalPoint extends Point
	{
		//Can either be upwards or downwards
		int TrendingDirection;
	}
	//Original points determined from original ArrayList.
	ArrayList<Point> OriginalPoints;
	//Points obtained from zeroes of differentiated trend equation
	ArrayList<Point> CriticalPoints; 
}
