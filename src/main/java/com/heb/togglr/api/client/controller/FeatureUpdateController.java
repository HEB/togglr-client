package com.heb.togglr.api.client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heb.togglr.api.client.service.TogglrUpdateNotifier;

@RestController
@RequestMapping("/togglr")
public class FeatureUpdateController {

    private static Logger logger = LoggerFactory.getLogger(FeatureUpdateController.class);

    private TogglrUpdateNotifier togglrUpdateNotifier;


    private FeatureUpdateController(TogglrUpdateNotifier togglrUpdateNotifier){
        this.togglrUpdateNotifier = togglrUpdateNotifier;
    }

    @PostMapping(path = "/update")
    public void featuresUpdatedWebhook(){
        logger.debug("Request for update received.");
        this.togglrUpdateNotifier.registerNewUpdate();
    }
}