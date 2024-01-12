package edu.curtin.calendarApp;

import java.time.LocalDate;
import java.time.LocalTime;

// API methods
public class calendarController implements calendarControllerApi{
    CalendarDisplay calendar;


    public calendarController(CalendarDisplay calendar){
        this.calendar = calendar;
    }


    @Override
    public void addTimeOfDayEvent(LocalDate date, String title, LocalTime time, int duration) {
        Event event = new Event(date.toString(), title, time.toString(), String.valueOf(duration));
        calendar.addEvent(event);
    }

    @Override
    public void addAllDayEvent(LocalDate date, String title) {
        Event event = new Event(date.toString(), title);
        calendar.addEvent(event);
    }


    @Override
    public Event checkEventBegan(String text) {
        Event event = calendar.searchEventBegan(text);
        return event;
    }}
