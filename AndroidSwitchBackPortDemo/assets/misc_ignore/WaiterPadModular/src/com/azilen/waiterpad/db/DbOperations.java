package com.azilen.waiterpad.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.azilen.waiterpad.data.CategoryMaster;
import com.azilen.waiterpad.data.GroupModifierMaster;
import com.azilen.waiterpad.data.ItemMaster;
import com.azilen.waiterpad.data.ItemModifierMapping;
import com.azilen.waiterpad.data.ItemModifiers;
import com.azilen.waiterpad.data.SectionItemMaster;

@SuppressWarnings("deprecation")
public class DbOperations {
	private static DbOperations dbOperations;
	private DBHelper dbHelper;
	private SQLiteDatabase sqLiteDatabase;
	private String TAG = this.getClass().getSimpleName();

	public DbOperations(Context context) {
		dbHelper = DBHelper.getInstance(context.getApplicationContext());
		dbOperations = this;
	}

	public static DbOperations getInstance() {
		// if (dbOperations == null) {
		// dbOperations = new DbOperations(context.getApplicationContext());
		// }
		return dbOperations;
	}

	public List<CategoryMaster> getCategories() {
		sqLiteDatabase = dbHelper.openDataBase();
		Cursor cursor = sqLiteDatabase.query(DBHelper.CATEGORY_TABLE, null,
				null, null, null, null, null);
		List<CategoryMaster> categories = new ArrayList<CategoryMaster>();
		if (cursor != null && cursor.moveToFirst()) {
			while (cursor.moveToNext()) {
				CategoryMaster categoryMaster = new CategoryMaster();
				categoryMaster.setCategoryId(cursor.getString(cursor
						.getColumnIndex(DBHelper.CATEGORY_ID)));
				categoryMaster.setCategoryName(cursor.getString(cursor
						.getColumnIndex(DBHelper.CATEGORY_NAME)));
				categoryMaster.setCategoryParentId(cursor.getString(cursor
						.getColumnIndex(DBHelper.CATEGORY_PARENT_ID)));
				categories.add(categoryMaster);
			}
			cursor.close();
		}
		return categories;
	}

	/*
	 * public Cursor getCategoryCursor(Context context) {
	 * 
	 * mLanguageSelection = Prefs.getKey(context, Prefs.LANGUAGE_SELECTED);
	 * mMemCache = Utils.getInstance();
	 * 
	 * if (mLanguageSelection != null && mLanguageSelection.trim().length() > 0)
	 * { mLanguageXml = (LanguageXml) mMemCache.get(mLanguageSelection); }
	 * 
	 * String columnList = "_id , " + DBHelper.CATEGORY_ID + "," +
	 * DBHelper.CATEGORY_NAME + "," + DBHelper.CATEGORY_PARENT_ID + "," +
	 * DBHelper.CATEGORY_NAME_LOWER;
	 * 
	 * sqLiteDatabase = dbHelper.openDataBase();
	 * 
	 * String sql = "";
	 * 
	 * if (mLanguageXml != null) { sql =
	 * "Select 9999 as _id, 9999 as category_id, " + "'" +
	 * mLanguageXml.getSelectCategory() + "' as " + DBHelper.CATEGORY_NAME +
	 * ",NULL as parent_id, 'Select Category' as " +
	 * DBHelper.CATEGORY_NAME_LOWER + " UNION ALL " + "Select " + columnList +
	 * " from " + DBHelper.CATEGORY_TABLE + " where (" + DBHelper.CATEGORY_TABLE
	 * + "." + DBHelper.CATEGORY_PARENT_ID + " = '' or " +
	 * DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_PARENT_ID +
	 * " IS NULL )"; } else { sql = "Select 9999 as _id, 9999 as category_id, "
	 * + "'Select Category' as " + DBHelper.CATEGORY_NAME +
	 * ",NULL as parent_id, 'Select Category' as " +
	 * DBHelper.CATEGORY_NAME_LOWER + " UNION ALL " + "Select " + columnList +
	 * " from " + DBHelper.CATEGORY_TABLE + " where (" + DBHelper.CATEGORY_TABLE
	 * + "." + DBHelper.CATEGORY_PARENT_ID + " = '' or " +
	 * DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_PARENT_ID +
	 * " IS NULL )"; }
	 * 
	 * Cursor cursor = sqLiteDatabase.rawQuery(sql, null); return cursor; }
	 */

	public Cursor getItemsPerCategoryAndSection(String categoryId,
			String sectionId) {
		Cursor cursor = null;

		sqLiteDatabase = dbHelper.openDataBase();

		String columnList = DBHelper.ITEM_TABLE + "._id ,"
				+ DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID + ","
				+ DBHelper.ITEM_TABLE + "." + DBHelper.CATEGORY_ID + ","
				+ DBHelper.ITEM_CODE + "," + DBHelper.ITEM_PRICE + ","
				+ DBHelper.ITEM_IS_ACTIVATED + ","
				+ DBHelper.ITEM_IS_RESTRICTED + "," + DBHelper.ITEM_DESC + ","
				+ DBHelper.ITEM_FULLNAME + "," + DBHelper.ITEM_NAME + ","
				+ DBHelper.HAVE_MODIFIERS;

		// Changes as on 30th October 2013
		// trial query
		String sql = "select " + columnList + " from " + DBHelper.ITEM_TABLE
				+ " join " + DBHelper.CATEGORY_TABLE + " on "
				+ DBHelper.ITEM_TABLE + "." + DBHelper.CATEGORY_ID + " = "
				+ DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_ID
				+ " join " + DBHelper.SECTION_ITEM_TABLE + " on "
				+ DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.ITEM_ID + "="
				+ DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID + " and "
				+ DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.SECTION_ID
				+ " = '" + sectionId + "'" + " and (" + DBHelper.CATEGORY_TABLE
				+ "." + DBHelper.CATEGORY_ID + "='" + categoryId + "' or "
				+ DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_PARENT_ID
				+ "='" + categoryId + "')";

		// changes end here
		// Log.i(TAG, " sql query >>> " + sql);
		cursor = sqLiteDatabase.rawQuery(sql, null);
		return cursor;
	
	}

