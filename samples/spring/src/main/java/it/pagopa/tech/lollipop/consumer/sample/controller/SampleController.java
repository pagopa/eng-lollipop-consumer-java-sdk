package it.pagopa.tech.lollipop.consumer.sample.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @RequestMapping("${sample.lollipop.consumer.config.endpoint}")
    public @ResponseBody String sample() { return "Sample";}
}
