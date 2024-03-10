package com.blockchain.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "my-properties")
public class MyProperties {

    public Map<String, String> startCoinMonitoringInfo = new HashMap<>();

    // getter and setter
    public Map<String, String> getStartCoinMonitoringInfo() {
        return startCoinMonitoringInfo;
    }

    public void setStartCoinMonitoringInfo(Map<String, String> startCoinMonitoringInfo) {
        this.startCoinMonitoringInfo = startCoinMonitoringInfo;
    }
}



