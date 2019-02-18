package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.config.Config;
import com.github.tpiskorski.vboxcm.config.ConfigService;
import javafx.event.ActionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class ConfigController {

    private final ConfigService configService;

    @Autowired public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    public void something(ActionEvent actionEvent) throws IOException {
        Config config = configService.loadConfig();
        System.out.println(config.getBackupLocation());
        System.out.println(config.getPollInterval());
        System.out.println(config.getSshPassword());
        System.out.println(config.getSshUser());
    }
}