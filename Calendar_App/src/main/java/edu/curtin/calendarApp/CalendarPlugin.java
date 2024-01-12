package edu.curtin.calendarApp;

import java.util.Map;

public interface CalendarPlugin {
    void start(calendarControllerApi api, Map<String, String> arguments);
}

