mvn versions:set -DnextSnapshot -DgenerateBackupPoms=false  && \
mvn versions:use-latest-versions -DgenerateBackupPoms=false -DallowSnapshots=true -Dincludes=com.nirocca && \
gcloud auth login nicolas.rocca@t-online.de && \
gcloud config set project ornitho-service  && \
mvn clean install && \
mvn appengine:deploy && \
gcloud app deploy cron.yaml --quiet