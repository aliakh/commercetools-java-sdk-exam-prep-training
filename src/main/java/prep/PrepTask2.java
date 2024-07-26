package prep;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartAddLineItemActionBuilder;
import com.commercetools.api.models.cart.CartChangeLineItemQuantityActionBuilder;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.cart.CartUpdateActionBuilder;
import com.commercetools.api.models.cart.CartUpdateBuilder;
import com.commercetools.api.models.cart.LineItem;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerChangeAddressActionBuilder;
import com.commercetools.api.models.customer.CustomerDraftBuilder;
import com.commercetools.api.models.customer.CustomerUpdateBuilder;
import com.commercetools.api.models.product.ProductProjection;
import com.commercetools.api.models.product.ProductProjectionPagedSearchResponse;
import com.commercetools.api.models.product.ProductVariant;
import com.commercetools.api.models.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prep.impl.ApiPrefixHelper;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static prep.impl.ClientService.createApiClient;

public class PrepTask2 {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(PrepTask1b_CREATE_CARTS.class.getName());

        final ProjectApiRoot apiRoot =
            createApiClient(
                ApiPrefixHelper.API_POC_CLIENT_PREFIX.getPrefix()
            );

        Project project = apiRoot
            .withProjectKey("cool-store")
            .get()
            .execute()
            .get()
            .getBody();

        logger.info("countries: {}", project.getCountries());
        logger.info("currencies: {}", project.getCurrencies());
        logger.info("languages: {}", project.getLanguages());
        logger.info("trial until: {}", project.getTrialUntil());

        String customerEmail = "john_doe@email.com";
        String customerPassword = "john_doe_password";
        String customerKey = "john_doe";
        String customerFirstName = "John";
        String customerLastName = "Doe";
        String customerCountry = "DE";

        // Update a Customer's phone number:
        //  Create a function that takes a Customer key, version, and updates the phone number.
        //  Handle version mismatch.

        Customer customer1 = apiRoot
            .customers()
            .post(CustomerDraftBuilder.of()
                .email(customerEmail)
                .password(customerPassword)
                .firstName(customerFirstName)
                .lastName(customerLastName)
                .key(customerKey)
                .addresses(
                    AddressBuilder.of()
                        .key(customerKey + "-" + customerCountry)
                        .country(customerCountry)
                        .build()
                )
                .defaultShippingAddress(0)
                .build())
            .execute()
            .get()
            .getBody()
            .getCustomer();

        Address address1 = customer1.getAddresses().get(0);
        logger.info("old phone: {}", address1.getPhone());

        Customer customer2 = apiRoot
            .customers()
            .withKey(customer1.getKey())
            .post(CustomerUpdateBuilder.of()
                .version(customer1.getVersion())
                .actions(
                    CustomerChangeAddressActionBuilder.of()
                        .addressKey(address1.getKey())
                        .address(
                            AddressBuilder.of(address1)
                                .phone(String.valueOf(Math.abs(new Random().nextInt())))
                                .build()
                        )
                        .build()
                )
                .build()
            )
            .execute()
            .get()
            .getBody();

        Address address2 = customer2.getAddresses().get(0);
        logger.info("new phone: {}", address2.getPhone());

        apiRoot
            .customers()
            .withKey(customer2.getKey())
            .delete()
            .withVersion(customer2.getVersion())
            .execute()
            .get()
            .getBody();

        // Check if a specific Product Variant (identified by an sku) is in stock for a given Store.
        //  Fetch the Product Variant using Product Search or Product Projection Search.
        //  Determine if there is at least one Inventory entry for the Store.
        //  Console log a user-friendly message indicating availability.

        ProductProjectionPagedSearchResponse response = apiRoot
            .productProjections()
            .search()
            .get()
            .withFilterQuery("variants.sku:\"RWG-09\"")
            .executeBlocking()
            .getBody();

        Optional<ProductProjection> productProjection = response.getResults().stream().findFirst();
        if (productProjection.isPresent()) {
            ProductVariant variant = productProjection.get().getMasterVariant();
            logger.info("variant: {}", variant);
            logger.info("is on stock: {}", variant.getAvailability().getIsOnStock());
        } else {
            logger.info("no variants found");
        }

        // Build a set of functions to manage a basic Shopping Cart.
        //  Create a new Cart for an anonymous user.
        //  Add a Product (which has available inventory) to the Cart.
        //  Increase the quantity of the existing Line Item in the Cart (check that you have enough inventory for this item to complete the action).
        //  Make multiple changes to the Cart in a single request. This should include creating a Cart, adding an email address, Line Item, Country, and Locale.

