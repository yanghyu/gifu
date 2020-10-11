package com.github.yanghyu.gifu.data.server.leaf.controller;

import com.github.yanghyu.gifu.data.leaf.segment.handler.SegmentHandler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;


public class IdControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(SegmentHandler.class);

    @Test
    public void next() throws IOException {
        String url = "http://localhost:8080/ids/next?key=default1";
        String body = url;
        logger.info("{}", body);
    }

}