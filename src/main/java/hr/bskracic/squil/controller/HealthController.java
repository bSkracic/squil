package hr.bskracic.squil.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/health")
public class HealthController {

    @GetMapping("/check")
    public String checkHealth() {
        return "Healthy";
    }

}
