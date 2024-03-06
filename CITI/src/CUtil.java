package ksnet.sap;
import java.io.*;
import java.net.*;
import java.util.*;

class CUtil
{
	static String config_file = null;
	static long lastModified  = 0;

	public static Properties props = new Properties();

	public static synchronized void setConfig(String fpath)
	{
		if (!new File(fpath).exists()) throw new IllegalArgumentException("Cfg File Path Error=["+fpath+"]");
		config_file = fpath;
	}

	public static synchronized boolean isNew()
	{
		try
		{
			File f = new File(config_file);
			long m_lastModified = f.lastModified();

			if (m_lastModified > lastModified + 2000)
			{
				props.load(new FileInputStream(f));

				lastModified = m_lastModified;

				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static String get(String kStr)
	{
		isNew();
		return props.getProperty(kStr);
	}
}
