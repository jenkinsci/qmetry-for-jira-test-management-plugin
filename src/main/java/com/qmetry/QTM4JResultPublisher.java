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
************************************************************************************/
package com.qmetry;

import hudson.Launcher;
import hudson.EnvVars;
import hudson.util.Secret;
import hudson.AbortException;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;
import hudson.tasks.Recorder;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.apache.http.ParseException;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import javax.servlet.ServletException;


import org.json.simple.parser.*;
import org.json.*;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import org.apache.commons.codec.binary.Base64;

import hudson.model.TaskListener;
import hudson.model.Run;
import hudson.FilePath;

@IgnoreJRERequirement
public class QTM4JResultPublisher extends Recorder implements SimpleBuildStep {

	
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
     private String username;
     private String password;
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

	public String getFormat(){
    	 return format;
    }
     
    public void setFormat(String format) {
		this.format = format;
	}

	public String getFormatserver(){
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
		//return apikeyserver;
		return Secret.toString(Secret.fromString(apikeyserver));
   	}

	public void setApikeyserver(String apikeyserver) {
		//this.apikeyserver = apikeyserver;
		this.apikeyserver = Secret.fromString(apikeyserver).getEncryptedValue();
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

	public String getPassword() throws AbortException{
		return Secret.toString(Secret.fromString(password));
	}

	public void setPassword(String password) {
		this.password = Secret.fromString(password).getEncryptedValue();
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
		//return apikey;
		return Secret.toString(Secret.fromString(apikey));
	}

	public void setApikey(String apikey) {
		//this.apikey = apikey;
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
	
	public String getTestrunkey()
	{
		return testrunkey;
	}
	
	public void setTestrunkey(String testrunkey){
		this.testrunkey=testrunkey;
	}
	
	public String getTestrunkeyserver()
	{
		return testrunkeyserver;
	}
	
	public void setTestrunkeyserver(String testrunkeyserver){
		this.testrunkeyserver=testrunkeyserver;
	}
	
	public String getTestassethierarchy()
	{
		return testassethierarchy;
	}
	
	public void setTestassethierarchy(String testassethierarchy){
		this.testassethierarchy=testassethierarchy;
	}
	
	public String getTestassethierarchyserver(){
		return testassethierarchyserver;
	}
	
	public void setTestassethierarchyserver(String testassethierarchyserver){
		this.testassethierarchyserver=testassethierarchyserver;
	}
	
	public String getTestCaseUpdateLevel()
	{
		return testCaseUpdateLevel;
	}
	
	public void setTestCaseUpdateLevel(String testCaseUpdateLevel)
	{
		this.testCaseUpdateLevel = testCaseUpdateLevel;
	}
	
	public String getTestCaseUpdateLevelServer()
	{
		return testCaseUpdateLevelServer;
	}
	
	public void setTestCaseUpdateLevelServer(String testCaseUpdateLevelServer)
	{
		this.testCaseUpdateLevelServer = testCaseUpdateLevelServer;
	}
	
	public String getJirafields(){
		return jirafields;
	}
	
	public void setJirafields(String jirafields){
		this.jirafields=jirafields;
	}
	
	
	public String getJirafieldsserver(){
		return jirafieldsserver;
	}
	
	public void setJirafieldsserver(String jirafieldsserver){
		this.jirafieldsserver=jirafieldsserver;
	}

	public boolean isDisableaction()
	{
		return disableaction;
	}
	
	public boolean isAttachFile()
	{
		return attachFile;
	}
	
	public boolean isAttachFileServer()
	{
		return attachFileServer;
	}
	
	public QTM4JResultPublisher(){
		
	}
    
	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public QTM4JResultPublisher(String name,String apikey, String file, boolean attachFile, String testrunname, 
    		String labels, String sprint, String version, String component, String format, String platform, String comment,
    		String apikeyserver, String jiraurlserver, String password, String testrunnameserver,
    		String labelsserver, String sprintserver, String versionserver, 
    		String componentserver, String username, String fileserver, boolean attachFileServer, String formatserver, String platformserver, String commentserver,
    		String testToRun,String testrunkey,String testassethierarchy, String testCaseUpdateLevel, String jirafields,String testrunkeyserver,String testassethierarchyserver, String testCaseUpdateLevelServer, String jirafieldsserver,boolean disableaction) throws AbortException{
        this.disableaction=disableaction;
		this.attachFile=attachFile;
		this.attachFileServer=attachFileServer;
		this.version = version;
        //this.apikey=apikey;
        
		if(apikey!=null && !apikey.isEmpty())
		{
			Secret ak = Secret.fromString(apikey);
			this.apikey = ak.getEncryptedValue();
		}
		
		this.file=file;
        this.testrunname=testrunname;
        this.labels=labels;
        this.sprint=sprint;
        this.component=component;
        this.format=format;
        this.platform=platform;
        this.comment=comment;
		this.testrunkey=testrunkey;
		this.testassethierarchy=testassethierarchy;
		this.testCaseUpdateLevel=testCaseUpdateLevel;
		this.jirafields=jirafields;
        
        //this.apikeyserver=apikeyserver;
		
		if(apikeyserver!=null && !apikeyserver.isEmpty())
		{
			Secret aps = Secret.fromString(apikeyserver);
			this.apikeyserver = aps.getEncryptedValue();
		}
		
        this.jiraurlserver=jiraurlserver;
		
		if(password != null && !password.isEmpty())
		{
			Secret p = Secret.fromString(password);
			this.password=p.getEncryptedValue();
		}
		
		this.testrunnameserver=testrunnameserver;
        this.labelsserver=labelsserver;
        this.sprintserver=sprintserver;

        this.versionserver=versionserver;
        this.componentserver=componentserver;
        this.username=username;
        this.fileserver=fileserver;
        this.formatserver=formatserver;
        this.platformserver=platformserver;
        this.commentserver=commentserver;
		this.testrunkeyserver=testrunkeyserver;
		this.testassethierarchyserver=testassethierarchyserver;
		this.testCaseUpdateLevelServer=testCaseUpdateLevelServer;
		this.jirafieldsserver=jirafieldsserver;
        
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
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException 
	{
		TestReportDeployPublisher trdp =new TestReportDeployPublisher(name,apikey, file,attachFile,  testrunname, labels, sprint, version, component, format, platform, comment, apikeyserver, jiraurlserver, password, testrunnameserver,
    		labelsserver, sprintserver, versionserver, 
    		componentserver, username, fileserver,attachFileServer, formatserver, platformserver, commentserver,
    		testToRun, testrunkey, testassethierarchy, testCaseUpdateLevel, jirafields, testrunkeyserver, testassethierarchyserver, testCaseUpdateLevelServer, jirafieldsserver, disableaction);
		trdp.perform(run,workspace,launcher,listener);
	}
	
	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		// TODO Auto-generated method stub
		return BuildStepMonitor.NONE;
	}
	
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
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> 
	{
		 public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return false;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Publish test result to QMetry for JIRA";
        }
	}
	
}
