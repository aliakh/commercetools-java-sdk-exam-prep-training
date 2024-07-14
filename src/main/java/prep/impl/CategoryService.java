package prep.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryDraftBuilder;
import com.commercetools.api.models.common.LocalizedString;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

public class CategoryService {

    final ProjectApiRoot apiRoot;

    public CategoryService(ProjectApiRoot apiRoot) {
        this.apiRoot = apiRoot;
    }

    public CompletableFuture<ApiHttpResponse<Category>> getCategoryByKey(
        String categoryKey)
    {
        return
            apiRoot
                .categories()
                .withKey(categoryKey)
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Category>> createCategory(

        final LocalizedString categoryName,
        final String categoryKey,
        final String orderHint)
    {
        return
            apiRoot
                .categories()
                .post(CategoryDraftBuilder.of()
                    .name(categoryName)
                    .key(categoryKey)
                    .orderHint(orderHint)
                    .slug(categoryName)
                    .build()
                )
                .execute();
    }
}
