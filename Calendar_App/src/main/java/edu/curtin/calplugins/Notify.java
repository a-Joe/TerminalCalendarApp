package edu.curtin.calplugins;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import edu.curtin.calendarApp.CalendarPlugin;
import edu.curtin.calendarApp.Event;
import edu.curtin.calendarApp.calendarControllerApi;

import java.time.LocalDateTime;

public class Notify implements CalendarPlugin {

    private ScheduledExecutorService scheduler;

    @Override
    public void start(calendarControllerApi api, Map<String, String> arguments) {
        String eventTitle = arguments.get("text");
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // task runs 30 seconds
        scheduler.scheduleAtFixedRate(() -> checkAndNotify(api, eventTitle), 0, 30, TimeUnit.SECONDS);
    }

    private void checkAndNotify(calendarControllerApi api, String eventTitle) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Event event = api.checkEventBegan(eventTitle);

        if (event != null && event.getStartDateTime().isBefore(currentDateTime)) {
            String eventMessage = 
            "----------------\n"
            + "EVENT OCCURED\n"
            + "Title: " + event.getTitle() + "\n"
            + "Duration: " + event.getDuration() + "\n"
            + "All Day Event? : " + event.getIsallDayEvent() + "\n"
            + "Started: " + event.getStartDateTime() + "\n"
            + "----------------";

            JOptionPane.showMessageDialog(null, eventMessage);
            stop();
        }
    }

    // Make sure to properly shut down the scheduler when the plugin is stopped
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
