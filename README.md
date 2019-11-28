# ornitho-service
Run locally with `mvn appengine:run`

Access [http://localhost:8080/last3days]()

Deploy to GCP with 
* `gcloud config set project ornitho-service`
* `gcloud auth login` 
* `mvn clean install` 
* `mvn appengine:deploy`
* url: [https://ornitho-service.appspot.com/last3days]()
