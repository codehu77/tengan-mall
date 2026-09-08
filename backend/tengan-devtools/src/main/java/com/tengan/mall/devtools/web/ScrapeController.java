package com.tengan.mall.devtools.web;

import com.tengan.mall.devtools.scrape.MomoScraperService;
import com.tengan.mall.devtools.scrape.dto.ScrapeResult;
import com.tengan.mall.devtools.web.dto.ScrapeRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ScrapeController {

    private final MomoScraperService momoScraperService;

    public ScrapeController(MomoScraperService momoScraperService) {
        this.momoScraperService = momoScraperService;
    }

    @PostMapping("/scrape")
    public ScrapeResult scrape(@Valid @RequestBody ScrapeRequest request) {
        return momoScraperService.scrape(request.url());
    }
}
