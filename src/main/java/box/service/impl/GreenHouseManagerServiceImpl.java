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
import java.io.IOException;
import java.util.Iterator;

@Service
@Transactional
public class GreenHouseManagerServiceImpl implements GreenHouseManagerService {

    private static final long START_PROFILE_SETTINGS = 1011L;
    private static final int WRONG_VALUE = -999;
    //private final Logger log = LoggerFactory.getLogger(GreenHouseManagerServiceImpl.class);

    @Inject
    private GreenHouseManagerRepository greenHouseManagerRepository;

    private GreenHouseManager manager;

    @PostConstruct
    public void initIt() {
        manager = greenHouseManagerRepository.findOne(START_PROFILE_SETTINGS);
        //Think about fans
//        for (OutSwitch fan : manager.getGreenHouse().getFans()) {
//            fan.turnOn();
//        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void manageHumidity() {
        double humidity = RaspiPinTools.getHumidity(manager.getGreenHouse().getTemperature().getPinNumber());
        //FOR DEBUGING OPITONS
        double temperature = RaspiPinTools.getTemperature(manager.getGreenHouse().getTemperature().getPinNumber());
     //   log.debug("Humidity read: " + humidity + ", temperature: " + temperature);
        if (humidity != WRONG_VALUE) {
            if (humidity < manager.getSettings().getMinHumidity()) {
       //         log.debug("HUMIDITY ON");
                manager.getGreenHouse().getHumidifier().turnOn();
            } else if (humidity >= manager.getSettings().getMaxHumidity()) {
         //       log.debug("HUMIDITY OFF");

                manager.getGreenHouse().getHumidifier().turnOff();
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void managePumps() {
        boolean wattering = true;
        double soilHumidity;
        //DEAL WITH THIS FUCKING IOEXCEPTION        
        try {
            for (Plant plant : manager.getGreenHouse().getPlants()) {
                //NOW IT WORKS BUT IF U WANT TO ADD MORE SENSORS RETHINK THIS WHOLE IDEA
                soilHumidity = RaspiPinTools.getSoilHumidity(1);
                if (soilHumidity < manager.getSettings().getMinGrounHumidity()) {
                    wattering = true;
                } else if (soilHumidity > manager.getSettings().getMaxGroundHumidity()) {
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
            //log.error("IOEXCEPTION FROM SOIL HUMIDITY READ");
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

    @Override
    @Scheduled(fixedDelay = 1000)
    public void run() {
          manageHumidity();
//        managePumps();
//        manageLights();

    }

    @Override
    @EventListener
    public void update(ProfileSettings profileSettings) {
        manager.setSettings(profileSettings);
    }

}
