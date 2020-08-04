package plus.gifu.data.server.leaf.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例 控制器
 *
 * @author yanghongyu
 * @since 20200608
 */
@RequestMapping("/demo")
@RestController
public class DemoController {

    public DemoController() {
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello demo";
    }

}
