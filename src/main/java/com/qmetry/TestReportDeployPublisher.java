	
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.http.ParseException;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

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
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link TestReportDeployPublisher} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked. 
 *
 */
@IgnoreJRERequirement
public class TestReportDeployPublisher extends Recorder implements SimpleBuildStep {
	
	private static final Logger LOGGER = Logger.getLogger(TestReportDeployPublisher.class.getName());
	
	 private String name;
     private String version;
     private String apikey;
     private String qtm4jurl;
     private String file;
     private String testrunname;
     private String labels;
     private String sprint;
     private String component;
     private String selection;
     private String platform;
     private String comment;
     private String testrunkey;
     private String testassethierarchy;
     
     private String apikeyserver;
     private String jiraurlserver;
     private String username;
     private Secret password;
     private String testrunnameserver;
     private String labelsserver;
     private String sprintserver;
     private String versionserver;
     private String componentserver;
     private String platformserver;
     private String commentserver;
     private String fileserver;
     private String selectionserver;
     private String testrunkeyserver;
     private String testassethierarchyserver;
     
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

	public String getSelection(){
    	 return selection;
     }
     
    public void setSelection(String selection) {
		this.selection = selection;
	}
     
    public String getSelectionserver(){
    	 return selectionserver;
    }
	public void setSelectionserver(String selectionserver) {
		this.selectionserver = selectionserver;
	}

	public String getFileserver() {
		return fileserver;
	}

	public void setFileserver(String fileserver) {
		this.fileserver = fileserver;
	}
     
    public String getTestrunkey() {
		return testrunkey;
	}

	public void setTestrunkey(String testrunkey) {
		this.testrunkey = testrunkey;
	}

	public String getTestassethierarchy() {
		return testassethierarchy;
	}

	public void setTestassethierarchy(String testassethierarchy) {
		this.testassethierarchy = testassethierarchy;
	}

	public String getTestrunkeyserver() {
		return testrunkeyserver;
	}

	public void setTestrunkeyserver(String testrunkeyserver) {
		this.testrunkeyserver = testrunkeyserver;
	}

	public String getTestassethierarchyserver() {
		return testassethierarchyserver;
	}

	public void setTestassethierarchyserver(String testassethierarchyserver) {
		this.testassethierarchyserver = testassethierarchyserver;
	}

	public String getApikeyserver() {
		return apikeyserver;
   	}

	public void setApikeyserver(String apikeyserver) {
		this.apikeyserver = apikeyserver;
	}

	public String getJiraurlserver() {
		return jiraurlserver;
	}

