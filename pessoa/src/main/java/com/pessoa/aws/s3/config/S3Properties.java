package com.pessoa.aws.s3.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.storage.s3")
public class S3Properties {

    private String bucket;
    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;
    private boolean pathStyleAccess;

}
