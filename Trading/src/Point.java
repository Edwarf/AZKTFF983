
public class Point
	{
		protected double Xval;
		protected double Yval;
		double GetXval()
		{
			return Xval;
		}
		double GetYval()
		{
			return Yval;
		}
		Point(double _Xval, double _Yval)
		{
			Xval = _Xval;
			Yval = _Yval;
		}
	}