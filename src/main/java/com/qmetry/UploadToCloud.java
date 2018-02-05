package com.qmetry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLException;
import org.apache.commons.io.IOUtils;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@IgnoreJRERequirement
public class UploadToCloud {
	
	//Call to 1st URL which gets
	public Map<String,String> uploadToTheCloud(String apikey, String file,
			String testrunname,String labels, String sprint, String versions, 
			String components, String selection, String platform, String comment,String testrunkey,String testassethierarchy,String jirafields,int buildnumber) throws MalformedURLException
	, IOException, UnsupportedEncodingException, ProtocolException, ParseException{
		
		Map<String,String> map=new HashMap<String,String>();
		
		//Getting cloud url from property file
		InputStream is=null;
		String uploadcloudurl="";
		
		is = (UploadToCloud.class).getClassLoader().getResourceAsStream("config.properties");
		Properties p = new Properties();
		
		//System.out.println("InputStream:"+is);
			
		p.load(is);
		uploadcloudurl=p.getProperty("uploadcloudurl");
		
		String encoding = "UTF-8";
		URL url = new URL(uploadcloudurl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		
		if((testrunname!=null && !testrunname.isEmpty()))
		{
			testrunname+="#"+buildnumber;
		}
		
		String filepath="";
		boolean iszip=false;
		File f=new File(file);
		if(f.isDirectory())
		{
			iszip=true;
			filepath=CreateZip.createZip(file,selection);
		}
		else{
			filepath=file;
		}

		/*if(selection.equals("qas/json"))
		{
			isZip=true;
		}*/

		JSONObject jsonbody=new JSONObject();
	
		jsonbody.put("format",selection);
		jsonbody.put("testRunName",testrunname);
		jsonbody.put("apiKey",apikey);
		jsonbody.put("isZip",String.valueOf(iszip));
		if(platform != null && !platform.isEmpty())
			jsonbody.put("platform",platform);
			
		if(labels != null && !labels.isEmpty())
			jsonbody.put("labels",labels);
		if(versions != null && !versions.isEmpty())
			jsonbody.put("versions",versions);
		if(components != null && !components.isEmpty())
			jsonbody.put("components",components);
		if(sprint != null && !sprint.isEmpty())
			jsonbody.put("sprint",sprint);
			
		if(comment != null && !comment.isEmpty())
			jsonbody.put("comment",comment);
			
		if(testrunkey!=null && !testrunkey.isEmpty())
			jsonbody.put("testRunKey",testrunkey);
		if(testassethierarchy!=null && !testassethierarchy.isEmpty())
			jsonbody.put("testAssetHierarchy",testassethierarchy);
		if(jirafields!=null && !jirafields.isEmpty())
		{
			JSONParser parser = new JSONParser();
			JSONArray jsonarray=(JSONArray)parser.parse(jirafields);
			jsonbody.put("JIRAFields",jsonarray);
		}
		
		
		//System.out.println(jsonbody.toString());

		OutputStream os = connection.getOutputStream();
		os.write(jsonbody.toString().getBytes("UTF-8"));
		InputStream fis = connection.getInputStream();
		StringWriter response = new StringWriter();
		
		IOUtils.copy(fis, response, encoding);
		//System.out.println(response.toString());
		
		JSONParser parser = new JSONParser();
		JSONObject obj =(JSONObject) parser.parse(response.toString());
		if(obj!=null)
		{
			Boolean success=(Boolean)obj.get("isSuccess");
			if(success.booleanValue())
			{
				//Call another method to upload to S3 bucket.
				String res=uploadToS3(response.toString(),filepath);
				map.put("success","true");
				map.put("message",res);
				//System.out.println(res);
			}
			else
			{
				String errorMessage=(String)obj.get("errorMessage");
				map.put("success","false");
				map.put("errorMessage",errorMessage);
			}
		}
		return map;
	}
	
	// This method gets the response, grabs the url from response and uploads the file to that url.
	public String uploadToS3(String response,String fileurl) throws IOException, org.json.simple.parser.ParseException{
		// JSONParser to parse the response into JSON Object.
		 JSONParser parser = new JSONParser();
		 Object obj = parser.parse(response);
		 JSONObject jsonObject = (JSONObject) obj;
		 String urlForUpload = (String) jsonObject.get("url");
		 URL url = new URL(urlForUpload);
		 HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		 connection.setRequestMethod("PUT");
		 connection.setRequestProperty("Content-Type", "multipart/form-data");
		 connection.setDoOutput(true);
		 connection.setDoInput(true);
		 String encoding = "UTF-8";
		 String responseValue="";
		 int count=0;
		 //try{
		 //Read the file from the path, copy the content to the url property of JSON Object.
		 while(count<3)
		 {
			 FileInputStream file = new FileInputStream(fileurl);
			 try
			 {
				//System.out.println("Getting output stream");
				OutputStream os = connection.getOutputStream();
				IOUtils.copy(file, os);
				break;
			 }
			 catch(SSLException e)
			 {
				 //System.out.println("SSLException");
				 count++;
			 }
			 finally
			 {
				 file.close();
			 }
		}
		/* }
		 catch (IOException e) {
			 e.printStackTrace();
		}*/

		 InputStream fis = connection.getInputStream();
		 StringWriter writer = new StringWriter();	 
		 IOUtils.copy(fis, writer, encoding);
		 if (connection.getResponseCode() == 200) {
		 	responseValue="Publishing the result has been succesfull. \n Response: " + connection.getResponseMessage()+"\n";
		 }else{
			 responseValue="false";
		 	//responseValue="Error has occured while uploading the file to temporary S3 bucket.";
		 }			 
		 return responseValue;
	}
}
