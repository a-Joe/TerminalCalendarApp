package edu.curtin.calendarApp;

import java.time.LocalDate;
import java.time.LocalTime;

// API Interface for calendar
public interface calendarControllerApi {

    
    void addTimeOfDayEvent(LocalDate date, String title, LocalTime time, int duration);
    void addAllDayEvent(LocalDate date, String title);

    Event checkEventBegan(String text);

    
}
