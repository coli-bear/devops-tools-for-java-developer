package co.example.demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("greeting")
public class GreetingLambda implements RequestHandler<InputObject, OutputObject> {
    @Inject
    ProcessingService service;
    @Override
    public OutputObject handleRequest(InputObject inputObject, Context context) {
        OutputObject outputObject = service.process(inputObject);
        outputObject.setRequestId(context.getAwsRequestId());
        return outputObject;
    }
}
