package edu.curtin.calendarApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.curtin.parser.CalenderParser;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        CalendarDisplay calendar = new CalendarDisplay();
        ArrayList<Event> eventList = new ArrayList<>();
        CalenderParser parser;
        calendarControllerApi api;
        ArrayList<PluginInfo> pluginList = new ArrayList<>();

        String filePath = args[0];
        Charset charset = determineUTF(filePath);

        try (FileInputStream fileInputStream = new FileInputStream(new File(filePath))) {
            parser = new CalenderParser(new InputStreamReader(fileInputStream, charset));
            parser.File();
            eventList = parser.getEvents(); 
            pluginList = parser.getPlugins();

            for (Event event : eventList) {
                calendar.addEvent(event);
            }

            api = new calendarController(calendar); // api will use the same calendar as the rest of the program
            executePlugins(api, pluginList);

            calendar.display(); //Display calendar
            calendar.handleUserInput(); //Begin handling user Input

            executePlugins(api, pluginList);

        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found: {0}", e.getMessage());
        } catch (edu.curtin.parser.ParseException e) {
            LOGGER.log(Level.SEVERE, "Parse exception: {0}", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error: {0}", e.getMessage());
        }
    }
    // determines via fileName which UTF is in use
    public static Charset determineUTF(String filePath) {
        if (filePath.endsWith(".utf16.cal")) {
            return Charset.forName("UTF-16");
        } else if (filePath.endsWith(".utf32.cal")) {
            return Charset.forName("UTF-32");
        } else {
            return Charset.forName("UTF-8");
        }
    }

    public static void executePlugins(calendarControllerApi api, ArrayList<PluginInfo> pluginList) {
        for (PluginInfo pluginInfo : pluginList) {
            try {
                Class<?> pluginClass = Class.forName(pluginInfo.getClassName());
                CalendarPlugin pluginObj = (CalendarPlugin) pluginClass.getConstructor().newInstance();
                pluginObj.start(api, pluginInfo.getArguments());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException e) {
                LOGGER.log(Level.WARNING, "Error executing plugin: {0}", e.getMessage());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected error in plugin execution: {0}", e.getMessage());
            }
        }
    }
}
