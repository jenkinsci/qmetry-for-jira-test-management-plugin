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
					
					//System.out.println("File Path:"+fileserver);
					Map<String,String> map=new HashMap<String,String>();
					
					CloseableHttpClient httpClient= HttpClients.createDefault();
					String toEncode=username.trim()+":"+password.trim();
					/*byte[] encodedBytes = Base64.getEncoder().encode(toEncode.getBytes(StandardCharsets.UTF_8));
			    	String encodedString= new String(encodedBytes,StandardCharsets.UTF_8);*/
					//System.out.println(encodedString);
					
					byte[] mes = toEncode.getBytes("UTF-8");
					String encodedString = DatatypeConverter.printBase64Binary(mes);
					
			    	String basicAuth = "Basic " + encodedString;
					
					if((testrunnameserver!=null && !testrunnameserver.isEmpty()))
					{
						//System.out.println("Testrun Name:"+testrunnameserver);
						testrunnameserver+=" #"+buildnumber;
					}
					
					String filepathserver="";
					boolean iszip=false;
					File f1=new File(fileserver);
					if(f1.isDirectory())
					{
						iszip=true;
						filepathserver=CreateZip.createZip(fileserver,selectionserver);
						//System.out.println("\n\nDebug check : "+filepathserver);
					}
					else
					{
						filepathserver=fileserver;
					}
			    	
				    	HttpPost uploadFile = new HttpPost(jiraurlserver.trim()+"/rest/qtm/latest/automation/importresults");
				    	uploadFile.addHeader("Authorization", basicAuth);
						
				    	
						MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				    	builder.addTextBody("apiKey", apikeyserver.trim(), ContentType.TEXT_PLAIN);
				    	builder.addTextBody("format", selectionserver.trim(), ContentType.TEXT_PLAIN);
				    	
						if(testrunnameserver!=null && !testrunnameserver.isEmpty())
						{
							builder.addTextBody("testRunName", testrunnameserver.trim(), ContentType.TEXT_PLAIN);
						}
						if(platformserver != null && !platformserver.isEmpty())
						{
							//System.out.println("Platform Name:"+platformserver);
				    		builder.addTextBody("platform", platformserver.trim(), ContentType.TEXT_PLAIN);
				    	}
						if(labelsserver != null && !labelsserver.isEmpty())
						{
							//System.out.println("Labels:"+labelsserver);
				    		builder.addTextBody("labels", labelsserver.trim(), ContentType.TEXT_PLAIN);
				    	}
						if(versionserver != null && !versionserver.isEmpty())
						{
							//System.out.println("Version:"+versionserver);
				    		builder.addTextBody("versions", versionserver.trim(), ContentType.TEXT_PLAIN);
				    	}
						if(componentserver != null && !componentserver.isEmpty())
						{
							//System.out.println("Component:"+componentserver);
				    		builder.addTextBody("components", componentserver.trim(), ContentType.TEXT_PLAIN);
				    	}
						if(sprintserver != null && !sprintserver.isEmpty())
						{
							//System.out.println("Sprint:"+sprintserver);
				    		builder.addTextBody("sprint", sprintserver.trim(), ContentType.TEXT_PLAIN);
						}
						if(commentserver != null && !commentserver.isEmpty())
						{
							//System.out.println("Comment:"+commentserver);
				    		builder.addTextBody("comment", commentserver.trim(), ContentType.TEXT_PLAIN);
						}
						if(testrunkeyserver!=null && !testrunkeyserver.isEmpty())
						{
							//System.out.println("Testrun Key:"+testrunkeyserver);
							builder.addTextBody("testRunKey",testrunkeyserver.trim());
						}
						if(testassethierarchyserver!=null && !testassethierarchyserver.isEmpty())
						{
							//System.out.println("Test Asset Hierarchy:"+testassethierarchyserver);
							builder.addTextBody("testAssetHierarchy",testassethierarchyserver.trim());
						}
						if(jirafieldsserver!=null && !jirafieldsserver.isEmpty())
						{
							JSONParser parser = new JSONParser();
							JSONArray array=(JSONArray)parser.parse(jirafieldsserver.trim());
							builder.addTextBody("JIRAFields",array.toString());
						}
						builder.addTextBody("isZip",String.valueOf(iszip));
				    	// This attaches the file to the POST:
				    	
				    	File f = new File(filepathserver);		
						//System.out.println("\n\nDebug check file exists : "+f.exists());
						//System.out.println("\n\nDebug check file path : "+f.getAbsolutePath());
				    	builder.addPart("file", new FileBody(f));
						
						
						
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
										//if(!iszip)
										//{
											
											map.put("iszip","false");
											String trk=(String)result.get("testRunKey");
											String tru=(String)result.get("testRunUrl");
											String message=(String)result.get("message");
											//System.out.println("Publishing the result has been succesfull.");
											//System.out.println("------Test Run Details------");
											//System.out.println("TestRun Key:"+trk);
											//System.out.println("TestRun URL:"+tru);
											map.put("testRunKey",trk);
											map.put("testRunUrl",tru);
											map.put("message",message);
											map.put("response",builder1.toString());
										/*}
										else
										{
											map.put("iszip","true");
											map.put("response",builder1.toString());
										}*/
									}
								}
								else
								{
									map.put("success","false");
									String errorMessage=(String)responsejson.get("errorMessage");
									//System.out.println("Eror has occured in publishing result QMetry - Test Management for JIRA");
									//System.out.println("Error Message:"+errorMessage);
									map.put("errorMessage",errorMessage);
								}
							}
						}
						else{
							map.put("success","error");
							map.put("responseCode",String.valueOf(statusLine.getStatusCode()));
							//System.out.println("Error has occured while uploading the file with response code:"+String.valueOf(statusLine.getStatusCode()));
						}
						return map;		
	}
}
