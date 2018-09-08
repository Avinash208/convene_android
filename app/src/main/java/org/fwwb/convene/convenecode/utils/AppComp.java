package org.fwwb.convene.convenecode.utils;




import org.fwwb.convene.convenecode.BeenClass.AppBean;

import java.util.Comparator;

public class AppComp implements Comparator<AppBean>
{

	@Override
	public int compare(AppBean appbean1, AppBean appbean2)
	{
		Double first = appbean1.getTotaltraffic() ;
		Double second = appbean2.getTotaltraffic() ;
		return second.compareTo(first);
	}
}
