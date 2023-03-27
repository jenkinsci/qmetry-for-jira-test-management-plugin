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

import java.io.File;

//import hudson.model.AbstractBuild;
//import hudson.model.BuildListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;

import org.apache.commons.io.IOUtils;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;


@IgnoreJRERequirement
public class UploadToCloudV4 {

	// Call to 1st URL which gets
	public Map<String, String> uploadToTheCloud(String apikey, String file, boolean attachFile, String format,
			String testCycleToReuse, String environment, String build, String testCycleLabels, String testCycleComponents, 
			String testCyclePriority, String testCycleStatus, String testCycleSprintId, String testCycleFixVersionId, 
			String testCycleSummary, String testCycleCustomFields, String testCycleDescription, String testCycleAssignee, 
			String testCycleReporter, String testCycleStartDate, String testCycleEndDate, String testCycleFolderId, String testCaseDescription,
			String testCasePrecondition, String testCaseAssignee, String testCaseReporter, String testCaseEstimatedTime, 
			String testCaseLabels, String testCaseComponents, String testCasePriority, String testCaseStatus, String testCaseSprintId, 
			String testCaseFixVersionId, String testCaseCustomFields, String testCaseFolderId, int buildnumber, Run<?, ?> run, TaskListener listener,
			FilePath workspace) throws MalformedURLException, IOException, UnsupportedEncodingException, ProtocolException,
			ParseException, FileNotFoundException, InterruptedException {

		PrintStream logger = listener.getLogger();

		File resultFile = FindFile.findFile(file, run, listener, format, workspace);
		if (resultFile == null) {
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();

		// Getting cloud url from property file
		InputStream is = null;
		String uploadcloudurlv4 = "";

		is = (UploadToCloudV4.class).getClassLoader().getResourceAsStream("config.properties");
		Properties p = new Properties();

		p.load(is);
		uploadcloudurlv4 = p.getProperty("uploadcloudurlv4");

		String encoding = "UTF-8";
		URL url = new URL(uploadcloudurlv4);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		connection.setRequestProperty("apiKey", apikey.trim());
		connection.setDoInput(true);
		connection.setDoOutput(true);

		String filepath = "";
		boolean iszip = false;
		// File f=new File(file);
		if (resultFile.isDirectory()) {
			iszip = true;
			listener.getLogger().println("QMetry for JIRA : " + "Given Path is Directory.");
			listener.getLogger().println("QMetry for JIRA : " + "Creating Zip...");
			filepath = CreateZip.createZip(resultFile.getAbsolutePath(), format, attachFile);
			listener.getLogger().println("QMetry for JIRA : Zip file path : " + filepath);
		} else {
			filepath = resultFile.getAbsolutePath();
			// for zip
			String fileExtension = "";
			if (filepath.contains(".") && filepath.lastIndexOf(".") != 0) {
				fileExtension = filepath.substring(filepath.lastIndexOf(".") + 1);
			}
			if (fileExtension.equals("zip")) {
				iszip = true;
			}
			// End of changes
		}

		Map<String, Object> requestDataMap = new HashMap<>();

		String format_short = "";
		if (String.valueOf(format).equals("testng/xml")) {
			format_short = "testng";
		} else if (String.valueOf(format).equals("junit/xml")) {
			format_short = "junit";
		} else if (String.valueOf(format).equals("qas/json")) {
			format_short = "qaf";
		} else if (String.valueOf(format).equals("cucumber/json")) {
			format_short = "cucumber";
		} else if (String.valueOf(format).equals("hpuft/xml")) {
			format_short = "hpuft";
		} else if (String.valueOf(format).equals("specflow/json")) {
			format_short = "specflow";
		}
		requestDataMap.put("format", String.valueOf(format_short));
		requestDataMap.put("isZip", String.valueOf(iszip));
		if (attachFile) {
			requestDataMap.put("attachFile", String.valueOf(attachFile));
		}
		if (testCycleToReuse != null && !testCycleToReuse.isEmpty()) {
			requestDataMap.put("testCycleToReuse", testCycleToReuse.trim());
		}

		if (environment != null && !environment.isEmpty()) {
			requestDataMap.put("environment", environment.trim());
		}

		if (build != null && !build.isEmpty()) {
			requestDataMap.put("build", build.trim());
		}

		Map<String, Object> testcycleDataMap = new HashMap<>();
		boolean isTestcycle = false;
		if (testCycleLabels != null && !testCycleLabels.isEmpty()) {
			isTestcycle = true;
			JSONArray mJSONArray = commaSepratedStringtoJson(testCycleLabels);
			testcycleDataMap.put("labels", mJSONArray);
		}
		if (testCycleComponents != null && !testCycleComponents.isEmpty()) {
			isTestcycle = true;
			JSONArray mJSONArray = commaSepratedStringtoJson(testCycleComponents);
			testcycleDataMap.put("components", mJSONArray);
		}
		if (testCyclePriority != null && !testCyclePriority.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("priority", testCyclePriority.trim());
		}
		if (testCycleStatus != null && !testCycleStatus.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("status", testCycleStatus.trim());
		}
		if (testCycleSprintId != null && !testCycleSprintId.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("sprintId", testCycleSprintId.trim());
		}
		if (testCycleFixVersionId != null && !testCycleFixVersionId.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("fixVersionId", testCycleFixVersionId.trim());
		}
		if (testCycleFolderId != null && !testCycleFolderId.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("folderId", testCycleFolderId.trim());
		}
		if (testCycleSummary != null && !testCycleSummary.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("summary", testCycleSummary.trim() + "_"+ buildnumber);
		}
		if (testCycleDescription != null && !testCycleDescription.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("description", testCycleDescription.trim());
		}
		if (testCycleStartDate != null && !testCycleStartDate.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("plannedStartDate", testCycleStartDate.trim());
		}
		if (testCycleEndDate != null && !testCycleEndDate.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("plannedEndDate", testCycleEndDate.trim());
		}
		if (testCycleAssignee != null && !testCycleAssignee.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("assignee", testCycleAssignee.trim());
		}
		if (testCycleReporter != null && !testCycleReporter.isEmpty()) {
			isTestcycle = true;
			testcycleDataMap.put("reporter", testCycleReporter.trim());
		}
		if (testCycleCustomFields != null && !testCycleCustomFields.isEmpty()) {
			isTestcycle = true;
			JSONParser parser = new JSONParser(); 
			org.json.simple.JSONArray testcycleCustomFieldsJson = (org.json.simple.JSONArray) parser.parse(testCycleCustomFields);
			testcycleDataMap.put("customFields", testcycleCustomFieldsJson);
		}

		Map<String, Object> testcaseDataMap = new HashMap<>();
		boolean isTestcase = false;
		if (testCaseLabels != null && !testCaseLabels.isEmpty()) {
			isTestcase = true;
			JSONArray mJSONArray = commaSepratedStringtoJson(testCaseLabels);
			testcaseDataMap.put("labels", mJSONArray);
		}
		if (testCaseComponents != null && !testCaseComponents.isEmpty()) {
			isTestcase = true;
			JSONArray mJSONArray = commaSepratedStringtoJson(testCaseComponents);
			testcaseDataMap.put("components", mJSONArray);
		}
		if (testCasePriority != null && !testCasePriority.isEmpty()) {
			isTestcase = true;
			testcaseDataMap.put("priority", testCasePriority.trim());
		}
		if (testCaseStatus != null && !testCaseStatus.isEmpty()) {
			isTestcase = true;
			testcaseDataMap.put("status", testCaseStatus.trim());
		}
		if (testCaseSprintId != null && !testCaseSprintId.isEmpty()) {
			isTestcase = true;
			testcaseDataMap.put("sprintId", testCaseSprintId.trim());
		}
		if (testCaseFixVersionId != null && !testCaseFixVersionId.isEmpty()) {
			isTestcase = true;
			testcaseDataMap.put("fixVersionId", testCaseFixVersionId.trim());
		}
		if (testCaseFolderId != null && !testCaseFolderId.isEmpty()) {
			isTestcase = true;
			testcaseDataMap.put("folderId", testCaseFolderId.trim());
		}
		if (testCaseDescription != null && !testCaseDescription.isEmpty()) {
			isTestcase = true;
			testcaseDataMap.put("description", testCaseDescription.trim());
		}
		if (testCasePrecondition != null && !testCasePrecondition.isEmpty()) {
			isTestcase = true;
			testcaseDataMap.put("precondition", testCasePrecondition.trim());
		}
		if (testCaseAssignee != null && !testCaseAssignee.isEmpty()) {
			isTestcase = true;
			testcaseDataMap.put("assignee", testCaseAssignee.trim());
		}
		if (testCaseReporter != null && !testCaseReporter.isEmpty()) {
			isTestcase = true;
			testcaseDataMap.put("reporter", testCaseReporter.trim());
		}
		if (testCaseEstimatedTime != null && !testCaseEstimatedTime.isEmpty()) {
			isTestcase = true;
			testcaseDataMap.put("estimatedTime", testCaseEstimatedTime.trim());
		}
		if (testCaseCustomFields != null && !testCaseCustomFields.isEmpty()) {
			isTestcase = true;
			JSONParser parser = new JSONParser(); 
			org.json.simple.JSONArray testcaseCustomFieldsJson = (org.json.simple.JSONArray) parser.parse(testCaseCustomFields);
			testcaseDataMap.put("customFields", testcaseCustomFieldsJson);
		}

		Map<String, Object> testCaseCycleDataMap = new HashMap<>();
		if (isTestcycle) {
			testCaseCycleDataMap.put("testCycle", testcycleDataMap);
			logger.println("QMetry for JIRA :" + " TestCycle  : " + testcycleDataMap);
		}
		if (isTestcase) {
			testCaseCycleDataMap.put("testCase", testcaseDataMap);
			logger.println("QMetry for JIRA :" + " TestCase : " + testcaseDataMap);
		}

		requestDataMap.put("fields", testCaseCycleDataMap);

		JSONObject jsonbody = new JSONObject(requestDataMap);

		listener.getLogger().println("QMetry for JIRA : JsonBody : " + jsonbody);

		OutputStream os = connection.getOutputStream();
		os.write(jsonbody.toString().getBytes("UTF-8"));
		// Get the response code 
		int statusCode = connection.getResponseCode();
		InputStream fis = null; 
		if(statusCode >= 200 && statusCode < 400){
			fis = connection.getInputStream();
		}else{
			fis = connection.getErrorStream();
		}
		

		StringWriter response = new StringWriter();

		IOUtils.copy(fis, response, encoding);

		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(response.toString());
		if (obj != null) {
			//logger.println("QMetry for JIRA :" + " Response : " + obj);
			String resUrl = (String) obj.get("url");
			if (resUrl != null && !resUrl.isEmpty()) {
				// Call another method to upload to S3 bucket.
				String res = uploadToS3(response.toString(), filepath);
				map.put("success", "true");
				map.put("message", res);
			} else {		
				map.put("success", "false");
				map.put("errorMessage", response.toString());
			}
		}
		return map;
	}

	// This method gets the response, grabs the url from response and uploads the file to that url.
	public String uploadToS3(String response, String fileurl) throws IOException, org.json.simple.parser.ParseException {
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
		String responseValue = "";
		int count = 0;

		// Read the file from the path, copy the content to the url property of JSON
		// Object.
		while (count < 3) {
			FileInputStream file = new FileInputStream(fileurl);
			try {
				OutputStream os = connection.getOutputStream();
				IOUtils.copy(file, os);
				break;
			} catch (SSLException e) {
				count++;
			} finally {
				file.close();
			}
		}

		InputStream fis = connection.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(fis, writer, encoding);
		if (connection.getResponseCode() == 200) {
			responseValue = "Publishing results has been successful. \n Response: " + connection.getResponseMessage()
					+ "\n";
		} else {
			responseValue = "false";
		}
		return responseValue;
	}

	public JSONArray commaSepratedStringtoJson(String commaSeparatedString) {
		String[] arrayStr = commaSeparatedString.split(",");
		JSONArray mJSONArray = new JSONArray();
		for (String s : arrayStr) {
			mJSONArray.put(s.trim());
		}
		return mJSONArray;
	}
}
