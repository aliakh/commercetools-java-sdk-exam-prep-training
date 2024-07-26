package prep;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartAddLineItemActionBuilder;
import com.commercetools.api.models.cart.CartChangeLineItemQuantityActionBuilder;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.cart.CartUpdateActionBuilder;
import com.commercetools.api.models.cart.CartUpdateBuilder;
import com.commercetools.api.models.cart.LineItem;
import com.commercetools.api.models.category.CategoryReference;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.common.BaseAddress;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerChangeAddressActionBuilder;
import com.commercetools.api.models.customer.CustomerDraftBuilder;
import com.commercetools.api.models.customer.CustomerPagedQueryResponse;
import com.commercetools.api.models.customer.CustomerUpdateBuilder;
import com.commercetools.api.models.product.ProductPagedQueryResponse;
import com.commercetools.api.models.product.ProductProjection;
import com.commercetools.api.models.product.ProductProjectionPagedQueryResponse;
import com.commercetools.api.models.product.ProductProjectionPagedSearchResponse;
import com.commercetools.api.models.product.ProductVariant;
import com.commercetools.api.models.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prep.impl.ApiPrefixHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static prep.impl.ClientService.createApiClient;

public class PrepTask3 {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(PrepTask1b_CREATE_CARTS.class.getName());

        final ProjectApiRoot apiRoot =
            createApiClient(
                ApiPrefixHelper.API_POC_CLIENT_PREFIX.getPrefix()
            );

        // HTTP API queries and Query Predicates
        String countryCode = "DE";
        String city = "Potsdam";

        // Write a function that retrieves all Customers from a specific country.
        apiRoot
            .customers()
            .get()
            .withWhere("addresses(country=\"" + countryCode + "\")")
            .executeBlocking()
            .getBody()
            .getResults()
            .forEach(customer ->
                logger.info("customer: {}", customer.getFirstName()
                    + " " + customer.getLastName()
                    + " " + customer.getAddresses()
                    .stream()
                    .map(BaseAddress::getCity)
                    .collect(Collectors.toList())
                )
            );

        // Modify the previous function to retrieve Customers from a specific city within a Country, ensuring the search is case-insensitive. (Hint: explore Query Predicates in the documentation).
        apiRoot
            .customers()
            .get()
            .withWhere(String.format("addresses(country=\"%s\" and city=\"%s\")", countryCode, city))
            .executeBlocking()
            .getBody()
            .getResults()
            .forEach(customer ->
                logger.info("customer: {}", customer.getFirstName()
                    + " " + customer.getLastName()
                    + " " + customer.getAddresses()
                    .stream()
                    .map(BaseAddress::getCity)
                    .collect(Collectors.toList())
                )
            );

//        Make a request to get data for a Product Listing Page.
//          Facets displayed should be: Price and Category
//          Twelve Products should be displayed at a time. Display the second page of results.
//          Products should be ordered by the setCategoryOrderHint. This ensures that Products are displayed in the correct merchandised order.
//          Filter by a single Store projection.
//          Use the Product Projections or Product Search endpoint.

        ProductProjectionPagedQueryResponse productProjectionPagedQueryResponse = apiRoot
            .productProjections()
            .get()
            .withOffset(12)
            .withLimit(12)
            .withSort("categoryOrderHints.f3697de5-4208-4b30-8c87-6f6307b03619 asc")
            .executeBlocking()
            .getBody();

        productProjectionPagedQueryResponse.getResults().forEach(productProjection -> {
                logger.info("key: {}", productProjection.getKey());
            }
        );

        ProductProjectionPagedSearchResponse productProjectionPagedSearchResponse = apiRoot
            .productProjections()
            .search()
            .get()
            .withOffset(12)
            .withLimit(12)
            .withSort("categoryOrderHints.f3697de5-4208-4b30-8c87-6f6307b03619 asc")
            .withFacet("price")
            .addFacet("categories.id")
            .executeBlocking()
            .getBody();

        productProjectionPagedSearchResponse.getResults().forEach(productProjection -> {
                logger.info("key: {}", productProjection.getKey());
            }
        );
        logger.info("facets: {}", productProjectionPagedSearchResponse.getFacets());

        // Create a function that takes a Category's id and a maximum price as input and returns all the Products within that Category below the specified price.
        ProductProjectionPagedQueryResponse responseByPrice = apiRoot
            .productProjections()
            .get()
            .withWhere("categories(id=\"82f452b8-6c0c-4bef-b115-f549c7e68a0d\") and variants(prices(value(centAmount < 10000)))")
            .executeBlocking()
            .getBody();

        logger.info("response: {}", responseByPrice);
        responseByPrice
            .getResults()
            .forEach(productProjection -> {
                logger.info("prices: {}", productProjection
                    .getVariants()
                    .stream()
                    .map(
                        productVariant -> productVariant.getPrices()
                            .stream()
                            .map(price -> price.getValue().getCentAmount())
                            .collect(Collectors.toList())
                    )
                    .collect(Collectors.toList()));
            }
        );
        responseByPrice
            .getResults()
            .forEach(productProjection -> {
                logger.info("categories: {}", productProjection
                    .getCategories()
                    .stream()
                    .map(CategoryReference::getId)
                    .collect(Collectors.toList()));
            }
        );

        // Write a function that accepts an array of sku and returns all products that have at least one Product Variant matching any of the provided sku values.
        ProductProjectionPagedQueryResponse responseBySkus = apiRoot
            .productProjections()
            .get()
            .withWhere(
                Stream.of("MR-08", "NTSS-02", "GPC-02", "MUTB-02")
                    .map(sku -> String.format("variants(sku=\"%s\")", sku))
                    .collect(Collectors.joining(" or "))
            )
            .executeBlocking()
            .getBody();

        responseBySkus
            .getResults()
            .forEach(productProjection -> {
                logger.info("key: {}", productProjection.getKey());

                logger.info("skus: {}", productProjection
                    .getVariants()
                    .stream()
                    .map(
                        productVariant -> productVariant.getSku()
                    )
                    .collect(Collectors.toList()));
            }
        );
    }
}
