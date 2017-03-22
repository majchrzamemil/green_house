package box.service.impl;

import com.pi4j.io.gpio.PinState;
import box.domain.GreenHouseManager;
import box.domain.OutSwitch;
import box.repository.GreenHouseManagerRepository;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import box.domain.ProfileSettings;
import box.utils.RaspiPinTools;
import org.springframework.context.event.EventListener;
import box.service.GreenHouseManagerService;
import box.web.websocket.dto.BoxStatsContainer;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@Transactional
public class GreenHouseManagerServiceImpl implements GreenHouseManagerService {

    private static final int WRONG_VALUE = -999;
    private static final int ERROR_COUNTER = 50;
    private static final String TAKE_PHOTO_SCRIPT = "sudo /home/pi/green_house/src/main/scripts/take_picture.sh";
    private final Logger log = LoggerFactory.getLogger(GreenHouseManagerServiceImpl.class);
    private static BoxStatsContainer humAndTemp;
    private static BoxStatsContainer previousHumAndTemp = null;
    private static int humidityNotChangingCounter = 0;
    private static int soilhumidityNotChangingCounter = 0;
    private static double previousSoilHum;
    private static int counter = 0;

    @Autowired
    private SimpMessagingTemplate template;

    @Inject
    private GreenHouseManagerRepository greenHouseManagerRepository;

    private GreenHouseManager manager;

