A server wrapper around the Stanford CoreNLP sentiment engine.

### Config

Port can be set in `application.properties`, by default it is set to 80, this
may cause problems on some machines.

### Deployment

Deployed using maven can be run with the command:

```
mvn spring-boot:run
```

### Features
Server features one endpoint: `/sentiment`. This endpoint will respond to
multiple GET parameters of `lines`. For example:
```
GET /sentiment?lines=The+movie+was+really+great.&lines=The+movie+was+really+
terrible&lines=The+movie+was+really+great.+The+movie+was+really+terrible HTTP/1.1
```
Responds:
```
{
    0: {
        sentiment: 0.8018029430617934,
        line: "The movie was really great."
    },
    1: {
        sentiment: 0.22636708981379178,
        line: "The movie was really terrible"
    },
    2: {
        sentiment: 0.5140850164377926,
        line: "The movie was really great. The movie was really terrible"
    }
}
```

Note the averaging of the third sentiment, this should be avoided but is
available if required.
