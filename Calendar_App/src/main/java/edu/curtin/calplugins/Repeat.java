package edu.curtin.calplugins;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import edu.curtin.calendarApp.CalendarPlugin;
import edu.curtin.calendarApp.calendarControllerApi;

public class Repeat implements CalendarPlugin{

    @Override
    public void start(calendarControllerApi api, Map<String, String> arguments) {
        
        if(arguments.containsKey("startTime")){
            createTimeOfDayEvent(api, arguments);
        }else{
            createAllDayEvent(api, arguments);
        }
    }

    // create new All Day Event and repeat it every repeatFrequency days
    public void createAllDayEvent(calendarControllerApi api, Map<String, String> arguments){
        Long repeatFrequency = Long.valueOf(arguments.get("repeat")); // number of days between event
        String title = arguments.get("title");                          
        LocalDate startDate = LocalDate.parse(arguments.get("startDate"));

        for (int i = 0; i < 365/repeatFrequency; i++) {
            api.addAllDayEvent(startDate,title);
            startDate = startDate.plusDays(repeatFrequency);
        }

    }
    // create new Time Of Day Event and repeat it every repeatFrequency days
    public void createTimeOfDayEvent(calendarControllerApi api, Map<String, String> arguments){
        Long repeatFrequency = Long.valueOf(arguments.get("repeat")); // number of days between subsequent events
        String title = arguments.get("title");                             
        LocalDate startDate = LocalDate.parse(arguments.get("startDate")); 
        LocalTime startTime = LocalTime.parse(arguments.get("startTime")); 
        int duration = Integer.parseInt(arguments.get("duration"));        

        for (int i = 0; i < 365/repeatFrequency; i++) {
            api.addTimeOfDayEvent(startDate,title, startTime, duration);
            startDate = startDate.plusDays(repeatFrequency);
        }

    }
    
}
