package com.github.yanghyu.gifu.data.leaf.segment.util;

import com.github.yanghyu.gifu.data.leaf.segment.exception.TooManyLoopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class LoopUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoopUtil.class);

    public static int loopStatus(int roll) {
        roll ++;
        if (roll > 10000) {
            throw new TooManyLoopException();
        } else if (roll > 1000) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                logger.error("sleep interrupted exception", e);
            }
        }
        return roll;
    }

}
