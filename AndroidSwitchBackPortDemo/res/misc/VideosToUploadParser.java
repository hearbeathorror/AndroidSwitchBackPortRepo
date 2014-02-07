package com.azilen.insuranceapp.managers.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.azilen.insuranceapp.entities.VideosToUpload;
import com.azilen.insuranceapp.utils.Global;

public class VideosToUploadParser extends DefaultHandler{
	private String mPath;
	private String mTempVal = "";
	private VideosToUpload mVideoToUpload;
	private List<VideosToUpload> mVidsToUpload;
	
	/**
	 * Constructor that takes the file path as a parameter
	 * @param xmlFilePath
	 */
	public VideosToUploadParser(String xmlFilePath) {
		mPath = xmlFilePath;
	}
	
	/**
	 * Parses the entire document
	 * @return
	 */
	public List<VideosToUpload> parseDocument() {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			saxParser.parse(new InputSource(new FileReader(mPath)),this);
			return mVidsToUpload;
		}catch(SAXException e) {
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		mTempVal = "";
		
		if(qName.equalsIgnoreCase(Global.XML_ROOT_NODE)) {
			mVidsToUpload = new ArrayList<VideosToUpload>();
		}else if(qName.equalsIgnoreCase(Global.VIDEO_DETAIL)) {
			mVideoToUpload = new VideosToUpload();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		mTempVal = new String(ch,start,length);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		if(qName.equalsIgnoreCase(Global.XML_ROOT_NODE)) {
			// do nothing
		}else if(qName.equalsIgnoreCase(Global.VIDEO_DETAIL)) {
			mVidsToUpload.add(mVideoToUpload);
		}else if(qName.equalsIgnoreCase(Global.VIDEO_PATH)) {
			mVideoToUpload.setVideoPath(mTempVal);
		}else if(qName.equalsIgnoreCase(Global.ACTIVITY_ID)) {
			mVideoToUpload.setActivityId(mTempVal);
		}else if(qName.equalsIgnoreCase(Global.IS_VIDEO_SET)) {
			mVideoToUpload.setIsVideoSet(mTempVal);
		}else if(qName.equalsIgnoreCase(Global.VIDEO_SIZE)) {
			mVideoToUpload.setVideoSize(mTempVal);
		}else if(qName.equalsIgnoreCase(Global.VIDEO_DURATION)) {
			mVideoToUpload.setVideoDuration(mTempVal);
		}
	}
}
