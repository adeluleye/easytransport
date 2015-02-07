package com.wizcodegroup.easytransport.model;

public class CityMenuList {
	private String vicinity = "";
	private String icon = "";
	private String id = "";
	private String name = "";
	private String rating = "";
	private String reference = "";
	private String types = "";
	private String photoReference = "";
	public String getVicinity() {
		return vicinity;
	}
	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public String getPhotoReference() {
		return photoReference;
	}
	public void setPhotoReference(String photoReference) {
		this.photoReference = photoReference;
	}
	@Override
	public String toString() {
		return "CityMenuList [icon=" + icon + ", id=" + id + ", name=" + name
				+ ", photoReference=" + photoReference + ", rating=" + rating
				+ ", reference=" + reference + ", types=" + types
				+ ", vicinity=" + vicinity + "]";
	}

	
}