package edu.curtin.calendarApp;


import java.text.Normalizer;


import java.io.PrintStream;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import edu.curtin.terminalgrid.TerminalGrid;

public class CalendarDisplay {
    private LocalDateTime currentDate;
    private TerminalGrid terminalGrid;

    private ArrayList<String> daysOfWeekDisplay;
    private ArrayList<String> hoursOfDayDisplay;
    private ArrayList<List<String>> eventsDisplay;

    // Internationalisation  
    private ResourceBundle bundle;
    private Locale currentLocale;

    // need 7 columns each representing a day
    // Rows will represent the time of day (hours).
    // Seperate row at bottom for all-day events.

    public CalendarDisplay() {
        
        // Set the default locale
        currentLocale = Locale.forLanguageTag("en");
        
        // Initialize the resource bundle with the default locale
        try {
            bundle = ResourceBundle.getBundle("bundle", currentLocale);
        } catch (MissingResourceException e) {
            System.err.println("Resource bundle not found: " + e);
            return;
        }
        
        currentDate = LocalDateTime.now();

        terminalGrid = new TerminalGrid(new PrintStream(System.out, true, java.nio.charset.Charset.forName("UTF-8")), 200);
        setupGrid();
        //bundle.getString("currentDay"); // Note: The result of this call is not used. You might want to use it somehow.
    }


    private void setupGrid() {

        
        
        hoursOfDayDisplay = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            // uses translated hours for locale
            String hourKey = "hour" + i;
            String localizedHour = bundle.getString(hourKey);
            
            hoursOfDayDisplay.add(localizedHour);
        }

        daysOfWeekDisplay = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime nextDate = currentDate.plusDays(i);
            DayOfWeek dayOfWeek = nextDate.getDayOfWeek();
            // get translated locale by  day
            String day = getLocalisedDayName(dayOfWeek);
            daysOfWeekDisplay.add(day);
        }
        
        

        eventsDisplay = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                row.add(""); // each cell starts empty
            }
            eventsDisplay.add(row);
        }
        for (Event event : events) {
            long daysBetween = ChronoUnit.DAYS.between(currentDate.toLocalDate(), event.getStartDateTime().toLocalDate());
            // If event is within the displayed week / grid
            if (daysBetween >= 0 && daysBetween < 7) { 
                int dayIndex = (int) daysBetween;
    
                if (event.getIsallDayEvent()) {
                    eventsDisplay.get(24).set(dayIndex, Normalizer.normalize(event.getTitle(), Normalizer.Form.NFC));
                } else {
                    String eventInfo = event.getTitle() + "\n" + event.getStartDateTime().toLocalTime() + "\n" + event.getDuration() + " Minutes";
                    eventsDisplay.get(event.getStartDateTime().getHour()).set(dayIndex, eventInfo);
                }
            }
        }

    }

    // gets the locale for each day of the week.
    private String getLocalisedDayName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return bundle.getString("monday");
            case TUESDAY:
                return bundle.getString("tuesday");
            case WEDNESDAY:
                return bundle.getString("wednesday");
            case THURSDAY:
                return bundle.getString("thursday");
            case FRIDAY:
                return bundle.getString("friday");
            case SATURDAY:
                return bundle.getString("saturday");
            case SUNDAY:
                return bundle.getString("sunday");
            default:
                return dayOfWeek.toString();
        }
    }

    // after setting up grid, 
    // utilises the terminalGrid class to display a calendar
    public void display() {

        setupGrid();
        terminalGrid.print(eventsDisplay, hoursOfDayDisplay, daysOfWeekDisplay);
        System.out.println(bundle.getString("date") +" : "+ currentDate.toLocalDate()); // use localDate
        System.out.println(bundle.getString("localeChange"));

    }

    // add event obj to events list
    public void addEvent(Event event) {
        events.add(event);
    }

    // reset to today by getting systems LocalDateTime
    public void returnToToday() {
        currentDate = LocalDateTime.now();
    }

    // Move the calendar forward x days
    public void moveForward(int numberOfDays) {
        currentDate = currentDate.plusDays(numberOfDays);
    }

    // Move the calendar back x days
    public void moveBack(int numberOfDays) {
        currentDate = currentDate.minusDays(numberOfDays);
    }


    private void findEvent(String searchTerm){
        // sort events by start DateTime
        events.sort((event1, event2) -> event1.getStartDateTime().compareTo(event2.getStartDateTime()));
        for (Event event : events) {
            // we only want to search from current date. So check if event is after current date
            // AND only search up to 1 year into the future. So continue if event is before current date + 1 year
            if (event.getStartDateTime().isAfter(currentDate) && event.getStartDateTime().isBefore(currentDate.plusYears(1))){ 

                // check if event title contains the search term (both converted to lowercase)
                if(event.getTitle().toLowerCase().contains(searchTerm.toLowerCase())){
                    currentDate = event.getStartDateTime();
                    return;
                }

            }
        }
        System.out.println(bundle.getString("noEventMatch"));

    }
    private ArrayList<Event> events = new ArrayList<>();
    

    // Handles all the user inputs,     
    public void handleUserInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) { // loop to continuously get user inputs
                System.out.println(bundle.getString("enterCommand"));

                String userInput = Normalizer.normalize(scanner.nextLine().trim(), Normalizer.Form.NFC).toLowerCase();

                switch (userInput) {
                    case "+d":
                        moveForward(1);
                        break;
                    case "+w":
                        moveForward(7);
                        break;
                    case "+m":
                        moveForward(30); // assuming forward 1 month = 30 days
                        break;
                    case "+y":
                        moveForward(365);
                        break;
                    case "-d":
                        moveBack(1);
                        break;
                    case "-w":
                        moveBack(7);
                        break;
                    case "-m":
                        moveBack(30);
                        break;
                    case "-y":
                        moveBack(365);
                        break;
                    case "t":
                        returnToToday();
                        break;
                    case "-locale":
                        System.out.println(bundle.getString("enterLocaleCode"));
                   
                        userInput = scanner.nextLine().trim();
                        setLocale(userInput);
                        break;
                    case "-s":
                        System.out.println(bundle.getString("searchPrompt"));

                        userInput = scanner.nextLine().trim().toLowerCase();
                        System.out.println(bundle.getString("searchingEvent"));
                        findEvent(userInput);
                        break;
                    case "exit":
                        System.out.println(bundle.getString("exiting"));
                        return;
                    default:
                        System.out.println(bundle.getString("invalidInput"));
                }
                display();
            }
        }
    }

    public void setLocale(String languageTag) {
        try {
            currentLocale = Locale.forLanguageTag(languageTag);
            bundle = ResourceBundle.getBundle("bundle", currentLocale);

        } catch (Exception e) {
                    System.out.println(bundle.getString("invalidInput") + e);
        }
    }

    public Event searchEventBegan(String text) {
        String eventTitle;
        String searchTerm;
        text = text.toLowerCase();
        Event notifyEvent = null;
        for (Event event : events) {
            // Normalised strings for string operations
            eventTitle = Normalizer.normalize(event.getTitle(), Normalizer.Form.NFC).toLowerCase();
            searchTerm = Normalizer.normalize(text, Normalizer.Form.NFC).toLowerCase();

            if (eventTitle.contains(searchTerm)) {
                if(event.getStartDateTime().isBefore(LocalDateTime.now())){
                    notifyEvent = event;
                }
            }
        }
        return notifyEvent;
    }

}
