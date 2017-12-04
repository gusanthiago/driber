import java.io.UnsupportedEncodingException;
import java.util.List;

import com.pengrad.telegrambot.model.Location;
import com.pengrad.telegrambot.model.Update;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;


/**
 * Controller.java
 * 
 * Fornece interface para as Controllers
 * 
 * @author gusanthiago
 * @author hmmoreira
 */
public interface Controller {
	
	public Ride request(Update update, ProductFare product);
	
	public List<ProductFare> findAllProducts(Location locationStart, Location locationFinish);
}
