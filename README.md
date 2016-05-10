# ordernator-loader

This is a cool way to run an [Apache Camel](http://camel.apache.org) application in a cheap, highly scalable, serverless fashion with [AWS Lambda](https://aws.amazon.com/lambda/)!

The example features a simple Camel route that concurrently processes messages from an [AWS SQS](https://aws.amazon.com/sqs/) queue. The Lambda function can be configured to trigger from a scheduled event, for example.

### Instructions

1. Use [ordernator-loader](https://github.com/armeniopinto/ordernator-loader) to load requests to the SQS queue.
2. Create a Java Lambda with a timeout of 1 minute.
3. Use [Maven 3](https://maven.apache.org/download.cgi) to build and package the application: `mvn clean package`.
4. Upload the big JAR created in `target/` to the Lambda.
5. Test!

You should see the messages quickly being sunk off the queue. The application will run for around 30 seconds and then gracefully stop.
