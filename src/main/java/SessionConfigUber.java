import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.Credential;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import com.uber.sdk.rides.client.CredentialsSession;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.Session;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.SessionConfiguration.Environment;

/**
 * SessionConfigUber.java
 * 
 * Responsável por configurar a sessão de acesso do uber
 * 
 * @author gusanthiago
 * @author hmmoreira
 */
public class SessionConfigUber {
	
	private static SessionConfigUber instance;
	
	public static ServerTokenSession getSessionConfig() throws UnsupportedEncodingException {
		
		SessionConfiguration config = new SessionConfiguration.Builder()
		    .setClientId("gMDRyAPtUbuYzT3YSo1xUHp4fMqPE8jt")
		    .setEnvironment(Environment.SANDBOX)
		    .setServerToken("Dws-BSWccRbrDEz-v-44Fao4Dt6GwNntdsfRZr17")
		    .build();

		ServerTokenSession session = new ServerTokenSession(config);
		
		// testando  config
		System.out.println(config.getServerToken());
		
		return session;
	}
	
}
