import java.io.IOException;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;
public class Article
{
		double Weight;
		//Make these loaded from text files eventually
		private static String[] GoodNewsIndicators = {"upgraded", "success", "beat the market", "good returns", "good buy", "appealing", "fast growing", "fast growth", 
				"impressive", "grew", "advanced"};
		private static String[] BadNewsIndicators = {"downgraded", "loss", "underperforming", "bad returns", "bad buy", "selloff", "sell now", "slow growing", "slow growth",
				"disappointing", "step backwards", "setback"};
		public static String[] RecognizedSites = {"marketwatch.com", "seekingalpha.com"};
		protected String URL = "";
		protected String title = "";
		protected String author = "";
		protected String publicationDate = "";
		//The date at which it was identified on google. 
		protected String parsedDate = "";
		protected String contents = "";
		protected boolean FoundStatus;
		void ParseWebArticle(Document article)
		{
			//Different sites have different formats, so we'll have to switch our stuff based on the formats
			//MarketWatch stuff
			FoundStatus = true;
			if(URL.contains("marketwatch.com"))
			{
				author = article.getElementById("author-bylines").getElementsByTag("a").get(0).text();
				publicationDate = article.getElementById("published-timestamp").getElementsByTag("span").text();
				contents = article.getElementById("article-body").getElementsByTag("p").text();
			}
			else if(URL.contains("seekingalpha.com"))
			{
				Elements RelevantInfo = article.getElementsByTag("meta");
				for(int i = 0; i < RelevantInfo.size(); i++)
				{
					if(RelevantInfo.get(i).attr("name").contains( "author"))
					{
						author = RelevantInfo.get(i).attr("content");
					}
					else if(RelevantInfo.get(i).attr("name").contains("last-modified"))
					{
						publicationDate = RelevantInfo.get(i).attr("content");
					}
				}
				contents = article.getElementsByAttributeValue("itemprop", "articleBody").text();
			}
			//WSJ Stuff
			else
			{
				System.out.println("Website not found for article");
				author = "Not Found";
				publicationDate = "Not Found";
				contents = "Not Found";
				title = "Not Found";
				FoundStatus = false;
			}
		}
		Article(String _URL, double _Weight)
		{
			Weight = _Weight;
			URL = _URL;
			Document article;
			parsedDate = "NULL";
			//Try to connect, fill article.
			try
			{
				article = Jsoup.connect(URL).get();
				title = article.title();
				ParseWebArticle(article);
			}
			catch(IOException except)
			{
				System.out.println("Could not fetch article: " + except.getStackTrace());
				FoundStatus = false;
			}
		}
		Article(String _URL, double _Weight, String _parsedDate)
		{			
			this(_URL, _Weight);
			parsedDate = _parsedDate;
		}
		Article(String _URL, String _title, String _author, String _PublicationDate, String _parsedDate, String _contents)
		{
			URL = _URL;
			title = _title;
			author = _author;
			publicationDate = _PublicationDate;
			parsedDate = _parsedDate;
			contents = _contents;
		}
		private double CountGoodNews()
		{ 
			double GoodThingsCounter = 0;
			String[] WordContent = contents.split(" ");
			for(String Word: WordContent)
			{			
				for(String Indicator : GoodNewsIndicators)
				{
				   if(Indicator.contains(Word))
				   {
					   GoodThingsCounter++;
					   break;
				   }
				} 
			}
			return GoodThingsCounter;
		}
		private double CountBadNews()
		{ 
			double BadThingsCounter = 0;
			String[] WordContent = contents.split(" ");
			for(String Word: WordContent)
			{
				for(String Indicator : BadNewsIndicators)
				{
				   if(Indicator.contains(Word))
				   {
					   BadThingsCounter++;
					   break;
				   }
				} 
			}
			return BadThingsCounter;
		}
		//Average of good/bad news
		public double ReturnNewsValue()
		{
			//System.out.println(((CountGoodNews() - CountBadNews()) / (CountGoodNews() + CountBadNews())) * Weight);
			return ((CountGoodNews() - CountBadNews()) / (CountGoodNews() + CountBadNews()));
		}
		public String GetURL()
		{
			return URL;
		}
		public String GetAuthor()
		{
			return author;
		}
		public String GetPublicationDate()
		{
			return publicationDate;
		}
		public String GetParsedDate()
		{
			return parsedDate;
		}
		public String GetContents()
		{
			return contents;
		}
		public String GetTitle()
		{
			return title;
		}
}