	public Cursor getItemsPerSection(String sectionId) {
		sqLiteDatabase = dbHelper.openDataBase();

		// changes as on 7th November 2013
		// query to get list of items not under any category
		String COLUMNS_FOR_CATEGORY = DBHelper.CATEGORY_TABLE + "._id as _id, "
				+ DBHelper.CATEGORY_ID + "," + DBHelper.CATEGORY_PARENT_ID
				+ "," + DBHelper.CATEGORY_NAME + ","
				+ DBHelper.CATEGORY_NAME_LOWER + "," + "'' as "
				+ DBHelper.ITEM_NAME_LOWER + "," + "'' as "
				+ DBHelper.ITEM_NAME + "," + "'' as " + DBHelper.ITEM_ID + ","
				+ "'' as " + DBHelper.ITEM_CODE + "," + "'' as "
				+ DBHelper.ITEM_DESC + "," + "'' as " + DBHelper.ITEM_FULLNAME
				+ "," + "'' as " + DBHelper.ITEM_PRICE + "," + "'' as "
				+ DBHelper.ITEM_IS_ACTIVATED + "," + "'' as "
				+ DBHelper.ITEM_IS_RESTRICTED + "," + "'' as "
				+ DBHelper.HAVE_MODIFIERS;

		String COLUMNS_FOR_ITEMS = DBHelper.ITEM_TABLE + "._id as _id, "
				+ DBHelper.CATEGORY_ID + "," + "'' as "
				+ DBHelper.CATEGORY_PARENT_ID + "," + "'' as "
				+ DBHelper.CATEGORY_NAME + "," + "'' as "
				+ DBHelper.CATEGORY_NAME_LOWER + "," + DBHelper.ITEM_NAME_LOWER
				+ "," + DBHelper.ITEM_NAME + "," + DBHelper.ITEM_TABLE + "."
				+ DBHelper.ITEM_ID + "," + DBHelper.ITEM_CODE + ","
				+ DBHelper.ITEM_DESC + "," + DBHelper.ITEM_FULLNAME + ","
				+ DBHelper.ITEM_PRICE + "," + DBHelper.ITEM_IS_ACTIVATED + ","
				+ DBHelper.ITEM_IS_RESTRICTED + "," + DBHelper.HAVE_MODIFIERS;

		String sql = "Select " + COLUMNS_FOR_CATEGORY + " from "
				+ DBHelper.CATEGORY_TABLE + " where "
				+ DBHelper.CATEGORY_PARENT_ID + "='9999' " + " union "
				+ "Select " + COLUMNS_FOR_ITEMS + " from "
				+ DBHelper.ITEM_TABLE + " join " + DBHelper.SECTION_ITEM_TABLE
				+ " on " + DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.ITEM_ID
				+ " = " + DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID
				+ " and " + DBHelper.SECTION_ITEM_TABLE + "."
				+ DBHelper.SECTION_ID + "='" + sectionId + "' " + " order by "
				+ DBHelper.ITEM_IS_ACTIVATED + " DESC ";

		// changes end here

		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		return cursor;
	}

	public Cursor getModifiersPerItem(String itemId, String sectionId) {
		sqLiteDatabase = dbHelper.openDataBase();

		// changes made to this as the modifiers in the previous query
		// displayed repeated modifiers
		// changes as on 14th September 2013
		String sql = "select * from " + DBHelper.MODIFIERS_TABLE + " join "
				+ DBHelper.ITEM_MODIFIERS_MAPPING + " on "
				+ DBHelper.MODIFIERS_TABLE + "." + DBHelper.MODIFIER_ID + " = "
				+ DBHelper.ITEM_MODIFIERS_MAPPING + "." + DBHelper.MODIFIER_ID
				+ " join " + DBHelper.SECTION_ITEM_TABLE + " on "
				+ DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.ITEM_ID + " = "
				+ DBHelper.ITEM_MODIFIERS_MAPPING + "." + DBHelper.ITEM_ID
				+ " and " + DBHelper.ITEM_MODIFIERS_MAPPING + "."
				+ DBHelper.ITEM_ID + " ='" + itemId + "'" + " and "
				+ DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.SECTION_ID
				+ " ='" + sectionId + "'" + " and ("
				+ DBHelper.ITEM_MODIFIERS_MAPPING + "." + DBHelper.GROUP_ID
				+ "=''" + " or " + DBHelper.ITEM_MODIFIERS_MAPPING + "."
				+ DBHelper.GROUP_ID + " IS NULL )"
				+ " order by "
				+ DBHelper.MODIFIER_IS_ACTIVE + " DESC ";

		Cursor c = sqLiteDatabase.rawQuery(sql, null);
		
		//TODO: changed query a little, checking
		Log.e("dhara", "modifiers query: " + sql);
		return c;
	}

