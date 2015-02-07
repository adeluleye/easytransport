package com.wizcodegroup.easytransport.data;

public class NavigationItem {
	private String title;
	private int icon;
	
	public NavigationItem(){}

	public NavigationItem(String title, int icon){
		this.title = title;
		this.icon = icon;
	}
	public String getTitle(){
		return this.title;
	}
	
	public int getIcon(){
		return this.icon;
	}
		
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setIcon(int icon){
		this.icon = icon;
	}
}
