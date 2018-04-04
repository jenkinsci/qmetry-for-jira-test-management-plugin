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
import java.lang.InterruptedException;


@IgnoreJRERequirement
public class UploadToServer {
	
	
	public Map<String,String> uploadToTheServer(String apikeyserver, String jiraurlserver, String password,
			String testrunnameserver, String labelsserver, String sprintserver, 
			String versionserver, String componentserver, String username, 
			String fileserver, String selectionserver, String platformserver,
			String commentserver,String testrunkeyserver,String testassethierarchyserver,String jirafieldsserver,int buildnumber,AbstractBuild build,BuildListener listener) throws InvalidCredentialsException, AuthenticationException, ProtocolException, IOException, ParseException, InterruptedException{
					
					File resultFile=FindFile.findFile(fileserver,build,listener);
					if(resultFile==null)
					{
						return null;
					}
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
						testrunnameserver=testrunnameserver.trim()+" #"+buildnumber;
					}
					
					String filepathserver="";
					boolean iszip=false;

					if(resultFile.isDirectory())
					{
						iszip=true;
						listener.getLogger().println("QMetry for JIRA:"+"Given Path is Directory.");
						listener.getLogger().println("QMetry for JIRA:"+"Creating Zip...");
						filepathserver=CreateZip.createZip(resultFile.getAbsolutePath(),selectionserver);
					}
					else
					{
						filepathserver=resultFile.getAbsolutePath();
					}
			    	
				    	HttpPost uploadFile = new HttpPost(jiraurlserver.trim()+"/rest/qtm/latest/automation/importresults");
				    	uploadFile.addHeader("Authorization", basicAuth);
						
				    	
						MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				    	builder.addTextBody("apiKey", apikeyserver.trim(), ContentType.TEXT_PLAIN);
				    	builder.addTextBody("format", selectionserver.trim(), ContentType.TEXT_PLAIN);
				    	
						if(testrunnameserver!=null && !testrunnameserver.isEmpty())
						{
							builder.addTextBody("testRunName", testrunnameserver, ContentType.TEXT_PLAIN);
						}
						if(platformserver != null && !platformserver.isEmpty())
						{
							builder.addTextBody("platform", platformserver.trim(), ContentType.TEXT_PLAIN);
				    	}
						if(labelsserver != null && !labelsserver.isEmpty())
						{
							builder.addTextBody("labels", labelsserver.trim(), ContentType.TEXT_PLAIN);
				    	}
						if(versionserver != null && !versionserver.isEmpty())
						{
							builder.addTextBody("versions", versionserver.trim(), ContentType.TEXT_PLAIN);
				    	}
						if(componentserver != null && !componentserver.isEmpty())
						{
							builder.addTextBody("components", componentserver.trim(), ContentType.TEXT_PLAIN);
				    	}
						if(sprintserver != null && !sprintserver.isEmpty())
						{
							builder.addTextBody("sprint", sprintserver.trim(), ContentType.TEXT_PLAIN);
						}
						if(commentserver != null && !commentserver.isEmpty())
						{
							builder.addTextBody("comment", commentserver.trim(), ContentType.TEXT_PLAIN);
						}
						if(testrunkeyserver!=null && !testrunkeyserver.isEmpty())
						{
							builder.addTextBody("testRunKey",testrunkeyserver.trim());
						}
						if(testassethierarchyserver!=null && !testassethierarchyserver.isEmpty())
						{
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

											map.put("iszip","false");
											String trk=(String)result.get("testRunKey");
											String tru=(String)result.get("testRunUrl");
											String message=(String)result.get("message");

											map.put("testRunKey",trk);
											map.put("testRunUrl",tru);
											map.put("message",message);
											map.put("response",builder1.toString());
									}
								}
								else
								{
									map.put("success","false");
									String errorMessage=(String)responsejson.get("errorMessage");
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
