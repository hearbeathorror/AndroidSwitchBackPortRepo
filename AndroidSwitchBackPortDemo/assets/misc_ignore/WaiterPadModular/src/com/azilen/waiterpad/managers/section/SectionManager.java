package com.azilen.waiterpad.managers.section;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.data.Section;
import com.azilen.waiterpad.data.SectionTable;
import com.azilen.waiterpad.data.SectionWiseTable;
import com.azilen.waiterpad.data.Tables;
import com.azilen.waiterpad.data.Tables.TableType;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import android.support.v4.util.LruCache;

public class SectionManager {
	/* Singleton Pattern using Eager Initialization */
	private static final SectionManager instance = new SectionManager();
	private LruCache<String, Object> sectionCache;
	
	// max cache size 1 MB = 1024 KB
	private int maxSize = 2 * 1024 * 1024;

	/* Singleton Pattern */
	public static SectionManager getInstance() {
		return instance;
	}
	
	public SectionManager() {
		sectionCache = new LruCache<String, Object>(maxSize);
	}
	
	public LruCache<String, Object> getSectionCache() {
		return sectionCache;
	}
	
	/**
	 * Stores data into the memCache of the device Used by
	 * {@GetTableListAsyncTask} and
	 * {@GetAllDataAsyncTask}
	 * 
	 * @param result
	 * @param memCache
	 */
	public void storeTableDataIntoCache(SectionWiseTable result) {
		if (result != null) {
			if (result.getSectionTables() != null
					&& result.getSectionTables().size() > 0) {

				sectionCache.put(Global.SECTION_WISE_TABLE, result);

				SectionWiseTable mSectionWiseTable = (SectionWiseTable) sectionCache
						.get(Global.SECTION_WISE_TABLE);

				if (mSectionWiseTable != null
						&& mSectionWiseTable.getSectionTables() != null) {
					List<SectionTable> lst = mSectionWiseTable
							.getSectionTables();
					List<Section> sections = new ArrayList<Section>();

					HashMap<String, List<Tables>> tableHashMap = new HashMap<String, List<Tables>>();
					String sectionIdInitial = "";
					String sectionNameInitial = "";

					int counter = 0;
					for (SectionTable sectionTable : lst) {
						Section sectionObj = new Section();
						sectionObj.setSectionId(sectionTable.getId());
						sectionObj
								.setSectionName(sectionTable.getSectionName());
						sections.add(sectionObj);

						if (counter == 0) {
							sectionIdInitial = sectionObj.getSectionId()
									.toString();
							sectionNameInitial = sectionObj.getSectionName()
									.toString();
							
							if(Prefs.getKey(Prefs.SECTION_ID) != null && 
									Prefs.getKey(Prefs.SECTION_ID).trim().length() <= 0) {
								Prefs.addKey(WaiterPadApplication.getAppContext(), 
										Prefs.SECTION_ID,sectionIdInitial);
								Prefs.addKey(WaiterPadApplication.getAppContext(), 
										Prefs.SECTION_NAME,sectionNameInitial);
							}
						}

						tableHashMap.put(sectionTable.getId(),
								sectionTable.getTableList());
						counter++;
					}
					sectionCache.put(Global.SECTIONS, sections);
					sectionCache.put(Global.TABLE_MAP, tableHashMap);
				}
			}
		}
	}
	
	public void clearSectionDetails() {
		sectionCache.remove(Global.SECTIONS);
		sectionCache.remove(Global.TABLE_MAP);
	}
	
	/**
	 * Deserializes the {@link TableType} enum gson receives
	 * 
	 * @author dhara.shah
	 * 
	 */
	public static class TableTypeDeserializer implements
			JsonDeserializer<Tables.TableType> {
		@Override
		public TableType deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext ctx) throws JsonParseException {
			int typeInt = json.getAsInt();
			return TableType.findByAbbr(typeInt);
		}
	}
}
