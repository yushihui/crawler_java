package com.wj.crawler.scheduler;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import javax.inject.Singleton;

/**
 * Created by SYu on 3/21/2017.
 */

@Singleton
public class SchedulerServiceManager {

    private ServiceManager serviceManager;

    public SchedulerServiceManager(Iterable<? extends Service> services){
        serviceManager = new ServiceManager(services);
    }

    public ServiceManager getInstance(){

        return serviceManager;
    }

    public void stop(){
        this.serviceManager.stopAsync();
    }

    public void start(){
        this.serviceManager.startAsync();
    }


}
