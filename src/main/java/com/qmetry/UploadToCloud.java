	
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


@IgnoreJRERequirement
public class UploadToCloud {
	
	private static final Logger LOGGER = Logger.getLogger(UploadToCloud.class.getName());
	
	//Call to 1st URL which gets
	public void uploadToTheCloud(String apikey, String qtm4jurl, String file,
			String testrunname,String labels, String sprint, String versions, 
			String components, String selection, String platform, String comment) throws MalformedURLException
	, IOException, UnsupportedEncodingException, ProtocolException, ParseException{
		
		
		
		String encoding = "UTF-8";
		URL url = new URL(qtm4jurl.trim());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		connection.setDoInput(true);
		connection.setDoOutput(true);

		StringBuilder jsonBody = new StringBuilder("{");
		jsonBody.append("\"format\":" + "\""+selection+"\"");
		jsonBody.append(",\"testRunName\":" + "\""+testrunname+"\"");
		jsonBody.append(",\"apiKey\":" + "\""+apikey+"\"");
		if(platform != null && !platform.isEmpty())
			jsonBody.append(",\"platform\":" + "\""+platform+"\"");
		if(labels != null && !labels.isEmpty())
			jsonBody.append(",\"labels\":" + "\""+labels+"\"");
		if(versions != null && !versions.isEmpty())
			jsonBody.append(",\"versions\":" + "\""+versions+"\"");
		if(components != null && !components.isEmpty())
			jsonBody.append(",\"components\":" + "\""+components+"\"");
		if(sprint != null && !sprint.isEmpty())
			jsonBody.append(",\"sprint\":" + "\""+sprint+"\"");		
		if(comment != null && !comment.isEmpty())
			jsonBody.append(",\"comment\":" + "\""+comment+"\"");
		jsonBody.append("}");
		
		LOGGER.info(jsonBody.toString());

		OutputStream os = connection.getOutputStream();
		os.write(jsonBody.toString().getBytes("UTF-8"));
		InputStream fis = connection.getInputStream();
		StringWriter response = new StringWriter();
		
		IOUtils.copy(fis, response, encoding);
		LOGGER.info(response.toString());
		
		//Call another method to upload to S3 bucket.
		LOGGER.info(uploadToS3(response.toString(),file));   	
		
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
		
		 try{
		 //Read the file from the path, copy the content to the url property of JSON Object.
		 FileInputStream file = new FileInputStream(fileurl);
		 try{OutputStream os = connection.getOutputStream();
		 IOUtils.copy(file, os);
		 } finally{
			 file.close();
		 	}
		 }
		 catch (IOException e) {
			 e.printStackTrace();
		}

		 InputStream fis = connection.getInputStream();
		 StringWriter writer = new StringWriter();	 
		 IOUtils.copy(fis, writer, encoding);
		 if (connection.getResponseCode() == 200) {
		 	responseValue="Publishing the result has been successful. \n Response: " + connection.getResponseMessage();
		 }else{
		 	responseValue="Error has occured while uploading test result file.";
		 }			 
		 return responseValue;
	}
}
