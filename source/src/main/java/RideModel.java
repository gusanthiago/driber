import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Response;

import com.pengrad.telegrambot.model.Location;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.ProductsResponse;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.services.RidesService;


/**
 * RideModel.java
 * 
 * Responsável por prover as informações da corrida
 *
 * @author gusanthiago
 * @author hmmoreira
 */
public class RideModel implements Subject {

	private static RideModel instance;
	private static RidesService ridesService;
	private List<Observer> observers = new LinkedList<Observer>();
	
	
	public static RideModel getInstance() {
		if (instance == null) {
			instance = new RideModel();
		}
		try {
			ridesService = RidesConfigUber.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return instance;
	}
	
	public void registerObserver(Observer observer) {
		observers.add(observer);
	}
	
	public void notifyObservers(long chatId, String data){
		for(Observer observer:observers){
			observer.update(chatId, data);
		}
	}
	
	/**
	 * Ride
	 * 
	 * @param productFare
	 * @return
	 */
	public Ride requestRide(ProductFare productFare) {
		
		Ride ride = null;
		try {
			
			RideRequestParameters ridesRequestParameters = new RideRequestParameters.Builder()
						.setPickupCoordinates(productFare.getLocationStart().latitude(), productFare.getLocationStart().longitude())
				       .setProductId(productFare.getProduct().getProductId())
				       .setFareId(productFare.getRideEstimate().getFare().getFareId())
				       .setDropoffCoordinates(productFare.getLocationFinish().latitude(), productFare.getLocationStart().longitude())
				       .build();
			
			ride = ridesService.requestRide(ridesRequestParameters).execute().body();
			System.out.println("Ride request " + ride.getStatus() + " - " + ride.getVehicle());
			return ride;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ride;
		
	}
	
	
	public Ride selectRide(Ride ride) {
		
		try {
			return ridesService.getRideDetails(ride.getRideId()).execute().body();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Return list from ProductFare
	 * 
	 * @param locationStart
	 * @param locationFinish
	 * @return List<ProductFare>
	 */
	public List<ProductFare> selectAllProductsFares(Location locationStart, Location locationFinish) {
		
		List<ProductFare> productsFares = new ArrayList<ProductFare>();
		try {
			
			Response<ProductsResponse> response = ridesService.getProducts(
				locationStart.latitude(), 
				locationStart.longitude()
			).execute();

			List<Product> products = response.body().getProducts();
			
			for (Product product : products) {
				ProductFare productFare = this.buildProductFare(product, locationStart, locationFinish);
				productsFares.add(productFare);
				System.out.println(
						productFare.getProduct().getDisplayName() +
						" - " + productFare.getRideEstimate().getFare().getDisplay());
			
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return productsFares;
	}
	
	/**
	 * Build ProductFare
	 * @param product
	 * @param locationStart
	 * @param locationFinish
	 * @return ProductFare
	 */
	private ProductFare buildProductFare(Product product, Location locationStart, Location locationFinish) {
		ProductFare productFare = new ProductFare();
		RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder()
				   .setPickupCoordinates(locationStart.latitude(), locationStart.longitude())
			       .setProductId(product.getProductId())
			       .setDropoffCoordinates(locationFinish.latitude(), locationFinish.longitude())
			       .build();				
		try {
			RideEstimate rideEstimate = ridesService.estimateRide(rideRequestParameters).execute().body();
			productFare.setLocationStart(locationStart);
			productFare.setLocationFinish(locationFinish);
			productFare.setProduct(product);
			productFare.setRideEstimate(rideEstimate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return productFare;
	}

	
}
