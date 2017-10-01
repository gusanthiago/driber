import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.SessionConfiguration.Environment;
import com.uber.sdk.rides.client.services.RidesService;


/**
 * RideModel.java
 * 
 * Responsável por prover as informações da corrida
 *
 * @author gusanthiago
 * @author hmmoreira
 */
public class RideModel implements Subject {

	private static RideModel instance;
	private List<Observer> observers = new LinkedList<Observer>();
	
	public static RideModel getInstance() {
		if (instance == null) {
			instance = new RideModel();
		}
		return instance;
	}
	
	public void registerObserver(Observer observer) {
		observers.add(observer);
	}
	
	public void notifyObservers(long chatId, String data){
		for(Observer observer:observers){
			observer.update(chatId, data);
		}
	}
	
	// TODO request car
	public static void requestRide() {
		
		SessionConfigUber sessionConfig = new SessionConfigUber();
		try {
			sessionConfig.getSessionConfig();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
