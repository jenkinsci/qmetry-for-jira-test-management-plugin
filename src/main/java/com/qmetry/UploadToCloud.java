/*******************************************************************************
* Copyright 2018 Infostretch Corporation
*
* This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
*
* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
* OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
* OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
*
* You should have received a copy of the GNU General Public License along with this program in the name of LICENSE.txt in the root folder of the distribution. If not, see https://opensource.org/licenses/gpl-3.0.html
*
*
* For any inquiry or need additional information, please contact qmetrysupport@infostretch.com
*******************************************************************************/
package com.qmetry;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

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
import java.lang.InterruptedException;
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
			String components, String selection, String platform, String comment,String testrunkey,String testassethierarchy,String jirafields,int buildnumber,AbstractBuild build,BuildListener listener) throws MalformedURLException
	, IOException, UnsupportedEncodingException, ProtocolException, ParseException, InterruptedException{
		
		File resultFile=FindFile.findFile(file,build,listener);
		if(resultFile==null)
		{
			return null;
		}
	
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
			//System.out.println("Testrun Name:"+testrunname);
			
			testrunname=testrunname.trim()+" #"+buildnumber;
		}
		
		String filepath="";
		boolean iszip=false;
		//File f=new File(file);
		if(resultFile.isDirectory())
		{
			iszip=true;
			listener.getLogger().println("QMetry for JIRA:"+"Given Path is Directory.");
			listener.getLogger().println("QMetry for JIRA:"+"Creating Zip...");
			filepath=CreateZip.createZip(resultFile.getAbsolutePath(),selection);
		}
		else{
			filepath=resultFile.getAbsolutePath();
		}

		/*if(selection.equals("qas/json"))
		{
			isZip=true;
		}*/

		JSONObject jsonbody=new JSONObject();
	
		jsonbody.put("format",selection.trim());
		jsonbody.put("testRunName",testrunname);
		jsonbody.put("apiKey",apikey.trim());
		jsonbody.put("isZip",String.valueOf(iszip));
		if(platform != null && !platform.isEmpty())
		{
			//System.out.println("Platform:"+platform);
			jsonbody.put("platform",platform.trim());
		}
		if(labels != null && !labels.isEmpty())
		{
			//System.out.println("Labels:"+labels);
			jsonbody.put("labels",labels.trim());
		}
		if(versions != null && !versions.isEmpty())
		{
			//System.out.println("Version:"+versions);
			jsonbody.put("versions",versions.trim());
		}
		if(components != null && !components.isEmpty())
		{
			//System.out.println("Component:"+components);
			jsonbody.put("components",components.trim());
		}
		if(sprint != null && !sprint.isEmpty())
		{
			//System.out.println("Sprint:"+sprint);
			jsonbody.put("sprint",sprint.trim());
		}
		if(comment != null && !comment.isEmpty())
		{
			//System.out.println("Comment:"+comment);
			jsonbody.put("comment",comment.trim());
		}
		if(testrunkey!=null && !testrunkey.isEmpty())
		{
			//System.out.println("Testrun Key:"+testrunkey);
			jsonbody.put("testRunKey",testrunkey.trim());
		}
		if(testassethierarchy!=null && !testassethierarchy.isEmpty())
		{
			//System.out.println("Test Asset Hierarchy:"+testassethierarchy);
			jsonbody.put("testAssetHierarchy",testassethierarchy.trim());
		}
		if(jirafields!=null && !jirafields.isEmpty())
		{
			JSONParser parser = new JSONParser();
			JSONArray jsonarray=(JSONArray)parser.parse(jirafields.trim());
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
