package com.wizcodegroup.easytransport.object;


public class StepObject {
	private String distance;
	private String duration;
	private String description;
	private String travelMode;
	//private Location startLocation;
	//private Location endLocation;

	public StepObject() {
		super();
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTravelMode() {
		return travelMode;
	}

	public void setTravelMode(String travelMode) {
		this.travelMode = travelMode;
	}


}
