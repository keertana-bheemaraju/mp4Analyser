## MP4Analyzer

### Introduction
This app exposes a service to analyze and return a JSON that describes the Box types and sizes of an MP4 file

### Build and Run Steps
- clone this repository
- From the root directory of this repo, run `mvn clean install` to package the app as an executable jar
- From the root directory of this repo, run `java -jar target/mp4Analyzer-0.0.1-SNAPSHOT.jar` to run the app

### Invoke the service using `curl`
```
curl localhost:8080/mp4/analyzeMp4?mp4Url=https://demo.castlabs.com/tmp/text0.mp4 | json_pp

```



