package com.wizcodegroup.easytransport.object;


public class ItemDrawerObject {
	
	private String name;
	private int iconRes;
	private boolean isSelected;
	
	public ItemDrawerObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIconRes() {
		return iconRes;
	}

	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	
	
	
	
}
