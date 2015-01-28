import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.*;

public class atomicClock {
	// Initialize variables
	// All variables are updated every second
	static int year, month, date, ampm, hour, hour_of_day, minute, second;
	static String secondString, minuteString, hourString;

	public atomicClock() {
	}

	// Decides what the month string will be based on the month number from the
	// gregorianCalendar object
	public static String checkMonth(int monthx) {
		switch (monthx) {
		case 0:
			return "January";
		case 1:
			return "February";
		case 2:
			return "March";
		case 3:
			return "April";
		case 4:
			return "May";
		case 5:
			return "June";
		case 6:
			return "July";
		case 7:
			return "August";
		case 8:
			return "September";
		case 9:
			return "October";
		case 10:
			return "November";
		case 11:
			return "December";
		}
		return "";
	}

	// Uses number from gregorianCalendar object to determine if time is AM or
	// PM
	public static String ampm(int x) {
		if (x == 0)
			return "AM";
		else
			return "PM";
	}

	// Increments values by one second when calendar is not checking against
	// system time
	// (One second is added to calendar's existing time every second in the main
	// method)
	public void incrementValues(GregorianCalendar x) {
		year = x.get(Calendar.YEAR);
		month = x.get(Calendar.MONTH);
		date = x.get(Calendar.DATE);
		hour_of_day = x.get(Calendar.HOUR_OF_DAY);
		minute = x.get(Calendar.MINUTE);
		second = x.get(Calendar.SECOND);
		ampm = x.get(Calendar.AM_PM);

		// Displays hours in 12 hour format
		if (hour_of_day >= 13)
			hour = hour_of_day - 12;
		else
			hour = hour_of_day;

		// If value is less than 10, zero is added in front of number
		if (second < 10)
			secondString = "0" + second;
		else
			secondString = "" + second;
		if (minute < 10)
			minuteString = "0" + minute;
		else
			minuteString = "" + minute;
		if (hour < 10)
			hourString = "0" + hour;
		else
			hourString = "" + hour;
	}

	// Resets values of incrementing calendar based on synced time
	public void setValues(GregorianCalendar x) {
		// Resets values to values based on system time
		x.set(year, month, date, hour_of_day, minute, second);

		// Displays hours in 12 hour format
		if (hour_of_day >= 13)
			hour = hour_of_day - 12;
		else
			hour = hour_of_day;

		// Adds 0 in front of number if less than 10
		if (second < 10)
			secondString = "0" + second;
		else
			secondString = "" + second;
		if (minute < 10)
			minuteString = "0" + minute;
		else
			minuteString = "" + minute;
		if (hour < 10)
			hourString = "0" + hour;
		else
			hourString = "" + hour;
	}

	// Uses premade class to return colorado time from NIST
	// Subtracted one hour from hour_of_day since one hour ahead
	// Substracted one from month num because gregorianCalendar uses months
	// starting at 0
	public String updatedInternetTime() {
		// Calls current time from other class, and puts it into a string
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss MM dd yyyy");
		String internetTime = null;
		try {
			internetTime = sdf.format(SyncTime.getAtomicTime().getTime());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Sets time to all the variables
		year = Integer.parseInt(internetTime.substring(15));
		month = Integer.parseInt(internetTime.substring(9, 11)) - 1;
		date = Integer.parseInt(internetTime.substring(12, 14));
		hour_of_day = Integer.parseInt(internetTime.substring(0, 2)) - 1;
		// NIST clock runs on 24 hours
		if (hour_of_day < 0) {
			hour_of_day = 23;
			date -= 1;
		}

		minute = Integer.parseInt(internetTime.substring(3, 5));
		second = Integer.parseInt(internetTime.substring(6, 8));

		if (hour_of_day < 12)
			ampm = 0;
		else
			ampm = 1;

		// Displays hours in 12 hour format
		if (hour_of_day >= 13)
			hour = hour_of_day - 12;
		else
			hour = hour_of_day;

		// Adds 0 in front of number if less than 10
		if (minute < 10)
			minuteString = "0" + minute;
		else
			minuteString = "" + minute;
		if (second < 10)
			secondString = "0" + second;
		else
			secondString = "" + second;
		if (hour < 10)
			hourString = "0" + hour;
		else
			hourString = "" + hour;

		// String for label
		return hourString + ":" + minuteString + ":" + secondString + " "
				+ atomicClock.ampm(ampm) + "    "
				+ atomicClock.checkMonth(month) + " " + date + ", " + year;
	}

	public static void main(String[] args) {

		// Create objects & Initialize Variables
		atomicClock clock = new atomicClock();
		GregorianCalendar cal = new GregorianCalendar();
		String labelText = clock.updatedInternetTime();
		clock.setValues(cal);

		// Create JFrame
		JFrame frame = new JFrame("Atomic Clock");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(250, 100);
		frame.setLocation(600, 300);

		// Create JLabel & set label on frame
		JLabel text = new JLabel(labelText, SwingConstants.CENTER);
		frame.getContentPane().add(text);
		frame.setVisible(true);

		// Count makes the time update itself every 60 seconds to the server
		int count = 0;
		while (frame.isVisible() == true) {
			// Waits one second
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Every 60 seconds, incrementing clock is updated based on internet
			// time
			if (count == 10) {
				labelText = clock.updatedInternetTime();
				clock.setValues(cal);
				System.out.println("updated");
				count = 0;
			}

			// else increment incrementing calendar by 1 second
			else {
				cal.add(Calendar.SECOND, 1);
				clock.incrementValues(cal);
				labelText = hourString + ":" + minuteString + ":"
						+ secondString + " " + atomicClock.ampm(ampm) + "    "
						+ atomicClock.checkMonth(month) + " " + date + ", "
						+ year;
			}

			// Display new time on JFrame
			text.setText(labelText);
			frame.getContentPane().add(text);
			count++;
		}
	}
}
