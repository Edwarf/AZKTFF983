import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
public class FileManager 
{
	String SaveLocation;
	ArrayList<Article> Articles;
	FileManager(String _SaveLocation)
	{ 
		Articles = new ArrayList<Article>();
		SaveLocation = _SaveLocation;
	}
	public void SaveArticle(Article article) 
	{
		Articles.add(article);
		try 
		{
			FileWriter ofstream = new FileWriter(SaveLocation);
			BufferedWriter bstream = new BufferedWriter(ofstream);
			bstream.write(article.GetURL());
			bstream.newLine();
			bstream.write(article.GetTitle());
			bstream.newLine();
			bstream.write(article.GetAuthor());
			bstream.newLine();
			bstream.write(article.GetPublicationDate());
			bstream.newLine();
			bstream.write(article.GetParsedDate());
			bstream.newLine();
			bstream.write(article.GetContents());
			bstream.newLine();
			ofstream.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void SaveArticles(ArrayList<Article> articles)
	{
		Articles.addAll(articles);
		try
		{
			FileWriter ofstream = new FileWriter(SaveLocation);
			BufferedWriter bstream = new BufferedWriter(ofstream);
			for(Article article : articles)
			{
				bstream.write(article.GetURL());
				bstream.newLine();
				bstream.write(article.GetTitle());
				bstream.newLine();
				bstream.write(article.GetAuthor());
				bstream.newLine();
				bstream.write(article.GetPublicationDate());
				bstream.newLine();
				bstream.write(article.GetParsedDate());
				bstream.newLine();
				bstream.write(article.GetContents());
				bstream.newLine();
			}
			ofstream.close();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//Load through dates
	public ArrayList<Article> FetchArticlesByParseDate(String ParsedDate)
	{
		ArrayList<Article> arr = new ArrayList<Article>();
		for(int i = 0; i < Articles.size(); i++)
		{
			if(Articles.get(i).GetParsedDate() == ParsedDate)
			{
				arr.add(Articles.get(i));
			}
		}
		return arr;
	}
	public ArrayList<Article> LoadArticles()
	{
		
		try
		{
			File newfile = new File(SaveLocation);
			newfile.createNewFile();		
			BufferedReader breader = new BufferedReader(new FileReader(newfile));
			String currline = "";
			ArrayList<String> lines = new ArrayList<String>();
			while((currline = breader.readLine()) != null)
			{
				lines.add(currline);
			}
			breader.close();
			for(int i = 0; i < lines.size(); i+=6)
			{
				Articles.add(new Article(lines.get(i), lines.get(i + 1),lines.get(i + 2), lines.get(i + 3),
				lines.get(i + 4), lines.get(i + 5)));
			}
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Articles;
	}
	public ArrayList<Article> RemoveArticlesByParsedDate(String Date)
	{
		ArrayList<Article> NewList = new ArrayList<Article>();
		for(int i = 0; i < Articles.size(); i++)
		{
			if(Articles.get(i).GetParsedDate() != Date)
			{
				NewList.add(Articles.get(i));
			}
		}
		Articles = NewList;
		return Articles;
	}
	public String GetLatestDate()
	{
		ArrayList<String> AllDates = new ArrayList<String>();
		if(Articles.size() == 0)
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -1);
			System.out.println("LOLOL" + DateFormatter.YieldDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
			return DateFormatter.YieldDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
		}
		else
		{
			for(int i = 0; i < Articles.size(); i++)
			{
				String currdate = Articles.get(i).GetParsedDate();
				AllDates.add(currdate);
			}
			System.out.println(DateFormatter.SortDates(AllDates).get(0));
			return DateFormatter.SortDates(AllDates).get(0);
		}
	}
	public String GetEarliestDate()
	{
		ArrayList<String> AllDates = new ArrayList<String>();
		ArrayList<Integer> AllYears = new ArrayList<Integer>();
		ArrayList<Integer> AllMonths = new ArrayList<Integer>();
		ArrayList<Integer> AllDays = new ArrayList<Integer>();
		for(int i = 0; i < Articles.size(); i++)
		{
			String currdate = Articles.get(i).GetParsedDate();
			AllDates.add(currdate);
		}
		return DateFormatter.SortDates(AllDates).get(AllDates.size() - 1);
	}
}
