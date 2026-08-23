package com.tengan.mall.media.domain.exception;

public class FileTooLargeException extends RuntimeException {

    public FileTooLargeException(long maxBytes) {
        super("檔案大小超過上限: " + maxBytes + " bytes");
    }
}
