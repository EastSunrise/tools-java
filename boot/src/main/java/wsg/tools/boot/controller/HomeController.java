package wsg.tools.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Home of the management pages.
 *
 * @author Kingen
 * @since 2020/6/25
 */
@Controller
@RequestMapping
public class HomeController extends AbstractController {

    @RequestMapping("/index")
    public String index() {
        return "index";
    }
}
