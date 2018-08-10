package org.assistindia.convene.utils;




import org.assistindia.convene.BeenClass.AppBean;

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
