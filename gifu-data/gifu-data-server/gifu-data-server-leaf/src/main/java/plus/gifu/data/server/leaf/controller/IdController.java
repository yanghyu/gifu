package plus.gifu.data.server.leaf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import plus.gifu.data.leaf.segment.generater.IdGenerator;
import plus.gifu.data.leaf.segment.model.IdResult;

import javax.annotation.Resource;

/**
 * ID 控制器
 *
 * @author yanghongyu
 * @since 2020-08-06
 */
@RequestMapping("/ids")
@RestController
public class IdController {

    @Resource
    private IdGenerator idGenerator;

    public IdController() {
    }

    @GetMapping("/next")
    public IdResult next(@RequestParam String key) {
        return idGenerator.generateId(key);
    }

}
