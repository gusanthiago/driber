
import java.util.ArrayList;
import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Location;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
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

	TelegramBot bot = TelegramBotAdapter.build("383542989:AAGgI48Fh4esc9mhKmMiJrCY7rYnpKkQxlI");	
	
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
	 * Queue for Message
	 */
	int queuesIndex=0;
	
	/**
	 * Flags for states
	 */
	boolean isRequestRide = false;
	boolean hasStartLocation = false;
	
	Location locationStart = null;
	Location locationFinish = null;
	
	List<ProductFare> listProductFare;
	
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
			
			//Queue of messages
			List<Update> updates = updatesResponse.updates();
			
			for (Update update : updates) {
				System.out.println(update.message().text());
				//updating queue's index
				queuesIndex = update.updateId()+1;	
				this.buildInteraction(update);
				
			}
	
		}
		
		
	}
	
	public void buildInteraction(Update update) {
		
		String messageText = null;
		
		// is start conversation
		if (update.message().text() != null && update.message().text().toUpperCase().equals("CANCELAR")) {
			this.cleanStates();
		} else if (this.isRequestRide == false && update.message().text() != null) {
			setController(new RideController(rideModel, this));
			this.isRequestRide = false;
			if (update.message().text().equals("TestApi")) {
				this.sendMessage(update.message().chat().id(), "Teste api");
				
			// todo upgrade text message initilize conversations
			} else if (update.message().text().toUpperCase().equals("OLA") || update.message().text().toUpperCase().equals("OLÁ")){ 	
				
				Keyboard keyboard = new ReplyKeyboardMarkup(
				        new KeyboardButton[] {
				                new KeyboardButton("Sim vamos lá"),
				                new KeyboardButton("Não")
			                }
				).oneTimeKeyboard(true);                
				messageText = "Olá " + update.message().from().firstName() + ", vamos viajar ?";
				this.sendMessageWithKeyBoard(
					update.message().chat().id(), 
					messageText, 
					keyboard
				);
				
			} else if (update.message().text().equals("Sim vamos lá")) {
				this.sendMessage(update.message().chat().id(), "Envie para mim sua localização de partida");
				this.isRequestRide = true;
			}
			
		// is get locations users
		} else if (this.isRequestRide && (this.locationFinish == null || this.locationStart == null)) {
			
			if (update.message().location() != null) {
				
				if (this.hasStartLocation == false) {
			
					this.locationStart = update.message().location();
					messageText = "Legal, agora precisamos do seu destino";
					this.sendMessage(update.message().chat().id(), messageText);	
					this.hasStartLocation = true;				
					
				} else {
					this.locationFinish = update.message().location();
					messageText = "Uau, vamos procurar...";
					this.sendMessage(update.message().chat().id(), messageText);
					listProductFare = this.controller.findAllProducts(this.locationStart, this.locationFinish);
					
					if (listProductFare.isEmpty()) {
						messageText = "Hmm, é uma penas mas, parece que não temos ubers disponíveis para sua região";
						this.sendMessage(update.message().chat().id(), messageText);	
						this.cleanStates();
					} else {
						
				        messageText = "";
				        KeyboardButton buttons[] = new KeyboardButton[listProductFare.size()];

				        for (int i = 0; i < listProductFare.size(); ++i) {
				        	messageText += listProductFare.get(i).getProduct().getDisplayName() + " ";
				        	messageText += listProductFare.get(i).getRideEstimate().getFare().getDisplay() + "\n";
				        	messageText += "\n";
				        	buttons[i] = new KeyboardButton(listProductFare.get(i).getProduct().getDisplayName());
				        }

				        Keyboard keyboard = new ReplyKeyboardMarkup(buttons).oneTimeKeyboard(true);
			        	this.sendMessageWithKeyBoard(update.message().chat().id(), messageText, keyboard);
						
					}
				
				}
				
	
			} else if (update.message().text() != null) {
				
				messageText = "Não entendi, precisamos da sua localização";
				if (this.hasStartLocation == false) {
					messageText += " de partida";
				} else {
					messageText += " final";
				}
				this.sendMessage(update.message().chat().id(), messageText);
			}
			
		// is working uber api
		} else if (update.message().text() != null && this.locationFinish != null && this.locationStart != null) {
			setController(new RideController(rideModel, this));
			
			for (ProductFare productFare : listProductFare) {
				if (update.message().text().equals(productFare.getProduct().getDisplayName())) {
					this.controller.request(update, productFare);
				}
			}
		}
		
	}
	
	public void cleanStates() {
		this.isRequestRide = false;
		this.hasStartLocation = false;
		this.locationStart = null;
		this.locationFinish = null;
	}
	
	public void update(long chatId, String data){
		sendResponse = bot.execute(new SendMessage(chatId, data));
	}
	
	public void sendMessage(Long long1, String message) {	
		SendMessage sendMessage = new SendMessage(long1, message);
		bot.execute(sendMessage);
	}
	
	public void sendMessageWithKeyBoard(Long long1, String message, Keyboard keyboard) {
		SendMessage sendMessage = new SendMessage(long1, message);
		sendMessage.replyMarkup(keyboard);
		bot.execute(sendMessage);
	}
	
	
	public void sendTypingMessage(Update update){
		baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));
	}
	
}
