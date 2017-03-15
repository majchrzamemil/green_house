package box.service;

import box.domain.ProfileSettings;

/**
 * Service responsible for checking conditions, that are in ProfileSettings with
 * sensors inputs. Also responsible for taking pictures and invoking WebSocket
 * Messages.
 * 
 * @author emil
 */
public interface GreenHouseManagerService {

    /**
     * Method that is invoked by TaskExecutor every 5 seconds. Invokes all
     * methods responsible for managing Green House
     *
     */
    public void run();

    /** Updates object of profile settings, after being changed.
     * 
     * @param profileSettings 
     */
    public void update(ProfileSettings profileSettings);

    /** Runs script that is taking picure.
     * 
     */
    public void takePicture();
}
