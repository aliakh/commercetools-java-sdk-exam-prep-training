package prep;

import com.commercetools.api.client.ProjectApiRoot;
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

        String sku = "RWG-09";

        ProductProjectionPagedSearchResponse response = apiRoot
            .productProjections()
            .search()
            .get()
            .withFilterQuery("variants.sku:\"" + sku + "\"")
            .executeBlocking()
            .getBody();

        Optional<ProductProjection> productProjection = response.getResults().stream().findFirst();
        if (productProjection.isPresent()) {
            ProductVariant variant = productProjection.get().getMasterVariant();
            System.out.println("variant: " + variant);
            System.out.println("is on stock: " + variant.getAvailability().getIsOnStock());
        } else {
            System.out.println("no variants found.");
        }

        // Build a set of functions to manage a basic Shopping Cart.
        //  Create a new Cart for an anonymous user.
        //  Add a Product (which has available inventory) to the Cart.
        //  Increase the quantity of the existing Line Item in the Cart (check that you have enough inventory for this item to complete the action).
        //  Make multiple changes to the Cart in a single request. This should include creating a Cart, adding an email address, Line Item, Country, and Locale.
    }
}
