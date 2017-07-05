package org.remipassmoilesel.safranlices.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Update specified directories when Spring boot application restart
 * <p>
 * Useful for templates
 */
public class UpdateFilesListener implements ApplicationListener<ApplicationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(UpdateFilesListener.class);

    /**
     * Peers of source / destination
     */
    private final HashMap<Path, Path> peers;

    public UpdateFilesListener() {
        peers = new HashMap<>();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ApplicationStartingEvent) {
            update();
        }

    }

    public void addPeer(Path src, Path dst) {
        peers.put(src, dst);
    }

    public void update() {
        Iterator<Path> keyIt = peers.keySet().iterator();
        while (keyIt.hasNext()) {

            Path src = keyIt.next();
            Path dst = peers.get(src);

            try {
                FileUtils.copyDirectory(src.toFile(), dst.toFile());
            } catch (IOException e) {
                logger.error("Error while updating directories : " + src + " / " + dst, e);
            }

            logger.warn("Directories updated: " + src + " / " + dst);
        }
    }
}
