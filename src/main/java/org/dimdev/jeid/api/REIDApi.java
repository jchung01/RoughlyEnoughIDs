package org.dimdev.jeid.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.jeid.jeid.Tags;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.ServiceLoader;

public final class REIDApi {
    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String API_NAME = Tags.API_NAME;
    public static final String API_VERSION = Tags.API_VERSION;

    public static final Logger logger = LogManager.getLogger(API_NAME);

    @ApiStatus.Internal
    public static <T> T loadService(Class<T> clazz) {
        Iterator<T> itr = ServiceLoader.load(clazz).iterator();
        if (itr.hasNext()) {
            return itr.next();
        } else {
            throw new NullPointerException("Failed to load service for " + clazz.getName());
        }
    }
}
