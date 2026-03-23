"""
B2 Image Uploader
Uploads card images to Backblaze B2 and returns BunnyCDN URLs.
Uses boto3 (S3-compatible API).

Install: pip install boto3 requests
"""

import boto3
from botocore.config import Config
import requests
import time
from pathlib import Path

# ── Config (set via env vars or pass directly) ────────────────
import os

B2_KEY_ID     = os.environ.get("B2_KEY_ID",     "005b0791155f8c30000000002")
B2_APP_KEY    = os.environ.get("B2_APP_KEY",    "K005DKsMhGObFyNwoipCBEbUuNJJWbI")
B2_BUCKET     = os.environ.get("B2_BUCKET",     "sonic-celestial")
B2_ENDPOINT   = os.environ.get("B2_ENDPOINT",   "https://s3.us-east-005.backblazeb2.com")
BUNNY_CDN     = os.environ.get("BUNNY_CDN_BASE","https://sonic-celestial.b-cdn.net")


def _get_s3():
    return boto3.client(
        "s3",
        endpoint_url=B2_ENDPOINT,
        aws_access_key_id=B2_KEY_ID,
        aws_secret_access_key=B2_APP_KEY,
        config=Config(signature_version="s3v4"),
        region_name="us-east-005"
    )


def upload_from_url(source_url: str, b2_key: str, retries: int = 3) -> str | None:
    """Download from source_url, upload to B2, return CDN URL."""
    for attempt in range(retries):
        try:
            resp = requests.get(source_url, timeout=20,
                                headers={"User-Agent": "CelestialBot/1.0"})
            resp.raise_for_status()
            content_type = resp.headers.get("Content-Type", "image/jpeg")
            return upload_bytes(resp.content, b2_key, content_type)
        except Exception as e:
            print(f"  [b2] Attempt {attempt+1} failed for {source_url}: {e}")
            time.sleep(2 ** attempt)
    return None


def upload_bytes(data: bytes, b2_key: str, content_type: str = "image/jpeg") -> str | None:
    """Upload raw bytes to B2, return CDN URL."""
    try:
        s3 = _get_s3()
        s3.put_object(
            Bucket=B2_BUCKET,
            Key=b2_key,
            Body=data,
            ContentType=content_type,
            ContentLength=len(data)
        )
        cdn_url = f"{BUNNY_CDN.rstrip('/')}/{b2_key}"
        print(f"  [b2] Uploaded → {cdn_url}")
        return cdn_url
    except Exception as e:
        print(f"  [b2] Upload failed for key={b2_key}: {e}")
        return None


def key_exists(b2_key: str) -> bool:
    """Check if a key already exists in B2 (skip re-upload)."""
    try:
        s3 = _get_s3()
        s3.head_object(Bucket=B2_BUCKET, Key=b2_key)
        return True
    except Exception:
        return False


def cdn_url(b2_key: str) -> str:
    return f"{BUNNY_CDN.rstrip('/')}/{b2_key}"
