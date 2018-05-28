import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
class Company
{
	
	//Investing Algorithm Specifics
	int Weight;
	News CompanyNews;
	//General Company Details - Pre Events
	String Name;
	String InvestName;
	double StockPrice;
	double MarketCap;
	double QuarterProfit;
	double QuarterOutstandingShares;
	double PriceEarningRatio;
	double ForwardPriceEarningRatio;
	double AverageShareVolume;
	double FiftyTwoWeekHighSharePrice;
	double FiftyTwoWeekLowSharePrice;
	double OneYearTargetPrice;
	double YesterdayClosingPrice;
	double EarningsPerShare;
	double AnnualizedDividend;
	double Beta;
	double Yield;
	String ExDividendDate;
	String DividendPaymentDate;
	//General Company Details - Day Events
	//Percent change from the same time interval in the previous day
	double PercentChangeFromPreviousDay;
	double HighSharePrice;
	double LowSharePrice;
	double DailyShareVolume;
	public void UpdateAttributes()
	{
		//7-39
		try
		{
			Document AttributeFetcher = Jsoup.connect("https://www.nasdaq.com/symbol/" + InvestName).get();
			Elements AllAttributes = AttributeFetcher.getElementsByAttributeValue("class", "table-cell");
			Elements RelevantAttributes = new Elements();
			ArrayList<String> AttributeValues = new ArrayList<String>();
			//Gets raw text from NASDAQ
			for(int i = 9; i < 39; i+=2)
			{
				RelevantAttributes.add(AllAttributes.get(i));
			}
			//Splits the raw text into individual tokens based on whitespace
			String[] Rels = RelevantAttributes.text().split(" ");
			for(int z = 0; z < Rels.length; z++)
			{
				//First round of filtering unnecessary characters, adding to AttributeValues where it'll later be sorted
				if(!Rels[z].contains("$") && !Rels[z].contains("%") && Rels[z].charAt(0) != '/')
				{
					Rels[z] = Rels[z].replaceAll(",", "");
					//Second round of filtering
					AttributeValues.add(Rels[z]);
				}
			}
			OneYearTargetPrice = Float.parseFloat(AttributeValues.get(0));
			HighSharePrice = Float.parseFloat(AttributeValues.get(1));
			LowSharePrice = Float.parseFloat(AttributeValues.get(2));
			DailyShareVolume = Float.parseFloat(AttributeValues.get(3));
			AverageShareVolume = Float.parseFloat(AttributeValues.get(4));
			YesterdayClosingPrice = Float.parseFloat(AttributeValues.get(5));
			FiftyTwoWeekHighSharePrice = Float.parseFloat(AttributeValues.get(6));
			FiftyTwoWeekLowSharePrice = Float.parseFloat(AttributeValues.get(7));
			MarketCap = Float.parseFloat(AttributeValues.get(8));
			PriceEarningRatio = Float.parseFloat(AttributeValues.get(9));
			ForwardPriceEarningRatio = Float.parseFloat(AttributeValues.get(10));
			EarningsPerShare = Float.parseFloat(AttributeValues.get(11));
			AnnualizedDividend = Float.parseFloat(AttributeValues.get(12));
			ExDividendDate = AttributeValues.get(13);
			DividendPaymentDate = AttributeValues.get(14);
			Yield = Float.parseFloat(AttributeValues.get(15));
			Beta = Float.parseFloat(AttributeValues.get(16));
		}
		catch(IOException except)
		{
			System.out.println("Could not load NASDAQ information." + except.getStackTrace());
		}
	}
	Company(String _Name, String _InvestName)
	{
		InvestName = _InvestName;
		Name = _Name;
		CompanyNews = new News(InvestName);
		UpdateAttributes();
	}
}
