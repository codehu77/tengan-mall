package com.tengan.mall.media.application.upload;

import java.util.List;

public record DeleteObjectsCommand(List<String> urls) {
}
