package com.wizcodegroup.easytransport.data;

public class CommentsItem {
	String ownerName,ownerImage,text,date;
	public CommentsItem(String ownerName,String ownerImage,String text,String date) {
		this.ownerName = ownerName;
		this.ownerImage = ownerName;
		this.text = text;
		this.date = date;
	}
	public CommentsItem() {
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getOwnerImage() {
		return ownerImage;
	}
	public void setOwnerImage(String ownerImage) {
		this.ownerImage = ownerImage;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
