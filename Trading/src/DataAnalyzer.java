import java.util.ArrayList;
public class DataAnalyzer 
{
	class DataSet
	{
		//Naming data set, conventions will follow as such. IndependentVariable::DependentVariable
		String Name;
		//Equation obtained by regression on OriginalDatapoints
		Equation Trends;
		//Original data
		ArrayList<Point> OriginalDatapoints;
		//Used to either increase the importance or decrease the importance of datasets based on knowledge of their impact on stock price
		double Weight;
		DataSet(String _Name, ArrayList<Point> _OriginalDatapoints, double _Weight)
		{
			Name = _Name;
			OriginalDatapoints = _OriginalDatapoints;
			Weight = _Weight;
			//Linear regression for now. Change later to change data to fit.
			Trends = new Equation(Equation.EquationDegree.FIRST, _OriginalDatapoints);
		}
		public double YieldDatasetBuyRating(double tester)
		{
			return Trends.Compute(tester) * Weight;
		}
	}
	//Will analyze based on DataSets
	ArrayList<DataSet> Data;
	public void AddDataSet(String SetName, ArrayList<Double> origX, ArrayList<Double> origY, double Weight)
	{
		ArrayList<Point> OriginalPoints = new ArrayList<Point>();
		for(int x = 0; x < origX.size() && x < origY.size(); x++)
		{
			OriginalPoints.add(new Point(origX.get(x), origY.get(x)));
		}
		Data.add(new DataSet(SetName, OriginalPoints, Weight));
	}
	public void AddDataSet(String SetName, ArrayList<Point> points, double Weight)
	{
		System.out.println(points.size());
		Data.add(new DataSet(SetName, points, Weight));
	}
	//The buy rating is the final prediction for the change in stock price. This will be used to determine how much to buy or sell.
	//TestingValues MUST be in the same order that datasets were added. Otherwise, data will be analyzed by the wrong equations. 
	public double YieldBuyRating(ArrayList<Double> TestingValues)
	{
		double BuyRating = 0;
		//Obtain raw, unaveraged score.
		for(int i = 0; i < Data.size(); i++)
		{
			BuyRating += Data.get(i).YieldDatasetBuyRating(TestingValues.get(i));
		}
		//Average score to predict price.
		return BuyRating / Data.size();
	}
	DataAnalyzer()
	{
		Data = new ArrayList<DataSet>();
	}
}
