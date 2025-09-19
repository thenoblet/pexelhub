package com.amalitech.pexelhub.controller;

import com.amalitech.pexelhub.service.PhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class WebController {

    private final PhotoService photoService;

    public WebController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Map<String, Object> photoPage = photoService.getPhotos(0, 5);

        model.addAttribute("photos", photoPage.get("photos"));
        model.addAttribute("hasMore", photoPage.get("hasMore"));
        return "index";
    }
}
