package com.azilen.waiterpad.data;

import java.util.List;

/**
 * Holds the list of sections and their menu details
 * @author dhara.shah
 *
 */
public class SectionMenuList {
	private List<SectionMenu> lstSectionMenus;

	/**
	 * @return the lstSectionMenus
	 */
	public List<SectionMenu> getLstSectionMenus() {
		return lstSectionMenus;
	}

	/**
	 * @param lstSectionMenus the lstSectionMenus to set
	 */
	public void setLstSectionMenus(List<SectionMenu> lstSectionMenus) {
		this.lstSectionMenus = lstSectionMenus;
	}
}
