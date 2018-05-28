import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
public class News
{
	String CompanyTradingName;
	ArrayList<Article> Articles;
	ArrayList<Article> DailyArticle;
	//This will parse google news on a company 
	void GenerateArticles(int DayLimit)
	{
		Articles = new ArrayList<Article>();
		ArrayList<String> articleURLs = new ArrayList<String>();
		Document searchQuery;
		try
		{
			for(int x = 0; x < 50; x+=10)
			{
				String GoogleRoot = "https://www.google.com/search?q=";
				String SearchBounds = "&start=" + Integer.toString(x);
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
			}
			for(int i = 0; i < articleURLs.size(); i++)
			{
				for(int z = 0; z < Article.RecognizedSites.length; z++)
				{
					if(articleURLs.get(i).contains(Article.RecognizedSites[z]));
					{
						Articles.add(new Article(articleURLs.get(i)));
						break;
					}
				}
			}
		}
		catch(IOException except)
		{
			System.out.println("Could not fetch article: " + except.getStackTrace());
		}
	}
	News(String _CompanyTradingName)
	{
		CompanyTradingName = _CompanyTradingName;
		GenerateArticles(1);
		for(Article art : Articles)
		{
			System.out.println(art.GetTitle());
			System.out.println(art.GetAuthor());
			System.out.println(art.GetPublicationDate());
			System.out.println(art.GetContents());
		}
	}
}