package com.wizcodegroup.easytransport.object;

import java.util.ArrayList;


public class ResponcePlaceResult extends ResultObject {
	
	private String pageToken;
	private ArrayList<PlaceObject> listPlaceObjects;
	
	public ResponcePlaceResult() {
		super();
	}

	public ResponcePlaceResult(String status) {
		super(status);
	}
	
	public String getPageToken() {
		return pageToken;
	}

	public void setPageToken(String pageToken) {
		this.pageToken = pageToken;
	}

	public ArrayList<PlaceObject> getListPlaceObjects() {
		return listPlaceObjects;
	}

	public void setListPlaceObjects(ArrayList<PlaceObject> listPlaceObjects) {
		this.listPlaceObjects = listPlaceObjects;
	}
	
	public void addPlaceObject(PlaceObject mPlaceObject){
		if(mPlaceObject!=null){
			if(listPlaceObjects==null){
				listPlaceObjects = new ArrayList<PlaceObject>();
			}
			listPlaceObjects.add(mPlaceObject);
		}
	}
	
	public void onDestroy(){
		if(listPlaceObjects!=null){
			if(listPlaceObjects.size()>0){
				for(PlaceObject mPlaceObject:listPlaceObjects){
					mPlaceObject.onDestroy();
				}
			}
			listPlaceObjects.clear();
			listPlaceObjects=null;
		}
	}
}
