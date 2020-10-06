package com.github.yanghyu.gifu.data.server.leaf.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.yanghyu.gifu.data.leaf.segment.handler.SegmentHandler;
import com.github.yanghyu.gifu.data.leaf.segment.model.Sequence;

import javax.annotation.Resource;

/**
 * KEY 控制器
 *
 * @author yanghongyu
 * @since 2020-08-06
 */
@RequestMapping("/keys")
@RestController
public class KeyController {

    @Resource
    private SegmentHandler segmentHandler;

    @PostMapping
    public Sequence add(@RequestParam String key) {
        return segmentHandler.insertKey(key, 0);
    }

}


