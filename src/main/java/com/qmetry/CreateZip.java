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
import java.util.zip.ZipFile;

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

	public static final FileFilter PNG_FILE_FILTER = new FileFilter()
	{
		public boolean accept(File file)
		{
			return file.isDirectory() || file.getName().toLowerCase().endsWith(".png");
		}
	};
	
	public static String createZip(String sourceDir, String formats, boolean attachFile) throws IOException,FileNotFoundException 
	{
		String zipDir=sourceDir+"/"+"testresult.zip";
		zipDirectory(sourceDir, zipDir, formats,attachFile);	
		return zipDir;
	}

	public static void zipDirectory(String sourceDir, String zipfile, String formats, boolean attachFile) throws IOException {
		String extention="";
		if(formats.equals("junit/xml") || formats.equals("testng/xml") || formats.equals("hpuft/xml")) {
			extention="xml";
		}else if(formats.equals("qas/json") || formats.equals("cucumber/json") || formats.equals("specflow/json")){
			extention="json";
		}
		File dir = new File(sourceDir);
		File zipFile = new File(zipfile);
		FileOutputStream fout = new FileOutputStream(zipFile,false);
		ZipOutputStream zout = new ZipOutputStream(fout);
		zipSubDirectory("", dir, zout,extention);
		if(attachFile && formats.equals("qas/json"))
		{
			File imageDir = new File(sourceDir + "/img");
			String imageExtention="png";
			zipSubDirectory("img/",imageDir,zout,imageExtention);
		}
		zout.close();
		ZipFile zf = new ZipFile(zipfile);
		int size = zf.size();
		if(size == 0)
		{
			throw new FileNotFoundException("cannot find files of proper format in directory : "+sourceDir);
		}
	}

	
	private static void zipSubDirectory(String basePath, File dir, ZipOutputStream zout,String extention) throws IOException 
	{
		byte[] buffer = new byte[1024];
		File[] files=null;
		if(extention.equals("xml"))
			files = dir.listFiles(XML_FILE_FILTER);
		else if(extention.equals("json"))
			files = dir.listFiles(JSON_FILE_FILTER);
		else if(extention.equals("png"))
			files = dir.listFiles(PNG_FILE_FILTER);
		if(files!=null)
		{
			for (File file : files) 
			{
				if (file.isDirectory()) 
				{
					String path = basePath + file.getName() + "/";
					zipSubDirectory(path, file, zout,extention);
				}
				else 
				{
					zout.putNextEntry(new ZipEntry(basePath + file.getName()));
					FileInputStream fin = null;
					try
					{
						fin = new FileInputStream(file);
						int length;
						while ((length = fin.read(buffer)) >= 0) 
						{
							zout.write(buffer, 0, length);
						}
							
					}
					finally
					{
						if(fin!=null)
						fin.close();
						zout.closeEntry();
					}
				}
			}
		}
	}
}