package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class GetFileEntity {

    protected String ok;

    private String file_id;
    private String file_unique_id;

    private int file_size;

    private String file_path;

    public String getOk() {
        return ok;
    }

    public String getFile_id() {
        return file_id;
    }

    public String getFile_unique_id() {
        return file_unique_id;
    }

    public int getFile_size() {
        return file_size;
    }

    public String getFile_path() {
        return file_path;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("result")
    private void unpackNested(Map<String,Object> result) {
        this.file_id = (String)result.get("file_id");
        this.file_unique_id = (String)result.get("file_unique_id");
        this.file_size = (int)result.get("file_size");
        this.file_path = (String)result.get("file_path");
    }
}
