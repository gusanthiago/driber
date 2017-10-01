import java.io.UnsupportedEncodingException;

import com.pengrad.telegrambot.model.Update;


/**
 * Controller.java
 * 
 * Fornece interface para as Controllers
 * 
 * @author gusanthiago
 * @author hmmoreira
 */
public interface Controller {
	
	public void request(Update update);
	
}
