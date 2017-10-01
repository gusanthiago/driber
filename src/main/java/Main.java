
/**
 * Main.java
 * 
 * Responsável por inicializar a aplicação
 * 
 * @author gusanthiago
 * @author hmmoreira
 *
 */
public class Main {

	public static Ride model;
	
	public static void main(String[] args) {
		model = Ride.getInstance();
		initilizeModels(model);
		View view = new View(model);
		model.registerObserver(view);
		view.receiveUsersMessages();

	}
	
	// todo
	public static  void initilizeModels(Ride model) {
		
	}

	
	
	
}
