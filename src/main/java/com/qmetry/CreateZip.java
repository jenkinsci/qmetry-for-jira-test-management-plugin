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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream; 

public class CreateZip
{

	public static final FileFilter XML_FILE_FILTER = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().toLowerCase().endsWith(".xml");
		}
	};
	
	public static final FileFilter JSON_FILE_FILTER = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory() || file.getName().toLowerCase().endsWith(".json");
		}
	};

	
	public static String createZip(String sourceDir, String formats) throws IOException,FileNotFoundException {
		//System.out.println("\n\nCreating zip file.........");
		String resultDir="";
		if(formats.equals("qas/json"))
		{
			File testDir=lastFileModified(sourceDir);
			//System.out.println("Test directory");
			//System.out.println(testDir.getPath());
			resultDir=testDir.getPath();
		}
		else
		{
			resultDir=sourceDir;
		}
		
		String zipDir=resultDir+"/"+"testresult.zip";
		//System.out.println("\nZipFile path:"+zipDir);
		zipDirectory(resultDir, zipDir, formats);
		
		return zipDir;
	}

	public static File lastFileModified(String dir) {
		File fl = new File(dir);
		File[] files = fl.listFiles();
		long lastMod = Long.MIN_VALUE;
		File choice = null;
		if(files!=null)
		{
			for (File file : files) {
				if(file.isDirectory() && !(file.getName()).equals("surefire"))
				{
					//System.out.println(file.getName()+":"+file.lastModified());
					if (file.lastModified() > lastMod) {
						choice = file;
						lastMod = file.lastModified();
					}
				}
			}
		}
		return choice;
	}
	
	 

	public static void zipDirectory(String sourceDir, String zipfile, String formats) throws IOException {
		/*System.out.println("\n\nDebug check 2 : "+sourceDir);
		System.out.println("\n\nDebug check 3 : "+zipfile);
		System.out.println("\n\nDebug check 4 : "+formats);*/
		String extention="";
		if(formats.equals("junit/xml") || formats.equals("testng/xml") || formats.equals("hpuft/xml")) {
			extention="xml";
		}else if(formats.equals("qas/json") || formats.equals("cucumber/json")){
			extention="json";
		}
		//System.out.println("\n\nDebug check 5 : "+extention);
		File dir = new File(sourceDir);
		File zipFile = new File(zipfile);
		//System.out.println("\n\nAbsolute zippath:"+zipFile.getAbsolutePath());
		FileOutputStream fout = new FileOutputStream(zipFile,false);
		ZipOutputStream zout = new ZipOutputStream(fout);
		//System.out.println("\n\nDebug check 6 : "+(fout!=null));
		//System.out.println("\n\nDebug check 7 : "+(zout!=null));
		zipSubDirectory("", dir, zout,extention);
		zout.close();
	}

	
	private static void zipSubDirectory(String basePath, File dir, ZipOutputStream zout,String extention) throws IOException 
	{
		//try
		//{
		//System.out.println("\n\nzipSubDirectory basepath : "+basePath);
		byte[] buffer = new byte[1024];
		File[] files=null;
		if(extention.equals("xml"))
			files = dir.listFiles(XML_FILE_FILTER);
		else
			files = dir.listFiles(JSON_FILE_FILTER);
		if(files!=null)
		{
			for (File file : files) 
			{
				//System.out.println("\n\nzipSubDirectory filefound : "+file.getAbsolutePath());
				if (file.isDirectory()) 
				{
					String path = basePath + file.getName() + "/";
					//zout.putNextEntry(new ZipEntry(path));
					zipSubDirectory(path, file, zout,extention);
					//zout.closeEntry();
				}
				else 
				{
					//if(file.isFile() && file.getName().endsWith(extention))
					//{
						zout.putNextEntry(new ZipEntry(basePath + file.getName()));
						FileInputStream fin = null;
						try{
							fin = new FileInputStream(file);
							//System.out.println("\n\nFile Name:"+file.getAbsolutePath());
							int length;
							while ((length = fin.read(buffer)) >= 0) 
							{
								//System.out.println("\nreading file:"+file.getName());
								zout.write(buffer, 0, length);
							}
							
						}
						/*catch(Exception e)
						{
							System.out.println("\n\nERROR DEBUG : "  + e);
						}*/
						finally
						{
							if(fin!=null)
							 fin.close();
							zout.closeEntry();
						}
					//}
				}
			}
		}
		/*}catch(Exception e)
		{
			System.out.println("\n\nERROR DEBUG : " + e.toString());
		}*/
	}
}