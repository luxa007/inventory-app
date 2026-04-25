package com.Luxa.inventory.controller;

import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Helper that wraps a MultipartFile's InputStream so Spring's RestTemplate
 * can include it as a named file in a multipart/form-data request.
 */
public class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename != null ? filename : "upload";
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() throws IOException {
        return -1; // unknown length – required to avoid IllegalStateException
    }
}
