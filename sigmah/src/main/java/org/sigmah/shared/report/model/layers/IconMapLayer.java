/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.report.model.layers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.google.gwt.resources.client.ImageResource;

/*
 * Displays an icon on a point of interest
 */
public class IconMapLayer extends AbstractMapLayer {
    
	private List<Integer> activityIds = new ArrayList<Integer>(0);
    private String icon;

    public IconMapLayer() {
    }

    @XmlElement(name="activity")
    @XmlElementWrapper(name="activities")
    public List<Integer> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<Integer> activityIds) {
        this.activityIds = activityIds;
    }

    @XmlElement
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void addActivityId(int activityId) {
        activityIds.add(activityId);
    }
    
    public static ImageResource getIconFromString(String iconName)
    {
    	if (iconName == "Water")
    		return MapIcons.INSTANCE.water();
    	if (iconName == "Fire")
    		return MapIcons.INSTANCE.fire();
    	if (iconName == "Water")
    		return MapIcons.INSTANCE.doctor();
    	if (iconName == "Food")
    		return MapIcons.INSTANCE.food();
    	
    	return null;
    }

	@Override
	public boolean supportsMultipleIndicators() {
		return false;
	}

	@Override
	public String getName() {
		return "Icon";
	}

	@Override
	public String getInternationalizedName() {
		return "Icon";
	}
}