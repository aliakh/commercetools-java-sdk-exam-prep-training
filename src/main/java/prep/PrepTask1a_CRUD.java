package prep;

import com.commercetools.api.client.ProjectApiRoot;
import prep.impl.CustomerService;
import prep.impl.ApiPrefixHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static prep.impl.ClientService.createApiClient;


public class PrepTask1a_CRUD {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // Learning Goals
        // Api Clients
        // Get, Post

        Logger logger = LoggerFactory.getLogger(PrepTask1a_CRUD.class.getName());

        // TODO Step 1: Create an admin api client for your own project
        // TODO Step 2: Provide credentials in dev.properties
        // TODO Step 3: Provide prefix in APIHelper
        // TODO Step 4: Check ClientService.java
         // TODO Step 5: Create a new customer.
        // TODO Step 6: Update the customer's billing address.
        // TODO Step 7: Create a customer group.
        // TODO Step 8: Assign the customer to the customer group.
        // TODO Step 9: Delete the customer.
        // TODO Step 10: Create a tax category.
        // TODO Step 11: Create a few product categories.
        // TODO Step 12: Query the categories by key.

//        String customerGroupName = "B2B Group L2";
//        String customerGroupKey = "b2b-group-l2";
        String customerEmail = "john_soe@email.com";
        String customerPassword = "john_soe_password";
        String customerKey = "john_soe";
        String customerFirstName = "John";
        String customerLastName = "Soe";
        String customerCountry = "DE";

        String customerStreetName = "Hedderichstrasse";
        String customerStreetNumber = "43";
        String customerPostalCode = "60594";
        String customerCity = "Frankfurt am Main";

        final ProjectApiRoot apiRoot_poc =
                createApiClient(
                        ApiPrefixHelper.API_POC_CLIENT_PREFIX.getPrefix()
                );

        CustomerService customerService = new CustomerService(apiRoot_poc);

//        logger.info("TODO List: Create a new customer.\n" +
//                "Update the customer's billing address.\n" +
//                "Create a customer group.\n" +
//                "Assign the customer to the customer group.\n" +
//                "Delete the customer.\n" +
//                "Create a tax category.\n" +
//                "Create a few product categories.\n" +
//                "Query the categories by key.\n");

//        logger.info("Create sign up completed."  +
//                customerService.createCustomer("","","","","",""));

        logger.info("Create a new customer.\n" +
            customerService.createCustomer(
                    customerEmail,
                    customerPassword,
                    customerKey,
                    customerFirstName,
                    customerLastName,
                    customerCountry
                )
                .get()
                .getBody()
                .getCustomer()
        );

        logger.info("Update the customer's billing address.\n" +
            customerService.updateCustomerBillingAddress(
                    customerService.getCustomerByKey(customerKey).get(),
                    customerStreetName,
                    customerStreetNumber,
                    customerPostalCode,
                    customerCity,
                    customerCountry
                )
                .get()
                .getBody()
        );

        apiRoot_poc.close();
    }
}
