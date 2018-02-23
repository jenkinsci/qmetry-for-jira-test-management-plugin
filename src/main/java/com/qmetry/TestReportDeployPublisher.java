/* Copyright 2017 Infostretch Corporation
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
import hudson.Launcher;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;
import hudson.model.Run;
import hudson.model.TaskListener;
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
//import org.json.simple.JSONArray;
import org.json.*;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

//import java.util.Base64;
//import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Base64;

//import org.apache.log4j.Logger;
//import java.util.logging.Logger;
//import java.util.logging.Level;




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
 * @author Vaibhavsinh Vaghela
 */
@IgnoreJRERequirement
public class TestReportDeployPublisher extends Recorder implements SimpleBuildStep {
	//Logger systemLogger = Logger.getLogger("com.qmetry");
	
	 private boolean disableaction;
	
	 private String name;
     private String version;
     private String apikey;
     private String file;
     private String testrunname;
	 private String testrunkey;
	 private String testassethierarchy;
     private String labels;
     private String sprint;
     private String component;
     private String selection;
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
     private String labelsserver;
     private String sprintserver;
     private String versionserver;
     private String componentserver;
     private String platformserver;
     private String commentserver;
     private String fileserver;
     private String selectionserver;
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

	public String getPassword() throws AbortException{
		//Decrypt password
		return decryptPassword(password);
	}

