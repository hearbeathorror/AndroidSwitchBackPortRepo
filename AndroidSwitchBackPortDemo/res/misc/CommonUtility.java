package com.azilen.insuranceapp.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.azilen.insuranceapp.entities.VideosToUpload;
import com.azilen.insuranceapp.managers.parser.VideosToUploadParser;
import com.azilen.insuranceapp.utils.Logger.modules;

/**
 * Created as on 31st December 2013, 1:56 PM
 * CommonUtility, contains all common util functions
 * @author dhara.shah
 *
 */
public class CommonUtility {
	private static String TAG = CommonUtility.class.getSimpleName();
	
	/**
	 * Gets the execption message from the exception
	 * @param e
	 * @return
	 */
	public static String getExceptionMSG(Exception e) {
		String msgString = "Exception : ";
		msgString += e.getMessage();
		msgString += "\nStack : ";
		StackTraceElement array[] = e.getStackTrace();
		for(int i = 0; i < array.length; i++) {
			msgString += array[i].getClassName();
			msgString +=  " : " + array[i].getMethodName();
			msgString += " : " + array[i].getFileName();
			msgString += " : " + array[i].getLineNumber();
			msgString += "\n";
		}
		return msgString;
	}
	
	/**
	 * When the record button is pressed
	 * and if the video is not uploaded on the server
	 * the entry will be made into the xml file 
	 * @param activityId
	 * @param videoPath
	 */
	public static void writeToXmlFile(String activityId, String videoPath, 
			String videoSize, String videoDuration) {
		try {
			String xmlFilePath = ExternalStorageManager.InsuranceAppXmlDIR
					+ "videolist.xml";
			File dir = new File(xmlFilePath);
			FileOutputStream fileOutputStream = null;
			
			if(!dir.exists()) {
				fileOutputStream = new FileOutputStream(dir);
				String data = writeToNewFile(activityId, videoPath, videoSize,videoDuration);
				fileOutputStream.write(data.getBytes());
				fileOutputStream.close();
			}else {
				// append data here
				parseFileAndAppend(xmlFilePath, activityId, videoPath, videoSize, videoDuration);
			}
		}catch(FileNotFoundException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		}catch(RuntimeException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		} catch (IOException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		}
	}
	
	/**
	 * If the xml file does not exist the following code will be added to it
	 * the xml files format will be :
	 * 
	 * <Root>
	 * 	<VideoDetail>
	 * 		<ActivityID>1</ActivityID>
	 * 		<VideoPath>path here</VideoPath>
	 * 	</VideoDetail>
	 * 	.. more nodes will be added here
	 *  and those uploaded will be removed from this file
	 * </Root>
	 * 
	 * @param activityId
	 * @param videoPath
	 * @param videoSize
	 * @param videoDuration
	 * @return
	 */
	public static String writeToNewFile(String activityId, String videoPath, 
			String videoSize, String videoDuration) {
		 XmlSerializer serializer = Xml.newSerializer();
		 StringWriter writer = new StringWriter();
		 try {
			 serializer.setOutput(writer);
			 serializer.startDocument("UTF-8", true);

			 // main tag
			 serializer.startTag("", Global.XML_ROOT_NODE);
			 serializer.startTag("", Global.VIDEO_DETAIL);

			 serializer.startTag("", Global.ACTIVITY_ID);
			 serializer.text(activityId);
			 serializer.endTag("", Global.ACTIVITY_ID);
			 
			 serializer.startTag("", Global.VIDEO_PATH);
			 serializer.text(videoPath);
			 serializer.endTag("", Global.VIDEO_PATH);
			 
			 // default value of is_video_set is false(0)
			 serializer.startTag("", Global.IS_VIDEO_SET);
			 serializer.text("0");
			 serializer.endTag("", Global.IS_VIDEO_SET);
			 
			 serializer.startTag("", Global.VIDEO_SIZE);
			 serializer.text(videoSize);
			 serializer.endTag("", Global.VIDEO_SIZE);
			 
			 serializer.startTag("", Global.VIDEO_DURATION);
			 serializer.text(videoDuration);
			 serializer.endTag("", Global.VIDEO_DURATION);

			 serializer.endTag("", Global.VIDEO_DETAIL);
			 serializer.endTag("", Global.XML_ROOT_NODE);
			 serializer.endDocument();
			 serializer.flush();

			 String dataWrite= writer.toString();
			 return dataWrite;
		 } catch (Exception e) {
			 throw new RuntimeException(e);
		 } 
	}
	
