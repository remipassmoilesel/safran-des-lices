package org.remipassmoilesel.safranlices.utils;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class ThreadExecutor extends ThreadPoolTaskExecutor{

    public ThreadExecutor(){
        this.setCorePoolSize(5);
        this.setMaxPoolSize(10);
        this.setQueueCapacity(50);
        this.setAllowCoreThreadTimeOut(true);
        this.setKeepAliveSeconds(120);
    }

}
