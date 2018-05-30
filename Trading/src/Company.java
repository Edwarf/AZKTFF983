import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
class Company
{
	
	//Investing Algorithm Specifics
	double Value;
	News CompanyNews;
	ArrayList<Double> HistoricalOpeningStockPrices;
	ArrayList<Double> HistoricalClosingStockPrices;
	ArrayList<Double> HistoricalDailyStockChanges;
	ArrayList<Double> HistoricalDailyStockVolume;
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
	double OpeningPrice;
	double PercentChangeFromPreviousDay;
	double HighSharePrice;
	double LowSharePrice;
	double DailyShareVolume;
	double UpdateIndividualDoubleAttribute(ArrayList<String> AttributeValues, int position)
	{
		if(AttributeValues.get(position).contains("N/A") || AttributeValues.get(position).contains("NE"))
		{
				return -0.54352;
		}
		else if(AttributeValues.get(position).contains("B"))
		{
			return Double.parseDouble(AttributeValues.get(position).substring(0, AttributeValues.get(position).length() - 1))
					* 1000000000;
		}
		else
		{
			return Double.parseDouble(AttributeValues.get(position));
		}
	}
	public void UpdateAttributes()
	{
		//7-39
		ObtainHistoricalPrices();
		try
		{
			Document AttributeFetcher = Jsoup.connect("https://finance.yahoo.com/quote/" + InvestName + "?p=" + InvestName).get();
			Elements AllAttributes = AttributeFetcher.getElementsByAttributeValue("class", "Trsdu(0.3s)");
			Elements RelevantAttributes = new Elements();
			ArrayList<String> AttributeValues = new ArrayList<String>();
			//Gets raw text from NASDAQ
			for(int i = 0; i < AllAttributes.size(); i+=1)
			{
				RelevantAttributes.add(AllAttributes.get(i));
			}
			//Splits the raw text into individual tokens based on whitespace
			String[] Rels = RelevantAttributes.text().split(" ");
			for(int z = 0; z < Rels.length; z++)
			{
			
				//First round of filtering unnecessary characters, adding to AttributeValues where it'll later be sorted
				if(!Rels[z].contains("x"))
				{
					Rels[z] = Rels[z].replaceAll(",", "");
					//Second round of filtering
					AttributeValues.add(Rels[z]);
				}
			}
			YesterdayClosingPrice = UpdateIndividualDoubleAttribute(AttributeValues, 0);
			OpeningPrice =  UpdateIndividualDoubleAttribute(AttributeValues, 1);
			//LowSharePrice =  UpdateIndividualDoubleAttribute(AttributeValues, 6);
			//HighSharePrice =  UpdateIndividualDoubleAttribute(AttributeValues, 7);
			//FiftyTwoWeekLowSharePrice =  UpdateIndividualDoubleAttribute(AttributeValues, 8);
			//FiftyTwoWeekHighSharePrice =  UpdateIndividualDoubleAttribute(AttributeValues, 9);
			DailyShareVolume = UpdateIndividualDoubleAttribute(AttributeValues, 6);
			AverageShareVolume = UpdateIndividualDoubleAttribute(AttributeValues, 7);
			MarketCap = UpdateIndividualDoubleAttribute(AttributeValues, 8);
			Beta =  UpdateIndividualDoubleAttribute(AttributeValues, 9);
			PriceEarningRatio =  UpdateIndividualDoubleAttribute(AttributeValues, 10);
			EarningsPerShare =  UpdateIndividualDoubleAttribute(AttributeValues, 11);
			ExDividendDate = AttributeValues.get(12);
		}
		catch(IOException except)
		{
			System.out.println("Could not load NASDAQ information." + except.getStackTrace());
		}
	}
	private double AnalyzeMarketInterest()
	{  
		double value;
		//Basically, if there's higher volume, then 
		if(StockPrice < YesterdayClosingPrice)
		{
			value = -1;
		}
		else if(StockPrice >= YesterdayClosingPrice)
		{
			value = 0;
		}
		else
		{
			value = 1;
		}
		return value;
	}
	public void DayAnalysis(double weight)
	{ 
		//Analysis will deal with ValueChange
		double ValueChange = 0;
		ValueChange += CompanyNews.GetNewsValue();
		Value += ValueChange;
		System.out.println(Value);
	}
	private void ObtainHistoricalPrices()
	{
		try
		{
			Document doc = Jsoup.connect("https://finance.yahoo.com/quote/" + InvestName + "/history/").get();
			Elements TableElems = doc.getElementsByClass("Py(10px) Pstart(10px)");
			for(int i = 0; i < TableElems.size(); i+=6)
			{
				HistoricalOpeningStockPrices.add(Double.parseDouble(TableElems.get(i).text()));
			}
			for(int i = 4; i < TableElems.size(); i+=6)
			{
				HistoricalClosingStockPrices.add(Double.parseDouble(TableElems.get(i).text()));
			}
			for(int i = 5; i < TableElems.size(); i+=6)
			{
				HistoricalDailyStockVolume.add(Double.parseDouble(TableElems.get(i).text().replaceAll(",", "")));
			}
			for(int i = 0; i < HistoricalOpeningStockPrices.size(); i++)
			{
				HistoricalDailyStockChanges.add(HistoricalClosingStockPrices.get(i) - HistoricalOpeningStockPrices.get(i));
			}
		}
		catch(IOException except)
		{
			System.out.println("Failed to connect to yahoo.com for financial info.");
		}
	}
	public ArrayList<String> ObtainPoints(ArrayList<Double> A1, ArrayList<Double> A2)
	{
		ArrayList<String> returner = new ArrayList<String>();
		for(int i = 0; i < A1.size(); i++)
		{
			System.out.println(Double.toString(A1.get(i)) + ", " + Double.toString(A2.get(i)));
		}
		return returner;
	}
	Company(String _Name, String _InvestName)
	{
		Value = 0;
		InvestName = _InvestName;
		Name = _Name;
		CompanyNews = new News(InvestName, 1);
		HistoricalOpeningStockPrices = new ArrayList<Double>();
		HistoricalClosingStockPrices = new ArrayList<Double>();
		HistoricalDailyStockChanges = new ArrayList<Double>();
		HistoricalDailyStockVolume = new ArrayList<Double>();
		UpdateAttributes();
		ObtainPoints(HistoricalDailyStockVolume, HistoricalDailyStockChanges);
	}
}
