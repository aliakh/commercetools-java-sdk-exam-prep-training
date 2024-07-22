package prep;

import com.commercetools.api.client.ProjectApiRoot;
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

        final ProjectApiRoot apiRoot_poc =
            createApiClient(
                ApiPrefixHelper.API_POC_CLIENT_PREFIX.getPrefix()
            );

        Project project = apiRoot_poc
            .withProjectKey("cool-store")
            .get()
            .execute()
            .get()
            .getBody();

        logger.info("countries {}", project.getCountries());
        logger.info("currencies {}", project.getCurrencies());
        logger.info("languages {}", project.getLanguages());
        logger.info("trial until {}", project.getTrialUntil());
    }
}
