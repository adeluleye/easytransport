package com.wizcodegroup.easytransport.dataMng;

import android.content.Context;
import android.location.Location;

import com.wizcodegroup.easytransport.R;
import com.wizcodegroup.easytransport.constanst.IWhereMyLocationConstants;
import com.wizcodegroup.easytransport.object.HomeSearchObject;
import com.wizcodegroup.easytransport.object.KeywordObject;
import com.wizcodegroup.easytransport.object.PlaceObject;
import com.ypyproductions.net.task.IDBCallback;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.DBListExcuteAction;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.IOUtils;
import com.ypyproductions.utils.StringUtils;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;


public class TotalDataManager implements IWhereMyLocationConstants {

	public static final String TAG = TotalDataManager.class.getSimpleName();

	private static TotalDataManager totalDataManager;
	private ArrayList<HomeSearchObject> listHomeSearchObjects;
	private Location currentLocation;
	private ArrayList<PlaceObject> listFavoriteObjects;
	private ArrayList<KeywordObject> listKeywordObjects;

	public static TotalDataManager getInstance() {
		if (totalDataManager == null) {
			totalDataManager = new TotalDataManager();
		}
		return totalDataManager;
	}

	private TotalDataManager() {
		listFavoriteObjects = new ArrayList<PlaceObject>();
	}

	public void onDestroy() {
		if (listFavoriteObjects != null) {
			listFavoriteObjects.clear();
			listFavoriteObjects = null;
		}
		if (listHomeSearchObjects != null) {
			listHomeSearchObjects.clear();
			listHomeSearchObjects = null;
		}
		totalDataManager = null;
	}
	
	public KeywordObject getKeyWordObject(String keyword){
		if(listKeywordObjects!=null && listKeywordObjects.size()>0 && keyword!=null){
			for(KeywordObject mKeywordObject:listKeywordObjects){
				if(mKeywordObject.getKeyword().equalsIgnoreCase(keyword)){
					return mKeywordObject;
				}
			}
		}
		return null;
	}

	public ArrayList<KeywordObject> getListKeywordObjects() {
		return listKeywordObjects;
	}

	public void setListKeywordObjects(ArrayList<KeywordObject> listKeywordObjects) {
		this.listKeywordObjects = listKeywordObjects;
	}

	public ArrayList<HomeSearchObject> getListHomeSearchObjects() {
		return listHomeSearchObjects;
	}

	public void setListHomeSearchObjects(ArrayList<HomeSearchObject> listHomeSearchObjects) {
		this.listHomeSearchObjects = listHomeSearchObjects;
	}

	public HomeSearchObject getHomeSearchSelected() {
		if (listHomeSearchObjects != null && listHomeSearchObjects.size() > 0) {
			for (HomeSearchObject mHomeSearchObject1 : listHomeSearchObjects) {
				if (mHomeSearchObject1.isSelected()) {
					DBLog.d(TAG, "============>home search selected=" + mHomeSearchObject1.getName());
					return mHomeSearchObject1;
				}
			}
			listHomeSearchObjects.get(1).setSelected(true);
			return listHomeSearchObjects.get(1);
		}
		return null;
	}
	
	public void setSelectedObject(int pos){
		if (listHomeSearchObjects != null && listHomeSearchObjects.size() > 0) {
			for (HomeSearchObject mHomeSearchObject : listHomeSearchObjects) {
				if(listHomeSearchObjects.indexOf(mHomeSearchObject)==pos){
					mHomeSearchObject.setSelected(true);
				}
				else{
					mHomeSearchObject.setSelected(false);
				}
			}
		}
	}
	public void setSelectedObject(HomeSearchObject pos){
		if (listHomeSearchObjects != null && listHomeSearchObjects.size() > 0) {
			for (HomeSearchObject mHomeSearchObject : listHomeSearchObjects) {
				if(mHomeSearchObject.equals(pos)){
					mHomeSearchObject.setSelected(true);
				}
				else{
					mHomeSearchObject.setSelected(false);
				}
			}
		}
	}
	
	public HomeSearchObject findHomeSearchObject(String query){
		if(StringUtils.isStringEmpty(query)){
			return null;
		}
		if (listHomeSearchObjects != null && listHomeSearchObjects.size() > 0) {
			for (HomeSearchObject mHomeSearchObject : listHomeSearchObjects) {
				String keyword =mHomeSearchObject.getKeyword();
				if(!StringUtils.isStringEmpty(keyword) && keyword.equalsIgnoreCase(query)){
					return mHomeSearchObject;
				}
			}
		}
		return null;
	}

