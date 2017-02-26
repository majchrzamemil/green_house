package box.service.impl;

import box.domain.GreenHouseManager;
import box.domain.OutSwitch;
import box.domain.Plant;
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
import box.utils.Dht11Container;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.io.IOException;
import java.util.Iterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@Transactional
public class GreenHouseManagerServiceImpl implements GreenHouseManagerService {

    private static final int WRONG_VALUE = -999;
    private static final int ERROR_COUNTER = 50;
    private static final String TAKE_PHOTO_SCRIPT = "sudo /home/pi/green_house/src/main/scripts/take_picture.sh";
    private final Logger log = LoggerFactory.getLogger(GreenHouseManagerServiceImpl.class);
    private static Dht11Container humAndTemp;
    private static Dht11Container previousHumAndTemp;
    private static int humidityNotChangingCounter = 0;
    private static int soilhumidityNotChangingCounter = 0;
    private static double previousSoilHum;

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
        //FOR DEBUGING OPITONS
        log.debug("Humidity read: " + humAndTemp.getHumidity() + ", temperature: " + humAndTemp.getTemperature());
        if (humAndTemp.getHumidity() != WRONG_VALUE) {
            if (humAndTemp.getHumidity() < manager.getSettings().getMinHumidity()) {
                if (Math.abs(humAndTemp.getHumidity() - previousHumAndTemp.getHumidity()) <= 2) {
                    humidityNotChangingCounter++;
                }
                previousHumAndTemp = humAndTemp;
                if (humidityNotChangingCounter < ERROR_COUNTER) {
                    manager.getGreenHouse().getHumidifier().turnOn();
                }
            } else if (humAndTemp.getHumidity() >= manager.getSettings().getMaxHumidity()) {
                humidityNotChangingCounter = 0;
                manager.getGreenHouse().getHumidifier().turnOff();
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void managePumps() {
        boolean wattering = true;
        double soilHumidity;
        try {
            for (Plant plant : manager.getGreenHouse().getPlants()) {
                //NOW IT WORKS BUT IF U WANT TO ADD MORE SENSORS RETHINK THIS WHOLE IDEA
                //Hardcoded first plant
                soilHumidity = RaspiPinTools.getSoilHumidity(1);
                if (soilHumidity < manager.getSettings().getMinGrounHumidity()) {
                    if (Math.abs(previousSoilHum - soilHumidity) <= 2) {
                        soilhumidityNotChangingCounter++;
                    }
                    if (soilhumidityNotChangingCounter < ERROR_COUNTER) {
                        wattering = true;
                        previousSoilHum = soilHumidity;
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
        } catch (IOException e) {
            //AGAIN DEAL IN NORMAL WAY WITH THIS EXCEPTION
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
    }

    //TODO FINISH THIS
    private boolean checkLights() {
        DateTime time = new DateTime();
        if (manager.getSettings().getStartHour() > time.getHourOfDay()
                && manager.getSettings().getEndHour() < time.getHourOfDay()) {

            return false;
        }
        boolean start = manager.getSettings().getStartHour() == time.getHourOfDay();
        if (start && manager.getSettings().getStartMinute() < time.getMinuteOfHour()) {
            return false;
        }
        boolean end = manager.getSettings().getEndHour() == time.getHourOfDay();
        if (start && manager.getSettings().getStartMinute() < time.getMinuteOfHour()) {
            return false;
        }
        if (start == end && manager.getSettings().getEndMinute() < time.getMinuteOfHour()
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
        //HANDLE EXCEPTION
        log.debug("taking picture");
        try {
            Runtime.getRuntime().exec(TAKE_PHOTO_SCRIPT);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(GreenHouseManagerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    @EventListener
    public void update(ProfileSettings profileSettings) {
        manager.setSettings(profileSettings);
    }

}
