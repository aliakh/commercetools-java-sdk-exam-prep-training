package prep.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.customer_group.CustomerGroup;
import com.commercetools.api.models.customer_group.TaxCategory;
import com.commercetools.api.models.tax_category.*;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TaxCategoryService {

    final ProjectApiRoot apiRoot;

    public TaxCategoryService(ProjectApiRoot apiRoot) {
        this.apiRoot = apiRoot;
    }

    public CompletableFuture<ApiHttpResponse<TaxCategory>> createTaxCategory(

        final String taxCategoryName,
        final String taxCategoryKey,
        final List<TaxRateDraft> taxRates)
    {
        return
            apiRoot
                .taxCategories()
                .post(
                    TaxCategoryDraftBuilder.of()
                        .name(taxCategoryName)
                        .key(taxCategoryKey)
                        .rates(taxRates)
                        .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<TaxCategory>> getTaxCategoryByKey(
        final String taxCategoryKey) {

        return
            apiRoot
                .taxCategories()
                .withKey(taxCategoryKey)
                .get()
                .execute();
    }
    
    public CompletableFuture<ApiHttpResponse<TaxCategory>> deleteTaxCategory(
        final ApiHttpResponse<TaxCategory> taxCategoryApiHttpResponse) {

        final TaxCategory taxCategory = taxCategoryApiHttpResponse.getBody();

        return
            apiRoot
                .taxCategories()
                .withKey(taxCategory.getKey())
                .delete()
                .withVersion(taxCategory.getVersion())
                .execute();
    }
}
