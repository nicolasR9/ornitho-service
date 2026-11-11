# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot service deployed on Google App Engine that monitors ornitho.de bird sightings and sends email alerts for rare/interesting birds. Uses Firebase Firestore to track already-sent sightings. Depends on custom `ornithoalert` library (com.nirocca:ornithoalert) for scraping ornitho.de data.

## Build & Development

```bash
# Run locally (starts on port 8080)
mvn appengine:run

# Build
mvn clean install

# Run tests
mvn test
```

Access local server: http://localhost:8080/last3days

## Deployment

Deploy to GCP with:
```bash
./release.sh
```

This script:
1. Bumps version to next snapshot
2. Updates ornithoalert dependency to latest snapshot
3. Authenticates with GCloud
4. Builds and deploys app
5. Deploys cron.yaml for scheduled jobs

Production URL: https://ornitho-service.appspot.com/last3days

## Architecture

### Core Components

**SightingsCalculator**: Fetches bird sighting data from ornitho.de via ornithoalert library. Multiple strategies (rare birds, not-this-year birds, vacation location monitoring).

**EmailController** (`/sendUpdateEmail`): Cron endpoint (runs every 15 minutes per cron.yaml). Checks new sightings, deduplicates against Firestore, sends email via SendGrid.

**SightingsController**: UI endpoints serving Thymeleaf templates. Shows different sighting views (last 3 days, rare, not-this-year-all, vacation).

**EmailSender**: SendGrid integration for alerts.

### Key Patterns

**VacationSettings**: Hardcoded location + ornitho URL for monitoring specific regions (currently Garmisch). Update in `SightingsController.VACATION_SETTINGS` when user travels.

**Firestore deduplication**: Uses sighting URL as document ID (URL-encoded). Prevents duplicate emails for same bird.

**SightingModel.HOME**: Default monitoring location coordinates for distance calculations.

## Dependencies

- Spring Boot 3.0.2 (Java 17)
- `com.nirocca:ornithoalert` (custom library, versioned separately)
- Firebase Admin SDK (Firestore)
- SendGrid (email)
- Google Cloud Platform (App Engine, authentication)

Config files:
- `src/main/appengine/app.yaml`: App Engine runtime config
- `src/main/resources/sendgrid.properties`: SendGrid API key
- `cron.yaml`: Scheduled job definitions