	public void setJiraurlserver(String jiraurlserver) {
		this.jiraurlserver = jiraurlserver;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Secret getPassword() {
		return password;
	}

	public void setPassword(Secret password) {
		this.password = password;
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
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public String getQtm4jurl() {
		return qtm4jurl;
	}

	public void setQtm4jurl(String qtm4jurl) {
		this.qtm4jurl = qtm4jurl;
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

	public TestReportDeployPublisher(){
		
	}
    
	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public TestReportDeployPublisher(String name,String apikey, String qtm4jurl, String file, String testrunname, 
    		String labels, String sprint, String version, String component, String selection, String platform, String comment,
    		String apikeyserver, String jiraurlserver, Secret password, String testrunnameserver,
    		String labelsserver, String sprintserver, String versionserver, 
    		String componentserver, String username, String fileserver, String selectionserver, String platformserver, String commentserver,
    		String testToRun, String testrunkey, String testassethierarchy, String testrunkeyserver, String testassethierarchyserver) {
        this.version = version;
        this.apikey=apikey;
        this.qtm4jurl=qtm4jurl;
        this.file=file;
        this.testrunname=testrunname;
        this.labels=labels;
        this.sprint=sprint;
        this.component=component;
        this.selection=selection;
        this.platform=platform;
        this.comment=comment;
        this.testassethierarchy=testassethierarchy;
        this.testrunkey=testrunkey;
        
        this.apikeyserver=apikeyserver;
        this.jiraurlserver=jiraurlserver;
        this.password=password;
        this.testrunnameserver=testrunnameserver;
        this.labelsserver=labelsserver;
        this.sprintserver=sprintserver;
        this.versionserver=versionserver;
        this.componentserver=componentserver;
        this.username=username;
        this.fileserver=fileserver;
        this.selectionserver=selectionserver;
        this.platformserver=platformserver;
        this.commentserver=commentserver;
        this.testassethierarchyserver=testassethierarchyserver;
        this.testrunkeyserver=testrunkeyserver;
        
        this.testToRun=testToRun;
        
    }

    /**
     * We'll use this from the {@code config.jelly}.
     * @throws IOException 
     */
    
    /**
     * Test if the test type names match (for marking the radio button).
     * @param testTypeName The String representation of the test type.
     * @return Whether or not the test type string matches.
     */
    
    public String isTestType(String testTypeName) {
        return this.testToRun.equalsIgnoreCase(testTypeName) ? "true" : "";
    }

    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) 
    		throws IOException, ParseException {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.
        // This also shows how you can consult the global configuration of the builder
    
    	String finalFilePath=workspace.toString()+"/"+this.getFile();
    	LOGGER.info(finalFilePath);
    	String finalFilePathServer=workspace.toString()+"/"+this.getFileserver();
    	LOGGER.info(finalFilePathServer);
    	
    	//----------------------------------------------------------------------------
    	switch (testToRun){
		case "CLOUD":
			//------------- FOR CLOUD INSTANCE ...
			UploadToCloud uploadToCloud=new UploadToCloud();
			
			try {
				uploadToCloud.uploadToTheCloud(this.getApikey(), this.getQtm4jurl(), finalFilePath, this.getTestrunname(), 
					this.getLabels(), this.getSprint(), this.getVersion(), this.getComponent(), this.getSelection(),
					this.getPlatform(), this.getComment(),this.getTestrunkey(),this.getTestassethierarchy());
			}catch(MalformedURLException e){
				throw new MalformedURLException("MalformedURLException has occured in QMetry - Test Management for JIRA plugin.");
			}
			catch(UnsupportedEncodingException e){
				throw new UnsupportedEncodingException("UnsupportedEncodingException has occured in QMetry - Test Management for JIRA plugin.");
			}
			catch(ProtocolException e){
				throw new ProtocolException("ProtocolException has occured in QMetry - Test Management for JIRA plugin.");
			}
			catch (IOException e){
				throw new IOException("IOException has occured in QMetry - Test Management for JIRA plugin.");
			}
			catch (org.json.simple.parser.ParseException e) {
				LOGGER.info("ParseException has occured in QMetry - Test Management for JIRA plugin.");
				e.printStackTrace();
			}
			break;
			
		case "SERVER":
			//-------------FOR SERVER INSTANCE ...    	
	    	UploadToServer uploadToServer=new UploadToServer();
	    	try{
	    	uploadToServer.uploadToTheServer(this.getApikeyserver(), this.getJiraurlserver(), this.getPassword().getPlainText(), 
	    			this.getTestrunnameserver(),this.getLabelsserver(), this.getSprintserver(), 
	    			this.getVersionserver(), this.getComponentserver(), this.getUsername(), finalFilePathServer,
	    			this.getSelectionserver(),this.getPlatformserver(),this.getCommentserver(),
	    			this.getTestrunkeyserver(),this.getTestassethierarchyserver());
	    	}
	    	catch( ProtocolException e){
	    		throw new ProtocolException("ProtocolException has occured in QMetry - Test Management for JIRA plugin.");
	    	}
	    	catch (org.apache.http.auth.InvalidCredentialsException e) {
				e.printStackTrace();
			}
	    	catch(IOException e){
	    		throw new IOException("IOException has occured in QMetry - Test Management for JIRA plugin.");
	    	} 
	    	break;
			
		default:
			break;
		}	
    	
    }
 
    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link TestReportDeployPublisher}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See {@code src/main/resources/hudson/plugins/hello_world/TextExamplePublisher/*.jelly}
     * for the actual HTML fragment for the configuration screen.
     */
    
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use {@code transient}.
         */
        
        public ListBoxModel doFillSelectionItems(@QueryParameter String selection)
        {
            return new ListBoxModel(
            		new Option("testng/XML", "testng/xml", selection.matches("testng/xml")),
                    new Option("JUnit/XML", "junit/xml", selection.matches("junit/xml")),
                    new Option("qas/JSON", "qas/json", selection.matches("qas/json")),
                    new Option("Cucumber/JSON","cucumber/json", selection.matches("cucumber/json")),
                    new Option("hpuft/xml","hpuft/xml", selection.matches("hpuft/xml"))
                   
                   // new Option("Red", "ff0000", false)
                    );
        }
        
        public ListBoxModel doFillTestassethierarchyItems(@QueryParameter String testassethierarchy)
        {
            return new ListBoxModel(
            		new Option("TestScenario-TestCase", "TestScenario-TestCase", testassethierarchy.matches("TestScenario-TestCase")),
                    new Option("TestCase-TestStep", "TestCase-TestStep", testassethierarchy.matches("TestCase-TestStep"))
                    );
        }
        
        public ListBoxModel doFillTestassethierarchyserverItems(@QueryParameter String testassethierarchyserver)
        {
            return new ListBoxModel(
            		new Option("TestScenario-TestCase", "TestScenario-TestCase", testassethierarchyserver.matches("TestScenario-TestCase")),
                    new Option("TestCase-TestStep", "TestCase-TestStep", testassethierarchyserver.matches("TestCase-TestStep"))
                    );
        }
        
        public ListBoxModel doFillSelectionserverItems(@QueryParameter String selectionserver)
        {
            return new ListBoxModel(
            		new Option("testng/XML", "testng/xml", selectionserver.matches("testng/xml")),
                    new Option("JUnit/XML", "junit/xml", selectionserver.matches("junit/xml")),
                    new Option("qas/JSON", "qas/json", selectionserver.matches("qas/json")),
                    new Option("Cucumber/JSON","cucumber/json", selectionserver.matches("cucumber/json"))
                   // new Option("Red", "ff0000", false)
                    );
        }
        
        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         *      <p>
         *      Note that returning {@link FormValidation#error(String)} does not
         *      prevent the form from being saved. It just means that a message
         *      will be displayed to the user. 
         */
       /* public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }*/
        
        public FormValidation doCheckApikey(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            return FormValidation.ok();
        }
        
        public FormValidation doCheckQtm4jurl(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            if (!((value.startsWith("https")) || (value.startsWith("http"))))
				return FormValidation.error("Not a valid url");
            return FormValidation.ok();
        }
        
        public FormValidation doCheckFile(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            return FormValidation.ok();
        }
       
        public FormValidation doCheckApikeyserver(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            return FormValidation.ok();
        }
        
        public FormValidation doCheckJiraurlserver(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            if (!((value.startsWith("https")) || (value.startsWith("http"))))
				return FormValidation.error("Not a valid URL");
            return FormValidation.ok();
        }
        
        public FormValidation doCheckUsername(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            return FormValidation.ok();
        }
        
        public FormValidation doCheckPassword(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            return FormValidation.ok();
        }
        
        public FormValidation doCheckTestrunnameserver(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            if (value.length() >= 255)
                return FormValidation.error("Cannot exceed 255 characters!");
            return FormValidation.ok();
        }
        
        public FormValidation doCheckFileserver(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            return FormValidation.ok();
        }

        public FormValidation doCheckTestrunname(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            if (value.length() >= 255)
                return FormValidation.error("Cannot exceed 255 characters!");
            return FormValidation.ok();
        }
        
        public FormValidation doCheckComment(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() >= 255)
                return FormValidation.error("Cannot exceed 255 characters!");
            return FormValidation.ok();
        }
        public FormValidation doCheckCommentserver(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() >=255)
                return FormValidation.error("Cannot exceed 255 characters!");
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
            return "Publish test result to QMetry for JIRA";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

    }

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		// TODO Auto-generated method stub
		return BuildStepMonitor.NONE;
	}
}

