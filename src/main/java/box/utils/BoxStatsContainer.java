/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package box.utils;

/**
 *
 * @author emil
 */
public class BoxStatsContainer {
   private double humidity;
   private double temperature;
   private int[] soilMoisture;
   private boolean lightsOn;
   private boolean pumpsOn;
   private boolean humidifierOn;

    public BoxStatsContainer(double humidity, double temperature, int[] soilMoisture, boolean lightsOn, boolean pumpsOn, boolean humidifierOn) {
        this.humidity = humidity;
        this.temperature = temperature;
        this.soilMoisture = soilMoisture;
        this.lightsOn = lightsOn;
        this.pumpsOn = pumpsOn;
        this.humidifierOn = humidifierOn;
    }

    public int[] getSoilMoisture() {
        return soilMoisture;
    }

    public void setSoilMoisture(int[] soilMoisture) {
        this.soilMoisture = soilMoisture;
    }

    public boolean isLightsOn() {
        return lightsOn;
    }

    public void setLightsOn(boolean lightsOn) {
        this.lightsOn = lightsOn;
    }

    public boolean isPumpsOn() {
        return pumpsOn;
    }

    public void setPumpsOn(boolean pumpsOn) {
        this.pumpsOn = pumpsOn;
    }

    public boolean isHumidifierOn() {
        return humidifierOn;
    }

    public void setHumidifierOn(boolean humiditifierOn) {
        this.humidifierOn = humiditifierOn;
    }

    public BoxStatsContainer(double humidity, double temperature) {
        this.humidity = humidity;
        this.temperature = temperature;
    }
    
    public BoxStatsContainer(){}

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
   
}
