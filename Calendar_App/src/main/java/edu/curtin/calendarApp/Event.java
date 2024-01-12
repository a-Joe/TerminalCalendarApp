package edu.curtin.calendarApp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private List<Event> events = new ArrayList<>();
    private String title;
    private LocalDateTime startDateTime;
    private int duration;
    private boolean isAllDayEvent;

    // Constructor for timed events
    public Event(String date, String title, String time, String duration) {
        this.title = title;
        this.startDateTime = LocalDateTime.parse(date + "T" + time);
        this.duration = Integer.parseInt(duration);
        this.isAllDayEvent = false;
    }

    // Constructor for all-day events
    public Event(String date, String title) {
        this.title = title;
        this.startDateTime = LocalDate.parse(date).atStartOfDay();
        this.duration = 0; //
        this.isAllDayEvent = true;

        // System.out.println("ALL-DAY EVENT CREATED! ");
    }

 

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public int getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }
        public List<Event> getEvents() {
        return events;
    }

    public boolean getIsallDayEvent(){
        return isAllDayEvent;
    }

    

}
