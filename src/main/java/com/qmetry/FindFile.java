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
import hudson.model.BuildListener;
import hudson.FilePath;
import hudson.model.FreeStyleProject;
import jenkins.model.Jenkins;
import hudson.model.Computer;
import hudson.model.Hudson.MasterComputer;
import hudson.slaves.SlaveComputer;

public class FindFile
{
	public static File findFile(String filePath, AbstractBuild build, BuildListener listener) throws IOException,InterruptedException
	{
			if(Computer.currentComputer() instanceof SlaveComputer) {
				listener.getLogger().println("QMetry for JIRA:build taking place on slave machine");
				FilePath slaveMachineWorkspace = build.getProject().getWorkspace();
				if(filePath.startsWith("/"))
				{
					filePath=filePath.substring(1);
				}
				FilePath f=new FilePath(slaveMachineWorkspace,filePath);
				if(f.isDirectory())
				{
					String absPath=f.toString();
					if(!filePath.endsWith("/"))
					{
						filePath=filePath.concat("/");
					}
				}
				if(!slaveMachineWorkspace.exists()) {
					listener.getLogger().println("QMetry for JIRA:[ERROR]Failed to access slave machine workspace directory");
					return null;
				}
				FilePath masterMachineWorkspace  = null;
				if(build.getProject().getCustomWorkspace() != null && build.getProject().getCustomWorkspace().length()>0 ) {
					masterMachineWorkspace = new FilePath(new File(build.getProject().getCustomWorkspace()));
				} 
				else 
				{
					masterMachineWorkspace = Jenkins.getInstance().getWorkspaceFor((FreeStyleProject)build.getProject());
				}
				if(masterMachineWorkspace==null)
				{
					listener.getLogger().println("QMetry for JIRA:[ERROR]Failed to access master machine workspace directory");
					return null;
				}
				listener.getLogger().println("QMetry for JIRA:Copying files from slave to master machine...");
				int fileCount = slaveMachineWorkspace.copyRecursiveTo(filePath, masterMachineWorkspace);
				listener.getLogger().println("QMetry for JIRA:Total "+fileCount+" result file(s) copied from slave to master machine");
				File finalResultFile = new File(masterMachineWorkspace.toURI());
				finalResultFile = new File(finalResultFile, filePath);
				return finalResultFile;
			}
			else if(Computer.currentComputer() instanceof MasterComputer) {
				File masterWorkspace = new File(build.getProject().getWorkspace().toURI());
				File resultFile = new File(masterWorkspace, filePath);
				return resultFile;
			}
			return null;
	}
}