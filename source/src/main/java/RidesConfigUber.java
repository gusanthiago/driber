

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import retrofit2.Response;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import com.uber.sdk.rides.client.CredentialsSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.SessionConfiguration.Environment;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.error.ApiError;
import com.uber.sdk.rides.client.error.ClientError;
import com.uber.sdk.rides.client.error.ErrorParser;
import com.uber.sdk.rides.client.model.UserProfile;
import com.uber.sdk.rides.client.services.RidesService;

/**
 * SessionConfigUber.java
 * 
 * Responsável por configurar a sessão de acesso do uber
 * 
 * @author gusanthiago
 * @author hmmoreira
 */
public class RidesConfigUber {
	
	private static RidesConfigUber instance;

    private static LocalServerReceiver localServerReceiver;

    /**
     * Pega um ridesService
     * 
     * @return RidesService
     * @throws Exception
     */
    public static RidesService getInstance() throws Exception {
        
    	SessionConfiguration config = createSessionConfiguration();
        Credential credential = authenticate(System.getProperty("user.name"), config);
        CredentialsSession session = new CredentialsSession(config, credential);
        UberRidesApi uberRidesApi = UberRidesApi.with(session).build();
        RidesService service = uberRidesApi.createService();
        
        System.out.println("Buscando usuário na API");
        Response<UserProfile> response = service.getUserProfile().execute();

        ApiError apiError = ErrorParser.parseError(response);
        if (apiError != null) {
            // Handle error.
            ClientError clientError = apiError.getClientErrors().get(0);
            System.out.printf("Erro ao buscar usuário. %s", clientError.getTitle());
            return null;
        }
     
        UserProfile userProfile = response.body();
        System.out.printf("Logado como %s %s%n", userProfile.getFirstName(), userProfile.getLastName());
        
        return service;
    }

    /**
     * Authenticate the given user. If you are distributing an installed application, this method
     * should exist on your server so that the client ID and secret are not shared with the end
     * user.
     */
    private static Credential authenticate(String userId, SessionConfiguration config) throws Exception {
        OAuth2Credentials oAuth2Credentials = createOAuth2Credentials(config);

        // First try to load an existing Credential. If that credential is null, authenticate the user.
        Credential credential = oAuth2Credentials.loadCredential(userId);
        if (credential == null || credential.getAccessToken() == null) {
            // Send user to authorize your application.
            oAuth2Credentials.getRedirectUri();
    
            System.in.read();

            // Generate an authorization URL.
            String authorizationUrl = oAuth2Credentials.getAuthorizationUrl();
            System.out.printf("In your browser, navigate to: %s%n", authorizationUrl);
            System.out.println("Waiting for authentication...");

            // Wait for the authorization code.
            String authorizationCode = localServerReceiver.waitForCode();
            System.out.println("Authentication received.");

            // Authenticate the user with the authorization code.
            credential = oAuth2Credentials.authenticate(authorizationCode, userId);
        }
        localServerReceiver.stop();

        return credential;
    }

    /**
     * Creates an {@link OAuth2Credentials} object that can be used by any of the servlets.
     */
    public static OAuth2Credentials createOAuth2Credentials(SessionConfiguration sessionConfiguration) throws Exception {




        // Store the users OAuth2 credentials in their home directory.
        File credentialDirectory =
                new File(System.getProperty("user.home") + File.separator + ".uber_credentials");
        credentialDirectory.setReadable(true, true);
        credentialDirectory.setWritable(true, true);
        // If you'd like to store them in memory or in a DB, any DataStoreFactory can be used.
        AbstractDataStoreFactory dataStoreFactory = new FileDataStoreFactory(credentialDirectory);

        // Build an OAuth2Credentials object with your secrets.
        return new OAuth2Credentials.Builder()
                .setCredentialDataStoreFactory(dataStoreFactory)
                .setRedirectUri(sessionConfiguration.getRedirectUri())
                .setClientSecrets(sessionConfiguration.getClientId(), sessionConfiguration.getClientSecret())
                .build();
    }
    
    /**
     * Configura sessão do usuário
     * 
     * @return SessionConfiguration
     * @throws Exception
     */
    public static SessionConfiguration createSessionConfiguration() throws Exception {
        // Load the client ID and secret from {@code resources/secrets.properties}. Ideally, your
        // secrets would not be kept local. Instead, have your server accept the redirect and return
        // you the accessToken for a userId.
        Properties secrets = loadSecretProperties();

        String clientId = secrets.getProperty("clientId");
        String clientSecret = secrets.getProperty("clientSecret");

        if (clientId.equals("INSERT_CLIENT_ID_HERE") || clientSecret.equals("INSERT_CLIENT_SECRET_HERE")) {
            throw new IllegalArgumentException(
                    "Please enter your client ID and secret in the resources/secrets.properties file.");
        }

        // Start a local server to listen for the OAuth2 redirect.
        localServerReceiver = new LocalServerReceiver.Builder().setPort(8181).build();
        String redirectUri = localServerReceiver.getRedirectUri();

        return new SessionConfiguration.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .setEnvironment(Environment.SANDBOX)
                .build();

    }

    /**
     * Loads the application's secrets.
     */
    private static Properties loadSecretProperties() throws Exception {
        Properties properties = new Properties();
        InputStream propertiesStream = RidesConfigUber.class.getClassLoader().getResourceAsStream("secrets.properties");
        if (propertiesStream == null) {
            // Fallback to file access in the case of running from certain IDEs.
            File buildPropertiesFile = new File("src/main/resources/secrets.properties");
            if (buildPropertiesFile.exists()) {
                properties.load(new FileReader(buildPropertiesFile));
            } else {
                buildPropertiesFile = new File("driber/src/main/resources/secrets.properties");
                if (buildPropertiesFile.exists()) {
                    properties.load(new FileReader(buildPropertiesFile));
                } else {
                    throw new IllegalStateException("Could not find secrets.properties");
                }
            }
        } else {
            properties.load(propertiesStream);
        }
        return properties;
    }
    

}