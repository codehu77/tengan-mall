package com.tengan.mall.devtools.web;

import com.tengan.mall.devtools.service.ImportService;
import com.tengan.mall.devtools.web.dto.ImportRequest;
import com.tengan.mall.devtools.web.dto.ImportResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/import")
    public ImportResponse importProduct(@Valid @RequestBody ImportRequest request) {
        return new ImportResponse(importService.importProduct(request));
    }
}
