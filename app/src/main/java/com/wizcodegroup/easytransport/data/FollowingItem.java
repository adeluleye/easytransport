package com.wizcodegroup.easytransport.data;

public class FollowingItem {
	private String name,job,image;
	int id;
	
	public FollowingItem(int id,String name, String job, String image){
		this.id = id;
		this.name = name;
		this.job = job;
		this.image = image;
	}
	public FollowingItem() {}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
