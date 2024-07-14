package prep;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.tax_category.TaxRateDraft;
import com.commercetools.api.models.tax_category.TaxRateDraftBuilder;

import prep.impl.CategoryService;
import prep.impl.CustomerGroupService;
import prep.impl.CustomerService;
import prep.impl.ApiPrefixHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prep.impl.TaxCategoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
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
        CustomerGroupService customerGroupService = new CustomerGroupService(apiRoot_poc);
        TaxCategoryService taxCategoryService = new TaxCategoryService(apiRoot_poc);
        CategoryService categoryService = new CategoryService(apiRoot_poc);

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

        String customerGroupName = "Customer Group 1";
        String customerGroupKey = "customer-group-1";

        logger.info("Create a customer group.\n" +
            customerGroupService.createCustomerGroup(
                    customerGroupName,
                    customerGroupKey
                )
                .get()
                .getBody()
                .getKey()
        );

        logger.info("Assign the customer to the customer group.\n" +
            customerService.assignCustomerToCustomerGroup(
                    customerService.getCustomerByKey(customerKey).get(),
                    customerGroupService.getCustomerGroupByKey(customerGroupKey).get()
                )
                .get()
                .getBody()
                .getKey()
        );

        logger.info("Delete the customer.\n" +
            customerService.deleteCustomer(
                    customerService.getCustomerByKey(customerKey).get()
                )
                .get()
                .getBody()
                .getKey()
        );

        logger.info("Delete the customer group.\n" +
            customerGroupService.deleteCustomerGroup(
                    customerGroupService.getCustomerGroupByKey(customerGroupKey).get()
                )
                .get()
                .getBody()
                .getKey()
        );

        String taxCategoryName = "Tax Category 1";
        String taxCategoryKey = "tax-category-1";

        List<TaxRateDraft> taxRates = new ArrayList<>();

        TaxRateDraft taxRateDraft1 =
            TaxRateDraftBuilder.of()
                .name("Germany Tax")
                .country("DE")
                .amount(0.14)
                .includedInPrice(true)
                .build();
        taxRates.add(taxRateDraft1);

        TaxRateDraft taxRateDraft2 =
            TaxRateDraftBuilder.of()
                .name("France Tax")
                .country("FR")
                .amount(0.11)
                .includedInPrice(true)
                .build();
        taxRates.add(taxRateDraft2);

        logger.info("Create a tax category.\n" +
            taxCategoryService.createTaxCategory(
                    taxCategoryName,
                    taxCategoryKey,
                    taxRates
                )
                .get()
                .getBody()
                .getKey()
        );

        LocalizedString categoryName1 = LocalizedStringBuilder.of()
            .values(new HashMap<String, String>() {
                {
                    put("en", "Home");
                    put("de", "Haus");
                }
            })
            .build();

        LocalizedString categoryName2 = LocalizedStringBuilder.of()
            .values(new HashMap<String, String>() {
                {
                    put("en", "Garden");
                    put("de", "Garten");
                }
            })
            .build();

        String categoryKey1 = "home";
        String orderHint1 = "0.9";
        String categoryKey2 = "garden";
        String orderHint2 = "0.8";

        logger.info("Create one product category.\n" +
            categoryService.createCategory(
                    categoryName1,
                    categoryKey1,
                    orderHint1)
                .get()
                .getBody()
                .getName()
        );

        logger.info("Create another product category.\n" +
            categoryService.createCategory(
                    categoryName2,
                    categoryKey2,
                    orderHint2)
                .get()
                .getBody()
                .getName()
        );

        logger.info("Query one category by key.\n" +
            categoryService.getCategoryByKey(categoryKey1)
                .get()
                .getBody()
                .getKey()
        );

        logger.info("Query another category by key.\n" +
            categoryService.getCategoryByKey(categoryKey2)
                .get()
                .getBody()
                .getKey()
        );

        logger.info("Delete one category.\n" +
            categoryService.deleteCategory(
                    categoryService.getCategoryByKey(categoryKey1).get()
                )
                .get()
                .getBody()
                .getKey()
        );

        logger.info("Delete another category.\n" +
            categoryService.deleteCategory(
                    categoryService.getCategoryByKey(categoryKey2).get()
                )
                .get()
                .getBody()
                .getKey()
        );

        logger.info("Delete the tax category.\n" +
            taxCategoryService.deleteTaxCategory(
                    taxCategoryService.getTaxCategoryByKey(taxCategoryKey).get()
                )
                .get()
                .getBody()
                .getKey()
        );

        apiRoot_poc.close();
    }
}
