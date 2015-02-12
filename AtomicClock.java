import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class AtomicClock {

    // Initialize variables
    // All variables are updated every second
    private static int year, month, date, ampm, hour, hourOfDay, minute, second;
    private static String secondString, minuteString, hourString;
    private GregorianCalendar cal;
    private String labelText;
    private JFrame frame;
    private JLabel text;

    /**
     * Creates objects and initializes JFrame
     */
    public AtomicClock() {

        // Create objects & Initialize Variables
        cal = new GregorianCalendar();
        labelText = updatedInternetTime();
        setValues(cal);

        // Create JFrame
        frame = new JFrame("Atomic Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(250, 100);
        frame.setLocation(600, 300);

        // Create JLabel & set label on frame
        text = new JLabel(labelText, SwingConstants.CENTER);
        frame.getContentPane().add(text);
    }

    /**
     * Sets frame visible and begins updating time.
     */
    public void run() {
        frame.setVisible(true);

        // Count makes the time update itself every 60 seconds to the server
        int count = 0;
        while (frame.isVisible()) {
            // Waits one second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Every 60 seconds, incrementing clock is updated based on internet
            // time
            if (count == 10) {
                labelText = updatedInternetTime();
                setValues(cal);
                // System.out.println("updated");
                count = 0;
            } else { // else increment incrementing calendar by 1 second
                cal.add(Calendar.SECOND, 1);
                incrementValues(cal);
                labelText = hourString + ":" + minuteString + ":"
                        + secondString + " " + AtomicClock.ampm(ampm) + "    "
                        + AtomicClock.checkMonth(month) + " " + date + ", "
                        + year;
            }

            // Display new time on JFrame
            text.setText(labelText);
            frame.getContentPane().add(text);
            count++;
        }
    }

    /**
     * Decides what the month is based on its integer
     * given by the GregorianCalendar object.
     *
     * @param monthx Integer representing month (0-11)
     * @return The name of the month represented
     */
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
        default:
            return "Invalid month integer parsed";
        }
    }

    /**
     * Uses a number from GregorianCalendar object to determine
     * if the time is AM or PM
     *
     * @param x The number representing AM or PM
     * @return "AM" or "PM"
     */
    public static String ampm(int x) {
        if (x == 0) {
            return "AM";
        }
        return "PM";
    }

    /**
     * Increments values of current time by one second
     * Does not check the system time.
     * Note that one second is added to the calendar's
     * existing time every second when run() is called.
     * @param x The GregorianCalendar whose data is changed.
     */
    public void incrementValues(GregorianCalendar x) {
        year = x.get(Calendar.YEAR);
        month = x.get(Calendar.MONTH);
        date = x.get(Calendar.DATE);
        hourOfDay = x.get(Calendar.HOUR_OF_DAY);
        minute = x.get(Calendar.MINUTE);
        second = x.get(Calendar.SECOND);
        ampm = x.get(Calendar.AM_PM);

        // Displays hours in 12 hour format
        if (hourOfDay >= 13) {
            hour = hourOfDay - 12;
        } else {
            hour = hourOfDay;

            // If value is less than 10, zero is added in front of number
            if (second < 10) {
                secondString = "0" + second;
            } else {
                secondString = "" + second;
            }

            if (minute < 10) {
                minuteString = "0" + minute;
            } else {
                minuteString = "" + minute;
            }

            if (hour < 10) {
                hourString = "0" + hour;
            } else {
                hourString = "" + hour;
            }
        }
    }

    /**
     * Sets values of incrementing calendar time to the synced time
     * @param x The GregorianCalendar object holding the current time
     */
    public void setValues(GregorianCalendar x) {
        // Resets values to values based on system time
        x.set(year, month, date, hourOfDay, minute, second);

        // Displays hours in 12 hour format
        if (hourOfDay >= 13) {
            hour = hourOfDay - 12;
        } else {
            hour = hourOfDay;
        }

        // Adds 0 in front of number if less than 10
        if (second < 10) {
            secondString = "0" + second;
        } else {
            secondString = "" + second;
        }

        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = "" + minute;
        }

        if (hour < 10) {
            hourString = "0" + hour;
        } else {
            hourString = "" + hour;
        }
    }

    /**
     * Uses premade class to return the atomic time from NIST over the web.
     * @return A pretty string containing the current date and time
     */
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
        // Subtracts one hour from hourOfDay since
        // NIST time server is one hour ahead.
        // Subtracts one from month num because
        // GregorianCalendar uses months starting at zero.
        year = Integer.parseInt(internetTime.substring(15));
        month = Integer.parseInt(internetTime.substring(9, 11)) - 1;
        date = Integer.parseInt(internetTime.substring(12, 14));
        hourOfDay = Integer.parseInt(internetTime.substring(0, 2)) - 1;
        // NIST clock runs on 24 hours
        if (hourOfDay < 0) {
            hourOfDay = 23;
            date -= 1;
        }

        minute = Integer.parseInt(internetTime.substring(3, 5));
        second = Integer.parseInt(internetTime.substring(6, 8));

        if (hourOfDay < 12) {
            ampm = 0;
        } else {
            ampm = 1;
        }

        // Displays hours in 12 hour format
        if (hourOfDay >= 13) {
            hour = hourOfDay - 12;
        } else {
            hour = hourOfDay;
        }

        // Adds 0 in front of number if less than 10
        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = "" + minute;
        }

        if (second < 10) {
            secondString = "0" + second;
        } else {
            secondString = "" + second;
        }

        if (hour < 10) {
            hourString = "0" + hour;
        } else {
            hourString = "" + hour;
        }

        // String for label
        return hourString + ":" + minuteString + ":" + secondString + " "
                + AtomicClock.ampm(ampm) + "    "
                + AtomicClock.checkMonth(month) + " " + date + ", " + year;
    }

    /**
     * Creates and runs an instance of the atomic clock.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        AtomicClock clock = new AtomicClock();
        clock.run();
    }
}
