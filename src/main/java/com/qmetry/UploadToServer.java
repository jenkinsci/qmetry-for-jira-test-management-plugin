package com.qmetry;


import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.http.HttpEntity;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;


@IgnoreJRERequirement
public class UploadToServer {
	

	public void uploadToTheServer(String apikeyserver, String jiraurlserver, String password,
			String testrunnameserver, String testrunkeyserver, String testassethierarchyserver, String labelsserver, String sprintserver, 
			String versionserver, String componentserver, String username, 
			String fileserver, String selectionserver, String platformserver,
			String commentserver,String jirafieldsserver,int buildnumber) throws InvalidCredentialsException,FileNotFoundException, AuthenticationException, ProtocolException, IOException{
				
					CloseableHttpClient httpClient= HttpClients.createDefault();
					String toEncode=username+":"+password;
					byte[] encodedBytes = Base64.getEncoder().encode(toEncode.getBytes(StandardCharsets.UTF_8));
			    	String encodedString= new String(encodedBytes,StandardCharsets.UTF_8);
			    	String basicAuth = "Basic " + encodedString;
			    	
				    	HttpPost uploadFile = new HttpPost(jiraurlserver);
				    	uploadFile.addHeader("Authorization", basicAuth);
						
						//add buildnumber in testrun name
						if(!(testrunkeyserver!=null && !testrunkeyserver.isEmpty()))
						{
							testrunnameserver+="#"+buildnumber;
						}
						
						//Check for zip
						String filepathserver;
						boolean iszipserver=false;
						File fl=new File(fileserver);
						if(fl.isDirectory())
						{
							//Create zip
							filepathserver=CreateZip.createZip(fileserver,selectionserver);
							iszipserver=true;
						}
						else
						{
							filepathserver=fileserver;
							iszipserver=false;
						}
										
				    	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				    	builder.addTextBody("apiKey", apikeyserver, ContentType.TEXT_PLAIN);
				    	builder.addTextBody("testRunName", testrunnameserver, ContentType.TEXT_PLAIN);
				    	builder.addTextBody("format", selectionserver, ContentType.TEXT_PLAIN);
						builder.addTextBody("isZip",String.valueOf(iszipserver),ContentType.TEXT_PLAIN);
				    	
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
							builder.addTextBody("testRunKey",testrunkeyserver,ContentType.TEXT_PLAIN);
						if(testassethierarchyserver!=null && !testassethierarchyserver.isEmpty())
							builder.addTextBody("testAssetHierarchy",testassethierarchyserver,ContentType.TEXT_PLAIN);
						if(jirafieldsserver!=null && jirafieldsserver.isEmpty())
							builder.addTextBody("JIRAFields",jirafieldsserver);
				    	// This attaches the file to the POST:
				    	
				    		File f = new File(filepathserver);		
				    		try {
				    				builder.addPart("file", new FileBody(f));
							} catch (Exception e) {
								e.printStackTrace();
							}; 

				    	HttpEntity multipart = builder.build();
				    	uploadFile.setEntity(multipart);
				    	CloseableHttpResponse response = httpClient.execute(uploadFile);
				    	//HttpEntity responseEntity = response.getEntity();
				    	httpClient.close();
				    	//Execute and get the response.
				    	System.out.println(response.toString());
						    	
				    /*System.out.println("Username "+ ":" + username);
				    System.out.println("Password "+ ":" + password);
				    System.out.println("Basic AUTH "+ ":" + basicAuth);
				    System.out.println("format "+ ":" + selectionserver);
				    System.out.println("API Key "+ ":" + apikeyserver);
				    System.out.println("file "+ ":" + fileserver);
				    System.out.println("URL "+ ":" + jiraurlserver);*/			
	}
}
