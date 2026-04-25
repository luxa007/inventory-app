package com.Luxa.inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles requests for the application's home page.
 *
 * <p>The home page is primarily used to display a welcome message and,
 * when present, a registration success message provided via flash
 * attributes by the registration flow.</p>
 */
@Controller
public class HomeController {

    /**
     * Render the home view.
     *
     * @return the logical Thymeleaf view name {@code "home"}
     */


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Home");
        return "home";
    }

}
