package com.qmetry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream; 

public class CreateZip
{
	public static String createZip(String filepath,String format) throws FileNotFoundException,IOException
	{	
		String extention="";
		if(format.equals("testng/xml") || format.equals("junit/xml"))
		{
			extention="xml";
		}
		else if(format.equals("qas/json") || format.equals("cucumber/json")) 
		{
			extention="json";
		}
		File f = new File(filepath+"/"+"testresult.zip");
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(f));
		File file=new File(filepath);
		File[] farray=file.listFiles();
		String filename;
		if(farray!=null)
		{	
			for(File f1:farray)
			{
				filename=f1.getName();
				if(filename.endsWith(extention))
				{
					ZipEntry e = new ZipEntry(filename);
					zos.putNextEntry(e);
					FileInputStream fis=null;
					try
					{
						fis = new FileInputStream(f1);
						byte[] bytes = new byte[1024];
						int length;
						
						while ((length = fis.read(bytes)) >= 0)
						{
							zos.write(bytes, 0, length);
						}
					}
					finally
					{
						if(fis!=null)
							fis.close();
						zos.closeEntry();
					}
				}
			}
		}
		zos.close();
		return filepath+"/"+"testresult.zip";
	}
}