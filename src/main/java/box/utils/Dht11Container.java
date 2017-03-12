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
public class Dht11Container {
   private double humidity;
   private double temperature;
   private double[] soilMoisture;
   private boolean lightsOn;
   private boolean pumpsOn;
   private boolean humiditifierOn;

    public Dht11Container(double humidity, double temperature, double[] soilMoisture, boolean lightsOn, boolean pumpsOn, boolean humiditifierOn) {
        this.humidity = humidity;
        this.temperature = temperature;
        this.soilMoisture = soilMoisture;
        this.lightsOn = lightsOn;
        this.pumpsOn = pumpsOn;
        this.humiditifierOn = humiditifierOn;
    }

    public double[] getSoilMoisture() {
        return soilMoisture;
    }

    public void setSoilMoisture(double[] soilMoisture) {
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

    public boolean isHumiditifierOn() {
        return humiditifierOn;
    }

    public void setHumiditifierOn(boolean humiditifierOn) {
        this.humiditifierOn = humiditifierOn;
    }

    public Dht11Container(double humidity, double temperature, double[] soilMoisture) {
        this.humidity = humidity;
        this.temperature = temperature;
        this.soilMoisture = soilMoisture;
    }
    
    public Dht11Container(){}

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
