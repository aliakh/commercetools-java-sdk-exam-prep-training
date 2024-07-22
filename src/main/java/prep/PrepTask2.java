package prep;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerChangeAddressActionBuilder;
import com.commercetools.api.models.customer.CustomerUpdateBuilder;
import com.commercetools.api.models.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prep.impl.ApiPrefixHelper;

import java.io.IOException;
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

        logger.info("countries {}", project.getCountries());
        logger.info("currencies {}", project.getCurrencies());
        logger.info("languages {}", project.getLanguages());
        logger.info("trial until {}", project.getTrialUntil());

        String customerEmail = "john_doe@email.com";
        String customerPassword = "john_doe_password";
        String customerKey = "john_doe";
        String customerFirstName = "John";
        String customerLastName = "Doe";
        String customerCountry = "DE";

        Customer customer = apiRoot
            .customers()
            .withKey(customerKey)
            .get()
            .execute()
            .get()
            .getBody();

        apiRoot
            .customers()
            .withKey(customer.getKey())
            .post(CustomerUpdateBuilder.of()
                .version(customer.getVersion())
                .actions(
                    CustomerChangeAddressActionBuilder.of()
                        .addressKey(customer.getKey() + "-" + customerCountry)
                        .address(
                            AddressBuilder.of()
                                .firstName(customer.getFirstName())
                                .lastName(customer.getLastName())
                                .streetName(streetName)
                                .streetNumber(streetNumber)
                                .postalCode(postalCode)
                                .city(city)
                                .country(country)
                                .build()
                        )
                        .build()
                )
                .build()
            )
            .execute();
    }
}
