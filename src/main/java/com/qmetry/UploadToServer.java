package com.qmetry;


import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
//import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


@IgnoreJRERequirement
public class UploadToServer {
	

	public Map<String,String> uploadToTheServer(String apikeyserver, String jiraurlserver, String password,
			String testrunnameserver, String labelsserver, String sprintserver, 
			String versionserver, String componentserver, String username, 
			String fileserver, String selectionserver, String platformserver,
			String commentserver,String testrunkeyserver,String testassethierarchyserver,String jirafieldsserver,int buildnumber) throws InvalidCredentialsException, AuthenticationException, ProtocolException, IOException,ParseException,ParseException{
					
					Map<String,String> map=new HashMap<String,String>();
					
					CloseableHttpClient httpClient= HttpClients.createDefault();
					String toEncode=username+":"+password;
					/*byte[] encodedBytes = Base64.getEncoder().encode(toEncode.getBytes(StandardCharsets.UTF_8));
			    	String encodedString= new String(encodedBytes,StandardCharsets.UTF_8);*/
					//System.out.println(encodedString);
					
					byte[] mes = toEncode.getBytes("UTF-8");
					String encodedString = DatatypeConverter.printBase64Binary(mes);
					
			    	String basicAuth = "Basic " + encodedString;
					
					if((testrunnameserver!=null && !testrunnameserver.isEmpty()))
					{
						testrunnameserver+="#"+buildnumber;
					}
					
					String filepathserver="";
					boolean iszip=false;
					File f1=new File(fileserver);
					if(f1.isDirectory())
					{
						iszip=true;
						filepathserver=CreateZip.createZip(fileserver,selectionserver);
					}
					else
					{
						filepathserver=fileserver;
					}
			    	
				    	HttpPost uploadFile = new HttpPost(jiraurlserver+"/rest/qtm/latest/automation/importresults");
				    	uploadFile.addHeader("Authorization", basicAuth);
						
				    	
						MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				    	builder.addTextBody("apiKey", apikeyserver, ContentType.TEXT_PLAIN);
				    	builder.addTextBody("format", selectionserver, ContentType.TEXT_PLAIN);
				    	
						if(testrunnameserver!=null && !testrunnameserver.isEmpty())
							builder.addTextBody("testRunName", testrunnameserver, ContentType.TEXT_PLAIN);
				    	if(platformserver != null && !platformserver.isEmpty())
				    		builder.addTextBody("platform", platformserver, ContentType.TEXT_PLAIN);
				    	if(labelsserver != null && !labelsserver.isEmpty())
				    		builder.addTextBody("labels", labelsserver, ContentType.TEXT_PLAIN);
				    	if(versionserver != null && !versionserver.isEmpty())
				    		builder.addTextBody("versions", versionserver, ContentType.TEXT_PLAIN);
				    	if(componentserver != null && !componentserver.isEmpty())
				    		builder.addTextBody("components", componentserver, ContentType.TEXT_PLAIN);
				    	if(sprintserver != null && !sprintserver.isEmpty())
				    		builder.addTextBody("sprint", sprintserver, ContentType.TEXT_PLAIN);
				    	if(commentserver != null && !commentserver.isEmpty())
				    		builder.addTextBody("comment", commentserver, ContentType.TEXT_PLAIN);
						if(testrunkeyserver!=null && !testrunkeyserver.isEmpty())
							builder.addTextBody("testRunKey",testrunkeyserver);
						if(testassethierarchyserver!=null && !testassethierarchyserver.isEmpty())
							builder.addTextBody("testAssetHierarchy",testassethierarchyserver);
						if(jirafieldsserver!=null && !jirafieldsserver.isEmpty())
						{
							JSONParser parser = new JSONParser();
							JSONArray array=(JSONArray)parser.parse(jirafieldsserver);
							builder.addTextBody("JIRAFields",array.toString());
						}
						builder.addTextBody("isZip",String.valueOf(iszip));
				    	// This attaches the file to the POST:
				    	
				    	File f = new File(filepathserver);		
				    	//try {
				    			builder.addPart("file", new FileBody(f));
						/*} catch (Exception e) {
							e.printStackTrace();
						}*/ 

				    	HttpEntity multipart = builder.build();
				    	uploadFile.setEntity(multipart);
						CloseableHttpResponse response = httpClient.execute(uploadFile);
						
						StatusLine statusLine = response.getStatusLine();
						
						if (statusLine.getStatusCode() == 200) {
							HttpEntity entity = response.getEntity();
							if(entity!=null)
							{
								InputStream content = entity.getContent();
								StringBuilder  builder1 = new StringBuilder();
								Reader read = new InputStreamReader(content, StandardCharsets.UTF_8);
								BufferedReader reader = new BufferedReader(read);
								String line;
								try {
									while ((line = reader.readLine()) != null) {
										builder1.append(line);
									}

								}
								finally{
									reader.close();
									content.close();
								}
								JSONParser parser=new JSONParser();
								JSONObject responsejson=(JSONObject)parser.parse(builder1.toString());
								Boolean success=(Boolean)responsejson.get("success");
								if(success.booleanValue())
								{
									map.put("success","true");
									JSONObject result=(JSONObject)responsejson.get("result");
									if(result!=null)
									{
										String trk=(String)result.get("testRunKey");
										String tru=(String)result.get("testRunUrl");
										String message=(String)result.get("message");
										System.out.println("Publishing the result has been succesfull.");
										System.out.println("------Test Run Details------");
										System.out.println("TestRun Key:"+trk);
										System.out.println("TestRun URL:"+tru);
										map.put("testRunKey",trk);
										map.put("testRunUrl",tru);
										map.put("message",message);
									}
								}
								else
								{
									map.put("success","false");
									String errorMessage=(String)responsejson.get("errorMessage");
									System.out.println("Eror has occured in publishing result QMetry - Test Management for JIRA");
									System.out.println("Error Message:"+errorMessage);
									map.put("errorMessage",errorMessage);
								}
							}
						}
						else{
							map.put("success","error");
							map.put("responseCode",String.valueOf(statusLine.getStatusCode()));
							System.out.println("Error has occured while uploading the file with response code:"+String.valueOf(statusLine.getStatusCode()));
						}
						return map;		
	}
}
