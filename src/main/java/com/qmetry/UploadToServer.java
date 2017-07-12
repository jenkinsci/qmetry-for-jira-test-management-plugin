
	
/*******************************************************************************
* Copyright 2017 Infostretch Corporation
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


import java.io.File;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

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
	
	private static final Logger LOGGER = Logger.getLogger(UploadToServer.class.getName());

	public void uploadToTheServer(String apikeyserver, String jiraurlserver, String password,
			String testrunnameserver, String labelsserver, String sprintserver, 
			String versionserver, String componentserver, String username, 
			String fileserver, String selectionserver, String platformserver,
			String commentserver) throws InvalidCredentialsException, AuthenticationException, ProtocolException, IOException{
				
					CloseableHttpClient httpClient= HttpClients.createDefault();
					String toEncode=username+":"+password;
					byte[] message = toEncode.getBytes("UTF-8");
                    			String encoded = DatatypeConverter.printBase64Binary(message);
			    		String basicAuth = "Basic " + encoded;
			    	
				    	HttpPost uploadFile = new HttpPost(jiraurlserver);
				    	uploadFile.addHeader("Authorization", basicAuth);
				    	
				    	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				    	builder.addTextBody("apiKey", apikeyserver, ContentType.TEXT_PLAIN);
				    	builder.addTextBody("testRunName", testrunnameserver, ContentType.TEXT_PLAIN);
				    	builder.addTextBody("format", selectionserver, ContentType.TEXT_PLAIN);
				    	
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

				    	// This attaches the file to the POST:
				    	
				    		File f = new File(fileserver);		
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
				
	}
}
