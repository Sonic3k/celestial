package com.sonic.celestial.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Uploads images to Backblaze B2 (S3-compatible) and returns the BunnyCDN URL.
 *
 * Required env vars:
 *   B2_KEY_ID          — Backblaze App Key ID
 *   B2_APP_KEY         — Backblaze Application Key
 *   B2_BUCKET_NAME     — e.g. sonic-celestial
 *   B2_ENDPOINT        — e.g. https://s3.us-east-005.backblazeb2.com
 *   BUNNY_CDN_BASE     — e.g. https://sonic-celestial.b-cdn.net
 */
@Service
public class ImageStorageService {

    @Value("${b2.key-id:}")
    private String keyId;

    @Value("${b2.app-key:}")
    private String appKey;

    @Value("${b2.bucket-name:sonic-celestial}")
    private String bucketName;

    @Value("${b2.endpoint:https://s3.us-east-005.backblazeb2.com}")
    private String b2Endpoint;

    @Value("${bunny.cdn-base:https://sonic-celestial.b-cdn.net}")
    private String cdnBase;

    private S3Client s3;
    private final HttpClient http = HttpClient.newHttpClient();

    @PostConstruct
    void init() {
        if (keyId == null || keyId.isBlank()) return; // skip if not configured
        s3 = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(keyId, appKey)))
                .endpointOverride(URI.create(b2Endpoint))
                .region(Region.of("us-east-005"))
                .build();
    }

    /**
     * Downloads a remote image and re-uploads it to B2.
     *
     * @param sourceUrl  Original URL (Wikimedia, tarotapi.dev, etc.)
     * @param b2Key      Destination key in bucket, e.g. "tarot/rider-waite/00-fool.jpg"
     * @return BunnyCDN URL, e.g. "https://sonic-celestial.b-cdn.net/tarot/rider-waite/00-fool.jpg"
     */
    public String uploadFromUrl(String sourceUrl, String b2Key) throws Exception {
        if (s3 == null) throw new IllegalStateException("B2 not configured — set B2_KEY_ID / B2_APP_KEY env vars");

        // Download original image
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(sourceUrl))
                .header("User-Agent", "CelestialBot/1.0")
                .build();
        HttpResponse<byte[]> resp = http.send(req, HttpResponse.BodyHandlers.ofByteArray());

        if (resp.statusCode() != 200) {
            throw new RuntimeException("Failed to download image: " + sourceUrl + " status=" + resp.statusCode());
        }

        byte[] bytes = resp.body();
        String contentType = resp.headers().firstValue("Content-Type").orElse("image/jpeg");

        // Upload to B2
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(b2Key)
                .contentType(contentType)
                .contentLength((long) bytes.length)
                .build();

        s3.putObject(putReq, RequestBody.fromBytes(bytes));

        return cdnBase.stripTrailing() + "/" + b2Key;
    }

    /**
     * Upload raw bytes directly (used when image is already in memory).
     */
    public String uploadBytes(byte[] bytes, String b2Key, String contentType) {
        if (s3 == null) throw new IllegalStateException("B2 not configured");

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(b2Key)
                .contentType(contentType)
                .contentLength((long) bytes.length)
                .build();

        s3.putObject(putReq, RequestBody.fromBytes(bytes));
        return cdnBase.stripTrailing() + "/" + b2Key;
    }

    public boolean isConfigured() {
        return s3 != null;
    }
}
