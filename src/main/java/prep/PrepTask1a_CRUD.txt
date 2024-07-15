package prep;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.tax_category.TaxRateDraft;
import com.commercetools.api.models.tax_category.TaxRateDraftBuilder;
import prep.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        String customerGroupName = "B2B Group L2";
        String customerGroupKey = "b2b-group-l2";
        String customerEmail = "leonardo1rossi@gcail.com";
        String customerPassword = "fdhghyg5%hg";
        String customerKey = "customer-leonardo1";
        String customerFirstName = "Leonardo";
        String customerLastName = "Rossi";
        String customerStreetName = "Am Borsigcurm";
        String customerStreetNumber = "2";
        String customerPostalCode = "13507 ";
        String customerCity = "Berlin";
        String customerCountry = "DE";
        String homeCategoryKey = "home";
        String homeCategoryOrderHint = "0.8";
        String electronicsCategoryKey = "electronics";
        String electronicsCategoryOrderHint = "0.9";

        String taxCategoryName = "new standard tax category";
        String taxCategoryKey = "new-standard-tax-category";
        List<TaxRateDraft> taxRates = new ArrayList<>();
        TaxRateDraft taxRateDraft =
                TaxRateDraftBuilder.of()
                        .name("Germany Tax")
                        .country("DE")
                        .amount(0.17)
                        .includedInPrice(true)
                        .build();
        taxRates.add(taxRateDraft);
        taxRateDraft =
                TaxRateDraftBuilder.of()
                        .name("Italy Tax")
                        .country("IT")
                        .amount(0.22)
                        .includedInPrice(true)
                        .build();
        taxRates.add(taxRateDraft);

        final LocalizedString localizedNameForHomeCategory = LocalizedStringBuilder.of()
                .values(new HashMap<String, String>() {
                    {
                        put("de", "Haus");
                        put("en", "Home");
                    }
                })
                .build();

        final LocalizedString localizedNameForElectronicsCategory = LocalizedStringBuilder.of()
                .values(new HashMap<String, String>() {
                    {
                        put("de", "Elektronik");
                        put("en", "Electronics");
                    }
                })
                .build();

        final ProjectApiRoot apiRoot_poc =
                createApiClient(
                        ApiPrefixHelper.API_POC_CLIENT_PREFIX.getPrefix()
                );

        CustomerService customerService = new CustomerService(apiRoot_poc);
        CustomerGroupService customerGroupService = new CustomerGroupService(apiRoot_poc);
        TaxCategoryService taxCategoryService = new TaxCategoryService(apiRoot_poc);
        CategoryService categoryService = new CategoryService(apiRoot_poc);

        logger.info("TODO List: Create a new customer.\n" +
                "Update the customer's billing address.\n" +
                "Create a customer group.\n" +
                "Assign the customer to the customer group.\n" +
                "Delete the customer.\n" +
                "Create a tax category.\n" +
                "Create a few product categories.\n" +
                "Query the categories by key.\n");

        logger.info("Create sign up completed." +
                customerService.createCustomer(
                                customerEmail,
                                customerPassword,
                                customerKey,
                                customerFirstName,
                                customerLastName,
                                customerCountry
                        )
                        .get().getBody().getCustomer().getKey()
        );

        logger.info("Customer billing address updated: " +
            customerService.updateCustomerBillingAddress(
                            customerService.getCustomerByKey(customerKey).get(),
                            customerStreetName,
                            customerStreetNumber,
                            customerPostalCode,
                            customerCity,
                            customerCountry).get().getBody().getKey()
        );

        logger.info("Customer group created: " +
                customerGroupService.createCustomerGroup(
                                customerGroupName,
                                customerGroupKey
                        )
                        .get()
                        .getBody().getName()
        );

        logger.info("Updated customer: " +
            customerService.assignCustomerToCustomerGroup(
                            customerService.getCustomerByKey(customerKey).get(),
                            customerGroupService.getCustomerGroupByKey(customerGroupKey).get())
                    .get().getBody().getKey()
        );

        logger.info("Deleted customer: " +
                customerService
                        .deleteCustomer(
                                customerService.getCustomerByKey(customerKey).get())
                        .get().getBody().getKey()
        );

        logger.info("Tax category created: " +
            taxCategoryService.createTaxCategory(
                    taxCategoryName,
                    taxCategoryKey,
                    taxRates)
                    .get().getBody().getName()
        );

        logger.info("Category created: " +
                categoryService.createCategory(
                                localizedNameForHomeCategory,
                                homeCategoryKey,
                                homeCategoryOrderHint)
                        .get().getBody().getName()
        );

        logger.info("Category created: " +
                categoryService.createCategory(
                                localizedNameForElectronicsCategory,
                                electronicsCategoryKey,
                                electronicsCategoryOrderHint)
                        .get().getBody().getName()
        );

        logger.info("Home category: " +
                categoryService.getCategoryByKey(
                                homeCategoryKey)
                        .get().getBody().getKey()
        );

        logger.info("Electronics category: " +
                categoryService.getCategoryByKey(
                                electronicsCategoryKey)
                        .get().getBody().getKey()
        );


        apiRoot_poc.close();
    }
}
