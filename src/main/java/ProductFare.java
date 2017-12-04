import com.pengrad.telegrambot.model.Location;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;


public class ProductFare {
	
	Product product;
	RideEstimate rideEstimate;
	Ride ride;
	Location locationStart;
	Location locationFinish;
	
	public Location getLocationStart() {
		return locationStart;
	}
	public void setLocationStart(Location locationStart) {
		this.locationStart = locationStart;
	}
	public Location getLocationFinish() {
		return locationFinish;
	}
	public void setLocationFinish(Location locationFinish) {
		this.locationFinish = locationFinish;
	}
	public Ride getRide() {
		return ride;
	}
	public void setRide(Ride ride) {
		this.ride = ride;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public RideEstimate getRideEstimate() {
		return rideEstimate;
	}
	public void setRideEstimate(RideEstimate rideEstimate) {
		this.rideEstimate = rideEstimate;
	}
}
