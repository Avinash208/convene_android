package org.mahiti.convenemis.utils;




import org.mahiti.convenemis.BeenClass.AppBean;

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
