package com.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by stcas on 2018/1/4.
 */
public class LoggerTest {
    private static Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    public static void main(String[] args) {
        logger.error("s");
        logger.info("u");
        logger.debug("v");
    }
}
