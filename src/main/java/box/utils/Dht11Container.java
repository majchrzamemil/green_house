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

    public Dht11Container(double humidity, double temperature) {
        this.humidity = humidity;
        this.temperature = temperature;
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