	/**
	 * If the file exists,then the video details 
	 * will be appended as a new node into the xml
	 * @param xmlFilePath
	 * @param activityId
	 * @param videoPath
	 * @param videoSize
	 * @param videoDuration
	 */
	public static void parseFileAndAppend(String xmlFilePath, String activityId, 
			String videoPath, String videoSize, String videoDuration) {
		
		// checks if the node is present
		// if yes, delete the node, 
		// so that the node would not be appended again
		if(isNodePresent(activityId)) {
			deleteFromXmlFile(activityId);
		}
		
		addNodeToFile(xmlFilePath, activityId, videoPath, videoSize, videoDuration);
	}
	
	/**
	 * Adds the node with the values to the xml file
	 * @param xmlFilePath
	 * @param activityId
	 * @param videoPath
	 * @param videoSize
	 * @param videoDuration
	 */
	private static void addNodeToFile(String xmlFilePath, String activityId, 
			String videoPath, String videoSize, String videoDuration) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(new File(xmlFilePath));
			
			Node xmlVideoNodes = document.getElementsByTagName(Global.XML_ROOT_NODE).item(0);
			
			Element root = document.createElement(Global.VIDEO_DETAIL);
			Element item = document.createElement(Global.ACTIVITY_ID);
	        item.appendChild(document.createTextNode(activityId));
	        root.appendChild(item);
	        
	        item = document.createElement(Global.VIDEO_PATH);
	        item.appendChild(document.createTextNode(videoPath));
	        root.appendChild(item);
	        
	        item = document.createElement(Global.IS_VIDEO_SET);
	        item.appendChild(document.createTextNode("0"));
	        root.appendChild(item);
	        
	        item = document.createElement(Global.VIDEO_SIZE);
	        item.appendChild(document.createTextNode(videoSize));
	        root.appendChild(item);
	        
	        item = document.createElement(Global.VIDEO_DURATION);
	        item.appendChild(document.createTextNode(videoDuration));
	        root.appendChild(item);
			
