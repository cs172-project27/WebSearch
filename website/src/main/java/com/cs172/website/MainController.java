package com.cs172.website;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/results")
    public String results(@RequestParam(name="query", required=false, defaultValue="World") String query, Model model) {
        model.addAttribute("query", query);
        return "results";
    }

}