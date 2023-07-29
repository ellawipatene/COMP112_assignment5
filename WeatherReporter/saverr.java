// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2021T1, Assignment 5
 * Name: Ella Wipatene
 * Username: wipateella
 * ID: 300558005
 */

import ecs100.*;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Color;
import java.util.ArrayList; // import the ArrayList class

/**
 * saverr
 * Analyses weather data from files of weather-station measurements.
 *
 * The weather data files consist of a set of measurements from weather stations around
 * New Zealand at a series of date/time stamps.
 * For each date/time, the file has:
 *  A line with the date and time (four integers for day, month, year, and time)
 *   eg "24 01 2021 1900"  for 24 Jan 2021 at 19:00 
 *  A line with the number of weather-stations for that date/time 
 *  Followed by a line of data for each weather station:
 *   - name: one token, eg "Cape-Reinga"
 *   - (x, y) coordinates on the map: two numbers, eg   186 38
 *   - four numbers for temperature, dew-point, suface-pressure, and sea-level-pressure
 * Some of the data files (eg hot-weather.txt, and cold-weather.txt) have data for just one date/time.
 * The weather-all.txt has data for lots of times. The date/times are all in order.
 * You should look at the files before trying to complete the methods below.
 *
 * Note, the data files were extracted from MetOffice weather data from 24-26 January 2021
 */

public class saverr{

    public static final double DIAM = 10;       // The diameter of the temperature circles.    
    public static final double LEFT_TEXT = 10;  // The left of the date text
    public static final double TOP_TEXT = 50;   // The top of the date text

    /**   CORE
     * Plots the temperatures for one date/time from a file on a map of NZ
     * Asks for the name of the file and opens a Scanner
     * It is good design to call plotSnapshot, passing the Scanner as an argument.
     */
    public void plotTemperatures(){
        String fileName = UIFileChooser.open("File to open:");
        boolean dateRecorded = false;
        try {
            List<String> dataList = Files.readAllLines(Path.of(fileName)); 
            
            // resetting variables 
            int stations = 0; 
            int counter = 0;
            String date = "";
            String time = ""; 
            String formatedDate = ""; 
            
            ArrayList<String> stationNameList = new ArrayList<String>();
            ArrayList<Double> xCordList = new ArrayList<Double>();
            ArrayList<Double> yCordList = new ArrayList<Double>();
            ArrayList<Double> tempList = new ArrayList<Double>();
            
            for(String line: dataList){ 
                Scanner scan = new Scanner(line);
                
                if (scan.hasNextInt() == true && dateRecorded == false){
                    date = scan.next(); 
                    date = date + "/" + scan.next(); 
                    date = date + "/" + scan.next();  
                    
                    time = scan.next();
                    if (time.length() == 3){
                        String hour = time.substring(0,1); 
                        String mins = time.substring(1,3); 
                        time = hour + ":" + mins; 
                    } else{
                        String hour = time.substring(0,2); 
                        String mins = time.substring(2,4); 
                        time = hour + ":" + mins; 
                    }
                    
                    formatedDate = date + " at " + time; 
                    
                    dateRecorded = true; 
                    counter = 0; 
                    UI.println(date + "   " + counter); 
                } else if(scan.hasNextInt() == true && dateRecorded == true){
                    stations = scan.nextInt(); 
                    dateRecorded = false; 
                    UI.println(stations); 
                } else if(counter <= stations) {
                    String stationName = scan.next(); 
                    stationNameList.add(stationName); 
                    
                    Double xCord = scan.nextDouble(); 
                    xCordList.add(xCord); 
                    
                    Double yCord = scan.nextDouble();
                    yCordList.add(yCord); 
                    
                    Double temp = scan.nextDouble();
                    tempList.add(temp); 
                    
                    counter++; 
                    
                    if (counter == stations){
                        plotSnapshot(formatedDate, stations, stationNameList, xCordList, yCordList, tempList); 
                        
                        UI.sleep(500); 
                        stationNameList.clear(); 
                        xCordList.clear(); 
                        yCordList.clear();
                        tempList.clear(); 
                    }
                    UI.println(stationName + "   "  + xCord +"   "  + yCord + "   "  +temp +"   "  + counter); 
                } 
            } 
        } catch(IOException e){UI.println("File reading failed" + e);}    
    }


