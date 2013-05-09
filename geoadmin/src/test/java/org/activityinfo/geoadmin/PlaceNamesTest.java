package org.activityinfo.geoadmin;

import org.junit.Test;

public class PlaceNamesTest {

	@Test
	public void tomasina() {

		System.out.println(PlaceNames.similiarity("TOAMASINA I", "TOAMASINA II"));
		System.out.println(PlaceNames.similiarity("TOAMASINA I", "TOAMASINA I"));
	
	}
}
