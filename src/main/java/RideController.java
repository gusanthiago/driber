import java.io.UnsupportedEncodingException;

import com.pengrad.telegrambot.model.Update;

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
	
	public void request(Update update) {
		view.sendTypingMessage(update);
		rideModel.requestRide();
	}
}
