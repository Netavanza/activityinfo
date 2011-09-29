/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.common.grid;

import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.page.common.grid.GridPresenter.SiteGridPresenter;
import org.sigmah.shared.dto.SiteDTO;

import com.extjs.gxt.ui.client.data.ModelData;

public interface GridView<P extends GridPresenter, M extends ModelData> {
    public void setActionEnabled(String actionId, boolean enabled);
    public void confirmDeleteSelected(ConfirmCallback callback);
    public M getSelection();
    public AsyncMonitor getDeletingMonitor();
    public AsyncMonitor getSavingMonitor();
    
    public interface SiteGridView extends GridView<SiteGridPresenter, SiteDTO> {
    	
    }
}
