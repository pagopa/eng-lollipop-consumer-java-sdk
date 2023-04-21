/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @RequestMapping("/")
    public @ResponseBody String test() {
        return "Test";
    }
}
