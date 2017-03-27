package com.wj.crawler.scheduler;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

/**
 * Created by SYu on 3/21/2017.
 */

@Singleton
public class SchedulerServiceManager {

    private static final Logger Log = LoggerFactory.getLogger(SchedulerServiceManager.class);

    private ServiceManager serviceManager;

    @Inject
    public SchedulerServiceManager(Set<Service> services){
        serviceManager = new ServiceManager(services);
    }

    public ServiceManager getInstance(){

        return serviceManager;
    }

    public void stop(){


        this.serviceManager.stopAsync();
    }

    public void start(){
        Log.debug("Service is going to start");
        this.serviceManager.startAsync();
    }


}
