//From http://www.rgagnon.com/javadetails/java-0589.html
//Uses method to return a gregorianCalendar
//gregorianCalendar is changed into a simple date format in atomicClock class and read as a string
import java.io.*;
import java.util.*;
import java.net.*;

public final class SyncTime {
  // NIST, Boulder, Colorado  (time-a.timefreq.bldrdoc.gov)
 public static final String ATOMICTIME_SERVER="http://132.163.4.101:13";
 
  public final static GregorianCalendar getAtomicTime() throws IOException{
    BufferedReader in = null;

    try {
       URLConnection conn = new URL(ATOMICTIME_SERVER).openConnection();
       in = new BufferedReader
         (new InputStreamReader(conn.getInputStream()));

       String atomicTime;
       while (true) {
          if ( (atomicTime = in.readLine()).indexOf("*") > -1) {
             break;
          }
       }
       String[] fields = atomicTime.split(" ");
       GregorianCalendar calendar = new GregorianCalendar();

       String[] date = fields[1].split("-");
       calendar.set(Calendar.YEAR, 2000 +  Integer.parseInt(date[0]));
       calendar.set(Calendar.MONTH, Integer.parseInt(date[1])-1);
       calendar.set(Calendar.DATE, Integer.parseInt(date[2]));

       // deals with the timezone and the daylight-saving-time
       TimeZone tz = TimeZone.getDefault();
       int gmt = (tz.getRawOffset() + tz.getDSTSavings()) / 3600000;

       String[] time = fields[2].split(":");
       calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]) + gmt);
       calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
       calendar.set(Calendar.SECOND, Integer.parseInt(time[2]));
       return calendar;
    }
    catch (IOException e){
       throw e;
    }
    finally {
       if (in != null) {
         in.close();
       }
    }
  }
}
