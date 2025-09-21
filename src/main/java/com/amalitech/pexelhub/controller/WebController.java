package com.amalitech.pexelhub.controller;

import com.amalitech.pexelhub.service.PhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * MVC controller serving the home page and initial photo data model.
 */
@Controller
public class WebController {

    private final PhotoService photoService;

    /**
     * Constructs the web controller with the required PhotoService.
     *
     * @param photoService service handling photo retrieval for the UI
     */
    public WebController(PhotoService photoService) {
        this.photoService = photoService;
    }

    /**
     * Renders the index page and populates the model with the first page of photos
     * and metadata required by the client-side infinite scroll.
     *
     * @param model the Spring MVC model to be populated
     * @return name of the Thymeleaf template to render
     */
    @GetMapping("/")
    public String home(Model model) {
        Map<String, Object> photoPage = photoService.getPhotos(0, 5);
        long totalPhotos = photoService.getTotalPhotoCount();

        model.addAttribute("photos", photoPage.get("photos"));
        model.addAttribute("hasMore", photoPage.get("hasMore"));
        model.addAttribute("totalPhotos", totalPhotos);

        return "index";
    }
}