	public void insertItemsIntoTable(List<ItemMaster> itemMaster) {
		int itemIdIndex, categoryIdIndex, isActivatedIndex, isRestrictedIndex, itemCodeIndex, itemPriceIndex, itemDescIndex, fullNameIndex, itemNameIndex, hasModifiersIndex;
		int itemNameLowerIndex;

		if (itemMaster != null && itemMaster.size() > 0) {



			sqLiteDatabase = dbHelper.openDataBase();
			InsertHelper insertHelper = new InsertHelper(sqLiteDatabase,
					DBHelper.ITEM_TABLE);
			itemIdIndex = insertHelper.getColumnIndex(DBHelper.ITEM_ID);
			categoryIdIndex = insertHelper.getColumnIndex(DBHelper.CATEGORY_ID);
			isActivatedIndex = insertHelper
					.getColumnIndex(DBHelper.ITEM_IS_ACTIVATED);
			isRestrictedIndex = insertHelper
					.getColumnIndex(DBHelper.ITEM_IS_RESTRICTED);
			itemCodeIndex = insertHelper.getColumnIndex(DBHelper.ITEM_CODE);
			itemPriceIndex = insertHelper.getColumnIndex(DBHelper.ITEM_PRICE);
			itemDescIndex = insertHelper.getColumnIndex(DBHelper.ITEM_DESC);
			fullNameIndex = insertHelper.getColumnIndex(DBHelper.ITEM_FULLNAME);
			itemNameIndex = insertHelper.getColumnIndex(DBHelper.ITEM_NAME);
			itemNameLowerIndex = insertHelper
					.getColumnIndex(DBHelper.ITEM_NAME_LOWER);

			hasModifiersIndex = insertHelper
					.getColumnIndex(DBHelper.HAVE_MODIFIERS);
			sqLiteDatabase.beginTransaction();
			try {
				for (ItemMaster itemMasterObj : itemMaster) {
					insertHelper.prepareForInsert();
					insertHelper.bind(itemIdIndex, itemMasterObj.getItemId());
					insertHelper.bind(categoryIdIndex,
							itemMasterObj.getCategoryId());
					insertHelper.bind(isActivatedIndex,
							itemMasterObj.isActivated());
					insertHelper.bind(isRestrictedIndex,
							itemMasterObj.isRestricted());
					insertHelper.bind(itemCodeIndex,
							itemMasterObj.getItemCode());
					insertHelper.bind(itemPriceIndex, itemMasterObj.getPrice());
					insertHelper.bind(itemDescIndex,
							itemMasterObj.getItemDescription());
					insertHelper.bind(fullNameIndex,
							itemMasterObj.getFullName());
					insertHelper.bind(fullNameIndex,
							itemMasterObj.getFullName());
					insertHelper.bind(itemNameIndex,
							itemMasterObj.getItemName());
					insertHelper.bind(itemNameLowerIndex, itemMasterObj
							.getItemName().toLowerCase());

					if (itemMasterObj.isHasModifiers()) {
						insertHelper.bind(hasModifiersIndex, 1);
					} else {
						insertHelper.bind(hasModifiersIndex, 0);
					}
					insertHelper.execute();
				}
				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
		}
	}

	public void insertCategoriesIntoTable(
			List<CategoryMaster> categoryMasterList) {
		int categoryIdIndex, categoryNameIndex, parentIdIndex;
		int categoryNameLowerIndex;

		if (categoryMasterList != null && categoryMasterList.size() > 0) {


			sqLiteDatabase = dbHelper.openDataBase();
			InsertHelper insertHelper = new InsertHelper(sqLiteDatabase,
					DBHelper.CATEGORY_TABLE);
			categoryIdIndex = insertHelper.getColumnIndex(DBHelper.CATEGORY_ID);

			categoryNameIndex = insertHelper
					.getColumnIndex(DBHelper.CATEGORY_NAME);

			categoryNameLowerIndex = insertHelper
					.getColumnIndex(DBHelper.CATEGORY_NAME_LOWER);

			parentIdIndex = insertHelper
					.getColumnIndex(DBHelper.CATEGORY_PARENT_ID);

			sqLiteDatabase.beginTransaction();
			try {
				for (CategoryMaster categoryMaster : categoryMasterList) {
					insertHelper.prepareForInsert();
					insertHelper.bind(categoryIdIndex,
							categoryMaster.getCategoryId());

					insertHelper.bind(categoryNameIndex,
							categoryMaster.getCategoryName());

					insertHelper.bind(parentIdIndex,
							categoryMaster.getCategoryParentId());

					insertHelper.bind(categoryNameLowerIndex, categoryMaster
							.getCategoryName().toLowerCase());

					insertHelper.execute();
				}
				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
		}
	}

	public void insertIntoSectionItemMaster(
			List<SectionItemMaster> sectionItemMasters) {
		int itemIdIndex, sectionIdIndex;

		if (sectionItemMasters != null && sectionItemMasters.size() > 0) {


			sqLiteDatabase = dbHelper.openDataBase();
			InsertHelper insertHelper = new InsertHelper(sqLiteDatabase,
					DBHelper.SECTION_ITEM_TABLE);
			itemIdIndex = insertHelper.getColumnIndex(DBHelper.ITEM_ID);
			sectionIdIndex = insertHelper.getColumnIndex(DBHelper.SECTION_ID);

			sqLiteDatabase.beginTransaction();
			try {
				for (SectionItemMaster sectionItemMaster : sectionItemMasters) {
					insertHelper.prepareForInsert();
					insertHelper.bind(itemIdIndex,
							sectionItemMaster.getItemId());
					insertHelper.bind(sectionIdIndex,
							sectionItemMaster.getSectionId());

					insertHelper.execute();
				}
				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
		
		}
	}

	public void insertItemModifiers(List<ItemModifiers> itemModifiers) {
		int modifierIdIndex, modifierNameIndex, modifierPriceIndex, modifierDescIndex, modifierIsActiveIndex, modifierQuantityIndex, groupIdIndex;

		if (itemModifiers != null && itemModifiers.size() > 0) {


			sqLiteDatabase = dbHelper.openDataBase();
			InsertHelper insertHelper = new InsertHelper(sqLiteDatabase,
					DBHelper.MODIFIERS_TABLE);
			modifierIdIndex = insertHelper.getColumnIndex(DBHelper.MODIFIER_ID);
			modifierNameIndex = insertHelper
					.getColumnIndex(DBHelper.MODIFIER_NAME);
			modifierPriceIndex = insertHelper
					.getColumnIndex(DBHelper.MODIFIER_PRICE);
			modifierDescIndex = insertHelper
					.getColumnIndex(DBHelper.MODIFIER_DESC);
			modifierIsActiveIndex = insertHelper
					.getColumnIndex(DBHelper.MODIFIER_IS_ACTIVE);
			modifierQuantityIndex = insertHelper
					.getColumnIndex(DBHelper.MODIFIER_QTY);
			groupIdIndex = insertHelper.getColumnIndex(DBHelper.GROUP_ID);

			sqLiteDatabase.beginTransaction();
			try {
				for (ItemModifiers itemModifier : itemModifiers) {
					insertHelper.prepareForInsert();
					insertHelper.bind(modifierIdIndex, itemModifier.getId());
					insertHelper.bind(modifierNameIndex,
							itemModifier.getModifierName());
					insertHelper.bind(modifierPriceIndex,
							itemModifier.getPrice());
					insertHelper.bind(modifierDescIndex,
							itemModifier.getDescription());
					insertHelper.bind(modifierIsActiveIndex,
							itemModifier.isActive());
					insertHelper.bind(modifierQuantityIndex,
							itemModifier.getQuantity());
					insertHelper.bind(groupIdIndex, itemModifier.getGroupId());

					insertHelper.execute();
				}
				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
		
		
		}
	}

	public void insertItemModifiersMapping(
			List<List<ItemModifierMapping>> itemModifierMapping) {
		int modifierIdIndex, itemIdIndex, maxAmountIndex, minAmountIndex, defaultAmountIndex, groupIdIndex;

		if (itemModifierMapping != null && itemModifierMapping.size() > 0) {


			sqLiteDatabase = dbHelper.openDataBase();
			InsertHelper insertHelper = new InsertHelper(sqLiteDatabase,
					DBHelper.ITEM_MODIFIERS_MAPPING);
			modifierIdIndex = insertHelper.getColumnIndex(DBHelper.MODIFIER_ID);
			itemIdIndex = insertHelper.getColumnIndex(DBHelper.ITEM_ID);
			maxAmountIndex = insertHelper.getColumnIndex(DBHelper.MAX_AMOUNT);
			minAmountIndex = insertHelper.getColumnIndex(DBHelper.MIN_AMOUNT);
			groupIdIndex = insertHelper.getColumnIndex(DBHelper.GROUP_ID);
			defaultAmountIndex = insertHelper
					.getColumnIndex(DBHelper.DEFAULT_AMOUNT);

			sqLiteDatabase.beginTransaction();
			try {
				for (List<ItemModifierMapping> lst : itemModifierMapping) {
					for (ItemModifierMapping itemMap : lst) {
						insertHelper.prepareForInsert();
						insertHelper.bind(modifierIdIndex,
								itemMap.getModifierId());
						insertHelper.bind(itemIdIndex, itemMap.getItemId());
						insertHelper.bind(maxAmountIndex,
								itemMap.getMaxAmount());
						insertHelper.bind(minAmountIndex,
								itemMap.getMinAmount());
						insertHelper.bind(groupIdIndex, itemMap.getGroupId());
						insertHelper.bind(defaultAmountIndex,
								itemMap.getDefaultAmount());
						insertHelper.execute();
					}
				}
				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
		}
	}

	public int getCountOfItems(String sectionId) {
		int counter = 0;
		sqLiteDatabase = dbHelper.openDataBase();
		Cursor cursor = sqLiteDatabase.query(DBHelper.SECTION_ITEM_TABLE,
				new String[] { DBHelper.ITEM_ID }, DBHelper.SECTION_ID + " = '"
						+ sectionId + "'", null, null, null, null);

		if (cursor != null) {
			counter = cursor.getCount();
		}
		cursor.close();
		
		if(sqLiteDatabase.isOpen()) {
			sqLiteDatabase.close();
		}

		return counter;
	}

	public ItemMaster getItem(String itemId) {
		sqLiteDatabase = dbHelper.openDataBase();
		Cursor cursor = sqLiteDatabase.query(DBHelper.ITEM_TABLE, null,
				DBHelper.ITEM_ID + " = '" + itemId + "'", null, null, null,
				null);
		ItemMaster item = null;
		if (cursor != null && cursor.moveToFirst()) {
			item = new ItemMaster();

			int isActivated = cursor.getInt(cursor
					.getColumnIndex(DBHelper.ITEM_IS_ACTIVATED));
			int isRestricted = cursor.getInt(cursor
					.getColumnIndex(DBHelper.ITEM_IS_RESTRICTED));
			int hasModifiers = cursor.getInt(cursor
					.getColumnIndex(DBHelper.HAVE_MODIFIERS));

			switch (isActivated) {
			case 0:
				item.setActivated(false);
				break;

			case 1:
				item.setActivated(true);
				break;

			default:
				break;

			}

			switch (isRestricted) {
			case 0:
				item.setRestricted(false);
				break;

			case 1:
				item.setRestricted(true);
				break;

			default:
				break;

			}

			switch (hasModifiers) {
			case 0:
				item.setHasModifiers(false);
				break;

			case 1:
				item.setHasModifiers(true);
				break;

			default:
				break;

			}

			// get the values of the item here
			item.setItemCode(cursor.getString(cursor
					.getColumnIndex(DBHelper.ITEM_CODE)));
			item.setItemDescription(cursor.getString(cursor
					.getColumnIndex(DBHelper.ITEM_DESC)));
			item.setFullName(cursor.getString(cursor
					.getColumnIndex(DBHelper.ITEM_FULLNAME)));
			item.setItemId(cursor.getString(cursor
					.getColumnIndex(DBHelper.ITEM_ID)));
			item.setItemName(cursor.getString(cursor
					.getColumnIndex(DBHelper.ITEM_NAME)));
			item.setPrice(cursor.getDouble(cursor
					.getColumnIndex(DBHelper.ITEM_PRICE)));
			item.setCategoryId(cursor.getString(cursor
					.getColumnIndex(DBHelper.CATEGORY_ID)));

			cursor.close();

			return item;
		}

		return item;
	}

	public Cursor getItemDataSearchedFor(String searchFor, String sectionId) {
		sqLiteDatabase = dbHelper.openDataBase();
		
		String COLUMNS_FOR_FINAL_QUERY =  DBHelper.ID + "," + DBHelper.CATEGORY_ID + "," 
										  + DBHelper.CATEGORY_PARENT_ID + "," 
										  + DBHelper.CATEGORY_NAME  + "," 
										  + DBHelper.CATEGORY_NAME_LOWER + "," 
										  + DBHelper.ITEM_NAME_LOWER + "," 
										  +	DBHelper.ITEM_NAME + "," 
										  + DBHelper.ITEM_ID + "," 
										  + DBHelper.ITEM_CODE + "," 
										  + DBHelper.ITEM_DESC + "," 
										  + DBHelper.ITEM_FULLNAME + "," 
										  +	DBHelper.ITEM_PRICE + "," 
										  + DBHelper.ITEM_IS_ACTIVATED + "," 
										  + DBHelper.ITEM_IS_RESTRICTED + "," 
										  + DBHelper.HAVE_MODIFIERS;

		String CATEGORY_COLUMNS =  "'' as " + DBHelper.CATEGORY_PARENT_ID + "," 
				+ DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_NAME + ","
				+ DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_NAME_LOWER;
		
		String CATEGORY_COLUMNS_NOT_PART_OF_CATEGORY_TABLE =  "'' as " + DBHelper.CATEGORY_PARENT_ID + "," 
				+ "'' as " + DBHelper.CATEGORY_NAME + ","
				+ "'' as " + DBHelper.CATEGORY_NAME_LOWER;

		String COLUMNS_FOR_ITEMS = DBHelper.ITEM_TABLE + "." + DBHelper.ID
				+ ", " + DBHelper.ITEM_TABLE + "." + DBHelper.CATEGORY_ID + ","
				+ CATEGORY_COLUMNS
				+ "," + DBHelper.ITEM_NAME_LOWER + "," + DBHelper.ITEM_NAME
				+ "," + DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID + ","
				+ DBHelper.ITEM_CODE + "," + DBHelper.ITEM_DESC + ","
				+ DBHelper.ITEM_FULLNAME + "," + DBHelper.ITEM_PRICE + ","
				+ DBHelper.ITEM_IS_ACTIVATED + ","
				+ DBHelper.ITEM_IS_RESTRICTED + "," + DBHelper.HAVE_MODIFIERS;
		
		String COLUMNS_FOR_ITEMS_WITHOUT_CATEGORIES = 
				DBHelper.ITEM_TABLE + "." + DBHelper.ID 
				+ ", " + DBHelper.ITEM_TABLE + "." + DBHelper.CATEGORY_ID + ","
				+ CATEGORY_COLUMNS_NOT_PART_OF_CATEGORY_TABLE
				+ "," + DBHelper.ITEM_NAME_LOWER + "," + DBHelper.ITEM_NAME
				+ "," + DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID + ","
				+ DBHelper.ITEM_CODE + "," + DBHelper.ITEM_DESC + ","
				+ DBHelper.ITEM_FULLNAME + "," + DBHelper.ITEM_PRICE + ","
				+ DBHelper.ITEM_IS_ACTIVATED + ","
				+ DBHelper.ITEM_IS_RESTRICTED + "," + DBHelper.HAVE_MODIFIERS;

		String sql = "select " + COLUMNS_FOR_ITEMS + " from "
				+ DBHelper.ITEM_TABLE + " join " + DBHelper.CATEGORY_TABLE 
				+ " on " + DBHelper.ITEM_TABLE + "." + DBHelper.CATEGORY_ID + "=" + DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_ID
				+ " and ( " + DBHelper.ITEM_TABLE + "."
				+ DBHelper.ITEM_NAME_LOWER + " like " + "'%"
				+ searchFor.toLowerCase() + "%'" + " or " + DBHelper.ITEM_TABLE
				+ "." + DBHelper.ITEM_DESC + " like " + "'%" + searchFor + "%'"
				+ " or " + DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_FULLNAME
				+ " like " + "'%" + searchFor + "%'" + " or "
				+ DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_NAME_LOWER
				+ " like " + "'%" + searchFor.toLowerCase() + "%' ) "
				+ " join " + DBHelper.SECTION_ITEM_TABLE + " on "
				+ DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID + " = "
				+ DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.ITEM_ID
				+ " and " + DBHelper.SECTION_ITEM_TABLE + "."
				+ DBHelper.SECTION_ID + " ='" + sectionId + "'";
		

		String sqlQueryForItemsNotUnderAnyCategory = "select " + COLUMNS_FOR_ITEMS_WITHOUT_CATEGORIES + " from "
				+ DBHelper.ITEM_TABLE 
				+ " join " + DBHelper.SECTION_ITEM_TABLE + " on "
				+ DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID + " = "
				+ DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.ITEM_ID
				+ " and " + DBHelper.SECTION_ITEM_TABLE + "."
				+ DBHelper.SECTION_ID + " ='" + sectionId + "'"
				+ " and ( " + DBHelper.ITEM_TABLE + "."
				+ DBHelper.ITEM_NAME_LOWER + " like " + "'%"
				+ searchFor.toLowerCase() + "%'" + " or " + DBHelper.ITEM_TABLE
				+ "." + DBHelper.ITEM_DESC + " like " + "'%" + searchFor + "%'"
				+ " or " + DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_FULLNAME
				+ " like " + "'%" + searchFor + "%') ";
		
		String orderByClause = " order by " + DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_IS_ACTIVATED
								+ " DESC ";
		
		String query = "select " + COLUMNS_FOR_FINAL_QUERY + " from (" +  sql + 
				" union " + sqlQueryForItemsNotUnderAnyCategory + orderByClause + " ) group by " +
				DBHelper.ID;
		

		//TODO: Dhara
		
		Cursor c = sqLiteDatabase.rawQuery(query, null);
		return c;
	}

	public void deleteFromAllTables() {


		sqLiteDatabase = dbHelper.openDataBase();
		sqLiteDatabase.delete(DBHelper.CATEGORY_TABLE, null, null);
		sqLiteDatabase.delete(DBHelper.ITEM_TABLE, null, null);
		sqLiteDatabase.delete(DBHelper.MODIFIERS_TABLE, null, null);
		sqLiteDatabase.delete(DBHelper.SECTION_ITEM_TABLE, null, null);
		sqLiteDatabase.delete(DBHelper.ITEM_MODIFIERS_MAPPING, null, null);
		sqLiteDatabase.delete(DBHelper.GROUP_MODIFIER_MASTER, null, null);
	
	
		if(sqLiteDatabase != null &&  
				sqLiteDatabase.isOpen()) {
			sqLiteDatabase.close();
		}
	}

	public int getCountOfItemsModifiers(String sectionId, String itemId) {
		sqLiteDatabase = dbHelper.openDataBase();

		String sql = "select " + DBHelper.ITEM_MODIFIERS_MAPPING + "."
				+ DBHelper.MODIFIER_ID + " from "
				+ DBHelper.ITEM_MODIFIERS_MAPPING + " where "
				+ DBHelper.ITEM_MODIFIERS_MAPPING + "." + DBHelper.ITEM_ID
				+ " ='" + itemId + "'";

		Cursor c = sqLiteDatabase.rawQuery(sql, null);

		int count = 0;

		if (c != null)
			count = c.getCount();

		c.close();
		return count;
	}

	public void insertIntoGroupMaster(List<GroupModifierMaster> groupList) {
		int groupIdIndex, groupNameIndex;

		if (groupList != null && groupList.size() > 0) {


			sqLiteDatabase = dbHelper.openDataBase();
			InsertHelper insertHelper = new InsertHelper(sqLiteDatabase,
					DBHelper.GROUP_MODIFIER_MASTER);

			groupIdIndex = insertHelper.getColumnIndex(DBHelper.GROUP_ID);
			groupNameIndex = insertHelper.getColumnIndex(DBHelper.GROUP_NAME);

			sqLiteDatabase.beginTransaction();
			try {
				for (GroupModifierMaster groupModifier : groupList) {
					insertHelper.prepareForInsert();
					insertHelper.bind(groupIdIndex, groupModifier.getGroupId());
					insertHelper.bind(groupNameIndex,
							groupModifier.getGroupName());
					insertHelper.execute();
				}
				sqLiteDatabase.setTransactionSuccessful();
			} finally {
				sqLiteDatabase.endTransaction();
			}
		
		
		}
	}

	public Cursor getModifiersGroup(String itemId) {
		sqLiteDatabase = dbHelper.openDataBase();

		String sql = "select * from " + DBHelper.ITEM_MODIFIERS_MAPPING
				+ " join " + DBHelper.GROUP_MODIFIER_MASTER + " on "
				+ DBHelper.ITEM_MODIFIERS_MAPPING + "." + DBHelper.GROUP_ID
				+ " = " + DBHelper.GROUP_MODIFIER_MASTER + "."
				+ DBHelper.GROUP_ID + " and ("
				+ DBHelper.ITEM_MODIFIERS_MAPPING + "." + DBHelper.MODIFIER_ID
				+ " = '' or " + DBHelper.ITEM_MODIFIERS_MAPPING + "."
				+ DBHelper.MODIFIER_ID + " IS NULL) and "
				+ DBHelper.ITEM_MODIFIERS_MAPPING + "." + DBHelper.ITEM_ID
				+ " = '" + itemId + "'";

		Cursor c = sqLiteDatabase.rawQuery(sql, null);

		Log.e("dhara","group modifiers : " + sql);
		
		if (c != null && c.moveToFirst())
			return c;
		
		
		return null;
	}

	public int getCountOfGroupModifiers(String itemId) {
		sqLiteDatabase = dbHelper.openDataBase();

		String sql = "select group_id from " + DBHelper.ITEM_MODIFIERS_MAPPING
				+ " where " + DBHelper.ITEM_MODIFIERS_MAPPING + "."
				+ DBHelper.MODIFIER_ID + " = '' and "
				+ DBHelper.ITEM_MODIFIERS_MAPPING + "." + DBHelper.ITEM_ID
				+ " = '" + itemId + "'";

		Cursor c = sqLiteDatabase.rawQuery(sql, null);

		int count = 0;

		if (c != null)
			count = c.getCount();

		c.close();

		return count;
	}

	public Cursor getModifiersUnderGroup(Context context, String groupId) {
		sqLiteDatabase = dbHelper.openDataBase();

		String sql = "select * from " + DBHelper.MODIFIERS_TABLE + " where "
				+ DBHelper.GROUP_ID + " ='" + groupId + "'";
		Cursor c = sqLiteDatabase.rawQuery(sql, null);

		if (c != null && c.moveToFirst())
			return c;
		return null;
	}

	public Cursor getModifiersUnderGroup(String groupId) {
		sqLiteDatabase = dbHelper.openDataBase();

		String sql = "select * from " + DBHelper.MODIFIERS_TABLE + " where "
				+ DBHelper.GROUP_ID + " ='" + groupId + "'";
		Cursor c = sqLiteDatabase.rawQuery(sql, null);

		if (c != null && c.moveToFirst())
			return c;
		return null;
	}

	/**
	 * Changes as on 7th November, 2013 Added this function to get all the
	 * immediate sub categories and items under a category id and section id
	 */

	public Cursor getSubcategoriesAndItems(Context context, String categoryId,
			String sectionId) {
		sqLiteDatabase = dbHelper.openDataBase();

		String COLUMNS_FOR_CATEGORY = DBHelper.CATEGORY_TABLE + "._id as _id, "
				+ DBHelper.CATEGORY_ID + "," + DBHelper.CATEGORY_PARENT_ID
				+ "," + DBHelper.CATEGORY_NAME + ","
				+ DBHelper.CATEGORY_NAME_LOWER + "," + "'' as "
				+ DBHelper.ITEM_NAME_LOWER + "," + "'' as "
				+ DBHelper.ITEM_NAME + "," + "'' as " + DBHelper.ITEM_ID + ","
				+ "'' as " + DBHelper.ITEM_CODE + "," + "'' as "
				+ DBHelper.ITEM_DESC + "," + "'' as " + DBHelper.ITEM_FULLNAME
				+ "," + "'' as " + DBHelper.ITEM_PRICE + "," + "'' as "
				+ DBHelper.ITEM_IS_ACTIVATED + "," + "'' as "
				+ DBHelper.ITEM_IS_RESTRICTED + "," + "'' as "
				+ DBHelper.HAVE_MODIFIERS;

		String COLUMNS_FOR_ITEMS = DBHelper.ITEM_TABLE + "._id as _id, "
				+ DBHelper.CATEGORY_ID + "," + "'' as "
				+ DBHelper.CATEGORY_PARENT_ID + "," + "'' as "
				+ DBHelper.CATEGORY_NAME + "," + "'' as "
				+ DBHelper.CATEGORY_NAME_LOWER + "," + DBHelper.ITEM_NAME_LOWER
				+ "," + DBHelper.ITEM_NAME + "," + DBHelper.ITEM_TABLE + "."
				+ DBHelper.ITEM_ID + "," + DBHelper.ITEM_CODE + ","
				+ DBHelper.ITEM_DESC + "," + DBHelper.ITEM_FULLNAME + ","
				+ DBHelper.ITEM_PRICE + "," + DBHelper.ITEM_IS_ACTIVATED + ","
				+ DBHelper.ITEM_IS_RESTRICTED + "," + DBHelper.HAVE_MODIFIERS;

		String sql = "Select " + COLUMNS_FOR_CATEGORY + " from "
				+ DBHelper.CATEGORY_TABLE + " where "
				+ DBHelper.CATEGORY_PARENT_ID + "='" + categoryId + "' "
				+ " union " + "Select " + COLUMNS_FOR_ITEMS + " from "
				+ DBHelper.ITEM_TABLE + " join " + DBHelper.SECTION_ITEM_TABLE
				+ " on " + DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.ITEM_ID
				+ " = " + DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID
				+ " and " + DBHelper.ITEM_TABLE + "." + DBHelper.CATEGORY_ID
				+ "='" + categoryId + "' and " + DBHelper.SECTION_ITEM_TABLE
				+ "." + DBHelper.SECTION_ID + "='" + sectionId + "'";

		Cursor c = sqLiteDatabase.rawQuery(sql, null);
		if (c != null && c.moveToFirst())
			return c;
		return null;

	}

	public Cursor getSubcategoriesAndItems(String categoryId, String sectionId) {
		sqLiteDatabase = dbHelper.openDataBase();

		String COLUMNS_FOR_CATEGORY = DBHelper.CATEGORY_TABLE + "._id as _id, "
				+ DBHelper.CATEGORY_ID + "," + DBHelper.CATEGORY_PARENT_ID
				+ "," + DBHelper.CATEGORY_NAME + ","
				+ DBHelper.CATEGORY_NAME_LOWER + "," + "'' as "
				+ DBHelper.ITEM_NAME_LOWER + "," + "'' as "
				+ DBHelper.ITEM_NAME + "," + "'' as " + DBHelper.ITEM_ID + ","
				+ "'' as " + DBHelper.ITEM_CODE + "," + "'' as "
				+ DBHelper.ITEM_DESC + "," + "'' as " + DBHelper.ITEM_FULLNAME
				+ "," + "'' as " + DBHelper.ITEM_PRICE + "," + "'' as "
				+ DBHelper.ITEM_IS_ACTIVATED + "," + "'' as "
				+ DBHelper.ITEM_IS_RESTRICTED + "," + "'' as "
				+ DBHelper.HAVE_MODIFIERS;

		String COLUMNS_FOR_ITEMS = DBHelper.ITEM_TABLE + "._id as _id, "
				+ DBHelper.CATEGORY_ID + "," + "'' as "
				+ DBHelper.CATEGORY_PARENT_ID + "," + "'' as "
				+ DBHelper.CATEGORY_NAME + "," + "'' as "
				+ DBHelper.CATEGORY_NAME_LOWER + "," + DBHelper.ITEM_NAME_LOWER
				+ "," + DBHelper.ITEM_NAME + "," + DBHelper.ITEM_TABLE + "."
				+ DBHelper.ITEM_ID + "," + DBHelper.ITEM_CODE + ","
				+ DBHelper.ITEM_DESC + "," + DBHelper.ITEM_FULLNAME + ","
				+ DBHelper.ITEM_PRICE + "," + DBHelper.ITEM_IS_ACTIVATED + ","
				+ DBHelper.ITEM_IS_RESTRICTED + "," + DBHelper.HAVE_MODIFIERS;

		String sql = "Select " + COLUMNS_FOR_CATEGORY + " from "
				+ DBHelper.CATEGORY_TABLE + " where "
				+ DBHelper.CATEGORY_PARENT_ID + "='" + categoryId + "' "
				+ " union " + "Select " + COLUMNS_FOR_ITEMS + " from "
				+ DBHelper.ITEM_TABLE + " join " + DBHelper.SECTION_ITEM_TABLE
				+ " on " + DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.ITEM_ID
				+ " = " + DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID
				+ " and " + DBHelper.ITEM_TABLE + "." + DBHelper.CATEGORY_ID
				+ "='" + categoryId + "' and " + DBHelper.SECTION_ITEM_TABLE
				+ "." + DBHelper.SECTION_ID + "='" + sectionId + "'";

		Cursor c = sqLiteDatabase.rawQuery(sql, null);
		if (c != null && c.moveToFirst())
			return c;
		return null;

	}

	public Cursor getCategoriesForList(String sectionId) {
		Cursor cursor = null;


		// changes as on 16th November 2013
		// query to get list of items not under any category
		String COLUMNS_FOR_CATEGORY = DBHelper.CATEGORY_TABLE + "._id as _id, "
				+ DBHelper.CATEGORY_ID + "," + DBHelper.CATEGORY_PARENT_ID
				+ "," + DBHelper.CATEGORY_NAME + ","
				+ DBHelper.CATEGORY_NAME_LOWER + "," + "'' as "
				+ DBHelper.ITEM_NAME_LOWER + "," + "'' as "
				+ DBHelper.ITEM_NAME + "," + "'' as " + DBHelper.ITEM_ID + ","
				+ "'' as " + DBHelper.ITEM_CODE + "," + "'' as "
				+ DBHelper.ITEM_DESC + "," + "'' as " + DBHelper.ITEM_FULLNAME
				+ "," + "'' as " + DBHelper.ITEM_PRICE + "," + "'' as "
				+ DBHelper.ITEM_IS_ACTIVATED + "," + "'' as "
				+ DBHelper.ITEM_IS_RESTRICTED + "," + "'' as "
				+ DBHelper.HAVE_MODIFIERS;

		// commenting this query since
		// deleted categories dont have to be shown
		// String sql = "Select " + COLUMNS_FOR_CATEGORY + " from "
		//		+ DBHelper.CATEGORY_TABLE + " where ("
		//		+ DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_PARENT_ID
		//		+ " = '' or " + DBHelper.CATEGORY_TABLE + "."
		//		+ DBHelper.CATEGORY_PARENT_ID + " IS NULL )";

		// changes as on 3rd December 2013
		// deleted categories are not displayed
		
		// changes as on 20th Jan 2014
		// since categories with no subcategories and just having direct items where not being displayed
		
		// The query fetches the list of categories who have subcategories and those subcategories have items,
		// and/or those categories who have items
		// categories, having no items will not be displayed
		
		/* String sql = "Select " + COLUMNS_FOR_CATEGORY + " from "
				+ DBHelper.CATEGORY_TABLE + " where " 
				+ DBHelper.CATEGORY_ID 
				+ " in ( Select " + DBHelper.CATEGORY_PARENT_ID 
				+ " from " + DBHelper.CATEGORY_TABLE + " where " 
				+ DBHelper.CATEGORY_ID 
				+ " in ( Select " + DBHelper.CATEGORY_ID 
				+ " from " + DBHelper.ITEM_TABLE + " group by " + DBHelper.CATEGORY_ID + " ) " 
				+ " and " + DBHelper.CATEGORY_PARENT_ID + " IS NOT NULL ) " 
				+ " and " + DBHelper.CATEGORY_PARENT_ID + " = '' or " 
				+ DBHelper.CATEGORY_PARENT_ID + " IS NULL "; */
		
		
		String sql = "Select " + COLUMNS_FOR_CATEGORY + " from "
				+ DBHelper.CATEGORY_TABLE + " where (" 
				+ DBHelper.CATEGORY_ID 
				+ " in ( Select " + DBHelper.CATEGORY_PARENT_ID 
				+ " from " + DBHelper.CATEGORY_TABLE + " where " 
				+ DBHelper.CATEGORY_ID 
				+ " in ( Select " + DBHelper.CATEGORY_ID 
				+ " from " + DBHelper.ITEM_TABLE
				+ " join " + DBHelper.SECTION_ITEM_TABLE 
				+ " on " 
				+ DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID + "=" + DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.ITEM_ID 
				+ " where " + DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.SECTION_ID + "='" + sectionId + "'"
				+ " group by " + DBHelper.CATEGORY_ID + " ) " 
				+ " and " + DBHelper.CATEGORY_PARENT_ID + " IS NOT NULL ) or "
				+ DBHelper.CATEGORY_ID + " in " 
				+ "(Select " + DBHelper.CATEGORY_ID + " from " + DBHelper.CATEGORY_TABLE
				+ " where " + DBHelper.CATEGORY_ID + " in (Select " + DBHelper.CATEGORY_ID 
				+ " from " + DBHelper.ITEM_TABLE + " join " + DBHelper.SECTION_ITEM_TABLE 
				+ " on " 
				+ DBHelper.ITEM_TABLE + "." + DBHelper.ITEM_ID + "=" + DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.ITEM_ID 
				+ " where " + DBHelper.SECTION_ITEM_TABLE + "." + DBHelper.SECTION_ID + "='" + sectionId + "'"
				+  " group by " + DBHelper.CATEGORY_ID +" ))) "
				+ " and " + DBHelper.CATEGORY_PARENT_ID + " = '' or " 
				+ DBHelper.CATEGORY_PARENT_ID + " IS NULL ";
				
		sqLiteDatabase = dbHelper.openDataBase();
		
		// 20th Jan 2013
		//Log.i("dhara","sql >>> " + sql);

		cursor = sqLiteDatabase.rawQuery(sql, null);

		return cursor;
	}
	
	public void close() {
		dbHelper.close();
	}
}
