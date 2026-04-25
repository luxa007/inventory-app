package com.Luxa.inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScanController {

    @GetMapping("/camera-scan")
    public String cameraScan() {
        return "camera-scan";
    }

    @GetMapping("/restock-advisor")
    public String restockAdvisor() { return "restock-advisor"; }

    @GetMapping("/bulk-upload")
    public String bulkUpload() {
        return "bulk-upload";
    }
}
