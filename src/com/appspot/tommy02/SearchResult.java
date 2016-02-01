package com.appspot.tommy02;

import java.util.List;

public class SearchResult {

	private List list;
	private int allNumber;
	private int allPages;

	public SearchResult(List list, int allNumber, int allPages){

		this.list = list;
		this.allNumber = allNumber;
		this.allPages = allPages;

	}

	/**
	 * listを取得します。
	 * @return list
	 */
	public List getList() {
	    return list;
	}
	/**
	 * listを設定します。
	 * @param list list
	 */
	public void setList(List list) {
	    this.list = list;
	}
	/**
	 * allNumberを取得します。
	 * @return allNumber
	 */
	public int getAllNumber() {
	    return allNumber;
	}
	/**
	 * allNumberを設定します。
	 * @param allNumber allNumber
	 */
	public void setAllNumber(int allNumber) {
	    this.allNumber = allNumber;
	}

	/**
	 * allPagesを取得します。
	 * @return allPages
	 */
	public int getAllPages() {
	    return allPages;
	}

	/**
	 * allPagesを設定します。
	 * @param allPages allPages
	 */
	public void setAllPages(int allPages) {
	    this.allPages = allPages;
	}

}
