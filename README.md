# rabbitmq-spring-custom-metrics

This is a very simple Java and Spring application demonstrating the use of RabbitMQ on Cloud Foundry and interrogating RabbitAdmin (For queuedepth) and sending that info as a custom app metric to Loggregator.

## Deploying to Cloud Foundry ##

You'll need a RabbitMQ service and a Metrics Frowarder service.

After installing in the 'cf' [command-line interface] for Cloud Foundry, targeting a Cloud Foundry instance, and logging in, the application can be pushed using these commands:

    $ mvn package
    $ cf push
    
    You will then want to bind your RabbitMQ and Metrics Forwarder services to the app and restage the app (cf restage {your_app_name})
    
