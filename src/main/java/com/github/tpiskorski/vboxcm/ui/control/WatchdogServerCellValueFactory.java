package com.github.tpiskorski.vboxcm.ui.control;

import com.github.tpiskorski.vboxcm.core.watchdog.Watchdog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class WatchdogServerCellValueFactory implements Callback<TableColumn.CellDataFeatures<Watchdog, String>, ObservableValue<String>> {

    @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<Watchdog, String> param) {
        return new SimpleStringProperty(param.getValue().getVirtualMachine().getServerAddress());
    }
}
