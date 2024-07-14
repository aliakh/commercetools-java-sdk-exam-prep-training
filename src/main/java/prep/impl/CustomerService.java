package prep.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.customer_group.CustomerGroup;
import com.commercetools.api.models.customer_group.CustomerGroupResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Customer}s.
 */
public class CustomerService {

    final ProjectApiRoot apiRoot;

    public CustomerService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

        public CompletableFuture<ApiHttpResponse<Customer>> getCustomerById(
                String customerId)
        {
            return
                    null;
        }

        public CompletableFuture<ApiHttpResponse<Customer>> getCustomerByKey(
            String customerKey) {
            return apiRoot
                .customers()
                .withKey(customerKey)
                .get()
                .execute();
        }

        public CompletableFuture<ApiHttpResponse<CustomerSignInResult>> createCustomer(
            final String email,
            final String password,
            final String customerKey,
            final String firstName,
            final String lastName,
            final String country)
        {
            return apiRoot
                .customers()
                .post(CustomerDraftBuilder.of()
                    .email(email)
                    .password(password)
                    .firstName(firstName)
                    .lastName(lastName)
                    .key(customerKey)
                    .addresses(
                        AddressBuilder.of()
                            .key(customerKey + "-" + country)
                            .country(country)
                            .build()
                    )
                    .defaultShippingAddress(0)
                    .build())
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Customer>> updateCustomerBillingAddress(
        final ApiHttpResponse<Customer> customerApiHttpResponse,
        final String streetName,
        final String streetNumber,
        final String postalCode,
        final String city,
        final String country) {

        final Customer customer = customerApiHttpResponse.getBody();

        return
            apiRoot
                .customers()
                .withKey(customer.getKey())
                .post(CustomerUpdateBuilder.of()
                    .version(customer.getVersion())
                    .actions(
                        CustomerChangeAddressActionBuilder.of()
                            .addressKey(customer.getKey() + "-" + country)
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

    public CompletableFuture<ApiHttpResponse<Customer>> assignCustomerToCustomerGroup(
            final ApiHttpResponse<Customer> customerApiHttpResponse,
            final ApiHttpResponse<CustomerGroup> customerGroupApiHttpResponse)
    {
        final Customer customer = customerApiHttpResponse.getBody();
        final CustomerGroup customerGroup = customerGroupApiHttpResponse.getBody();

        return
            apiRoot
                .customers()
                .withKey(customer.getKey())
                .post(CustomerUpdateBuilder.of()
                    .version(customer.getVersion())
                    .actions(
                        CustomerSetCustomerGroupActionBuilder.of()
                            .customerGroup(CustomerGroupResourceIdentifierBuilder.of()
                                .key(customerGroup.getKey())
                                .build())
                            .build()
                    )
                    .build())
                .execute();
    }
}
