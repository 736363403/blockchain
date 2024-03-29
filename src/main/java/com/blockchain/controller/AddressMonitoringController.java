package com.blockchain.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.blockchain.service.AddressMonitoringService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/GecFeedback")
public class AddressMonitoringController {

    private static final Logger logger = LogManager.getLogger(AddressMonitoringController.class);

    @Value("${isMonitoring}")
    boolean isMonitoring;

    @Value("${isCoinMonitoring}")
    boolean isCoinMonitoring;

    @Resource
    private AddressMonitoringService addressMonitoringService;

//    @RequestMapping("/startMonitoring")
    @Scheduled(cron = "0 * * * * ?")
    public void startMonitoring() throws Exception {
        if (isMonitoring){
            addressMonitoringService.startMonitoring();
        }
    }

    @Scheduled(cron = "0 */3 * * * ?")
    public void startCoinMonitoring() throws Exception {
        if (isCoinMonitoring){
            addressMonitoringService.startCoinMonitoring();
        }
    }


}
