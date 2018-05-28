import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;
public class Article
{
		public static String[] RecognizedSites = {"marketwatch.com, seekingalpha.com"};
		protected String URL;
		protected String title;
		protected String author;
		protected String publicationDate;
		protected String contents;
		void ParseWebArticle(Document article)
		{
			//Different sites have different formats, so we'll have to switch our stuff based on the formats
			//MarketWatch stuff
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
			}
		}
		Article(String _URL)
		{
			URL = _URL;
			Document article;
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
			}
			
		}
		String GetURL()
		{
		 return URL;
		}
		String GetAuthor()
		{
			return author;
		}
		String GetPublicationDate()
		{
			return publicationDate;
		}
		String GetContents()
		{
			return contents;
		}
		String GetTitle()
		{
			return title;
		}
}