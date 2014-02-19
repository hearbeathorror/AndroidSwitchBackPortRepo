package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Languages extends ResponseEntity{
	@SerializedName(value="Languages")
	private List<String> languages;
	@SerializedName(value="Xml")
	private String languageXml;
	
	public List<String> getLanguages() {
		return languages;
	}
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	public String getLanguageXml() {
		return languageXml;
	}
	public void setLanguageXml(String languageXml) {
		this.languageXml = languageXml;
	}
}
