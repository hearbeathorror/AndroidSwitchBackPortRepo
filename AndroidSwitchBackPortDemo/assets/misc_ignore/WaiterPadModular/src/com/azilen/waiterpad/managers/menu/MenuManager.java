package com.azilen.waiterpad.managers.menu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.data.Category;
import com.azilen.waiterpad.data.CategoryMaster;
import com.azilen.waiterpad.data.Item;
import com.azilen.waiterpad.data.ItemMaster;
import com.azilen.waiterpad.data.ModifierMaster;
import com.azilen.waiterpad.data.SectionMenu;
import com.azilen.waiterpad.data.WholeMenu;
import com.azilen.waiterpad.data.Item.ItemType;
import com.azilen.waiterpad.db.DbOperations;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.IOUtils;
import com.azilen.waiterpad.utils.search.GetAllSelectedModifers;
import com.google.common.collect.Collections2;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MenuManager {
	/* Singleton Pattern using Eager Initialization */
	private static final MenuManager instance = new MenuManager();
	
	private static LruCache<String, Object> menuCache;
	private static int mCacheSize = 10 * 1024 * 1024;
	
	private static List<Item> lstOfItems;
	private List<Item> mCategoryWiseListItems;
	private static HashMap<String, List<Item>> categoryWiseItemMap;
	private HashMap<String, List<Category>> sectionWiseCategory;

	/* Singleton Pattern */
	public static MenuManager getInstance() {
		return instance;
	}

	public void storeDataIntoDatabase(WholeMenu mWholeMenu) {
		// store the items and other details into the db
		DbOperations dbOperations = DbOperations.getInstance();
		dbOperations.insertItemsIntoTable(mWholeMenu.getLstItems());
		dbOperations.insertCategoriesIntoTable(mWholeMenu.getLstCategories());
		dbOperations.insertIntoSectionItemMaster(mWholeMenu
				.getLstSectionItemMapping());
		dbOperations.insertItemModifiers(mWholeMenu.getLstModifiers());
		dbOperations.insertItemModifiersMapping(mWholeMenu
				.getItemModifierMapping());
		dbOperations.insertIntoGroupMaster(mWholeMenu.getGroupList());
		
		dbOperations.close();
	}
	
	public void deleteFromAllTables() {
		DbOperations dbOperations = DbOperations.getInstance();
		dbOperations.deleteFromAllTables();
	}
	
	public int getCountOfItems(String sectionId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getCountOfItems(sectionId);
	}

	public int getCountOfItemsModifiers(String sectionId,
			String itemId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations
				.getCountOfItemsModifiers(sectionId, itemId);
	}

	public List<CategoryMaster> getAllCategoriesFromDB() {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getCategories();
	}

	public Cursor getAllItemFromDB(String sectionId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getItemsPerSection(sectionId);
	}

	public Cursor getAllItemsUnderCategoryFromDB(String categoryId, String sectionId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getItemsPerCategoryAndSection(categoryId,
				sectionId);
	}

	public ItemMaster getItem(String itemId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getItem(itemId);
	}

	public Cursor getItemDataSearchedFor(String searchFor,
			String sectionId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getItemDataSearchedFor(searchFor,
				sectionId);
	}

	/*public Cursor getCategoryCursor(Context context) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getCategoryCursor(context);
	}*/

	public Cursor getModifiersPerItem(String itemId,
			String sectionId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getModifiersPerItem(itemId, sectionId);
	}

	public Cursor getGroupModifiers(String itemId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getModifiersGroup(itemId);
	}

	public Cursor getModifiersUnderCursor(String groupId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getModifiersUnderGroup(groupId);
	}

	public int getCountOfGroupModifiers(String itemId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getCountOfGroupModifiers(itemId);
	}

	public Cursor getSubcategoriesAndItems(String categoryId,
			String sectionId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getSubcategoriesAndItems(categoryId,
				sectionId);
	}

	// changes as on 16th November 2013
	// get the categories for the list
	// removing spinner from the ui
	public Cursor getCategoriesForList(String sectionId) {
		DbOperations dbOperations = DbOperations.getInstance();
		return dbOperations.getCategoriesForList(sectionId);
	}
	
	/**
	 * Deserializes the {@link ItemType} enum received by gson
	 * 
	 * @author dhara.shah
	 * 
	 */
	public static class MenuDeserializer implements
			JsonDeserializer<Item.ItemType> {
		@Override
		public ItemType deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext ctx) throws JsonParseException {
			int typeInt = json.getAsInt();
			return ItemType.getItemType(typeInt);
		}
	}
	
	/**
	 * Returns a list of selected modifiers
	 * 
	 * @param lstOfModifiers
	 * @return
	 */
	public List<ModifierMaster> getAllModifiers(
			List<ModifierMaster> lstOfModifiers) {
		List<ModifierMaster> appliedModifiers = null;
		// obtain the list of modifiers to add into the item
		if (lstOfModifiers != null) {
			Collection<ModifierMaster> collection = Collections2.filter(
					lstOfModifiers, new GetAllSelectedModifers());
			appliedModifiers = new ArrayList<ModifierMaster>(collection);
		}
		return appliedModifiers;
	}

	/**
	 * Returns the price of all the selected modifiers
	 * 
	 * @param lstOfModifiers
	 * @return
	 */
	public double getModifiersCost(List<ModifierMaster> lstOfModifiers) {
		double price = 0.0;
		List<ModifierMaster> appliedModifiers = getAllModifiers(lstOfModifiers);

		if (appliedModifiers != null) {
			for (ModifierMaster modifiers : appliedModifiers) {
				price += modifiers.getPrice();
			}
		}

		return price;
	}

	/**
	 * Sets quantity 1 to all selected modifiers
	 * 
	 * @param lstOfModifiers
	 */
	public void setQuantityForModifiers(List<ModifierMaster> lstOfModifiers) {
		List<ModifierMaster> appliedModifiers = getAllModifiers(lstOfModifiers);

		for (ModifierMaster modifiers : appliedModifiers) {
			modifiers.setQuantity(1);
		}
	}
	
	// NOT USED
	
	/**
	 * Gets the menu and stores the item list into the cache and also stores the
	 * data categorial wise
	 * 
	 * @param sectionMenuList
	 * @param sectionId
	 */
	public void getMenuFromCache(List<SectionMenu> sectionMenuList) {
		// TODO: menuCache is not initialized.
	
		if (sectionMenuList != null) {
			// // remove the old items
			// // and then add the new items
			// cache.remove("categorialItems");
			// cache.remove("itemListSectionWise");
			// cache.remove("categoryWiseItems");
			// cache.remove("sectionWiseCategories");

			// categorial representation of items section wise
			HashMap<String, List<Category>> categorialList = new HashMap<String, List<Category>>();

			// for the list of items section wise
			HashMap<String, List<Item>> itemMap = new HashMap<String, List<Item>>();

			// store the items into the map against the id of each category
			categoryWiseItemMap = new HashMap<String, List<Item>>();
			sectionWiseCategory = new HashMap<String, List<Category>>();

			for (SectionMenu sectionMenu : sectionMenuList) {
				// SectionMenu mainSectionMenu = Iterables.find(sectionMenuList,
				// new SearchPredicate(sectionMenu.getId()));
				Log.i("tag", "name of section: " + sectionMenu.getName());

				if (sectionMenu != null) {
					// store data into the lru cache
					// list of items categorial wise
					// list of items as a list itself

					lstOfItems = new ArrayList<Item>();
					getAllItems(sectionMenu);

					categorialList.put(sectionMenu.getId(),
							sectionMenu.getCategoryList());

					if (lstOfItems.size() > 0) {
						itemMap.put(sectionMenu.getId(), lstOfItems);
					}

					sectionWiseCategory.put(sectionMenu.getId(),
							sectionMenu.getCategoryList());
				}
			}


			menuCache.put(Global.CATEGORIAL_ITEMS, categorialList);
			menuCache.put(Global.ITEMLIST_SECTION_WISE, itemMap);
			menuCache.put(Global.CATEGORY_WISE_ITEMS, categoryWiseItemMap);
			menuCache.put(Global.SECTION_WISE_CATEGORIES, sectionWiseCategory);
		}
	}

	/**
	 * Iterates through the category list to get all the items
	 */
	private void getAllItems(SectionMenu mainSectionMenu) {
		List<Category> lstCategories = mainSectionMenu.getCategoryList();
		if (lstCategories != null) {
			iterateThroughCategories(lstCategories);
		}
	}

	/**
	 * Returns all the items in the list of categories
	 */
	public List<Item> getAllItems(List<Category> lstCategories) {
		lstOfItems = new ArrayList<Item>();
		if (lstCategories != null) {
			iterateThroughCategories(lstCategories);
		}
		return lstOfItems;
	}

	/**
	 * Iterates through the list to get the items and subcategories per category
	 * 
	 * @param lstCategory
	 */
	private void iterateThroughCategories(List<Category> lstCategory) {
		categoryWiseItemMap = new HashMap<String, List<Item>>();
		for (int i = 0; i < lstCategory.size(); i++) {
			mCategoryWiseListItems = new ArrayList<Item>();
			Category category = lstCategory.get(i);


			// changes as on 15th June 2013
			// parent category is null because its the main category
			// it would have no parents
			category.setParentCategoryName(null);
			category.setParentCategoryIds(null);
			// changes end here

			looper(category);
		}
	}

	/**
	 * Identifies if there is an item list if yes, it returns the list else null
	 * 
	 * @param category
	 * @return
	 */
	public ArrayList<Item> getItemList(Category category) {
		List<Item> lst = category.getItemList();
		List<Item> itemListToReturn = new ArrayList<Item>();

		if (lst != null && lst.size() > 0) {

			// changes as on 15th June 2013
			// adding the parent name to the item list
			// for search and list refresh purposes
			for (Item item : lst) {
				item.setParentCategoryIds(category.getParentCategoryIds()
						+ " @ " + category.getCategoryId() + " @ "
						+ item.getItemName());

				item.setParentCategoryName(category.getParentCategoryName()
						+ " @ " + category.getCategoryName() + " @ "
						+ item.getItemName());

				itemListToReturn.add(item);
			}

			return (ArrayList<Item>) itemListToReturn;
			// changes end here

		}

		return null;
	}

	public List<Item> getAllItemsUnderCategory(Category category) {
		lstOfItems = new ArrayList<Item>();
		if (category.getSubCategoryList() != null
				&& category.getSubCategoryList().size() > 0) {

			// has a subcategory
			// therefore get all the items
			category.setParentCategoryName(null);
			category.setParentCategoryIds(null);
			looper(category);
		}

		if (category.getItemList() != null) {
			List<Item> itemsToAdd = getItemList(category);
			if (itemsToAdd != null) {
				lstOfItems.addAll(itemsToAdd);
			}
		}

		return lstOfItems;
	}

	/**
	 * Obtains a category and iterates through the item list And also the
	 * subcategories.
	 * 
	 * @param category
	 */
	private void looper(Category category) {
		if (category != null && category.getSubCategoryList() != null) {

			List<Category> lstSubcat = category.getSubCategoryList();
			ArrayList<Item> lstTemp = new ArrayList<Item>();

			// This loop will fetch the category object inside each
			// subcategory inorder to fetch all the items inside the
			// category.
			if (lstSubcat != null && lstSubcat.size() > 0) {
				for (int j = 0; j < lstSubcat.size(); j++) {
					Category cat = lstSubcat.get(j);
					// Log.i(TAG,"------");
					// Log.i(TAG,"category under: " +
					// category.getCategoryName());
					// Log.i(TAG, "subcat looper: " + cat.getCategoryName());
					// Log.i(TAG, "-------");

					// Set the category id as the parent name of the subcat
					cat.setParentCategoryName(category.getCategoryName());
					cat.setParentCategoryIds(category.getCategoryId());

					if (category.getParentCategoryName() != null) {
						cat.setParentCategoryName(category
								.getParentCategoryName()
								+ " @ "
								+ category.getCategoryName());
					} else {
						cat.setParentCategoryName(category.getCategoryName());
					}

					if (category.getParentCategoryIds() != null) {
						cat.setParentCategoryIds(category
								.getParentCategoryIds()
								+ " @ "
								+ category.getCategoryId());
					} else {
						// Log.i(TAG,
						// " value of category id in the else looper method : "
						// + category.getCategoryId());
						cat.setParentCategoryIds(category.getCategoryId());
					}

					lstTemp = getItemList(cat);

					if (lstTemp != null) {
						lstOfItems.addAll(lstTemp);
						mCategoryWiseListItems.addAll(lstTemp);
					}

					// Log.i(TAG, " value of category parent name item : " +
					// cat.getParentCategoryName());

					looper(cat);
				}
			}

			// against each category id storing the list of items
			categoryWiseItemMap.put(category.getCategoryId(),
					mCategoryWiseListItems);
		}
	}
	
	public void writeToMenuFile(InputStream is, String sectionId) {
		File sdcard = Environment.getExternalStorageDirectory();
		File file = null;
		OutputStream outputStream = null;

		if (sdcard.exists()) {
			file = new File(sdcard, "waiterpad/files");

			try {
				if (!file.exists()) {
					file.getParentFile().mkdirs();
				}

				if (file.exists()) {
					// write to the file
					file = new File(file, sectionId + ".txt");
					outputStream = new FileOutputStream(file);
					int read = 0;
					byte[] bytes = new byte[1024 * 10];

					Log.i("tag", " outside while  " + is);

					/*
					 * while ((read = is.read(bytes)) != -1) { Log.i("tag",read
					 * + " values " ); outputStream.write(bytes, 0, read); }
					 */

					outputStream.write(IOUtils.readFully(is, -1, false));

				}
			} catch (IOException e) {
				WaiterPadApplication.LOG.debug("MenuManager" + " \n\n" + e.getMessage() + "\n\n"
						+ String.valueOf(e.getCause()));
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
						WaiterPadApplication.LOG.debug("MenuManager" + " \n\n" + e.getMessage()
								+ "\n\n" + String.valueOf(e.getCause()));
					}
				}
			}
		}
	}
	
	public File getMenuFile(String sectionId) {
		File sdcard = Environment.getExternalStorageDirectory();
		File file = null;

		if (sdcard.exists()) {
			// Get the text file
			file = new File(sdcard, "waiterpad/files/" + sectionId + ".txt");
		}
		return file;
	}
}
