package com.armeniopinto.ordernator.process;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.sqs.AmazonSQSClient;

/**
 * A simple AWS Lambda function that concurrently processes SQS messages with Apache Camel.
 *  
 * @author armenio.pinto
 */
public class RequestHandler {

	private static class OrdernatorProcess extends RouteBuilder {
		@Override
		public void configure() throws Exception {
			from("aws-sqs://ordernator-requests?concurrentConsumers=4&maxMessagesPerPoll=4&amazonSQSClient=#sqsClient")
					.routeId("ordernator-process").log("Message received: ${body}").delay(1000L)
					.stop();
		}
	}

	public void handle(final Context lambdaContext) throws Exception {
		final LambdaLogger logger = lambdaContext.getLogger();

		// When running in Lambda, the AWS credential are provided by the runtime.
		final AmazonSQSClient sqsClient = new AmazonSQSClient(
				new EnvironmentVariableCredentialsProvider());
		sqsClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
		logger.log("Amazon SQS client created.");

		// We want Camel to use our SQS client, so that we don't have to supply credentials.
		final SimpleRegistry camelRegistry = new SimpleRegistry();
		camelRegistry.put("sqsClient", sqsClient);
		final CamelContext camelContext = new DefaultCamelContext(camelRegistry);
		camelContext.addRoutes(new OrdernatorProcess());
		camelContext.start();
		logger.log("Camel context created and started.");

		// Lets Camel work on the requests for a bit and then gracefully shuts down.
		Thread.sleep(30000L);
		camelContext.stop();
		logger.log("Camel context stopped.");
	}

}
