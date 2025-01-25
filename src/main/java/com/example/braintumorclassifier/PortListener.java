package com.example.braintumorclassifier;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PortListener implements ApplicationListener<WebServerInitializedEvent> {
    @Override
    public void onApplicationEvent(@NonNull WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        System.out.println("Server is running on port: " + port);
    }
}