	        xmlVideoNodes.appendChild(root);
			saveXmlFile(document,xmlFilePath);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		} catch (SAXException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		} catch(Exception e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		}
	}
	
	
	/**
	 * Any changes made to the xml, either append or 
	 * deletion of nodes will be written back to the xml
	 * @param document
	 * @param xmlFilePath
	 */
	private static void saveXmlFile(Document document,String xmlFilePath) {
		try {
			Source source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(xmlFilePath));
	        //StreamResult result = new StreamResult(new OutputStreamWriter(fileOutputStream, "ISO-8859-1"));
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();                        
	        xformer.transform(source, result);
		}catch (TransformerConfigurationException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e.getException()));
		} catch (TransformerException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		}
	}
	
	/**
	 * When a video is uploaded, 
	 * the node will be removed from the xml
	 * @param activityId
	 */
	public static void deleteFromXmlFile(String activityId) {
		try {
			boolean isToDelete = false;
			String xmlFilePath = ExternalStorageManager.InsuranceAppXmlDIR
					+ "videolist.xml";
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(new File(xmlFilePath));
			
			Node xmlVideoNodes = document.getElementsByTagName(Global.XML_ROOT_NODE).item(0);
			NodeList nodeList = xmlVideoNodes.getChildNodes();
			
			for(int i=0;i<nodeList.getLength();i++) {
				Node node = nodeList.item(i);
				NodeList vidDetailNodeList = node.getChildNodes();
				
				for(int j=0;j<vidDetailNodeList.getLength();j++) {
					Node innerNode = vidDetailNodeList.item(j);
					if (innerNode.getNodeName().equals(Global.ACTIVITY_ID)) {
						if(innerNode.getTextContent().equals(activityId)) {
							// delete this node
							// and save xml doc
							isToDelete = true;
							break;
						}
					}
				}
				
				if(isToDelete) {
					xmlVideoNodes.removeChild(node);
					break;
				}
			}
			
			// save xml only if changes have been made
			if(isToDelete) {
				saveXmlFile(document, xmlFilePath);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		} catch (SAXException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		} catch (IOException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		}
	}
	
	/**
	 * Checks if the node is present or not
	 * @param activityId
	 * @return
	 */
	public static boolean isNodePresent(String activityId) {
		try {
			String xmlFilePath = ExternalStorageManager.InsuranceAppXmlDIR
					+ "videolist.xml";
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(new File(xmlFilePath));
			
			NodeList videoListNodes = document.getElementsByTagName(Global.VIDEO_DETAIL);
			
			for(int i=0;i<videoListNodes.getLength();i++) {
				Node innerNode = videoListNodes.item(i);
				
				if(innerNode.getChildNodes().item(0).getNodeName().equals(Global.ACTIVITY_ID)) {
					if(innerNode.getChildNodes().item(0).getTextContent().equals(activityId)) {
						return true;
					}
				}
			}
			
			return false;
		}catch(SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * If a video is not uploaded, it is possible that it is also set
	 * If a video is uploaded, then the video will be set
	 * @param activityId
	 * @param isSet
	 */
	public static void modifyIsVideoSetValue(String activityId, String isSet) {
		try {
			boolean isModified = false;
			String xmlFilePath = ExternalStorageManager.InsuranceAppXmlDIR
					+ "videolist.xml";
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(new File(xmlFilePath));
			
			NodeList videoListNodes = document.getElementsByTagName(Global.VIDEO_DETAIL);
			
			for(int i=0;i<videoListNodes.getLength();i++) {
				Node innerNode = videoListNodes.item(i);
				
				if(innerNode.getChildNodes().item(0).getNodeName().equals(Global.ACTIVITY_ID)) {
					if(innerNode.getChildNodes().item(0).getTextContent().equals(activityId)) {
						innerNode.getChildNodes().item(2).setTextContent(isSet);
						isModified = true;
						break;
					}
				}
			}
			
			// save xml only if changes have been made
			if(isModified) {
				saveXmlFile(document, xmlFilePath);
			}
		}catch(ParserConfigurationException e){
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		}catch(IOException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the value of isVideoSet of the video to be uploaded.
	 * A video may not be set, hence set the video from the service
	 * @param activityId
	 * @return
	 */
	public static String getIsSetValueOfVideo(String activityId) {
		String videoSetValue = null;
		try {
			String xmlFilePath = ExternalStorageManager.InsuranceAppXmlDIR
					+ "videolist.xml";
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(new File(xmlFilePath));
			
			NodeList videoListNodes = document.getElementsByTagName(Global.VIDEO_DETAIL);
			
			for(int i=0;i<videoListNodes.getLength();i++) {
				Node innerNode = videoListNodes.item(i);
				
				if(innerNode.getChildNodes().item(0).getNodeName().equals(Global.ACTIVITY_ID)) {
					if(innerNode.getChildNodes().item(0).getTextContent().equals(activityId)) {
						videoSetValue = innerNode.getChildNodes().item(2).getTextContent();
						break;
					}
				}
			}
			return videoSetValue;
		}catch(ParserConfigurationException e){
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		}catch(IOException e) {
			e.printStackTrace();
			Logger.e(modules.INSURANCE_APP, TAG, getExceptionMSG(e));
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return videoSetValue;
	}
	
	/**
	 * Gets the list of video details that have to be uploaded
	 * @return
	 */
	public static List<VideosToUpload> getVideosToUpload() {
		List<VideosToUpload> videoList = new ArrayList<VideosToUpload>();
		
		String xmlFilePath = ExternalStorageManager.InsuranceAppXmlDIR
				+ "videolist.xml";
		
		VideosToUploadParser videosToUploadParser = 
				new VideosToUploadParser(xmlFilePath);
		videoList = videosToUploadParser.parseDocument();
		
		return videoList;
	}
}
