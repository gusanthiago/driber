import java.util.LinkedList;
import java.util.List;


/**
 * View.java
 * 
 * Responsável por prover as informações da aplicação
 *
 * @author gusanthiago
 * @author hmmoreira
 */
public class Ride implements Subject {

	private static Ride instance;
	private List<Observer> observers = new LinkedList<Observer>();
	
	public static Ride getInstance() {
		if (instance == null) {
			instance = new Ride();
		}
		
		return instance;
	}
	
	public void registerObserver(Observer observer){
		observers.add(observer);
	}
}
