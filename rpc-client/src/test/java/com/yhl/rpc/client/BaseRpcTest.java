package com.yhl.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuhongliang on 17-8-14.
 */
class BaseRpcTest {
    protected static Logger LOGGER = LoggerFactory.getLogger(BaseRpcTest.class);

    protected static <T> void printResult(String prefix, T result) {
        printEmptyln(1);
        LOGGER.warn("{} result = {}", prefix, result);
        printEmptyln(1);
    }

    protected static void printEmptyln(int number) {
        for (int i = 0; i < number; i++) {
            System.out.println();
        }
    }
}
