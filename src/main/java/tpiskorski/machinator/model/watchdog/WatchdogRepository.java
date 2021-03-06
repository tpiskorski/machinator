package tpiskorski.machinator.model.watchdog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

@Repository
public class WatchdogRepository {

    private ObservableList<Watchdog> watchdogs = FXCollections.observableArrayList(Watchdog.extractor());

    void add(Watchdog watchdog) {
        watchdogs.add(watchdog);
    }

    ObservableList<Watchdog> getWatchdogs() {
        return watchdogs;
    }

    void remove(Watchdog watchdog) {
        watchdogs.remove(watchdog);
    }

    public boolean contains(Watchdog watchdog) {
        return watchdogs.contains(watchdog);
    }
}
