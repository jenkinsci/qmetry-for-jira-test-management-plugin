package com.qmetry;

import java.io.File;
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
import org.apache.commons.io.IOUtils;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@IgnoreJRERequirement
public class UploadToCloud {
	
	//Call to 1st URL which gets
	public void uploadToTheCloud(String apikey, String qtm4jurl, String file,
			String testrunname,String testrunkey,String testassethierarchy,String labels, String sprint, String versions, 
			String components, String selection, String platform, String comment,String jirafields,int buildnumber) throws MalformedURLException,FileNotFoundException
	, IOException, UnsupportedEncodingException, ProtocolException, ParseException{
		String encoding = "UTF-8";
		URL url = new URL(qtm4jurl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		
		//add buildnumber in testrun
		if(!(testrunkey!=null && !testrunkey.isEmpty()))
		{
			testrunname+="#"+buildnumber;
		}

		//Check for zip file
		String filepath="";
		boolean iszip=false;
		File f=new File(file);
		if(f.isDirectory())
		{
			//Create zip
			filepath=CreateZip.createZip(file,selection);
			iszip=true;
		}
		else
		{
			filepath=file;
			iszip=false;
		}
		
		JSONObject jsonbody=new JSONObject();
		//StringBuilder jsonBody = new StringBuilder("{");
		//jsonBody.append("\"format\":" + "\""+selection+"\"");
		jsonbody.put("format",selection);
		//jsonBody.append(",\"testRunName\":" + "\""+testrunname+"\"");
		jsonbody.put("testRunName",testrunname);
		//jsonBody.append(",\"apiKey\":" + "\""+apikey+"\"");
		jsonbody.put("apiKey",apikey);
		//jsonBody.append(",\"isZip\":"+"\""+iszip+"\"");
		jsonbody.put("isZip",iszip);
		if(platform != null && !platform.isEmpty())
			jsonbody.put("platform",platform);
			//jsonBody.append(",\"platform\":" + "\""+platform+"\"");
		if(labels != null && !labels.isEmpty())
			jsonbody.put("labels",labels);
			//jsonBody.append(",\"labels\":" + "\""+labels+"\"");
		if(versions != null && !versions.isEmpty())
			jsonbody.put("versions",versions);
			//jsonBody.append(",\"versions\":" + "\""+versions+"\"");
		if(components != null && !components.isEmpty())
			jsonbody.put("components",components);
			//jsonBody.append(",\"components\":" + "\""+components+"\"");
		if(sprint != null && !sprint.isEmpty())
			jsonbody.put("sprint",sprint);
			//jsonBody.append(",\"sprint\":" + "\""+sprint+"\"");		
		if(comment != null && !comment.isEmpty())
			jsonbody.put("comment",comment);
			//jsonBody.append(",\"sprint\":" + "\""+comment+"\"");
		if(testrunkey!=null && !testrunkey.isEmpty())
			jsonbody.put("testRunKey",testrunkey);
			//jsonBody.append(",\"testRunKey\":"+"\""+testrunkey+"\"");
		if(testassethierarchy!=null && !testassethierarchy.isEmpty())
			jsonbody.put("testAssetHierarchy",testassethierarchy);
			//jsonBody.append(",\"testAssetHierarchy\":"+"\""+testassethierarchy+"\"");
		if(jirafields!=null && !jirafields.isEmpty())
		{
			JSONParser parser = new JSONParser();
			JSONArray jirafieldsarray=(JSONArray)parser.parse(jirafields);
			jsonbody.put("JIRAFileds",jirafieldsarray);
			//jsonBody.append(",\"JIRAFileds\":"+"\""+jirafields+"\"");
		}
		//jsonBody.append("}");
		
		//System.out.println(jsonbody.toString());

		OutputStream os = connection.getOutputStream();
		os.write(jsonbody.toString().getBytes("UTF-8"));
		InputStream fis = connection.getInputStream();
		StringWriter response = new StringWriter();
		
		IOUtils.copy(fis, response, encoding);
		//System.out.println(response.toString());
		
		//Call another method to upload to S3 bucket.
		System.out.println(uploadToS3(response.toString(),filepath));   	
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
		 boolean flag=true;
		
		//try
		//{
			//Read the file from the path, copy the content to the url property of JSON Object.
			while(flag)
			{
				FileInputStream file = new FileInputStream(fileurl);
				try
				{
					//System.out.println("Getting outputstrem.......");
					OutputStream os = connection.getOutputStream();
					IOUtils.copy(file, os);
					flag=false;
				}
				catch(SSLException e)
				{
					count++;
					//System.out.println("SSLException");
					file.close();
					if(count>2)
					{
						flag=false;
					}
				}
				finally
				{
					file.close();
				}
			}
		/*}
		catch (IOException e) {
			 
			 //e.printStackTrace();
		}*/

		 InputStream fis = connection.getInputStream();
		 StringWriter writer = new StringWriter();	 
		 IOUtils.copy(fis, writer, encoding);
		 if (connection.getResponseCode() == 200) {
		 	responseValue="Publishing the result has been succesfull. \n Response: " + connection.getResponseMessage();
		 }else{
		 	responseValue="Error has occured while uploading the file to temporary S3 bucket.";
		 }			 
		 return responseValue;
	}
}
