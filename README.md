# Sieve of Eratosthenes — Cloud Run App

Java Servlet backend + plain HTML/CSS/JS frontend, containerised with Docker.

## Project layout

```
sieve-app/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/com/sieve/
    │   └── SieveServlet.java      ← GET /primes?limit=N
    └── webapp/
        ├── index.html             ← Single-page frontend
        └── WEB-INF/
            └── web.xml
```

## Run locally (Docker)

```bash
docker build -t sieve-app .
docker run -p 8080:8080 sieve-app
# Open http://localhost:8080
```

## Run locally (Maven + Tomcat, no Docker)

```bash
mvn package
# Drop target/sieve-app.war into your local Tomcat webapps/ROOT.war
```

## Deploy to Google Cloud Run

```bash
# 1. Build & push to Artifact Registry
PROJECT_ID=your-gcp-project-id
IMAGE=us-docker.pkg.dev/$PROJECT_ID/sieve/sieve-app

docker build -t $IMAGE .
docker push $IMAGE

# 2. Deploy
gcloud run deploy sieve-app \
  --image $IMAGE \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --port 8080
```

## API

`GET /primes?limit=N`

| Case | HTTP status | Body |
|------|-------------|------|
| Valid N (2–15000) | 200 | `{"limit":N,"count":K,"primes":[2,3,5,...]}` |
| N > 15000 | 400 | `{"error":"You can only request primes up to 15,000."}` |
| Bad / missing param | 400 | `{"error":"..."}` |
