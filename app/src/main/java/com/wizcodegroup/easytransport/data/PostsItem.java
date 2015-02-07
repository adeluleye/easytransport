package com.wizcodegroup.easytransport.data;

public class PostsItem {
	private String name, status, image, profilePic, timeStamp;
	private int id,likes,views,ownerID;
	private boolean liked;
	public PostsItem() {
		
	}
	public PostsItem(int id, int ownerID,int likes,int views,String name, String image, String status,
			String profilePic, String timeStamp, String url) {
		super();
		this.id = id;
		this.ownerID = 	ownerID;
		this.name = name;
		this.image = image;
		this.status = status;
		this.profilePic = profilePic;
		this.timeStamp = timeStamp;
		this.likes = likes;
		this.views = views;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setViews(int views) {
		this.views = views;
	}
	
	public int getViews() {
		return views;
	}

	public void setLikes(int uses) {
		this.likes = uses;
	}
	
	public int getLikes() {
		return likes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImge() {
		return image;
	}

	public void setImge(String image) {
		this.image = image;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public void setOwnerID(int ownerID) {
		this.ownerID = 	ownerID;
	}
	public int getOwnerID() {
		return ownerID;
	}
	public boolean isLiked() {
		return liked;
	}
	public void setLiked(boolean liked) {
		this.liked = liked;
	}
}
