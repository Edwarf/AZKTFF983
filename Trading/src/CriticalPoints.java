import java.util.ArrayList;
public class CriticalPoints 
{
	public class CriticalPoint extends Point
	{
		//Can either be upwards or downwards, 1 or -1
		int TrendingDirection;
		CriticalPoint(double _Xval, double _Yval,  int _TrendingDirection)
		{
			super(_Xval, _Yval);
			TrendingDirection = _TrendingDirection;
		}
		int GetTrendingDirection()
		{
			return TrendingDirection;
		}
	}
	CriticalPoints(ArrayList<Double> origX, ArrayList<Double> origY)
	{
		OriginalPoints = new ArrayList<Point>();
		CriticalPoints = new ArrayList<Point>();
		for(int x = 0; x < origX.size() && x < origY.size(); x++)
		{
			OriginalPoints.add(new Point(origX.get(x), origY.get(x)));
		}
	}
	//Original points determined from original ArrayList.
	ArrayList<Point> OriginalPoints;
	//Points obtained from zeroes of differentiated trend equation
	ArrayList<Point> CriticalPoints; 
	//The following methods form regression equations out of the dataset.
}
