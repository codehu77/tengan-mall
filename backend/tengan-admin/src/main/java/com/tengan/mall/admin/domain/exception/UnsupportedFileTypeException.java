package com.tengan.mall.admin.domain.exception;

public class UnsupportedFileTypeException extends RuntimeException {

    public UnsupportedFileTypeException(String contentType) {
        super("不支援的檔案格式: " + contentType);
    }
}
