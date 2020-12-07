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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.io.FileUtils;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.json.JSONArray;
import org.json.JSONException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import hudson.util.ListBoxModel.Option;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

/**
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link TestReportDeployPublisher} is created. The created instance is
 * persisted to the project configuration XML by using XStream, so this allows
 * you to use instance fields (like {@link #name}) to remember the
 * configuration.
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked.
 *
 * @author Vaibhavsinh Vaghela
 */
@IgnoreJRERequirement
public class TestReportDeployPublisher extends Recorder implements SimpleBuildStep {

	private boolean disableaction;
	private boolean attachFile;
	private boolean attachFileServer;

	private String name;
	private String version;
	private String apikey;
	private String file;
	private String testrunname;
	private String testrunkey;
	private String testassethierarchy;
	private String testCaseUpdateLevel;
	private String labels;
	private String sprint;
	private String component;
	private String format;
	private String platform;
	private String comment;
	private String jirafields;

	private String apikeyserver;
	private String jiraurlserver;
	private String proxyUrl;
	private String username;
	private Secret password;
	private String testrunnameserver;
	private String testrunkeyserver;
	private String testassethierarchyserver;
	private String testCaseUpdateLevelServer;
	private String labelsserver;
	private String sprintserver;
	private String versionserver;
	private String componentserver;
	private String platformserver;
	private String commentserver;
	private String fileserver;
	private String formatserver;
	private String jirafieldsserver;

	public String testToRun;

	public String getPlatformserver() {
		return platformserver;
	}

	public void setPlatformserver(String platformserver) {
		this.platformserver = platformserver;
	}

	public String getCommentserver() {
		return commentserver;
	}