	public void onResetResultSearch(boolean isResetAll) {
		if (listHomeSearchObjects != null && listHomeSearchObjects.size() > 0) {
			for (HomeSearchObject mHomeSearchObject1 : listHomeSearchObjects) {
				mHomeSearchObject1.setResponcePlaceResult(null);
			}
			boolean isSelected = listHomeSearchObjects.get(0).isSelected();
			if(isSelected && isResetAll){
				listHomeSearchObjects.get(0).setSelected(false);
				listHomeSearchObjects.get(0).setType(TYPE_SEARCH_BY_TYPES);
				listHomeSearchObjects.get(0).setKeyword(null);
				listHomeSearchObjects.get(1).setSelected(true);
			}
		}
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public int getResIconMapPin(Context mContext, String img) {
		if (StringUtils.isStringEmpty(img)) {
			return R.drawable.icon_custom_search;
		}
		if (img.equals("atm.png")) {
			return R.drawable.icon_custom_search;
		}
		else if (img.equals("bank.png")) {
			return R.drawable.icon_custom_search;
		}
		else if (img.equals("taxi_stand.png")) {
			return R.drawable.icon_custom_search;
		}
		else if (img.equals("bus_station.png")) {
			return R.drawable.icon_custom_search;
		}
		else if (img.equals("airport.png")) {
			return R.drawable.icon_custom_search;
		}
		else if (img.equals("dentist.png")) {
			return R.drawable.icon_custom_search;
		}

		else if (img.equals("cafe.png")) {
			return R.drawable.icon_custom_search;
		}
		else if (img.equals("bar.png")) {
			return R.drawable.icon_custom_search;
		}
		return R.drawable.icon_custom_search;
	}

	public int getResIconHome(Context mContext, String keyword) {
		if (StringUtils.isStringEmpty(keyword)) {
			return -1;
		}
		if (keyword.equals("atm.png")) {
			return R.drawable.atm;
		}
		else if (keyword.equals("taxi_stand.png")) {
			return R.drawable.taxi_stand;
		}
		else if (keyword.equals("bus_station.png")) {
			return R.drawable.bus_station;
		}
		else if (keyword.equals("airport.png")) {
			return R.drawable.airport;
		}
		else if (keyword.equals("dentist.png")) {
			return R.drawable.dentist;
		}
        else if (keyword.equals("cafe.png")) {
			return R.drawable.cafe;
		}
		else if (keyword.equals("bar.png")) {
			return R.drawable.bar;
		}
		return -1;
	}

	public int getResMiniIconHome(Context mContext, String name) {
		if (StringUtils.isStringEmpty(name)) {
			return -1;
		}
		if (name.equals("atm.png")) {
			return R.drawable.atm;
		}
		else if (name.equals("taxi_stand.png")) {
			return R.drawable.taxi_stand;
		}
		else if (name.equals("bus_station.png")) {
			return R.drawable.bus_station;
		}
		else if (name.equals("airport.png")) {
			return R.drawable.airport;
		}
		else if (name.equals("dentist.png")) {
			return R.drawable.dentist;
		}
        else if (name.equals("cafe.png")) {
			return R.drawable.cafe;
		}
		else if (name.equals("bar.png")) {
			return R.drawable.bar;
		}
		return -1;
	}

	public ArrayList<PlaceObject> getListFavoriteObjects() {
		return listFavoriteObjects;
	}

	public void setListFavoriteObjects(ArrayList<PlaceObject> listFavoriteObjects) {
		if (listFavoriteObjects != null) {
			this.listFavoriteObjects.clear();
			this.listFavoriteObjects = null;
			this.listFavoriteObjects = listFavoriteObjects;
		}
	}

	public boolean isFavoriteLocation(String id) {
		if (listFavoriteObjects != null && listFavoriteObjects.size() > 0 && !StringUtils.isStringEmpty(id)) {
			for (PlaceObject mPlaceObject : listFavoriteObjects) {
				String idNew = mPlaceObject.getId();
				if (!StringUtils.isStringEmpty(idNew) && idNew.equals(id)) {
					return true;
				}
			}
		}
		return false;
	}

	public void addFavoritePlace(final Context mContext, PlaceObject mPlaceObject) {
		if (mPlaceObject != null && listFavoriteObjects != null) {
			boolean isAdd = isFavoriteLocation(mPlaceObject.getId());
			if (!isAdd) {
				listFavoriteObjects.add(mPlaceObject);
				DBListExcuteAction.getInstance().queueAction(new IDBCallback() {
					@Override
					public void onAction() {
						saveFavoritePlaces(mContext);
					}
				});
			}
		}
	}

	public void removeFavoritePlace(final Context mContext, PlaceObject mPlaceObject) {
		if (mPlaceObject != null && listFavoriteObjects != null) {
			Iterator<PlaceObject> mListIterator = listFavoriteObjects.iterator();
			String id = mPlaceObject.getId();
			boolean isSyncAgain = false;
			while (mListIterator.hasNext()) {
				PlaceObject placeObject = (PlaceObject) mListIterator.next();
				String idNew = placeObject.getId();
				if (!StringUtils.isStringEmpty(idNew) && !StringUtils.isStringEmpty(id) && id.equals(idNew)) {
					mListIterator.remove();
					isSyncAgain = true;
					break;
				}
			}
			DBLog.d(TAG, "============>isSyncAgain=" + isSyncAgain);
			if (isSyncAgain) {
				DBListExcuteAction.getInstance().queueAction(new IDBCallback() {
					@Override
					public void onAction() {
						saveFavoritePlaces(mContext);
					}
				});
			}
		}
	}

	public synchronized void saveFavoritePlaces(Context mContext) {
		if (!ApplicationUtils.hasSDcard()) {
			return;
		}
		File mFile = IOUtils.getDiskCacheDir(mContext, DIR_DATA);
		if (!mFile.exists()) {
			mFile.mkdirs();
		}
		if (listFavoriteObjects != null && listFavoriteObjects.size() > 0) {
			JSONArray mJsArray = new JSONArray();
			for (PlaceObject mSongObject : listFavoriteObjects) {
				mJsArray.put(mSongObject.toJson());
			}
			DBLog.d(TAG, "=============>favoriteDatas=" + mJsArray.toString());
			IOUtils.writeString(mFile.getAbsolutePath(), FILE_FAVORITE_PLACES, mJsArray.toString());
			return;
		}
		IOUtils.writeString(mFile.getAbsolutePath(), FILE_FAVORITE_PLACES, "");
	}

}