	public void setPassword(String password) {
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
	
	public TestReportDeployPublisher(){
		
	}
    
	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public TestReportDeployPublisher(String name,String apikey, String file, String testrunname, 
    		String labels, String sprint, String version, String component, String selection, String platform, String comment,
    		String apikeyserver, String jiraurlserver, String password, String testrunnameserver,
    		String labelsserver, String sprintserver, String versionserver, 
    		String componentserver, String username, String fileserver, String selectionserver, String platformserver, String commentserver,
    		String testToRun,String testrunkey,String testassethierarchy,String jirafields,String testrunkeyserver,String testassethierarchyserver,String jirafieldsserver,boolean disableaction) throws AbortException{
        this.disableaction=disableaction;
		
		this.version = version;
        this.apikey=apikey;
        this.file=file;
        this.testrunname=testrunname;
        this.labels=labels;
        this.sprint=sprint;
        this.component=component;
        this.selection=selection;
        this.platform=platform;
        this.comment=comment;
		this.testrunkey=testrunkey;
		this.testassethierarchy=testassethierarchy;
		this.jirafields=jirafields;
        
        this.apikeyserver=apikeyserver;
        this.jiraurlserver=jiraurlserver;
		
		//Encrypt password and store in configuration file
		this.password=encryptPassword(password);
        
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
		this.testrunkeyserver=testrunkeyserver;
		this.testassethierarchyserver=testassethierarchyserver;
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
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, ParseException{
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.
        // This also shows how you can consult the global configuration of the builder
		
		File fcloud=new File(workspace.toString(),this.getFile().trim().replace("\\","/"));
    	//String finalFilePath=workspace.toString()+"/"+this.getFile();
    	String finalFilePath=fcloud.getAbsolutePath();
		
		//System.out.println(finalFilePath);
		
		File fserver=new File(workspace.toString(),this.getFileserver().trim().replace("\\","/"));
    	//String finalFilePathServer=workspace.toString()+"/"+this.getFileserver();
    	String finalFilePathServer=fserver.getAbsolutePath();
		//System.out.println(finalFilePathServer);
		
		int buildnumber=build.number;
		
		PrintStream logger=listener.getLogger();
		
    	//----------------------------------------------------------------------------
    	if(disableaction==false)
		{
			switch (testToRun){
			case "CLOUD":
				//------------- FOR CLOUD INSTANCE ...
				UploadToCloud uploadToCloud=new UploadToCloud();
				logger.print("\n--------------------------------------------------------");
				logger.print("\nQMetry for JIRA : Uploading result file(s) to JIRA Cloud");
				logger.print("\n--------------------------------------------------------");
				logger.print("\nQMetry for JIRA:"+"File Path:"+finalFilePath);
				logger.print("\nQMetry for JIRA:"+"Format:"+this.getSelection());
				if((this.getTestrunname())!=null && !(this.getTestrunname()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Testrun Name:"+this.getTestrunname());
				if((this.getTestrunkey())!=null && !(this.getTestrunkey()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Testrun key:"+this.getTestrunkey());
				if((this.getTestassethierarchy())!=null && !(this.getTestassethierarchy()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Test Asset Hierarchy:"+this.getTestassethierarchy());
				if((this.getLabels())!=null && !(this.getLabels()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Labels:"+this.getLabels());
				if((this.getSprint())!=null && !(this.getSprint()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Sprint:"+this.getSprint());
				if((this.getVersion())!=null && !(this.getVersion()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Version:"+this.getVersion());
				if((this.getComponent())!=null && !(this.getComponent()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Component:"+this.getComponent());
				if((this.getPlatform())!=null && !(this.getPlatform()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Platform:"+this.getPlatform());
				if((this.getComment())!=null && !(this.getComment()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Comment:"+this.getComment());
				if(fcloud.isDirectory())
				{
					logger.print("\nQMetry for JIRA:"+"Given Path is Directory.");
					logger.print("\nQMetry for JIRA:"+"Creating Zip......");
				}
				try {
					Map response=uploadToCloud.uploadToTheCloud(this.getApikey(), finalFilePath, this.getTestrunname(), 
						this.getLabels(), this.getSprint(), this.getVersion(), this.getComponent(), this.getSelection(),
						this.getPlatform(), this.getComment(),this.getTestrunkey(),this.getTestassethierarchy(),this.getJirafields(),buildnumber);
						if(response.get("success").equals("true"))
						{
							if(response.get("message").equals("false"))
							{
								logger.print("\nQMetry for JIRA:"+"Error has occured while uploading the file to temporary S3 bucket.");
								//System.out.println("Error has occured while uploading the file to temporary S3 bucket.");
								throw new AbortException("CustomException");
							}
							else
							{
								logger.print("\nQMetry for JIRA:"+""+response.get("message"));
								//System.out.println(response.get("message"));
							}
						}
						else
						{
							logger.print("\nQMetry for JIRA:"+""+response.get("errorMessage"));
							//System.out.println(response.get("errorMessage"));
							throw new AbortException("CustomException");
						}
				}
				catch(MalformedURLException e){
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]MalformedURLException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					//System.out.println(e);
					e.printStackTrace();
					//throw new MalformedURLException("[ERROR]MalformedURLException has occured in QMetry - Test Management for JIRA plugin.");
					throw new AbortException();
				}
				catch(UnsupportedEncodingException e){
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]UnsupportedEncodingException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					//System.out.println(e);
					e.printStackTrace();
					//throw new UnsupportedEncodingException("[ERROR]UnsupportedEncodingException has occured in QMetry - Test Management for JIRA plugin.");
					throw new AbortException();
				}
				catch(ProtocolException e){
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]ProtocolException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					//System.out.println(e);
					e.printStackTrace();
					//throw new ProtocolException("[ERROR]ProtocolException has occured in QMetry - Test Management for JIRA plugin.");
					throw new AbortException();
				}
				catch(FileNotFoundException e)
				{
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]FileNotFoundException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					e.printStackTrace();
					throw new AbortException();
				}
				catch (IOException e){
					if(e.getMessage()!=null){
						if(!(e.getMessage()).equals("CustomException"))
						{
							logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
							logger.print("\nQMetry for JIRA:"+"[ERROR]IOException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
							e.printStackTrace();
						}
					}
					throw new AbortException();
					
				}
				catch (org.json.simple.parser.ParseException e) {
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]ParseException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					//System.out.println(e);
					e.printStackTrace();
					throw new AbortException();
				}
				catch(Exception e)
				{
					//System.out.println(e);
					e.printStackTrace();
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]GeneralException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					throw new AbortException();
				}
				break;
				
			case "SERVER":
				
				//-------------FOR SERVER INSTANCE ...    	
				UploadToServer uploadToServer=new UploadToServer();
				logger.print("\n---------------------------------------------------------");
				logger.print("\nQMetry for JIRA : Uploading result file(s) to JIRA Server");
				logger.print("\n---------------------------------------------------------");
				logger.print("\nQMetry for JIRA:"+"File Path:"+finalFilePathServer);
				logger.print("\nQMetry for JIRA:"+"Format:"+this.getSelectionserver());
				if((this.getTestrunnameserver())!=null && !(this.getTestrunnameserver()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Testrun Name:"+this.getTestrunnameserver());
				if((this.getTestrunkeyserver())!=null && !(this.getTestrunkeyserver()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Testrun Key:"+this.getTestrunkeyserver());
				if((this.getTestassethierarchyserver())!=null && !(this.getTestassethierarchyserver()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Test Asset Hierarchy:"+this.getTestassethierarchyserver());
				if((this.getLabelsserver())!=null && !(this.getLabelsserver()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Labels:"+this.getLabelsserver());
				if((this.getSprintserver())!=null && !(this.getSprintserver()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Sprint:"+this.getSprintserver());
				if((this.getVersionserver())!=null && !(this.getVersionserver()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Version:"+this.getVersionserver());
				if((this.getComponentserver())!=null && !(this.getComponentserver()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Component:"+this.getComponentserver());
				if((this.getPlatformserver())!=null && !(this.getPlatformserver()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Platform:"+this.getPlatformserver());
				if((this.getCommentserver())!=null && !(this.getCommentserver()).isEmpty())
					logger.print("\nQMetry for JIRA:"+"Comment:"+this.getCommentserver());
				if(fserver.isDirectory())
				{
					logger.print("\nQMetry for JIRA:"+"Given Path is Directory.");
					logger.print("\nQMetry for JIRA:"+"Creating Zip......");
				}
				try{
					Map<String,String> response=uploadToServer.uploadToTheServer(this.getApikeyserver(), this.getJiraurlserver(), this.getPassword(), 
						this.getTestrunnameserver(),this.getLabelsserver(), this.getSprintserver(), 
						this.getVersionserver(), this.getComponentserver(), this.getUsername(), finalFilePathServer,
						this.getSelectionserver(),this.getPlatformserver(),this.getCommentserver(),this.getTestrunkeyserver(),this.getTestassethierarchyserver(),this.getJirafieldsserver(),buildnumber);
						//logger.print("Publishing the result has been succesfull.");
						if(response!=null)
						{
							if(response.get("success").equals("error"))
							{
								//logger.print("Error has occured while uploading the file ");
								logger.print("\nQMetry for JIRA:"+"Error has occured while uploading the file with response code:"+response.get("responseCode")+"\n");
								//System.out.println("Error has occured while uploading the file with response code:"+response.get("responseCode")+"\nContact QMetry Support for more information.");
								throw new AbortException("CustomException");
							}
							else if(response.get("success").equals("false"))
							{
								logger.print("\nQMetry for JIRA:"+"Error has occured in publishing result QMetry - Test Management for JIRA");
								logger.print("\nQMetry for JIRA:"+"Error Message:"+response.get("errorMessage")+"\n"+"Contact QMetry Support for more information.");
								//System.out.println("Error has occured in publishing result QMetry - Test Management for JIRA\n");
								//System.out.println("Error Message:"+response.get("errorMessage")+"\n");
								throw new AbortException("CustomException");
							}
							else if(response.get("success").equals("true"))
							{
								//if(response.get("iszip").equals("false"))
								//{
									logger.print("\nQMetry for JIRA:"+"Publishing the result has been succesfull.");
									
									//System.out.println("Publishing the result has been succesfull.");
									
									if(response.get("testRunKey")!=null)
									{
										logger.print("\nQMetry for JIRA:"+"TestRun Key:"+response.get("testRunKey"));
									}
									if(response.get("testRunUrl")!=null){
										logger.print("\nQMetry for JIRA:"+"TestRun Key:"+response.get("testRunUrl"));
									}	
									if(response.get("message")!=null)
									{
										logger.print("\nQMetry for JIRA:"+"Message:"+response.get("message"));
									}
									if(response.get("testRunKey")==null && response.get("testRunUrl")==null){
										logger.print("\nResponse------>"+response.get("response"));
									}
									
									
								/*}
								else
								{
									logger.print(response.get("response"));
									/*logger.print("\nQMetry for JIRA:"+"Zip file uploaded.");
									logger.print("\nQMetry for JIRA:"+"Publishing the result has been succesfull.\n");*/
									//System.out.println("Publishing the result has been succesfull.");
								//}
							}
						}
				}
				catch( ProtocolException e){
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]ProtocolException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					e.printStackTrace();
					//throw new ProtocolException("[ERROR]ProtocolException has occured in QMetry - Test Management for JIRA plugin.");
					//System.out.println(e);
					throw new AbortException();
				}
				catch (org.apache.http.auth.InvalidCredentialsException e) {
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]InvalidCredentialsException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					e.printStackTrace();
					//System.out.println(e);
					//throw new InvalidCredentialsException("[ERROR]InvalidCredentialsException has occured in QMetry - Test Management for JIRA plugin.");
					throw new AbortException();
				}
				catch(FileNotFoundException e)
				{
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]FileNotFoundException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					e.printStackTrace();
					throw new AbortException();
				}
				catch(IOException e){
					if(e.getMessage()!=null)
					{
					
						if(!(e.getMessage()).equals("CustomException"))
						{
							logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
							logger.print("\nQMetry for JIRA:"+"[ERROR]IOException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
							e.printStackTrace();
						}
					}
					throw new AbortException();
				} 
				catch (org.json.simple.parser.ParseException e) {
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]ParseException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					//System.out.println(e);
					e.printStackTrace();
					throw new AbortException();
					
				}
				catch(Exception e)
				{
					//System.out.println(e);
					logger.print("\nQMetry for JIRA:"+"Exception Message:"+e.getMessage());
					logger.print("\nQMetry for JIRA:"+"[ERROR]GeneralException has occured in QMetry - Test Management for JIRA plugin.Contact QMetry Support for more information.\n");
					e.printStackTrace();
					throw new AbortException();
				}
				break;
				
			default:
				break;
			}	
    	}
		else
		{
			logger.print("\nQmetry for JIRA:Action 'Publish test result to QMetry for JIRA' is disabled");
			//System.out.println("Action 'Publish test result to QMetry for JIRA' is disabled");
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
					new Option("hpuft/XML","hpuft/xml",selection.matches("hpuft/xml"))
                    );
        }
        
        public ListBoxModel doFillSelectionserverItems(@QueryParameter String selectionserver)
        {
            return new ListBoxModel(
            		new Option("testng/XML", "testng/xml", selectionserver.matches("testng/xml")),
                    new Option("JUnit/XML", "junit/xml", selectionserver.matches("junit/xml")),
                    new Option("qas/JSON", "qas/json", selectionserver.matches("qas/json")),
                    new Option("Cucumber/JSON","cucumber/json", selectionserver.matches("cucumber/json")),
					new Option("hpuft/XML","hpuft/xml",selectionserver.matches("hpuft/xml"))
                    );
        }
		
		public ListBoxModel doFillTestassethierarchyItems(@QueryParameter String testassethierarchy)
		{
			return new ListBoxModel(
					new Option("TestScenario-TestCase","TestScenario-TestCase",testassethierarchy.matches("TestScenario-TestCase")),
					new Option("TestCase-TestStep","TestCase-TestStep",testassethierarchy.matches("TestCase-TestStep")));					
		}
		
		public ListBoxModel doFillTestassethierarchyserverItems(@QueryParameter String testassethierarchyserver)
		{
			return new ListBoxModel(
					new Option("TestScenario-TestCase","TestScenario-TestCase",testassethierarchyserver.matches("TestScenario-TestCase")),
					new Option("TestCase-TestStep","TestCase-TestStep",testassethierarchyserver.matches("TestCase-TestStep")));					
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
      
        
        public FormValidation doCheckApikey(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
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
        
        public FormValidation doCheckFileserver(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Required");
            return FormValidation.ok();
        }
		
		public FormValidation doCheckJirafields(@QueryParameter String value) throws IOException,ServletException
		{
			if(value.length()!=0)
			{
				try{
					new JSONArray(value);
				}
				catch(JSONException e)
				{
					return FormValidation.error("Invalid JSON");
				}
			}
			return FormValidation.ok();
		}
		
		public FormValidation doCheckJirafieldsserver(@QueryParameter String value) throws IOException,ServletException
		{
			if(value.length()!=0)
			{
				try{
					new JSONArray(value);
				}
				catch(JSONException e)
				{
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
	
	public String encryptPassword(String password) throws AbortException
	{
		String encryptedPassword="";
		try{
			//byte[] bytesEncoded = Base64.getEncoder().encode(password.getBytes("UTF-8"));
			//encryptedPassword=new String(bytesEncoded,"UTF-8");
			//encryptedPassword=DatatypeConverter.printBase64Binary(password.getBytes("UTF-8"));
		
			byte[] bytesEncoded=Base64.encodeBase64(password.getBytes("UTF-8"));
			encryptedPassword=new String(bytesEncoded,"UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
			throw new AbortException();
		}
		return encryptedPassword;
	}
	
	public String decryptPassword(String password) throws AbortException
	{
		String decryptedPassword="";
		try{
			//byte[] valueDecoded = Base64.getDecoder().decode(password.getBytes("UTF-8"));
			//decryptedPassword=new String(valueDecoded,"UTF-8");
			//decryptedPassword=DatatypeConverter.printBase64Binary(password.getBytes("UTF-8"));
		
			byte[] valueDecoded=Base64.decodeBase64(password.getBytes("UTF-8"));
			decryptedPassword=new String(valueDecoded,"UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
			throw new AbortException();
		}
		return decryptedPassword;
	}
}

