package com.savo.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {

    private String bucket;
    private String region = "ap-southeast-1";
    private long maxFileSize = 10485760;
    private int urlExpirationMinutes = 60;
    private String folderStructure = "{folder}/{userId}/{year}/{month}/{day}";

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getUrlExpirationMinutes() {
        return urlExpirationMinutes;
    }

    public void setUrlExpirationMinutes(int urlExpirationMinutes) {
        this.urlExpirationMinutes = urlExpirationMinutes;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getFolderStructure() {
        return folderStructure;
    }

    public void setFolderStructure(String folderStructure) {
        this.folderStructure = folderStructure;
    }
}
