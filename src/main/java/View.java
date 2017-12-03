import java.io.UnsupportedEncodingException;
import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;


/**
 * View.java
 * 
 * Criado por monitorar interação do usuário
 *
 * @author gusanthiago
 * @author hmmoreira
 */
public class View implements Observer {

	TelegramBot bot = TelegramBotAdapter.build("465392658:AAF293v7b3VEUtriI6WNJKnUTB2jOooGWCs");	
	
	/**
	 * Request and response for Telegram API 
	 */
	GetUpdatesResponse updatesResponse;
	SendResponse sendResponse;
	BaseResponse baseResponse;
		
	/**
	 * Models
	 */
	private RideModel rideModel;
	
	/**
	 * Interface Controller
	 */
	private Controller controller;
	
	/**
	 * Behavior flag
	 */
	boolean behaviour = false;
	
	
	/**
	 * Queue for Message
	 */
	int queuesIndex=0;
	
	/**
	 * Construtor da View
	 * @param model
	 */
	public View(RideModel model){
		this.rideModel = model; 
	}

	public void setController(Controller controller){ //Strategy Pattern
		this.controller = controller;
	}
	
	public void receiveUsersMessages() {

		while (true){

			
			updatesResponse =  bot.execute(new GetUpdates().limit(100).offset(queuesIndex));	
			List<Update> updates = updatesResponse.updates();
//			GetUpdates getUpdates = new GetUpdates().limit(100).offset(0).timeout(queuesIndex);
//			List<Update> updates = updatesResponse.updates();
			
			if (updates != null) {
				for (Update update : updates) {
					
					//updating queue's index
					queuesIndex = update.updateId()+1;	
					
					
					if(this.behaviour==true){
						this.callController(update);
						
					} else if (update.message().text().equals("TestApi")) {
						setController(new RideController(rideModel, this));
						sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Teste api"));
						this.behaviour = true;
						this.callController(update);
					}
					
				}
			} else {
				System.out.println("null");
			}
		}
		
		
	}
	
	
	public void callController(Update update){
		this.controller.request(update);
		this.behaviour = false;
	}
	
	public void update(long chatId, String data){
		sendResponse = bot.execute(new SendMessage(chatId, data));
	}
	
	public void sendTypingMessage(Update update){
		baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));
	}
	
}
