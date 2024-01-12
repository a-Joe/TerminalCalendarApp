package edu.curtin.calendarApp;
import java.util.Map;

// Plogin Data object
// Stores non specific information about the plugins
// Being the classname as string and arguments as Key-Value pairs
public class PluginInfo {
    private String className;
    private Map<String, String> arguments;
    
    public PluginInfo(String className, Map<String, String> arguments) {
        this.className = className;
        this.arguments = arguments;
    }
    
    public String getClassName() {
        return className;
    }
    
    public Map<String, String> getArguments() {
        return arguments;
    }
}