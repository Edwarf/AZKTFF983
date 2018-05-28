import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
//To schedule updates for intervals, create a class with a void run that extends from TimerTask
public class CompanyManager
{
	ArrayList<Company> Companies;
	int UpdateInterval;
	class Update extends TimerTask
	{
		//Update Code Here
		@Override 
		public void run()
		{
			
		}
	}
	void InitialDaySetup()
	{
		
	}
	//This is constantly running. When a new date occurs, we'll call InitialDaySetup
	void DayLoop()
	{
		Timer DayTimer = new Timer();
		DayTimer.scheduleAtFixedRate(new Update(), 0, UpdateInterval);
	}
	void MainLoop()
	{
		int OldDayOfMonth = Calendar.DAY_OF_MONTH;
		//This is on it's own thread, so everything should work out
		DayLoop();
		while(true)
		{
			if(Calendar.DAY_OF_MONTH != OldDayOfMonth)
			{
				InitialDaySetup();
				OldDayOfMonth = Calendar.DAY_OF_MONTH;
			}
		}
	}
	CompanyManager(int _UpdateInterval, ArrayList<Company> _Companies)
	{
		this.UpdateInterval = _UpdateInterval;
		Companies = _Companies;
	}
}
//Idea: model daytime importance weight as parabolic function
