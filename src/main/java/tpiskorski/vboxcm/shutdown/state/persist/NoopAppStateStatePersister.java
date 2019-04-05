package tpiskorski.vboxcm.shutdown.state.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;

@Profile("dev")
public class NoopAppStateStatePersister implements AppStatePersister {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStatePersister.class);

    @Override public void persist() {
        LOGGER.info("Not persisting anything because spring dev profile is active");
    }
}
