package org.sigmah.shared.search;

import java.util.List;

import org.sigmah.shared.report.model.DimensionType;

import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Searcher<M> {
	public void search(String testQuery, final AsyncCallback<List<Integer>> callback, SqlTransaction tx);

	public DimensionType getDimensionType();
}