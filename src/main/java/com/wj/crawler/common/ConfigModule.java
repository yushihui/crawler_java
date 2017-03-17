package com.wj.crawler.common;

import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;

import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by SYu on 3/14/2017.
 */
@Module
public class ConfigModule {

    @Provides
    @Singleton
    Properties providerPropertiesConfig() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            input = classloader.getResourceAsStream("config.properties");
            prop.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return prop;
    }

//    @Provides
//    @Singleton
//    Gson providerGson(){
//        return new Gson();
//    }
}
