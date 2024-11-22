package com.example.triptracker;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class DataCalculations {
    private final String TAG = "CALCULATIONS";
    private UpdateUIThread uiThread;
    private MainActivity activity;
    private long fuelCnt = 0L;
    private long speedCnt = 0L;

    private int currSpeed;

    private double avgFuel = 0; //Litres per second
    private float avgSpeed = 0;
    private static double fuelPrice = 0.00;
    private long startTime;

    private final double AirFuelRatio = 14.7;
    private final double FuelDensity = 0.832;

    private double previousDistance = 0.00;

    private LocalDateTime startingTime;
    Scraper scraper = new Scraper();


    public DataCalculations(MainActivity activity) {
        this.activity = activity;
        uiThread = new UpdateUIThread(activity);

        startTime = System.currentTimeMillis();
        startingTime = LocalDateTime.now();
        scraper.execute();
    }
    public DataCalculations(){}

    public static double getFuelPrice() {
        return fuelPrice;
    }

    public static void setFuelPrice(double fuelPrice) {
        DataCalculations.fuelPrice = fuelPrice;
    }

    public String getAvgFuelFormatted() {
        try{
            return String.valueOf(round(avgFuel*100/avgSpeed, 1)).concat(" l/100km");
        }
        catch (NumberFormatException e){
            return "0 l/100km";
        }
    }

    public double getAvgFuel(){
        return round(avgFuel*100/avgSpeed, 1);
    }

    public String getAvgSpeed(){
        return String.valueOf(round(avgSpeed, 1));
    }

    public String calculateSpeed(SpeedCommand command) {

        currSpeed = Integer.parseInt(command.getCalculatedResult());
        avgSpeed = (avgSpeed * speedCnt + currSpeed) / (speedCnt + 1);
        speedCnt++;

        return String.valueOf(round(avgSpeed, 1));
    }

    public String calculateFuel(SpeedCommand speed, MassAirFlowCommand maf) {

        currSpeed = Integer.parseInt(speed.getCalculatedResult());
        double currMAF = Double.parseDouble(maf.getCalculatedResult());
        double litresPerHr = (((currMAF / 2000) / AirFuelRatio) / FuelDensity) * 3600;

        avgFuel = ((avgFuel * fuelCnt) + litresPerHr) / (fuelCnt + 1);
        fuelCnt++;

        if (currSpeed > 0) {
            return String.valueOf(round(litresPerHr * 100 / currSpeed, 1)).concat(" l/100km");
        } else {
            return String.valueOf(round(litresPerHr, 1)).concat(" l/h");
        }
    }

    private float round(float d, int decimalPlace) {

        return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    private double round(double d, int decimalPlace) {

        return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public String getElapsedTime() {
        long elapsedTime = System.currentTimeMillis()- startTime;
        long elapsedSeconds = elapsedTime / 1000;
        long secondsDisplay = elapsedSeconds % 60;
        long elapsedMinutes = elapsedSeconds / 60;
        long elapsedHours = elapsedMinutes /60;

        if(elapsedHours>0){
            String.format("%d:%d:%d hours",elapsedHours,elapsedMinutes,secondsDisplay);
        }
        else if(elapsedMinutes>0){
            return String.format("%d:%d minutes",elapsedMinutes,secondsDisplay);
        }
        else{
            return String.format("%d seconds",secondsDisplay);
        }
        return String.valueOf(System.currentTimeMillis()- startTime);//Never gonna hit it
    }

    private Duration getDuration(){
        LocalDateTime now = LocalDateTime.now();
        return Duration.between(startingTime, now);
    }

    public String getFormattedDuration(){
        Duration duration = getDuration();

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        // Format as HH:mm:ss
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public double getDistanceTravelled(){
        Duration duration = getDuration();
        double hours = duration.getSeconds()/3600.00;
        double distance = round(avgSpeed*hours, 3);
        if(distance >= previousDistance){
            previousDistance = distance;
            return distance;
        }
        return previousDistance;
    }
}