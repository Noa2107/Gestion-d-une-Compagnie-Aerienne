package com.compagnie.controller;

import com.compagnie.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(name = "days", required = false, defaultValue = "14") int days, Model model) {
        Map<String, Object> data = dashboardService.buildDashboardData(days);
        model.addAllAttributes(data);
        model.addAttribute("title", "Dashboard");
        return "dashboard";
    }
}