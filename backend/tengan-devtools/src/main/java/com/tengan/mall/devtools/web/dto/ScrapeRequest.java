package com.tengan.mall.devtools.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ScrapeRequest(@NotBlank String url) {
}
