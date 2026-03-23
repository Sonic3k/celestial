# Celestial — Tarot Crawl Scripts

## Setup
```bash
cd scripts
pip install -r requirements.txt
```

## Environment variables (optional — defaults to Celestial B2 bucket)
```
B2_KEY_ID       Backblaze App Key ID
B2_APP_KEY      Backblaze Application Key
B2_BUCKET       Bucket name (default: sonic-celestial)
B2_ENDPOINT     B2 S3 endpoint (default: https://s3.us-east-005.backblazeb2.com)
BUNNY_CDN_BASE  BunnyCDN base URL (default: https://sonic-celestial.b-cdn.net)
```

## Run
```bash
# Crawl Rider-Waite (full — includes image upload to B2)
python run_deck.py --deck rider-waite --api http://localhost:8080

# Skip images (data only, faster for testing)
python run_deck.py --deck rider-waite --api http://localhost:8080 --skip-images

# Re-seed if deck already exists
python run_deck.py --deck rider-waite --api http://localhost:8080 --update

# Against production backend
python run_deck.py --deck rider-waite \
  --api https://celestial-backend-production-8ab8.up.railway.app
```

## Admin UI
Open: http://localhost:8080/admin (or production URL + /admin)
