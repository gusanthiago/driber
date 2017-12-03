/**
 * Main.java
 * 
 * Responsável por inicializar a aplicação
 * 
 * @author gusanthiago
 * @author hmmoreira
 */
public class Main {

	public static RideModel model;
	
	public static void main(String[] args) {
		System.out.println("oi tudo bem");
		model = RideModel.getInstance();
		initializeModels(model);
		View view = new View(model);
		model.registerObserver(view);
		view.receiveUsersMessages();

	}
	
	// TODO pegar userId do uber
	public static void initializeModels(RideModel model) {
		
	}

	
	
}
