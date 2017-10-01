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

	TelegramBot bot = TelegramBotAdapter.build("465392658:AAExTEgOlK6ar4HHvhHjFID5-qCBRNH5fDw");	
	
	/**
	 * Request and response for Telegram API 
	 */
	GetUpdatesResponse updatesResponse;
	SendResponse sendResponse;
	BaseResponse baseResponse;
		
	/**
	 * Models
	 */
	private Ride model;
	
	int queuesIndex=0;
	
	public View(Ride model){
		this.model = model; 
	}
	
	public void receiveUsersMessages() {

		//infinity loop
		while (true){
		
			//taking the Queue of Messages
			updatesResponse =  bot.execute(new GetUpdates().limit(100).offset(queuesIndex));
			
			//Queue of messages
			List<Update> updates = updatesResponse.updates();

			//taking each message in the Queue
			for (Update update : updates) {
				
				//updating queue's index
				queuesIndex = update.updateId()+1;	
				System.out.println(update.message().text());
				
				
			}

		}
		
		
	}
	
	
	public void callController(Update update){
	
	}
	
	public void update(long chatId, String studentsData){
		sendResponse = bot.execute(new SendMessage(chatId, studentsData));
	}
	
	public void sendTypingMessage(Update update){
		baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));
	}
	
}
