package com.example.just.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
public class PrometheusController {
    @GetMapping("/get/info")
    public String info() {
        log.info("info test message");
        return "infoOK";
    }

    @GetMapping("/get/warn")
    public String warn() {
        log.warn("warn test message");
        return "warnOK";
    }

    @GetMapping("/get/error")
    public String error() {
        log.error("error test message");
        return "errorOK";
    }
}
