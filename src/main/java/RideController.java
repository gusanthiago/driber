import java.io.UnsupportedEncodingException;
import java.util.List;

import com.pengrad.telegrambot.model.Location;
import com.pengrad.telegrambot.model.Update;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;

/**
 * RideController.java
 * 
 * Responsável por controller ações da corrida
 * 
 * @author gusanthiago
 * @author hmmoreira
 *
 */
public class RideController implements Controller {

	private RideModel rideModel;
	private View view;
	
	public RideController(RideModel model, View view){
		this.rideModel = model; //connection Controller -> Model
		this.view = view; //connection Controller -> View
	}
	
	public Ride request(Update update, ProductFare productFare) {
		view.sendTypingMessage(update);
		return rideModel.requestRide(productFare);
	}
	
	public List<ProductFare> findAllProducts(Location locationStart, Location locationFinish) {
		return this.rideModel.selectAllProductsFares(locationStart, locationFinish);
	}
	
}