    /**
     * CORE:
     *  Plot the temperatures for the next snapshot in the file by drawing
     *   a filled coloured circle (size DIAM) at each weather-station location.
     *  The colour of the circle should indicate the temperature.
     *
     *  The method should
     *   - read the date/time and draw the date/time at the top-left of the map.
     *   - read the number of stations, then
     *   - for each station,
     *     - read the name, coordinates, and data, and
     *     - plot the temperature for that station. 
     *   (Hint: You will find the getTemperatureColor(...) method useful.)
     *
     *  COMPLETION:
     *  Also finds the highest and lowest temperatures at that time, and
     *  plots them with a larger circle.
     *  (Hint: If more than one station has the highest (or coolest) temperature,
     *         you only need to draw a larger circle for one of them.
     */     
    public void plotSnapshot(String date, int stations,List<String> stationNames, List<Double> xCords, List<Double> yCords, List<Double> temp ){
        UI.drawImage("map-new-zealand.gif", 0, 0);
        
        double maxTemp = getMaxTemp(temp);
        double minTemp = getMinTemp(temp); 
        UI.println(minTemp); 
        UI.println(maxTemp); 
        
        UI.drawString(date, LEFT_TEXT, TOP_TEXT); 
        for (int i = 0; i < stations; i++){
            UI.setColor(getTemperatureColor(temp.get(i)));
            if (temp.get(i) == -999){
                UI.drawOval(xCords.get(i), yCords.get(i), DIAM, DIAM); 
            }else if (temp.get(i) == maxTemp || temp.get(i) == minTemp){
                UI.fillOval(xCords.get(i), yCords.get(i), 2* DIAM, 2* DIAM); // campbell island will not show on the map
            } else{
                UI.fillOval(xCords.get(i), yCords.get(i), DIAM, DIAM); 
            }
        }
    }
    
    
    /** For the animation challenge */
    public void plotSnapshots(List<String> dates, int stations,List<String> stationNames, List<Double> xCords, List<Double> yCords, List<Double> temp ){
        UI.drawImage("map-new-zealand.gif", 0, 0);
        
        double maxTemp = getMaxTemp(temp);
        double minTemp = getMinTemp(temp); 
        UI.println(minTemp); 
        UI.println(maxTemp); 
        
        UI.drawString(dates.get(1), LEFT_TEXT, TOP_TEXT); 
        for (int i = 0; i < stations; i++){
            UI.setColor(getTemperatureColor(temp.get(i)));
            if (temp.get(i) == -999){
                UI.drawOval(xCords.get(i), yCords.get(i), DIAM, DIAM); 
            }else if (temp.get(i) == maxTemp || temp.get(i) == minTemp){
                UI.fillOval(xCords.get(i), yCords.get(i), 2* DIAM, 2* DIAM); // campbell island will not show on the map
            } else{
                UI.fillOval(xCords.get(i), yCords.get(i), DIAM, DIAM); 
            }
        }
    }
    
    public double getMaxTemp(List<Double> temp){
        double max = Collections.max(temp);     
        return max;     
    }
    
    public double getMinTemp(List<Double> temp){
        double min = Collections.min(temp);     
        return min;  
    }

    /**   COMPLETION
     * Displays an animated view of the temperatures over all
     * the times in a weather data files, plotting the temperatures
     * for the first date/time, as in the core, pausing for half a second,
     * then plotting the temperatures for the second date/time, and
     * repeating until all the data in the file has been shown.
     * 
     * (Hint, use the plotSnapshot(...) method that you used in the core)
     */
    public void animateTemperatures(){
        UI.drawImage("map-new-zealand.gif", 0, 0);
        UI.drawString("Snapshots:", 20, 613);  
        UI.drawRect(20, 620, 350, 10); 
        
        String fileName = UIFileChooser.open("File to open:");
        boolean dateRecorded = false;
        try {
            List<String> dataList = Files.readAllLines(Path.of(fileName)); 
            
            // resetting variables 
            int stations = 0; 
            int counter = 0;
            String date = "";
            String time = ""; 
            String formatedDate = ""; 
            
            ArrayList<String> formatedDateList = new ArrayList <String>(); 
            ArrayList<String> stationNameList = new ArrayList<String>();
            ArrayList<Double> xCordList = new ArrayList<Double>();
            ArrayList<Double> yCordList = new ArrayList<Double>();
            ArrayList<Double> tempList = new ArrayList<Double>();
            
            for(String line: dataList){ 
                Scanner scan = new Scanner(line);
                
                if (scan.hasNextInt() == true && dateRecorded == false){
                    date = scan.next(); 
                    date = date + "/" + scan.next(); 
                    date = date + "/" + scan.next();  
                    
                    time = scan.next();
                    if (time.length() == 3){
                        String hour = time.substring(0,1); 
                        String mins = time.substring(1,3); 
                        time = hour + ":" + mins; 
                    } else{
                        String hour = time.substring(0,2); 
                        String mins = time.substring(2,4); 
                        time = hour + ":" + mins; 
                    }
                    
                    formatedDate = date + " at " + time; 
                    formatedDateList.add(formatedDate); 
                    
                    dateRecorded = true; 
                    //counter = 0; 
                    UI.println(date + "   " + counter); 
                } else if(scan.hasNextInt() == true && dateRecorded == true){
                    stations = scan.nextInt(); 
                    dateRecorded = false; 
                    UI.println(stations); 
                } else if(counter <= stations) {
                    String stationName = scan.next(); 
                    stationNameList.add(stationName); 
                    
                    Double xCord = scan.nextDouble(); 
                    xCordList.add(xCord); 
                    
                    Double yCord = scan.nextDouble();
                    yCordList.add(yCord); 
                    
                    Double temp = scan.nextDouble();
                    tempList.add(temp); 
                    
                    counter++; 
                    
                    if (counter == dataList.size()){
                        plotSnapshots(formatedDateList, stations, stationNameList, xCordList, yCordList, tempList); 
                        UI.println(formatedDateList.size());
                        UI.println(xCordList.size());
                        
                        
                        // UI.sleep(500); 
                        // stationNameList.clear(); 
                        // xCordList.clear(); 
                        // yCordList.clear();
                        // tempList.clear(); 
                    }
                    //UI.println(stationName + "   "  + xCord +"   "  + yCord + "   "  +temp +"   "  + counter); 
                } 
            } 
        } catch(IOException e){UI.println("File reading failed" + e);} 
            
    }

