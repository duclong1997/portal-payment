package com.ezpay.main.config;

import com.ezpay.main.process.Facade.ProcessFacade;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ProcessConfig {

    private final ProcessFacade processFacade;

    public ProcessConfig(final ProcessFacade processFacade) {
        this.processFacade = processFacade;
    }

    @Scheduled(initialDelay = 1 * 1000, fixedDelayString = "${app.process.fixedRate.interval}")
    public void updateTransaction() {
        processFacade.updateTransaction();
    }

    @Scheduled(initialDelay = 10 * 1000, fixedDelayString = "${app.process.fixedRate.interval}")
    public void queryTransaction() {
        processFacade.queryTransaction();
    }
}
