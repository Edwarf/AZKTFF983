import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class DateFormatter 
{
	public static String YieldDate(int Day, int Month, int Year)
	{
		//In this case, x is an integer from 0 to 365
		String Monthf = Integer.toString(Month).length() < 2 ? "0" 
		+ Integer.toString(Month) : Integer.toString(Month);

		String Dayf = Integer.toString(Day).length() < 2 ? "0" 
		+ Integer.toString(Day) : Integer.toString(Day);
		String Date = Monthf + "/" + Dayf + "/" + Integer.toString(Year);
		return Date;
	}
	//This function is sick and im a genius.
	public static ArrayList<String> SortDates(ArrayList<String> previousDates)
	{
		ArrayList<String> returner = new ArrayList<String>();
		ArrayList<Integer> DateNumbers = new ArrayList<Integer>();
		//Assign different magnitudes of value for day, month, and year. in this format, a higher year is valued most, then month, then day. always. 
		for(String currdate : previousDates)
		{
			DateNumbers.add(Integer.parseInt(currdate.split("/")[2]) * 10000 + Integer.parseInt(currdate.split("/")[0]) * 100 + Integer.parseInt(currdate.split("/")[1]));
		}
		Collections.sort(DateNumbers);
		for(int i = 0; i < DateNumbers.size(); i++)
		{
			for(String currdate : previousDates)
			{
				if(Integer.parseInt(currdate.split("/")[2]) * 10000 + Integer.parseInt(currdate.split("/")[0]) * 100 + Integer.parseInt(currdate.split("/")[1]) == DateNumbers.get(i))
				{
					returner.add(currdate);
				}
			}
		}
		return returner;
	}
	//Make sure that Date1 is before Date2
	public static int FindDayDifferenceBetweenDates(String Date1, String Date2)
	{
		int DAYDIFF = 0;
		Calendar D1C = new GregorianCalendar(Integer.parseInt(Date1.split("/")[2]), Integer.parseInt(Date1.split("/")[0]), Integer.parseInt(Date1.split("/")[1]));
		Calendar D2C = new GregorianCalendar(Integer.parseInt(Date2.split("/")[2]), Integer.parseInt(Date2.split("/")[0]), Integer.parseInt(Date2.split("/")[1]));
		while(D1C.get(Calendar.YEAR) > D2C.get(Calendar.YEAR) || D1C.get(Calendar.MONTH) > D2C.get(Calendar.MONTH) || D1C.get(Calendar.DAY_OF_MONTH) > D2C.get(Calendar.DAY_OF_MONTH))
		{
			DAYDIFF++;
			D1C.add(Calendar.DAY_OF_YEAR, -1);
		}
		return DAYDIFF;
	}
}
