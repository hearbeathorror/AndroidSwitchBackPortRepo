package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SectionWiseTable {
	@SerializedName(value="SectionsList")
	private List<SectionTable> sectionTables;

	/**
	 * @return the sectionTables
	 */
	public List<SectionTable> getSectionTables() {
		return sectionTables;
	}

	/**
	 * @param sectionTables the sectionTables to set
	 */
	public void setSectionTables(List<SectionTable> sectionTables) {
		this.sectionTables = sectionTables;
	}
}
