package prep;

import com.commercetools.api.client.ProjectApiRoot;
import prep.impl.ApiPrefixHelper;
import prep.impl.CartService;
import prep.impl.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static prep.impl.ClientService.createApiClient;


public class PrepTask1b_CREATE_CARTS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // Learning Goals
        // Create a cart
        // Create an anonymous cart

        Logger logger = LoggerFactory.getLogger(PrepTask1b_CREATE_CARTS.class.getName());

        String customerEmail = "john_doe@email.com";
        String customerPassword = "john_doe_password";
        String customerKey = "john_doe";
        String customerFirstName = "John";
        String customerLastName = "Doe";
        String customerCountry = "DE";

        final ProjectApiRoot apiRoot_poc =
                createApiClient(
                        ApiPrefixHelper.API_POC_CLIENT_PREFIX.getPrefix()
                );
        CustomerService customerService = new CustomerService(apiRoot_poc);
        CartService cartService = new CartService(apiRoot_poc);

        // TODO Step 1: Create a cart for the customer
        // TODO Add Line Items to it
        // TODO Copy the cart ID
        logger.info("Cart created: " +
                ""
        );

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
                .getKey()
        );

        String customerStreetName = "Hedderichstrasse";
        String customerStreetNumber = "43";
        String customerPostalCode = "60594";
        String customerCity = "Frankfurt am Main";

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
                .getAddresses()
        );

        String cartId = customerService.getCustomerByKey(customerKey)
            .thenComposeAsync(cartService::createCart)
            .get().getBody().getId();
        logger.info("Create a new cart.\n" + cartId);

        logger.info("Cart updated: " +
            cartService.getCartById(cartId)
                .thenComposeAsync(cartApiHttpResponse -> cartService.addProductToCartBySkusAndChannel(cartApiHttpResponse,"french-cooking-bundle"))
                .get().getBody().getId()
        );

        logger.info("Delete the customer.\n" +
            customerService.deleteCustomer(
                    customerService.getCustomerByKey(customerKey).get()
                )
                .get()
                .getBody()
                .getKey()
        );

        apiRoot_poc.close();
    }
}
