package prep;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.cart.LineItem;
import com.commercetools.api.models.customer.CustomerSigninBuilder;
import prep.impl.ApiPrefixHelper;
import prep.impl.CartService;
import prep.impl.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.commercetools.api.models.customer.AnonymousCartSignInMode.MERGE_WITH_EXISTING_CUSTOMER_CART;
import static com.commercetools.api.models.customer.AnonymousCartSignInMode.USE_AS_NEW_ACTIVE_CUSTOMER_CART;
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

        Cart cart = cartService.getCartById(cartId)
            .thenComposeAsync(cartApiHttpResponse ->
                cartService.addProductToCartBySkusAndChannel(cartApiHttpResponse, "HDG-02"))
            .get().getBody();
        logger.info("Add a line item to the cart.\n" +
            cart.getLineItems().stream().map(LineItem::getProductKey).collect(Collectors.joining())
        );



        String anonymousCartId1 = cartService.createAnonymousCart()
            .toCompletableFuture().get()
            .getBody().getId();
        logger.info("Create a new anonymous cart.\n" + anonymousCartId1);

        Cart anonymousCart1 = cartService.getCartById(anonymousCartId1)
            .thenComposeAsync(cartApiHttpResponse ->
                cartService.addProductToCartBySkusAndChannel(cartApiHttpResponse, "RWG-09"))
            .get().getBody();
        logger.info("Add a line item to the anonymous cart.\n" +
            anonymousCart1.getLineItems().stream().map(LineItem::getProductKey).collect(Collectors.joining(", "))
        );

        Cart mergedCart1 = apiRoot_poc
            .login()
            .post(
                CustomerSigninBuilder.of()
                    .anonymousCartSignInMode(MERGE_WITH_EXISTING_CUSTOMER_CART)
                    .email(customerEmail)
                    .password(customerPassword)
                    .anonymousCart(CartResourceIdentifierBuilder.of()
                        .id(anonymousCartId1)
                        .build())
                    .build()
            )
            .execute()
            .toCompletableFuture().get().getBody().getCart();
        logger.info("Merge the anonymous cart with mode MERGE_WITH_EXISTING_CUSTOMER_CART.\n" +
            mergedCart1.getLineItems().stream().map(LineItem::getProductKey).collect(Collectors.joining(", "))
        );



        String anonymousCartId2 = cartService.createAnonymousCart()
            .toCompletableFuture().get()
            .getBody().getId();
        logger.info("Create another new anonymous cart.\n" + anonymousCartId2);

        Cart anonymousCart2 = cartService.getCartById(anonymousCartId2)
            .thenComposeAsync(cartApiHttpResponse ->
                cartService.addProductToCartBySkusAndChannel(cartApiHttpResponse, "CDG-09"))
            .get().getBody();
        logger.info("Add a line item to the another anonymous cart.\n" +
            anonymousCart2.getLineItems().stream().map(LineItem::getProductKey).collect(Collectors.joining(", "))
        );

        Cart mergedCart2 = apiRoot_poc
            .login()
            .post(
                CustomerSigninBuilder.of()
                    .anonymousCartSignInMode(USE_AS_NEW_ACTIVE_CUSTOMER_CART)
                    .email(customerEmail)
                    .password(customerPassword)
                    .anonymousCart(CartResourceIdentifierBuilder.of()
                        .id(anonymousCartId2)
                        .build())
                    .build()
            )
            .execute()
            .toCompletableFuture().get().getBody().getCart();
        logger.info("Merge the anonymous cart with mode USE_AS_NEW_ACTIVE_CUSTOMER_CART.\n" +
            mergedCart2.getLineItems().stream().map(LineItem::getProductKey).collect(Collectors.joining(", "))
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