    @PostConstruct
    public void initIt() {
        manager = greenHouseManagerRepository.findAll().get(0);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void manageHumidity() {
        humAndTemp = RaspiPinTools.getTemperatureAndHumidity(manager.getGreenHouse().getTemperature().getPinNumber());
        //TODO: Make it better(working).
        if (manager.getGreenHouse().getHumidifier().getPin() != null) {
            log.debug("not null");
            if (manager.getGreenHouse().getHumidifier().getPin().getState().isHigh()) {
                humAndTemp.setHumidifierOn(false);
            } else {
                humAndTemp.setHumidifierOn(true);
            }
        }
        //FOR DEBUGING OPITONS
        log.debug("Humidity read: " + humAndTemp.getHumidity() + ", temperature: " + humAndTemp.getTemperature());
        if (humAndTemp.getHumidity() != WRONG_VALUE) {
            if (previousHumAndTemp == null) {
                previousHumAndTemp = humAndTemp;
            }
            if (humAndTemp.getHumidity() < manager.getSettings().getMinHumidity()) {
                if (Math.abs(humAndTemp.getHumidity() - previousHumAndTemp.getHumidity()) <= 2) {
                    humidityNotChangingCounter++;
                }
                previousHumAndTemp = humAndTemp;
                if (humidityNotChangingCounter < ERROR_COUNTER) {
                    humAndTemp.setHumidifierOn(true);
                    manager.getGreenHouse().getHumidifier().turnOn();
                } else {
                    humAndTemp.setHumidifierOn(false);

                    manager.getGreenHouse().getHumidifier().turnOff();
                }
            } else if (humAndTemp.getHumidity() >= manager.getSettings().getMaxHumidity()) {
                humidityNotChangingCounter = 0;
                humAndTemp.setHumidifierOn(false);

                manager.getGreenHouse().getHumidifier().turnOff();
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void managePumps() {
        boolean wattering = true;
        int soilHumidity;
        final int plantsNumber = manager.getGreenHouse().getPlants().size();
        int[] soilMoisture = new int[plantsNumber];
        try {
            for (int i = 0; i < plantsNumber; i++) {
                //chanel coresponding with place in collection
                soilHumidity = RaspiPinTools.getSoilHumidity(i + 1);
                log.debug("SOIL HUMIDITY: " + soilHumidity + "soilHumNotChanging: " + soilhumidityNotChangingCounter);
                soilMoisture[counter] = soilHumidity;
                if (soilHumidity < manager.getSettings().getMinGrounHumidity()) {
                    if (Math.abs(previousSoilHum - soilHumidity) <= 2) {
                        soilhumidityNotChangingCounter++;
                    }
                    if (soilhumidityNotChangingCounter < ERROR_COUNTER) {
                        wattering = true;
                        previousSoilHum = soilHumidity;
                    } else {
                        wattering = false;
                    }
                } else if (soilHumidity > manager.getSettings().getMaxGroundHumidity()) {
                    soilhumidityNotChangingCounter = 0;
                    wattering = false;
                }

                for (OutSwitch pump : manager.getGreenHouse().getLights()) {
                    if (wattering) {
                        pump.turnOn();
                    } else {
                        pump.turnOff();
                    }
                }

            }
            counter = 0;
            humAndTemp.setSoilMoisture(soilMoisture);
            humAndTemp.setPumpsOn(wattering);
        } catch (IOException e) {
            log.error("IOEXCEPTION FROM SOIL HUMIDITY READ");
        }

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void manageLights() {
        boolean lightsOn = true;
        lightsOn = checkLights();
        for (OutSwitch light : manager.getGreenHouse().getLights()) {
            if (lightsOn) {
                light.turnOn();
            } else {
                light.turnOff();
            }
        }
        humAndTemp.setLightsOn(lightsOn);

    }

    private boolean checkLights() {
        DateTime time = new DateTime();
        if (manager.getSettings().getStartHour() > time.getHourOfDay()
                || manager.getSettings().getEndHour() < time.getHourOfDay()) {
            return false;
        }
        boolean start = manager.getSettings().getStartHour() == time.getHourOfDay();
        if (start && manager.getSettings().getStartMinute() > time.getMinuteOfHour()) {
            return false;
        }
        boolean end = manager.getSettings().getEndHour() == time.getHourOfDay();
        if (start && manager.getSettings().getEndMinute() < time.getMinuteOfHour()) {
            return false;
        }
        if (start == true && start == end && manager.getSettings().getEndMinute() < time.getMinuteOfHour()
                || manager.getSettings().getStartMinute() > time.getMinuteOfHour()) {
            return false;
        }

        return true;
    }

    private void handleTemparature() {
        if (humAndTemp.getTemperature() < manager.getSettings().getMinTemperature()) {
            template.convertAndSend("/topic/exceptions", "To low temperature!!!!");
        } else if (humAndTemp.getTemperature() > manager.getSettings().getMaxTemperature()) {
            template.convertAndSend("/topic/exceptions", "To high temperature!!!!");
        }
    }

    private void handleHumidity() {
        if (humidityNotChangingCounter > ERROR_COUNTER) {
            template.convertAndSend("/topic/exceptions", "Humidity not rising!!!!");

        }
    }

    private void handleSoilHumidity() {
        if (soilhumidityNotChangingCounter > ERROR_COUNTER) {
            log.debug("Send exception, soil humidity");
            template.convertAndSend("/topic/exceptions", "Soil humidity not rising!!!!");

        }
    }

    private void sendStatistics() {
        if (humAndTemp.getHumidity() != WRONG_VALUE && humAndTemp.getTemperature() != WRONG_VALUE) {
            template.convertAndSend("/topic/tempAndHum", humAndTemp);
        }
    }

    @Override
    @Scheduled(fixedDelay = 5000)
    public void run() {
        manageHumidity();
        managePumps();
        manageLights();
        sendStatistics();
        handleHumidity();
        handleSoilHumidity();
        handleTemparature();
    }

    @Override
    @Scheduled(cron = "0 0 0/4 * * *")
    public void takePicture() {
        log.debug("taking picture");
        try {
            Runtime.getRuntime().exec(TAKE_PHOTO_SCRIPT);
        } catch (IOException ex) {
            log.error("Taking photo filed");
        }

    }

    @Override
    @EventListener
    public void update(ProfileSettings profileSettings) {
        manager.setSettings(profileSettings);
    }

}
