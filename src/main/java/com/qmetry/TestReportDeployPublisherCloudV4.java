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
 * {@link TestReportDeployPublisherCloudV4} is created. The created instance is
 * persisted to the project configuration XML by using XStream, so this allows
 * you to use instance fields (like {@link #name}) to remember the
 * configuration.
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked.
 *
 * @author Dhaval Mistry
 */
@IgnoreJRERequirement
public class TestReportDeployPublisherCloudV4 extends Recorder implements SimpleBuildStep {

	private boolean disableaction;

	private String apikey;
	private String format;
	private String file;
	private String testCycleToReuse;
	private String environment;
	private String build;

	private boolean attachFile;

	private String testCycleLabels;
	private String testCycleComponents;
	private String testCyclePriority;
	private String testCycleStatus;
	private String testCycleSprintId;
	private String testCycleFixVersionId;
	private String testCycleSummary;

	private String testCaseLabels;
	private String testCaseComponents;
	private String testCasePriority;
	private String testCaseStatus;
	private String testCaseSprintId;
	private String testCaseFixVersionId;

	public String testToRun;

	public String getTestCycleToReuse() {
		return testCycleToReuse;
	}

	public void setTestCycleToReuse(String testCycleToReuse) {
		this.testCycleToReuse = testCycleToReuse;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
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

	public boolean isDisableaction() {
		return disableaction;
	}

	public boolean isAttachFile() {
		return attachFile;
	}

	public String getTestCycleLabels() {
		return testCycleLabels;
	}

	public void setTestCycleLabels(String testCycleLabels) {
		this.testCycleLabels = testCycleLabels;
	}

	public String getTestCycleComponents() {
		return testCycleComponents;
	}

	public void setTestCycleComponents(String testCycleComponents) {
		this.testCycleComponents = testCycleComponents;
	}

	public String getTestCyclePriority() {
		return testCyclePriority;
	}

	public void setTestCyclePriority(String testCyclePriority) {
		this.testCyclePriority = testCyclePriority;
	}

	public String getTestCycleStatus() {
		return testCycleStatus;
	}

	public void setTestCycleStatus(String testCycleStatus) {
		this.testCycleStatus = testCycleStatus;
	}

	public String getTestCycleSprintId() {
		return testCycleSprintId;
	}

	public void setTestCycleSprintId(String testCycleSprintId) {
		this.testCycleSprintId = testCycleSprintId;
	}

	public String getTestCycleFixVersionId() {
		return testCycleFixVersionId;
	}

	public void setTestCycleFixVersionId(String testCycleFixVersionId) {
		this.testCycleFixVersionId = testCycleFixVersionId;
	}

	public String getTestCycleSummary() {
		return testCycleSummary;
	}

	public void setTestCycleSummary(String testCycleSummary) {
		this.testCycleSummary = testCycleSummary;
	}

	public String getTestCaseLabels() {
		return testCaseLabels;
	}

	public void setTestCaseLabels(String testCaseLabels) {
		this.testCaseLabels = testCaseLabels;
	}

	public String getTestCaseComponents() {
		return testCaseComponents;
	}

	public void setTestCaseComponents(String testCaseComponents) {
		this.testCaseComponents = testCaseComponents;
	}

	public String getTestCasePriority() {
		return testCasePriority;
	}

	public void setTestCasePriority(String testCasePriority) {
		this.testCasePriority = testCasePriority;
	}

	public String getTestCaseStatus() {
		return testCaseStatus;
	}

	public void setTestCaseStatus(String testCaseStatus) {
		this.testCaseStatus = testCaseStatus;
	}

	public String getTestCaseSprintId() {
		return testCaseSprintId;
	}

	public void setTestCaseSprintId(String testCaseSprintId) {
		this.testCaseSprintId = testCaseSprintId;
	}

	public String getTestCaseFixVersionId() {
		return testCaseFixVersionId;
	}

	public void setTestCaseFixVersionId(String testCaseFixVersionId) {
		this.testCaseFixVersionId = testCaseFixVersionId;
	}

	public TestReportDeployPublisherCloudV4() {

	}

	// Fields in config.jelly must match the parameter names in the
	// "DataBoundConstructor"
	@DataBoundConstructor
	public TestReportDeployPublisherCloudV4(String testToRun, String apikey, String file, boolean attachFile,
			String format, boolean disableaction, String testCycleToReuse, String environment, String build,
			String testCycleLabels, String testCycleComponents, String testCyclePriority,
			String testCycleStatus, String testCycleSprintId, String testCycleFixVersionId, String testCycleSummary,
			String testCaseLabels, String testCaseComponents, String testCasePriority, String testCaseStatus,
			String testCaseSprintId, String testCaseFixVersionId) throws AbortException {

		this.testToRun = testToRun;
		this.disableaction = disableaction;
		this.attachFile = attachFile;

		this.apikey = apikey;
		this.format = format;
		this.file = file;

		this.testCycleToReuse = testCycleToReuse;
		this.environment = environment;
		this.build = build;

		this.testCycleLabels = testCycleLabels;
		this.testCycleComponents = testCycleComponents;
		this.testCyclePriority = testCyclePriority;
		this.testCycleStatus = testCycleStatus;
		this.testCycleSprintId = testCycleSprintId;
		this.testCycleFixVersionId = testCycleFixVersionId;
		this.testCycleSummary = testCycleSummary;

		this.testCaseLabels = testCaseLabels;
		this.testCaseComponents = testCaseComponents;
		this.testCasePriority = testCasePriority;
		this.testCaseStatus = testCaseStatus;
		this.testCaseSprintId = testCaseSprintId;
		this.testCaseFixVersionId = testCaseFixVersionId;

		if (apikey != null && !apikey.isEmpty()) {
			Secret ak = Secret.fromString(apikey);
			this.apikey = ak.getEncryptedValue();
		}
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
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
			throws InterruptedException, IOException
	// public boolean perform(AbstractBuild build, Launcher launcher, BuildListener
	// listener) throws IOException, ParseException{
	{
		// This is where you 'build' the project.
		// Since this is a dummy, we just say 'hello world' and call that a build.
		// This also shows how you can consult the global configuration of the builder

		// int buildnumber=build.number;
		int buildnumber = run.number;

		PrintStream logger = listener.getLogger();
		// logger.println("[DEBUG] : workspace : " + workspace);
		// ----------------------------------------------------------------------------
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
				// ------------- FOR CLOUD INSTANCE ...
				UploadToCloudV4 uploadToCloud = new UploadToCloudV4();
				logger.println("--------------------------------------------------------");
				logger.println("QMetry for JIRA : Uploading result file(s) to JIRA Cloud");
				logger.println("--------------------------------------------------------");

				String file_chkd = env.expand(this.getFile());
				String format_chkd = env.expand(this.getFormat());
				String apikey_chkd = env.expand(this.getApikey());

				String testCycleToReuse_chkd = env.expand(this.getTestCycleToReuse());
				String environment_chkd = env.expand(this.getEnvironment());
				String build_chkd = env.expand(this.getBuild());

				String testCycleLabels_chkd = env.expand(this.getTestCycleLabels());
				String testCycleComponents_chkd = env.expand(this.getTestCycleComponents());
				String testCyclePriority_chkd = env.expand(this.getTestCyclePriority());
				String testCycleStatus_chkd = env.expand(this.getTestCycleStatus());
				String testCycleSprintId_chkd = env.expand(this.getTestCycleSprintId());
				String testCycleFixVersionId_chkd = env.expand(this.getTestCycleFixVersionId());
				String testCycleSummary_chkd = env.expand(this.getTestCycleSummary());

				String testCaseLabels_chkd = env.expand(this.getTestCaseLabels());
				String testCaseComponents_chkd = env.expand(this.getTestCaseComponents());
				String testCasePriority_chkd = env.expand(this.getTestCasePriority());
				String testCaseStatus_chkd = env.expand(this.getTestCaseStatus());
				String testCaseSprintId_chkd = env.expand(this.getTestCaseSprintId());
				String testCaseFixVersionId_chkd = env.expand(this.getTestCaseFixVersionId());

				if (apikey_chkd == null || apikey_chkd.isEmpty()) {
					logger.println("QMetry for JIRA : [ERROR] : Enter API Key.");
					throw new AbortException();
				}
				if (file_chkd == null || file_chkd.isEmpty()) {
					logger.println("QMetry for JIRA : [ERROR] : Enter path to the test result files.");
					throw new AbortException();
				}
				if (format_chkd == null || format_chkd.isEmpty()) {
					logger.println("QMetry for JIRA : [ERROR] : Enter format for test result files.");
					throw new AbortException();
				}

				logger.println("QMetry for JIRA :" + " File Path : " + file_chkd);
				logger.println("QMetry for JIRA :" + " Format : " + format_chkd);

				if (testCycleToReuse_chkd != null && !testCycleToReuse_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCycle To Reuse : " + testCycleToReuse_chkd);

				if (environment_chkd != null && !environment_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " Environment : " + environment_chkd);

				if (build_chkd != null && !build_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " Build : " + build_chkd);

				if (testCycleLabels_chkd != null && !testCycleLabels_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCycle Labels : " + testCycleLabels_chkd);

				if (testCycleComponents_chkd != null && !testCycleComponents_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCycle Components : " + testCycleComponents_chkd);

				if (testCyclePriority_chkd != null && !testCyclePriority_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCycle Priority : " + testCyclePriority_chkd);

				if (testCycleStatus_chkd != null && !testCycleStatus_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCycle Status : " + testCycleStatus_chkd);

				if (testCycleSprintId_chkd != null && !testCycleSprintId_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCycle SprintId : " + testCycleSprintId_chkd);

				if (testCycleFixVersionId_chkd != null && !testCycleFixVersionId_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCycle Fix Version Id : " + testCycleFixVersionId_chkd);

				if (testCycleSummary_chkd != null && !testCycleSummary_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCycle Summary : " + testCycleSummary_chkd);

				if (testCaseLabels_chkd != null && !testCaseLabels_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCase Label : " + testCaseLabels_chkd);

				if (testCaseComponents_chkd != null && !testCaseComponents_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCase Components : " + testCaseComponents_chkd);

				if (testCasePriority_chkd != null && !testCasePriority_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCase Priority : " + testCasePriority_chkd);

				if (testCaseStatus_chkd != null && !testCaseStatus_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCase Status : " + testCaseStatus_chkd);

				if (testCaseSprintId_chkd != null && !testCaseSprintId_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCase SprintId : " + testCaseSprintId_chkd);

				if (testCaseFixVersionId_chkd != null && !testCaseFixVersionId_chkd.isEmpty())
					logger.println("QMetry for JIRA :" + " TestCase Fix Version Id : " + testCaseFixVersionId_chkd);

				try {
					Map response = uploadToCloud.uploadToTheCloud(apikey_chkd, file_chkd.trim().replace("\\", "/"),
							attachFile, format_chkd, testCycleToReuse_chkd, environment_chkd, build_chkd,
							testCycleLabels_chkd, testCycleComponents_chkd, testCyclePriority_chkd,
							testCycleStatus_chkd, testCycleSprintId_chkd, testCycleFixVersionId_chkd,
							testCycleSummary_chkd, testCaseLabels_chkd, testCaseComponents_chkd, testCasePriority_chkd,
							testCaseStatus_chkd, testCaseSprintId_chkd, testCaseFixVersionId_chkd, buildnumber, run,
							listener, workspace);
					if (response != null) {
						if (response.get("success").equals("true")) {
							if (response.get("message").equals("false")) {
								logger.println("QMetry for JIRA :"
										+ " Error has occurred while uploading the file to temporary S3 bucket.");
								throw new AbortException();
							} else {
								logger.println("QMetry for JIRA :" + " " + response.get("message"));
							}
						} else {
							logger.println("QMetry for JIRA :" + " " + response.get("errorMessage"));
							throw new AbortException();
						}
					} else {

						throw new AbortException();
					}
				} catch (MalformedURLException e) {
					if (e.getMessage() != null) {
						logger.println("QMetry for JIRA :" + " Exception Message: " + e.getMessage());
					}
					logger.println("QMetry for JIRA :"
							+ " [ERROR] : MalformedURLException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qmetryforjira@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (UnsupportedEncodingException e) {
					if (e.getMessage() != null) {
						logger.println("QMetry for JIRA :" + " Exception Message: " + e.getMessage());
					}
					logger.println("QMetry for JIRA :"
							+ " [ERROR] : UnsupportedEncodingException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qmetryforjira@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (ProtocolException e) {
					if (e.getMessage() != null) {
						logger.println("QMetry for JIRA :" + " Exception Message: " + e.getMessage());
					}
					logger.println("QMetry for JIRA :"
							+ " [ERROR] : ProtocolException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qmetryforjira@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (FileNotFoundException e) {
					if (e.getMessage() != null) {
						logger.println("QMetry for JIRA :" + " Exception Message: " + e.getMessage());
					}
					logger.println("QMetry for JIRA :"
							+ " [ERROR] : FileNotFoundException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qmetryforjira@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (IOException e) {
					if (e.getMessage() != null) {
						if (!(e.getMessage()).equals("CustomException")) {
							logger.println("QMetry for JIRA :" + " Exception Message: " + e.getMessage());
							logger.println("QMetry for JIRA :"
									+ " [ERROR] : IOException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qmetryforjira@qmetrysupport.atlassian.net for more information");
							e.printStackTrace();
						}
					}
					throw new AbortException();

				} catch (org.json.simple.parser.ParseException e) {
					if (e.getMessage() != null) {
						logger.println("QMetry for JIRA :" + " Exception Message: " + e.getMessage());
					}
					logger.println("QMetry for JIRA :"
							+ " [ERROR] : ParseException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qmetryforjira@qmetrysupport.atlassian.net for more information");
					e.printStackTrace();
					throw new AbortException();
				} catch (Exception e) {
					e.printStackTrace();
					if (e.getMessage() != null) {
						logger.println("QMetry for JIRA :" + " Exception Message: " + e.getMessage());
					}
					logger.println("QMetry for JIRA :"
							+ " [ERROR] : GeneralException has occurred in QMetry - Test Management for JIRA plugin.Please send these logs to qmetryforjira@qmetrysupport.atlassian.net for more information");
					throw new AbortException();
				} finally {
					if (FindFile.getOnSlave()) {
						if (FindFile.getQtm4jFile() != null) {
							try {
								FileUtils.cleanDirectory(FindFile.getQtm4jFile());
							} catch (IOException e) {
								listener.getLogger().println("QMetry for JIRA : Copying task failed");
							} catch (IllegalArgumentException e) {
								listener.getLogger().println("QMetry for JIRA : Copying task failed");
							}
						}
					}
				}
				break;

			default:
				break;
			}
		} else {
			logger.println("Qmetry for JIRA : Action 'Publish test result to QMetry for JIRA V4' is disabled");
		}
	}

	// Overridden for better type safety.
	// If your plugin doesn't really define any property on Descriptor,
	// you don't have to do this.

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	/**
	 * Descriptor for {@link TestReportDeployPublisherCloudV4}. Used as a singleton.
	 * The class is marked as public so that it can be accessed from views.
	 *
	 * <p>
	 * See
	 * {@code src/main/resources/hudson/plugins/hello_world/TextExamplePublisher/*.jelly}
	 * for the actual HTML fragment for the configuration screen.
	 */

	@Extension // This indicates to Jenkins that this is an implementation of an extension
				// point.
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

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			// Indicates that this builder can be used with all kinds of project types
			return true;
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		public String getDisplayName() {
			return "Publish results to QMetry for Jira version 4.X above";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			// To persist global configuration information,
			// set that to properties and call save().
			// ^Can also use req.bindJSON(this, formData);
			// (easier when there are many fields; need set* methods for this, like
			// setUseFrench)

			save();
			return super.configure(req, formData);
		}
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
}