	public void setCommentserver(String commentserver) {
		this.commentserver = commentserver;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormatserver() {
		return formatserver;
	}

	public void setFormatserver(String formatserver) {
		this.formatserver = formatserver;
	}

	public String getFileserver() {
		return fileserver;
	}

	public void setFileserver(String fileserver) {
		this.fileserver = fileserver;
	}

	public String getApikeyserver() {
		// return apikeyserver;
		return Secret.toString(Secret.fromString(apikeyserver));
	}

	public void setApikeyserver(String apikeyserver) {
		// this.apikeyserver = apikeyserver;
		this.apikeyserver = Secret.fromString(apikeyserver).getEncryptedValue();
	}

	public String getJiraurlserver() {
		return jiraurlserver;
	}

	public void setJiraurlserver(String jiraurlserver) {
		this.jiraurlserver = jiraurlserver;
	}

	public String getProxyUrl() {
		return this.proxyUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	// public String getPassword() throws AbortException {
	// 	return Secret.toString(Secret.fromString(password));
	// }

	// public void setPassword(String password) {
	// 	this.password = Secret.fromString(password).getEncryptedValue();
	// }

	public void setPassword(Secret password) { 
        this.password = password;
    }

    public Secret getPassword() {
        return password;
    }

	public String getTestrunnameserver() {
		return testrunnameserver;
	}

	public void setTestrunnameserver(String testrunnameserver) {
		this.testrunnameserver = testrunnameserver;
	}

	public String getLabelsserver() {
		return labelsserver;
	}

	public void setLabelsserver(String labelsserver) {
		this.labelsserver = labelsserver;
	}

	public String getSprintserver() {
		return sprintserver;
	}

	public void setSprintserver(String sprintserver) {
		this.sprintserver = sprintserver;
	}

	public String getVersionserver() {
		return versionserver;
	}

	public void setVersionserver(String versionserver) {
		this.versionserver = versionserver;
	}

	public String getComponentserver() {
		return componentserver;
	}

	public void setComponentserver(String componentserver) {
		this.componentserver = componentserver;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getApikey() {
		// return apikey;
		return Secret.toString(Secret.fromString(apikey));
	}

	public void setApikey(String apikey) {
		// this.apikey = apikey;
		this.apikey = Secret.fromString(apikey).getEncryptedValue();
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getTestrunname() {
		return testrunname;
	}

	public void setTestrunname(String testrunname) {
		this.testrunname = testrunname;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getSprint() {
		return sprint;
	}

	public void setSprint(String sprint) {
		this.sprint = sprint;

	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getTestrunkey() {
		return testrunkey;
	}

	public void setTestrunkey(String testrunkey) {
		this.testrunkey = testrunkey;
	}

	public String getTestrunkeyserver() {
		return testrunkeyserver;
	}

	public void setTestrunkeyserver(String testrunkeyserver) {
		this.testrunkeyserver = testrunkeyserver;
	}

	public String getTestassethierarchy() {
		return testassethierarchy;
	}

	public void setTestassethierarchy(String testassethierarchy) {
		this.testassethierarchy = testassethierarchy;
	}

	public String getTestassethierarchyserver() {
		return testassethierarchyserver;
	}

	public void setTestassethierarchyserver(String testassethierarchyserver) {
		this.testassethierarchyserver = testassethierarchyserver;
	}

	public String getTestCaseUpdateLevel() {
		return testCaseUpdateLevel;
	}

	public void setTestCaseUpdateLevel(String testCaseUpdateLevel) {
		this.testCaseUpdateLevel = testCaseUpdateLevel;
	}

	public String getTestCaseUpdateLevelServer() {
		return testCaseUpdateLevelServer;
	}

	public void setTestCaseUpdateLevelServer(String testCaseUpdateLevelServer) {
		this.testCaseUpdateLevelServer = testCaseUpdateLevelServer;
	}

	public String getJirafields() {
		return jirafields;
	}

	public void setJirafields(String jirafields) {
		this.jirafields = jirafields;
	}

	public String getJirafieldsserver() {
		return jirafieldsserver;
	}

	public void setJirafieldsserver(String jirafieldsserver) {
		this.jirafieldsserver = jirafieldsserver;
	}

	public boolean isDisableaction() {
		return disableaction;
	}

	public boolean isAttachFile() {
		return attachFile;
	}

	public boolean isAttachFileServer() {
		return attachFileServer;
	}

	public TestReportDeployPublisher() {

	}

	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
	@DataBoundConstructor
	public TestReportDeployPublisher(String name, String apikey, String file, boolean attachFile, String testrunname,
			String labels, String sprint, String version, String component, String format, String platform,
			String comment, String apikeyserver, String jiraurlserver, String proxyUrl, Secret password,
			String testrunnameserver, String labelsserver, String sprintserver, String versionserver,
			String componentserver, String username, String fileserver, boolean attachFileServer, String formatserver,
			String platformserver, String commentserver, String testToRun, String testrunkey, String testassethierarchy,
			String testCaseUpdateLevel, String jirafields, String testrunkeyserver, String testassethierarchyserver,
			String testCaseUpdateLevelServer, String jirafieldsserver, boolean disableaction) throws AbortException {
		this.disableaction = disableaction;
		this.attachFile = attachFile;
		this.attachFileServer = attachFileServer;
		this.version = version;

		if (apikey != null && !apikey.isEmpty()) {
			Secret ak = Secret.fromString(apikey);
			this.apikey = ak.getEncryptedValue();
		}

		this.file = file;
		this.testrunname = testrunname;
		this.labels = labels;
		this.sprint = sprint;
		this.component = component;
		this.format = format;
		this.platform = platform;
		this.comment = comment;
		this.testrunkey = testrunkey;
		this.testassethierarchy = testassethierarchy;
		this.testCaseUpdateLevel = testCaseUpdateLevel;
		this.jirafields = jirafields;

		if (apikeyserver != null && !apikeyserver.isEmpty()) {
			Secret aps = Secret.fromString(apikeyserver);
			this.apikeyserver = aps.getEncryptedValue();
		}

		this.jiraurlserver = jiraurlserver;
		this.proxyUrl = proxyUrl;

		// if (password != null && !password.isEmpty()) {
		// 	Secret p = Secret.fromString(password);
		// 	this.password = p.getEncryptedValue();
		// }
		this.password = password;
		this.testrunnameserver = testrunnameserver;
		this.labelsserver = labelsserver;
		this.sprintserver = sprintserver;

		this.versionserver = versionserver;
		this.componentserver = componentserver;
		this.username = username;
		this.fileserver = fileserver;
		this.formatserver = formatserver;
		this.platformserver = platformserver;
		this.commentserver = commentserver;
		this.testrunkeyserver = testrunkeyserver;
		this.testassethierarchyserver = testassethierarchyserver;
		this.testCaseUpdateLevelServer = testCaseUpdateLevelServer;
		this.jirafieldsserver = jirafieldsserver;
		this.testToRun = testToRun;
	}

	/**
	 * We'll use this from the {@code config.jelly}.
	 * 
	 * @throws IOException
	 */

	/**
	 * Test if the test type names match (for marking the radio button).
	 * 
	 * @param testTypeName The String representation of the test type.
	 * @return Whether or not the test type string matches.
	 */

	public String isTestType(String testTypeName) {
		return this.testToRun.equalsIgnoreCase(testTypeName) ? "true" : "";
	}

	@Override
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException
	// public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, ParseException{
	{
		// This is where you 'build' the project. Since this is a dummy, we just say 'hello world' and call that a build.
		// This also shows how you can consult the global configuration of the builder
		String pluginName = "QMetry for JIRA : ";
		// int buildnumber=build.number;
		int buildnumber = run.number;

		PrintStream logger = listener.getLogger();
		
		if (disableaction == false) {
			EnvVars env = null;
			try {
				// env = build.getEnvironment(listener);
				env = run.getEnvironment(listener);
			} catch (Exception e) {
				listener.getLogger().println("Error retrieving environment variables: " + e.getMessage());

			}
			switch (testToRun) {
			case "CLOUD":

				UploadToCloud uploadToCloud = new UploadToCloud();
				logger.println("--------------------------------------------------------");
				logger.println(pluginName + "Uploading result file(s) to JIRA Cloud");
				logger.println("--------------------------------------------------------");

				String file_chkd = env.expand(this.getFile());
				String format_chkd = env.expand(this.getFormat());
				String testrunname_chkd = env.expand(this.getTestrunname());
				String testrunkey_chkd = env.expand(this.getTestrunkey());
				String testassethierarchy_chkd = env.expand(this.getTestassethierarchy());
				String labels_chkd = env.expand(this.getLabels());
				String sprint_chkd = env.expand(this.getSprint());
				String version_chkd = env.expand(this.getVersion());
				String component_chkd = env.expand(this.getComponent());
				String comment_chkd = env.expand(this.getComment());
				String platform_chkd = env.expand(this.getPlatform());
				String jirafields_chkd = env.expand(this.getJirafields());
				String apikey_chkd = env.expand(this.getApikey());

				if (apikey_chkd == null || apikey_chkd.isEmpty()) {
					logger.println(pluginName + "[ERROR] : Enter API Key.");
					throw new AbortException();
				}
				if (file_chkd == null || file_chkd.isEmpty()) {
					logger.println(pluginName + "[ERROR] : Enter path to the test result files.");
					throw new AbortException();
				}
				if (format_chkd == null || format_chkd.isEmpty()) {
					logger.println(pluginName + "[ERROR] : Enter format for test result files.");
					throw new AbortException();
				}

				logger.println(pluginName + "File Path : " + file_chkd);
				logger.println(pluginName + "Format : " + format_chkd);

				if (testrunname_chkd != null && !testrunname_chkd.isEmpty())
					logger.println(pluginName + "Testrun Name : " + testrunname_chkd);
				if (testrunkey_chkd != null && !testrunkey_chkd.isEmpty())
					logger.println(pluginName + "Testrun key : " + testrunkey_chkd);
				if (testassethierarchy_chkd != null && !testassethierarchy_chkd.isEmpty())
					logger.println(pluginName + "Test asset hierarchy : " + testassethierarchy_chkd);
				if (testCaseUpdateLevel != null && !testCaseUpdateLevel.isEmpty())
					logger.println(pluginName + "Testcase update level : " + testCaseUpdateLevel);
				if (labels_chkd != null && !labels_chkd.isEmpty())
					logger.println(pluginName + "Labels : " + labels_chkd);
				if (sprint_chkd != null && !sprint_chkd.isEmpty())
					logger.println(pluginName + "Sprint : " + sprint_chkd);
				if (version_chkd != null && !version_chkd.isEmpty())
					logger.println(pluginName + "Version : " + version_chkd);
				if (component_chkd != null && !component_chkd.isEmpty())
					logger.println(pluginName + "Component : " + this.getComponent());
				if (platform_chkd != null && !platform_chkd.isEmpty())
					logger.println(pluginName + "Platform : " + platform_chkd);
				if (comment_chkd != null && !comment_chkd.isEmpty())
					logger.println(pluginName + "Comment : " + this.getComment());
				if (jirafields_chkd != null && !jirafields_chkd.isEmpty())
					logger.println(pluginName + "JIRA fields : " + jirafields_chkd);

				try {
					Map response = uploadToCloud.uploadToTheCloud(apikey_chkd, file_chkd.trim().replace("\\", "/"),
							attachFile, testrunname_chkd, labels_chkd, sprint_chkd, version_chkd, component_chkd,
							format_chkd, platform_chkd, comment_chkd, testrunkey_chkd, testassethierarchy_chkd,
							testCaseUpdateLevel, jirafields_chkd, buildnumber, run, listener, workspace);
					if (response != null) {
						if (response.get("success").equals("true")) {
							if (response.get("message").equals("false")) {
								logger.println(pluginName + "Error has occurred while uploading the file to temporary S3 bucket.");
								throw new AbortException();
							} else {
								logger.println(pluginName +  response.get("message"));
							}
						} else {
							logger.println(pluginName + response.get("errorMessage"));
							throw new AbortException();
						}
					} else {
						throw new AbortException();
					}
				} catch (MalformedURLException e) {
					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : MalformedURLException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (UnsupportedEncodingException e) {
					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : UnsupportedEncodingException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (ProtocolException e) {
					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : ProtocolException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (FileNotFoundException e) {
					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : FileNotFoundException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (IOException e) {
					if (e.getMessage() != null) {
						if (!(e.getMessage()).equals("CustomException")) {
							logger.println(pluginName + "Exception Message: " + e.getMessage());
							logger.println(pluginName + "[ERROR] : IOException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
							e.printStackTrace();
						}
					}
					throw new AbortException();

				} catch (org.json.simple.parser.ParseException e) {
					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : ParseException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (Exception e) {
					e.printStackTrace();
					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : GeneralException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
					throw new AbortException();
				} finally {
					if (FindFile.getOnSlave()) {
						if (FindFile.getQtm4jFile() != null) {
							try {
								FileUtils.cleanDirectory(FindFile.getQtm4jFile());
							} catch (IOException e) {
								listener.getLogger().println(pluginName + "Copying task failed");
							} catch (IllegalArgumentException e) {
								listener.getLogger().println(pluginName + "Copying task failed");
							}
						}
					}
				}
				break;

			case "SERVER":

				UploadToServer uploadToServer = new UploadToServer();

				logger.println("---------------------------------------------------------");
				logger.println(pluginName + "Uploading result file(s) to JIRA Server");
				logger.println("---------------------------------------------------------");

				file_chkd = env.expand(this.getFileserver());
				format_chkd = env.expand(this.getFormatserver());
				testrunname_chkd = env.expand(this.getTestrunnameserver());
				testrunkey_chkd = env.expand(this.getTestrunkeyserver());
				testassethierarchy_chkd = env.expand(this.getTestassethierarchyserver());
				labels_chkd = env.expand(this.getLabelsserver());
				sprint_chkd = env.expand(this.getSprintserver());
				version_chkd = env.expand(this.getVersionserver());
				component_chkd = env.expand(this.getComponentserver());
				comment_chkd = env.expand(this.getCommentserver());
				platform_chkd = env.expand(this.getPlatformserver());
				jirafields_chkd = env.expand(this.getJirafieldsserver());
				apikey_chkd = env.expand(this.getApikeyserver());
				
				String jiraurlserver_chkd = env.expand(this.getJiraurlserver());
				String proxyUrl_chkd = env.expand(this.getProxyUrl());
				String username_chkd = env.expand(this.getUsername());
				//Secret password_chkd = env.expand(Secret.toString(Secret.fromString(this.getPassword())));
				Secret password_chkd = Secret.fromString(env.expand(Secret.toString(this.getPassword())));
				if (jiraurlserver_chkd == null || jiraurlserver_chkd.isEmpty()) {
					logger.println(pluginName + "[ERROR] : Enter JIRA URL for server instance.");
					throw new AbortException();
				}
				if (username_chkd == null || username_chkd.isEmpty()) {
					logger.println(pluginName + "[ERROR] : Enter Username for JIRA server instance.");
					throw new AbortException();
				}
				if (password_chkd == null) {
					logger.println(pluginName + "[ERROR] : Enter Password for JIRA server instance.");
					throw new AbortException();
				}
				if (apikey_chkd == null || apikey_chkd.isEmpty()) {
					logger.println(pluginName + "[ERROR] : Enter API Key for your project.");
					throw new AbortException();
				}
				if (file_chkd == null || file_chkd.isEmpty()) {
					logger.println(pluginName + "[ERROR] : Enter path to the test result files.");
					throw new AbortException();
				}
				if (format_chkd == null || format_chkd.isEmpty()) {
					logger.println(pluginName + "[ERROR] : Enter format for test result files.");
					throw new AbortException();
				}

				logger.println(pluginName + "JIRA URL : " + jiraurlserver_chkd);

				logger.println(pluginName + "File Path : " + file_chkd);
				logger.println(pluginName + "Format : " + format_chkd);
				if (proxyUrl_chkd != null && !proxyUrl_chkd.isEmpty())
					logger.println(pluginName + "Proxy URL : " + proxyUrl_chkd);
				if (testrunname_chkd != null && !testrunname_chkd.isEmpty())
					logger.println(pluginName + "Test run Name : " + testrunname_chkd);
				if (testrunkey_chkd != null && !testrunkey_chkd.isEmpty())
					logger.println(pluginName + "Test run Key : " + testrunkey_chkd);
				if (testassethierarchy_chkd != null && !testassethierarchy_chkd.isEmpty())
					logger.println(pluginName + "Test asset hierarchy : " + testassethierarchy_chkd);
				if (testCaseUpdateLevelServer != null && !testCaseUpdateLevelServer.isEmpty())
					logger.println(pluginName + "Test case update level : " + testCaseUpdateLevelServer);
				if (labels_chkd != null && !labels_chkd.isEmpty())
					logger.println(pluginName + "Labels : " + labels_chkd);
				if (sprint_chkd != null && !sprint_chkd.isEmpty())
					logger.println(pluginName + "Sprint : " + sprint_chkd);
				if (version_chkd != null && !version_chkd.isEmpty())
					logger.println(pluginName + "Version : " + version_chkd);
				if (component_chkd != null && !component_chkd.isEmpty())
					logger.println(pluginName + "Component : " + component_chkd);
				if (platform_chkd != null && !platform_chkd.isEmpty())
					logger.println(pluginName + "Platform : " + platform_chkd);
				if (comment_chkd != null && !comment_chkd.isEmpty())
					logger.println(pluginName + "Comment : " + comment_chkd);
				if (jirafields_chkd != null && !jirafields_chkd.isEmpty())
					logger.println(pluginName + "JIRA Fields :" + jirafields_chkd);

				try {
					Map<String, String> response = uploadToServer.uploadToTheServer(apikey_chkd, jiraurlserver_chkd,
							proxyUrl_chkd, password_chkd, testrunname_chkd, labels_chkd, sprint_chkd, version_chkd,
							component_chkd, username_chkd, file_chkd.trim().replace("\\", "/"), attachFileServer,
							format_chkd, platform_chkd, comment_chkd, testrunkey_chkd, testassethierarchy_chkd,
							testCaseUpdateLevelServer, jirafields_chkd, buildnumber, run, listener, workspace, pluginName);
					if (response != null) {
						if (response.get("success").equals("error")) {
							logger.println(pluginName + "Error has occurred while uploading the file with response code: "
									+ response.get("responseCode") + " : " + response.get("errorMessage")
									+ ". Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
							throw new AbortException();
						} else if (response.get("success").equals("false")) {

							logger.println(pluginName + "Error has occurred in publishing result QMetry - Test Management for JIRA");
							logger.println(pluginName + "Error Message: " + response.get("errorMessage") + " Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
							throw new AbortException();
						} else if (response.get("success").equals("true")) {
							logger.println(pluginName + "Publishing results has been successful.");

							if (response.get("testRunUrl") != null) {
								logger.println(pluginName + "Test run URL: " + response.get("testRunUrl"));
							}
							if (response.get("testRunKey") != null) {
								logger.println(pluginName + "Test run Key: " + response.get("testRunKey"));
							}
							if (response.get("message") != null) {
								logger.println(pluginName + "Message: " + response.get("message"));
							}
							if (response.get("testRunKey") == null && response.get("testRunUrl") == null && response.get("message") == null) {
								logger.println(pluginName + "Response------>" + response.get("response"));
							}

						}
					} else {
						throw new AbortException();
					}
				} catch (ProtocolException e) {
					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : ProtocolException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (org.apache.http.auth.InvalidCredentialsException e) {
					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : InvalidCredentialsException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (FileNotFoundException e) {
					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : FileNotFoundException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");

					e.printStackTrace();
					throw new AbortException();
				} catch (IOException e) {
					if (e.getMessage() != null) {

						if (!(e.getMessage()).equals("CustomException")) {
							logger.println(pluginName + "Exception Message: " + e.getMessage());
							logger.println(pluginName + "[ERROR] : IOException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
							e.printStackTrace();
						}
					}
					throw new AbortException();
				} catch (org.json.simple.parser.ParseException e) {

					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : ParseException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();

				} catch (Exception e) {
					if (e.getMessage() != null) {
						logger.println(pluginName + "Exception Message: " + e.getMessage());
					}
					logger.println(pluginName + "[ERROR] : GeneralException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qtmfj@qmetrysupport.atlassian.net for more information");

					e.printStackTrace();
					throw new AbortException();
				} finally {
					if (FindFile.getOnSlave()) {
						if (FindFile.getQtm4jFile() != null) {
							try {
								FileUtils.cleanDirectory(FindFile.getQtm4jFile());
							} catch (IOException e) {
								listener.getLogger().println(pluginName + "Copying task failed");
							} catch (IllegalArgumentException e) {
								listener.getLogger().println(pluginName + "Copying task failed");
							}
						}
					}
				}
				break;

			default:
				break;
			}
		} else {
			logger.println(pluginName + "Action 'Publish test result to QMetry for JIRA' is disabled");
		}
	}

	// Overridden for better type safety.If your plugin doesn't really define any property on Descriptor, you don't have to do this.
	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	/**
	 * Descriptor for {@link TestReportDeployPublisher}. Used as a singleton. The
	 * class is marked as public so that it can be accessed from views.
	 *
	 * <p>
	 * See
	 * {@code src/main/resources/hudson/plugins/hello_world/TextExamplePublisher/*.jelly}
	 * for the actual HTML fragment for the configuration screen.
	 */

	@Extension // This indicates to Jenkins that this is an implementation of an extension point.
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		/**
		 * To persist global configuration information, simply store it in a field and
		 * call save().
		 *
		 * <p>
		 * If you don't want fields to be persisted, use {@code transient}.
		 */
		public ListBoxModel doFillFormatItems(@QueryParameter String format) {
			return new ListBoxModel(new Option("testng/XML", "testng/xml", format.matches("testng/xml")),
					new Option("JUnit/XML", "junit/xml", format.matches("junit/xml")),
					new Option("qas/JSON", "qas/json", format.matches("qas/json")),
					new Option("Cucumber/JSON", "cucumber/json", format.matches("cucumber/json")),
					new Option("hpuft/XML", "hpuft/xml", format.matches("hpuft/xml")),
					new Option("SpecFlow/JSON", "specflow/json", format.matches("specflow/json")));
		}

		public ListBoxModel doFillFormatserverItems(@QueryParameter String formatserver) {
			return new ListBoxModel(new Option("testng/XML", "testng/xml", formatserver.matches("testng/xml")),
					new Option("JUnit/XML", "junit/xml", formatserver.matches("junit/xml")),
					new Option("qas/JSON", "qas/json", formatserver.matches("qas/json")),
					new Option("Cucumber/JSON", "cucumber/json", formatserver.matches("cucumber/json")),
					new Option("hpuft/XML", "hpuft/xml", formatserver.matches("hpuft/xml")),
					new Option("SpecFlow/JSON", "specflow/json", formatserver.matches("specflow/json")));
		}

		public ListBoxModel doFillTestassethierarchyItems(@QueryParameter String testassethierarchy) {
			return new ListBoxModel(
					new Option("TestScenario-TestCase", "TestScenario-TestCase",
							testassethierarchy.matches("TestScenario-TestCase")),
					new Option("TestCase-TestStep", "TestCase-TestStep",
							testassethierarchy.matches("TestCase-TestStep")));
		}

		public ListBoxModel doFillTestassethierarchyserverItems(@QueryParameter String testassethierarchyserver) {
			return new ListBoxModel(
					new Option("TestScenario-TestCase", "TestScenario-TestCase",
							testassethierarchyserver.matches("TestScenario-TestCase")),
					new Option("TestCase-TestStep", "TestCase-TestStep",
							testassethierarchyserver.matches("TestCase-TestStep")));
		}

		public ListBoxModel doFillTestCaseUpdateLevelItems(@QueryParameter String testCaseUpdateLevel) {
			return new ListBoxModel(
					new Option("(2) No change in test steps while reusing Test Case.", "2",
							testCaseUpdateLevel.matches("2")),
					new Option("(1) Override test steps while reusing Test Case.", "1",
							testCaseUpdateLevel.matches("1")),
					new Option("(0) Append test steps while reusing Test Case.", "0",
							testCaseUpdateLevel.matches("0")));
		}

		public ListBoxModel doFillTestCaseUpdateLevelServerItems(@QueryParameter String testCaseUpdateLevelServer) {
			return new ListBoxModel(
					new Option("(2) No change in test steps while reusing Test Case.", "2",
							testCaseUpdateLevelServer.matches("2")),
					new Option("(1) Override test steps while reusing Test Case.", "1",
							testCaseUpdateLevelServer.matches("1")),
					new Option("(0) Append test steps while reusing Test Case.", "0",
							testCaseUpdateLevelServer.matches("0")));
		}

		/**
		 * In order to load the persisted global configuration, you have to call load()
		 * in the constructor.
		 */
		public DescriptorImpl() {
			load();
		}

		/**
		 * Performs on-the-fly validation of the form field 'name'.
		 *
		 * @param value This parameter receives the value that the user has typed.
		 * @return Indicates the outcome of the validation. This is sent to the browser.
		 *         <p>
		 *         Note that returning {@link FormValidation#error(String)} does not
		 *         prevent the form from being saved. It just means that a message will
		 *         be displayed to the user.
		 */

		public FormValidation doCheckApikey(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Required");
			return FormValidation.ok();
		}

		public FormValidation doCheckFile(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Required");
			return FormValidation.ok();
		}

		public FormValidation doCheckApikeyserver(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Required");
			return FormValidation.ok();
		}

		public FormValidation doCheckJiraurlserver(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Required");
			return FormValidation.ok();
		}

		public FormValidation doCheckUsername(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Required");
			return FormValidation.ok();
		}

		public FormValidation doCheckPassword(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Required");
			return FormValidation.ok();
		}

		public FormValidation doCheckFileserver(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Required");
			return FormValidation.ok();
		}

		public FormValidation doCheckJirafields(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() != 0) {
				try {
					new JSONArray(value);
				} catch (JSONException e) {
					return FormValidation.error("Invalid JSON");
				}
			}
			return FormValidation.ok();
		}

		public FormValidation doCheckJirafieldsserver(@QueryParameter String value)
				throws IOException, ServletException {
			if (value.length() != 0) {
				try {
					new JSONArray(value);
				} catch (JSONException e) {
					return FormValidation.error("Invalid JSON");
				}
			}
			return FormValidation.ok();
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			// Indicates that this builder can be used with all kinds of project types
			return true;
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		public String getDisplayName() {
			return "Publish results to QMetry for Jira version 3.X below";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			// To persist global configuration information, set that to properties and call save(). Can also use req.bindJSON(this, formData);
			// (easier when there are many fields; need set* methods for this, like setUseFrench)
			save();
			return super.configure(req, formData);
		}
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
}