        Cart cart1 = apiRoot
            .carts()
            .post(
                CartDraftBuilder.of()
                    .currency("EUR")
                    .deleteDaysAfterLastModification(90L)
                    .anonymousId("anonymous" + System.nanoTime())
                    .country("DE")
                    .build()
            )
            .execute()
            .get()
            .getBody()
            .get();
        logger.info("anonymous cart created: {}", cart1);

        Cart cart2 = apiRoot
            .carts()
            .withId(cart1.getId())
            .get()
            .execute()
            .thenComposeAsync(cartApiHttpResponse -> {
                    Cart cart = cartApiHttpResponse.getBody();
                    return apiRoot.carts()
                        .withId(cart.getId())
                        .post(
                            CartUpdateBuilder.of()
                                .version(cart.getVersion())
                                .actions(
                                    Stream.of("RWG-09")
                                        .map(sku -> CartAddLineItemActionBuilder.of()
                                            .sku(sku)
                                            .build()
                                        )
                                        .collect(Collectors.toList())
                                )
                                .build()
                        )
                        .execute();
                }
            )
            .get().getBody();

        LineItem lineItem2 = cart2.getLineItems().stream().findFirst().orElseThrow();
        logger.info("product added to cart: {}",
            "product key: " + lineItem2.getProductKey() + ", quantity " + lineItem2.getQuantity()
        );

        Cart cart3 = apiRoot.carts()
            .withId(cart2.getId())
            .post(
                CartUpdateBuilder.of()
                    .version(cart2.getVersion())
                    .actions(
                        CartChangeLineItemQuantityActionBuilder.of()
                            .lineItemId(lineItem2.getId())
                            .quantity(2L)
                            .build()
                    )
                    .build()
            )
            .execute().get().getBody();

        LineItem lineItem3 = cart3.getLineItems().stream().findFirst().orElseThrow();
        logger.info("product increased to cart: {}",
            "product key: " + lineItem3.getProductKey() + ", quantity " + lineItem3.getQuantity()
        );

        Cart cart4 = apiRoot
            .carts()
            .post(
                CartDraftBuilder.of()
                    .currency("EUR")
                    .deleteDaysAfterLastModification(90L)
                    .anonymousId("anonymous" + System.nanoTime())
                    .country("DE")
                    .build()
            )
            .execute()
            .thenComposeAsync(cartApiHttpResponse -> {
                    Cart cart = cartApiHttpResponse.getBody();
                    return apiRoot.carts()
                        .withId(cart.getId())
                        .post(
                            CartUpdateBuilder.of()
                                .version(cart.getVersion())
                                .actions(
                                    Stream.of(
                                            CartUpdateActionBuilder.of()
                                                .setCustomerEmailBuilder().email(Math.abs(new Random().nextInt()) + "@email.com")
                                                .build(),
                                            CartAddLineItemActionBuilder.of()
                                                .sku("RWG-09")
                                                .build(),
                                            CartUpdateActionBuilder.of()
                                                .setCountryBuilder().country("AU")
                                                .build(),
                                            CartUpdateActionBuilder.of()
                                                .setLocaleBuilder().locale("en-AU")
                                                .build()
                                        )
                                        .collect(Collectors.toList())
                                )
                                .build()
                        )
                        .execute();
                }
            )
            .get()
            .getBody()
            .get();

//        LineItem lineItem4 = cart4.getLineItems().stream().findFirst().orElseThrow();
        logger.info("cart created: {}",
            " customer email: " + cart4.getCustomerEmail() +
//                ", line item: product key: " + lineItem4.getProductKey() + ", quantity " + lineItem4.getQuantity() +
                ", country: " + cart4.getCountry() +
                ", locale: " + cart4.getLocale()
        );

//        HTTP API queries and Query Predicates
//        Write a function that retrieves all Customers from a specific country.
//            Modify the previous function to retrieve Customers from a specific city within a Country, ensuring the search is case-insensitive. (Hint: explore Query Predicates in the documentation).
//        Make a request to get data for a Product Listing Page.
//        Facets displayed should be: Price and Category
//        Twelve Products should be displayed at a time. Display the second page of results.
//        Products should be ordered by the setCategoryOrderHint. This ensures that Products are displayed in the correct merchandised order.
//            Filter by a single Store projection.
//            Use the Product Projections or Product Search endpoint.
//            Create a function that takes a Category's id and a maximum price as input and returns all the Products within that Category below the specified price.
//        Write a function that accepts an array of sku and returns all products that have at least one Product Variant matching any of the provided sku values.
    }
}
