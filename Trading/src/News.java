import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class News
{
	String CompanyTradingName;
	ArrayList<Article> Articles;
	ArrayList<ArrayList<Article>> DailyArticles;
	ArrayList<Double> DailyArticleValues;
	FileManager FManager;
	String StorageFile;
	//either a bad weight or a good weight. 
	double Value;
	//might want to implement individual article weights based on author, article, source, etc. later. 
	double Weight;
	//Used to deal with google dates
	//This will parse google news on a company
	private void AnalyzeArticles()
	{ 
		double AggregatedValue = 0;
		for(Article article : Articles)
		{
			AggregatedValue += article.ReturnNewsValue();
		}
		Value = (AggregatedValue / Articles.size()); 
		System.out.println(Value);
	}
	public double GetNewsValue()
	{
		AnalyzeArticles();
		return Value;
	}
	void GenerateYearArticles()
	{
		Articles = new ArrayList<Article>();
		ArrayList<String> articleURLs = new ArrayList<String>();
		GregorianCalendar Cal = new GregorianCalendar();
		Document searchQuery;
		try
		{
			FManager.LoadArticles();
			final WebClient webClient = new WebClient();
			Calendar tempcal = Calendar.getInstance();
			String LatestDate = FManager.GetLatestDate();
			int DateDiff = DateFormatter.FindDayDifferenceBetweenDates(DateFormatter.YieldDate(tempcal.get(Calendar.DAY_OF_MONTH), tempcal.get(Calendar.MONTH) + 1, tempcal.get(Calendar.YEAR)), LatestDate);
			System.out.println(DateDiff);
			for(int x = 0; x <= DateDiff ; x++)
			{
				Calendar NewCal = Calendar.getInstance();
				//In this case, x is an integer from 0 to 365
				NewCal.add(Calendar.DAY_OF_YEAR, -x);
				String GoogleRoot = "https://www.google.com/search?q=";
				String SearchBounds = "&num=10";
				String Month = Integer.toString(NewCal.get(Calendar.MONTH)).length() < 2 ? "0" 
				+ Integer.toString(NewCal.get(Calendar.MONTH) + 1) : Integer.toString(NewCal.get(Calendar.MONTH) + 1);
	
				String Day = Integer.toString(NewCal.get(Calendar.DAY_OF_MONTH)).length() < 2 ? "0" 
				+ Integer.toString(NewCal.get(Calendar.DAY_OF_MONTH)) : Integer.toString(NewCal.get(Calendar.DAY_OF_MONTH));
				
				String Date = Month + "/" + Day + "/" + Integer.toString(NewCal.get(Calendar.YEAR));
				System.out.println(Date);
				String DateBounds = "&tbs=" + URLEncoder.encode("cdr:1,cd_min:" + Date + ",cd_max:" + Date, "UTF-8");
				String SearchSection = "&tbm=nws";
				//Connect with query, encode, etc. 
				System.out.println(GoogleRoot + URLEncoder.encode(CompanyTradingName, "UTF-8")  + SearchSection + SearchBounds + DateBounds);
				HtmlPage Page = null;
				try
				{
					Page = webClient.getPage(GoogleRoot + URLEncoder.encode(CompanyTradingName, "UTF-8")  + SearchSection + SearchBounds + DateBounds);
				}
				catch(FailingHttpStatusCodeException e)
				{
					System.out.println("News acquisition stopped by google.");
					return;
				}
				Elements URLs = Jsoup.parse(Page.asXml()).select(".g .r>a");
				//Iterate through to find valid articles			
				System.out.println(Integer.toString(URLs.size()) + "FWEEFWA");
				for(int i = 0; i < URLs.size(); i++ )
				{	
					String url = URLs.get(i).absUrl("href"); 
		           	articleURLs.add(url);
				}
				for(int i = 0; i < articleURLs.size(); i++)
				{
					for(int z = 0; z < Article.RecognizedSites.length; z++)
					{
						if(articleURLs.get(i).contains((Article.RecognizedSites[z])))
						{
							System.out.println(articleURLs.get(i));
							Article Currart = new Article(articleURLs.get(i), Weight, Date);
							if(Currart.FoundStatus == true)
							{
								Articles.add(Currart);
							}
						}
					}	
				}
				articleURLs.clear();
				FManager.SaveArticles(Articles);
			}
		}
		catch(IOException except)
		{
			System.out.println("Could not fetch article: " + except.getStackTrace());
		}
	}
	void GenerateArticles()
	{
		Articles = new ArrayList<Article>();
		ArrayList<String> articleURLs = new ArrayList<String>();
		Document searchQuery;
		try
		{
		
			String GoogleRoot = "https://www.google.com/search?q=";
			String SearchBounds = "&num=10";
			//Connect with query, encode, etc. 
			System.out.println(GoogleRoot + URLEncoder.encode(CompanyTradingName, "UTF-8") + SearchBounds + "&tbm=nws");
			Elements URLs = Jsoup.connect(GoogleRoot + URLEncoder.encode(CompanyTradingName, "UTF-8") + SearchBounds + "&tbm=nws").userAgent("Cesion Software").get().select(".g .r>a");
			//Iterate through to find valid articles			
			for(int i = 0; i < URLs.size(); i++ )
			{	
				String url = URLs.get(i).absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
	           	url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
	           	articleURLs.add(url);
				System.out.println(url);
			}
			for(int i = 0; i < articleURLs.size(); i++)
			{
				for(int z = 0; z < Article.RecognizedSites.length; z++)
				{
					if(articleURLs.get(i).contains((Article.RecognizedSites[z])))
					{
						System.out.println(articleURLs.get(i));
						System.out.println(Article.RecognizedSites[z]);
						Articles.add(new Article(articleURLs.get(i), Weight));
					}
				}
			}
		}
		catch(IOException except)
		{
			System.out.println("Could not fetch article: " + except.getStackTrace());
		}
	}
	public ArrayList<Point> ObtainDailyArticleRatings(ArrayList<Double> StockChanges)
	{
		ArrayList<Point> ArticleRatingsToStockChanges = new ArrayList<Point>();
		//Add one more month to whatever formatting you do since months start from 0
		Calendar StockChangeDates = Calendar.getInstance();
		StockChangeDates.add(Calendar.YEAR, -1);
		for(int i = 0; i < StockChanges.size(); i++)
		{
			ArrayList<Article> DailyArticles = new ArrayList<Article>();
			String CurrDate = DateFormatter.YieldDate(StockChangeDates.get(Calendar.DAY_OF_MONTH), StockChangeDates.get(Calendar.MONTH) + 1, StockChangeDates.get(Calendar.YEAR));
			for(Article artic : Articles)
			{
				if(artic.GetParsedDate().equals(CurrDate))
				{
					DailyArticles.add(artic);
				}
			}
			double AggregatedValue = 0;
			for(Article article : DailyArticles)
			{
				AggregatedValue += article.ReturnNewsValue();
				System.out.println(AggregatedValue);
			}
			ArticleRatingsToStockChanges.add(new Point(AggregatedValue / DailyArticles.size(), StockChanges.get(i)));
			StockChangeDates.add(Calendar.DAY_OF_YEAR, 1);
		}
		return ArticleRatingsToStockChanges;
	}
	News(String _CompanyTradingName, double _Weight)
	{
		Weight = _Weight;
		CompanyTradingName = _CompanyTradingName;
		StorageFile = "LOCALSTORAGE_" + CompanyTradingName + ".txt";
		FManager = new FileManager(StorageFile);
		GenerateYearArticles();
		for(Article art : Articles)
		{
			System.out.println(art.GetTitle());
			System.out.println(art.GetAuthor());
			System.out.println(art.GetPublicationDate());
			System.out.println(art.GetContents());
		}
	}
}