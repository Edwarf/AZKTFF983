import java.math.*;
import java.util.ArrayList;
public class Equation 
{
	public static enum EquationDegree
	{
		FIRST,
		SECOND,
		THIRD,
		FOURTH,
		FIFTH,
	}
	class Term
	{
		int Power;
		double Coefficient;
		Term(int _Power, double _Coefficient)
		{
			Power = _Power;
			Coefficient = _Coefficient;
		}
		public int GetPower()
		{
			return Power;
		}
		public double GetCoefficient()
		{
			return Coefficient;
		}
		public double Operate(double value)
		{
			double origvalue = value;
			for(int i = 0; i < Power; i++)
			{
				value *= origvalue;
			}
			return value * Coefficient;
		}
	}
	double Constant;
	int Power;
	ArrayList<Term> Terms;
	Equation(Equation.EquationDegree deg, ArrayList<Point> Dataset)
	{
		Terms = new ArrayList<Term>();
		switch(deg)
		{
		case FIRST:
			LinearRegression(Dataset);
			break;
		}
	}
	//Coefficients should be ordered with Coefficients.begin() being coefficient of the first degree term
	private void GenerateTermList(int Pow, double[] Coefficients)
	{
		for(int i = 1; i <= Pow; i ++)
		{
			Terms.add(new Term(i,  Coefficients[i - 1]));
		}
	}
	private void LinearRegression(ArrayList<Point> Dataset)
	{
		this.Power = 1;
		double Slope; 
		double Intercept;
		//Calculate correlation coefficient
		double CorrelationCoefficient;
		double XPointSummation = 0;
		double XSquaredPointSummation = 0;
		double YSquaredPointSummation = 0;
		double YPointSummation = 0;
		double XYPointSummation = 0;
		double SampleSize = Dataset.size();
		for(int i = 0; i < Dataset.size(); i++)
		{
			XPointSummation += Dataset.get(i).GetXval();
			YPointSummation += Dataset.get(i).GetYval();
			XSquaredPointSummation += (Dataset.get(i).GetXval()*Dataset.get(i).GetXval());
			YSquaredPointSummation += (Dataset.get(i).GetYval()*Dataset.get(i).GetYval());
			XYPointSummation += (Dataset.get(i).GetXval()*Dataset.get(i).GetYval());
		}
		double Numerator = XYPointSummation*SampleSize - XPointSummation*YPointSummation;
		double Denominator = (XSquaredPointSummation*SampleSize - Math.pow(XPointSummation, 2))* (YSquaredPointSummation*SampleSize - Math.pow(YPointSummation, 2)); 
		CorrelationCoefficient = Numerator / Math.sqrt(Denominator);
		//Calculate Standard Deviations
		double XInterimNum = 0;
		double YInterimNum = 0;
		double XMean = XPointSummation/SampleSize;
		double YMean = YPointSummation/SampleSize;
		for(int i = 0; i < Dataset.size(); i++)
		{
			XInterimNum += Math.pow(Dataset.get(i).GetXval() - XMean, 2);
			YInterimNum += Math.pow(Dataset.get(i).GetYval() - YMean, 2);
		}
		double StandardXDeviation = Math.sqrt(XInterimNum/SampleSize);
		double StandardYDeviation = Math.sqrt(YInterimNum/SampleSize);
		//Calculate Slope
		Slope = CorrelationCoefficient*(StandardXDeviation/StandardYDeviation);
		//Calculate Intercept
		Intercept = YMean - Slope*XMean;
		//Construct Equation 
		Constant = Intercept;
		double [] Coefficients = {Slope};
		GenerateTermList(1, Coefficients);
		System.out.println("Generated equation via linear regression: " + "y = " + Double.toString(Slope) + "x" + " + " + Double.toString(Intercept));
		System.out.println("Correlation Coefficient for above equation is: " + Double.toString(CorrelationCoefficient));
	}
	//Returns y for a given x
	public double Compute(double input)
	{
		double output = 0;
		//Adds up polynomial terms
		for(int i = 0; i < Terms.size(); i++)
		{
			output += Terms.get(i).Operate(input);
		}
		//Adds constant
		return output + Constant;
	}
}
