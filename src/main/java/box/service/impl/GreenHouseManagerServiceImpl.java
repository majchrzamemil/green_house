package box.service.impl;

import box.service.GreenHouseManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GreenHouseManagerServiceImpl implements GreenHouseManagerService {

    private final Logger log = LoggerFactory.getLogger(GreenHouseManagerServiceImpl.class);

}
