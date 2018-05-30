import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
	//either a bad weight or a good weight. 
	double Value;
	//might want to implement individual article weights based on author, article, source, etc. later. 
	double Weight;
	//Used to deal with google dates
	public static double toJulian(int[] ymd) 
	{
		 	int JGREG= 15 + 31*(10+12*1582);
		   int year=ymd[0];
		   int month=ymd[1]; // jan=1, feb=2,...
		   int day=ymd[2];
		   int julianYear = year;
		   if (year < 0) julianYear++;
		   int julianMonth = month;
		   if (month > 2) {
		     julianMonth++;
		   }
		   else {
		     julianYear--;
		     julianMonth += 13;
		   }

		   double julian = (java.lang.Math.floor(365.25 * julianYear)
		        + java.lang.Math.floor(30.6001*julianMonth) + day + 1720995.0);
		   if (day + 31 * (month + 12 * year) >= JGREG) {
		     // change over to Gregorian calendar
		     int ja = (int)(0.01 * julianYear);
		     julian += 2 - ja + (0.25 * ja);
		   }
		   return java.lang.Math.floor(julian);
		 }

	//This will parse google news on a company
	private void AnalyzeArticles()
	{ 
		double AggregatedValue = 0;
		for(Article article : Articles)
		{
			AggregatedValue += article.ReturnNewsValue();
		}
		Value = (AggregatedValue / Articles.size()) * Weight; 
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
			for(int x = 0; x <= 365; x++)
			{
				GregorianCalendar NewCal = new GregorianCalendar(2015, 4, 22);
				//In this case, x is an integer from 0 to 365
				NewCal.add(Calendar.DAY_OF_MONTH, -50);
				String GoogleRoot = "https://www.google.com/search?q=";
				String SearchBounds = "&num=10";
				String Month = Integer.toString(NewCal.get(Calendar.MONTH)).length() < 2 ? "0" 
				+ Integer.toString(NewCal.get(Calendar.MONTH)) : Integer.toString(NewCal.get(Calendar.MONTH));
				
				String Day = Integer.toString(NewCal.get(Calendar.DAY_OF_MONTH)).length() < 2 ? "0" 
				+ Integer.toString(NewCal.get(Calendar.DAY_OF_MONTH)) : Integer.toString(NewCal.get(Calendar.DAY_OF_MONTH));
				
				String Date = Day + "/" + Month + "/" + Integer.toString(NewCal.get(Calendar.YEAR));
				String DateBounds = "&tbs=" + URLEncoder.encode("cdr:1,cd_min:" + Date + ",cd_max:" + Date, "UTF-8");
				String SearchSection = "&tbm=nws";
				//Connect with query, encode, etc. 
				System.out.println(GoogleRoot + URLEncoder.encode(CompanyTradingName, "UTF-8")  + SearchSection + SearchBounds + DateBounds);
				Elements URLs = Jsoup.connect(GoogleRoot + URLEncoder.encode(CompanyTradingName, "UTF-8")  + SearchSection + SearchBounds + DateBounds).userAgent("Independent").get().select(".g .r>a");
				//Iterate through to find valid articles			
				for(int i = 0; i < URLs.size(); i++ )
				{	
					String url = URLs.get(i).absUrl("href"); 
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
	
	News(String _CompanyTradingName, double _Weight)
	{
		Weight = _Weight;
		CompanyTradingName = _CompanyTradingName;
		GenerateArticles();
		//GenerateYearArticles();
		for(Article art : Articles)
		{
			System.out.println(art.GetTitle());
			System.out.println(art.GetAuthor());
			System.out.println(art.GetPublicationDate());
			System.out.println(art.GetContents());
		}
	}
}