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
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.NullPointerException;

import hudson.model.AbstractBuild;
//import hudson.model.BuildListener;
import hudson.FilePath;
import hudson.model.AbstractProject;
import jenkins.model.Jenkins;
import hudson.model.Computer;
import hudson.model.Hudson.MasterComputer;
import hudson.slaves.SlaveComputer;

import hudson.model.TaskListener;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import hudson.model.Node;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;	
import java.io.FileNotFoundException;

import java.util.List;

public class FindFile
{
	public static File findFile(String filePath, Run<?, ?> run, TaskListener listener,String format,FilePath workspace) throws IOException,InterruptedException,FileNotFoundException
	{
		if(filePath.startsWith("/"))
		{
			filePath=filePath.substring(1);
		}
		
		if(workspace!= null /*&& workspace.toComputer()!=null && workspace.toComputer().getNode()!=null*/)
		{
			Computer comp = workspace.toComputer();
			if(comp!=null)
			{
				Node node = comp.getNode();
				if(node!=null)
				{
					Computer comp1 = node.toComputer(); 
					if(comp1!=null)
					{
						if(comp1 instanceof SlaveComputer) 
						{
							listener.getLogger().println("QMetry for JIRA : build taking place on slave machine");
							//FilePath slaveMachineWorkspace = project.getWorkspace();
							FilePath slaveMachineWorkspace = workspace;
							/*if(filePath.startsWith("/"))
							{
								filePath=filePath.substring(1);
							}*/
							FilePath f = null;
							if(format.equals("qas/json"))
							{
								//Getting latest testresult files for QAS
								listener.getLogger().println("QMetry for JIRA : Getting latest test-result folder for QAS...");
								f = lastFileModified(slaveMachineWorkspace , filePath );
								filePath = filePath + "/" + f.getName();
								listener.getLogger().println("QMetry for JIRA : Latest test-result folder : " + f.toString());
								//listener.getLogger().println("[DEBUG] : final path : "+f.toString());
								//listener.getLogger().println("[DEBUG] : filepath : " +filePath);
							}
							else
							{
								f = new FilePath(slaveMachineWorkspace , filePath);
								if(!f.exists())
								{
									throw new FileNotFoundException("cannot find file : " + f);
								}
							}
							
							//boolean filter = false;
							//String fileMask = "";
							if(f.isDirectory())
							{
								String absPath=f.toString();
								
								if(!filePath.endsWith("/"))
								{
									filePath=filePath.concat("/");
								}
								
								//Changes for filtering files
								/*
								if(format.equals("junit/xml") || format.equals("hpuft/xml") || format.equals("testng/xml"))
								{
									filter=true;
									fileMask=filePath.concat("*.xml");
									listener.getLogger().println("[DEBUG] : xml filepath : "+fileMask);
								}
								else if(format.equals("cucumber/json") || format.equals("qas/json"))
								{
									filter=true;
									fileMask=filePath.concat("*.json");
									listener.getLogger().println("[DEBUG] : json filepath : "+fileMask);
								}*/
							}
							if(!slaveMachineWorkspace.exists()) 
							{
								listener.getLogger().println("QMetry for JIRA : [ERROR]Failed to access slave machine workspace directory");
								return null;
							}
							
							FilePath masterMachineWorkspace  = null;
							//for free style job
							if(run.getParent() instanceof AbstractProject)
							{
								AbstractProject project = (AbstractProject)run.getParent();
								if(project.getCustomWorkspace() != null && project.getCustomWorkspace().length()>0 ) 
								{
									masterMachineWorkspace = new FilePath(new File(project.getCustomWorkspace()));
								} 
								else 
								{
									masterMachineWorkspace = Jenkins.getInstance().getWorkspaceFor((TopLevelItem)project);
								}
							}
							//for pipeline job
							else if(run.getParent() instanceof WorkflowJob)
							{
								//listener.getLogger().println("[DEBUG] : instance of WorkFlowJob");
								WorkflowJob project = (WorkflowJob)run.getParent();
								masterMachineWorkspace = Jenkins.getInstance().getWorkspaceFor((TopLevelItem)project);
							}
							//listener.getLogger().println("[DEBUG] : masterMachineWorkspace : " + masterMachineWorkspace);
							if(masterMachineWorkspace==null)
							{
								listener.getLogger().println("QMetry for JIRA : [ERROR]Failed to access master machine workspace directory");
								return null;
							}
							listener.getLogger().println("QMetry for JIRA : Copying files from slave to master machine...");
							int fileCount = slaveMachineWorkspace.copyRecursiveTo(filePath, masterMachineWorkspace);
							//Changes for file filtering
							/*
								int fileCount;
								if(filter)
								{
									fileCount = slaveMachineWorkspace.copyRecursiveTo(fileMask, masterMachineWorkspace);
								}
								else
								{
									fileCount = slaveMachineWorkspace.copyRecursiveTo(filePath, masterMachineWorkspace);
								}
							*/
							listener.getLogger().println("QMetry for JIRA : Total "+fileCount+" result file(s) copied from slave to master machine");
							File finalResultFile = new File(masterMachineWorkspace.toURI());
							finalResultFile = new File(finalResultFile, filePath);
							return finalResultFile;
						}
						else if(comp1 instanceof MasterComputer) 
						{
							//File masterWorkspace = new File(project.getWorkspace().toURI());
							//listener.getLogger().println("[DEBUG] : Build taking place on master machine");
							File masterWorkspace = new File(workspace.toString());
							FilePath resultFilePath = null;
							if(format.equals("qas/json"))
							{
								//Getting latest testresult files for QAS
								listener.getLogger().println("QMetry for JIRA : Getting latest test-result folder for QAS...");
								resultFilePath = lastFileModified(new FilePath(masterWorkspace),filePath);
								listener.getLogger().println("QMetry for JIRA : Latest test-result folder : " + resultFilePath.toString());
								//listener.getLogger().println("[DEBUG]: final path : "+resultFilePath.toString());
							}
							else
							{
								resultFilePath = new FilePath(new File(masterWorkspace, filePath));
								if(!resultFilePath.exists())
								{
									throw new FileNotFoundException("cannot find file : " + resultFilePath);
								}
								//listener.getLogger().println("[DEBUG]: final path : "+resultFilePath.toString());
							}
							File resultFile = new File(resultFilePath.toString());
							return resultFile;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static FilePath lastFileModified(FilePath base, String path) throws IOException,InterruptedException,FileNotFoundException
	{
		FilePath slaveDir = new FilePath(base,path); 
		if(!slaveDir.exists())
		{
			throw new FileNotFoundException("cannot find file : " + slaveDir);
		}
		List<FilePath> files = slaveDir.listDirectories();
		long lastMod = Long.MIN_VALUE;
		FilePath choice = null;
		if(files!=null)
		{
			for (FilePath file : files) 
			{
				if(file.isDirectory() && !(file.getName()).equals("surefire"))
				{
					if (file.lastModified() > lastMod) 
					{
						choice = file;
						lastMod = file.lastModified();
					}
				}
			}
		}
		return choice;
	}
}