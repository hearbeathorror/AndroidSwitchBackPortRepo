package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.ModifierMaster;
import com.google.common.base.Predicate;

/**
 * Returns the modifiers that are selected
 * @author dhara.shah
 *
 */
public class GetAllSelectedModifers implements Predicate<ModifierMaster>{
	/**
	 * Empty constructor
	 */
	public GetAllSelectedModifers() {
		
	}

	@Override
	public boolean apply(ModifierMaster modifiers) {
		return modifiers.isSelected() == true;
	}
}