    /**   COMPLETION
     * Prints a table of all the weather data from a single station, one line for each day/time.
     * Asks for the name of the station.
     * Prints a header line
     * Then for each line of data for that station in the weather-all.txt file, it prints 
     * a line with the date/time, temperature, dew-point, surface-pressure, and  sealevel-pressure
     * If there are no entries for that station, it will print a message saying "Station not found".
     * Hint, the \t in a String is the tab character, which helps to make the table line up.
     */
    public void reportStation(){
        String stationName = UI.askString("Name of a station: ");
        UI.printf("Report for %s: \n", stationName);
        UI.println("Date       \tTime \ttemp \tdew \tkPa \tsea kPa");   

        String fileName = UIFileChooser.open("File to open:");
        boolean dateRecorded = false;
        try {
            List<String> dataList = Files.readAllLines(Path.of(fileName)); 
            
            // resetting variables 
            int stations = 0; 
            String date = ""; 
            String time = ""; 
            
            ArrayList<String> stationNameList = new ArrayList<String>();
            
            for(String line: dataList){ 
                Scanner scan = new Scanner(line);
                
                if (scan.hasNextInt() == true && dateRecorded == false){
                    
                    date = scan.next(); 
                    date = date + "/" + scan.next(); 
                    date = date + "/" + scan.next();  
                    
                    time = scan.next();
                    if (time.length() == 3){
                        String hour = time.substring(0,1); 
                        String mins = time.substring(1,3); 
                        time = hour + ":" + mins; 
                    } else{
                        String hour = time.substring(0,2); 
                        String mins = time.substring(2,4); 
                        time = hour + ":" + mins; 
                    }
                    
                    dateRecorded = true; 
                } else if(scan.hasNextInt() == true && dateRecorded == true){
                    stations = scan.nextInt(); 
                    dateRecorded = false;  
                } else{
                    String station = scan.next(); 
                    stationNameList.add(station); 
                    if (station.equals(stationName)){                    
                        Double xCord = scan.nextDouble(); 
                        Double yCord = scan.nextDouble();
                        
                        Double temp = scan.nextDouble();
                        Double dew = scan.nextDouble(); 
                        Double kPa = scan.nextDouble(); 
                        Double seaKPa = scan.nextDouble(); 
                        
                        if(temp == -999){
                            UI.println(date + "\t" + time +"\t -" + "\t" + dew + "\t" + kPa + "\t" + seaKPa);
                        } else if(dew == -999){ 
                            UI.println(date + "\t" + time +"\t" + temp + "\t -" + "\t" + kPa + "\t" + seaKPa);
                        } else if(kPa == -999){
                            UI.println(date + "\t" + time +"\t" + temp + "\t" + dew + "\t -" + "\t" + seaKPa);
                        } else if(seaKPa == -999){
                            UI.println(date + "\t" + time +"\t" + temp + "\t" + dew + "\t" + kPa + "\t - ");
                        } else{
                            UI.println(date + "\t" + time +"\t" + temp + "\t" + dew + "\t" + kPa + "\t" + seaKPa);
                        }
                        
                    
                    } 
                } 
            } 
            if (stationNameList.contains(stationName) == false){
                    UI.println("Station not found"); 
                
                }
        } catch(IOException e){UI.println("File reading failed" + e);}    

    }

    /** Returns a color representing that temperature
     *  The colors are increasingly blue below 15 degrees, and
     *  increasingly red above 15 degrees.
     */
    public Color getTemperatureColor(double temp){
        double max = 37, min = -5, mid = (max+min)/2;
        if (temp < min || temp > max){
            return Color.white;
        }
        else if (temp <= mid){ //blue range: hues from .7 to .5
            double tempFracOfRange = (temp-min)/(mid-min);
            double hue = 0.7 -  tempFracOfRange*(0.7-0.5); 
            return Color.getHSBColor((float)hue, 1.0F, 1.0F);
        }
        else { //red range: .15 to 0.0
            double tempFracOfRange = (temp-mid)/(max-mid);
            double hue = 0.15 -  tempFracOfRange*(0.15-0.0); 
            return Color.getHSBColor((float)hue, 1.0F, 1.0F);
        }
    }

    public void setupGUI(){
        UI.initialise();
        UI.addButton("Plot temperature", this::plotTemperatures);
        UI.addButton("Animate temperature", this::animateTemperatures);
        UI.addButton("Report",  this::reportStation);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(800,750);
        UI.setFontSize(18);
    }

    public static void main(String[] arguments){
        saverr obj = new saverr();
        obj.setupGUI();
    }    

}
