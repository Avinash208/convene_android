package org.fwwb.convene.convenecode.BeenClass;

import android.graphics.drawable.Drawable;

public class AppBean
{

	private String appname;
    private double totaltraffic;
	private Drawable icon;

	public void setPercentage(int percentage)
	{
		int percentage1 = percentage;
	}

	public void setPackagename(String packagename)
	{
		String packagename1 = packagename;
	}

	public void setAppuid(int appuid)
	{
        int appuid1 = appuid;
	}

	public String getAppname()
	{
		return appname;
	}

	public void setAppname(String appname)
	{
		this.appname = appname;
	}

	public double getTotaltraffic()
	{
		return totaltraffic;
	}

	public void setTotaltraffic(double totaltraffic)
	{
		this.totaltraffic = totaltraffic;
	}


}
