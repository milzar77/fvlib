/*
 * PageBean.java - jsptags (jsptags.jar)
 * Copyright (C) 2004
 * Source file created on 15-ott-2004
 * Author: Francesco Valle <fv@weev.it>
 * http://www.weev.it
 *
 * [PageBean]
 * 2DO:
 *
 */

package com.blogspot.fravalle.j2ee.web.beans;


import java.util.Date;


/**
 * @author antares
 */
public class PageBean extends BaseBean {

	private int	pageId;
	private String	pageTitle;
	private String	pageSubtitle;
	
	// --> select
	private Date	pageDatetime;
	
	// convertire in StringBuffer --> textarea
	private String	pageContent;
	private String	pageAuthor;
	private String	pageDirection;
	private String	pageFlow;
	

	public PageBean() {
		this.pageId = super.dataSeed();
		// fill(pageId)
	}

	public PageBean(int pageId) {
		this.pageId = pageId;
		this.pageTitle = "Titolo della pagina";
		this.pageAuthor = "Francesco Valle";
		this.pageDirection = DIRECTION_FORWARD;
		this.pageFlow = "0";
	}
	
	/**
	 * @return Returns the pageContent.
	 */

	public String getPageContent() {
		return pageContent;
	}

	/**
	 * @param parPageBody
	 *            The pageContent to set.
	 */
	public void setPageContent(String parPageBody) {
		pageContent = parPageBody;
	}

	/**
	 * @return Returns the pageDatetime.
	 */
	public Date getPageDatetime() {
		return pageDatetime;
	}

	/**
	 * @param parPageDatetime
	 *            The pageDatetime to set.
	 */
	public void setPageDatetime(Date parPageDatetime) {
		pageDatetime = parPageDatetime;
	}

	/**
	 * @return Returns the pageSubtitle.
	 */
	public String getPageSubtitle() {
		return pageSubtitle;
	}

	/**
	 * @param parPageSubtitle
	 *            The pageSubtitle to set.
	 */
	public void setPageSubtitle(String parPageSubtitle) {
		pageSubtitle = parPageSubtitle;
	}

	/**
	 * @return Returns the pageId.
	 */
	public int getPageId() {
		return pageId;
	}

	/**
	 * @param parPageId
	 *            The pageId to set.
	 */
	public void setPageId(int parPageId) {
		pageId = parPageId;
	}

	/**
	 * @return Returns the pageAuthor.
	 */
	public String getPageAuthor() {
		return pageAuthor;
	}

	/**
	 * @param parPageAuthor
	 *            The pageAuthor to set.
	 */
	public void setPageAuthor(String parPageAuthor) {
		pageAuthor = parPageAuthor;
	}

	/**
	 * @return Returns the pageDirection.
	 */
	public String getPageDirection() {
		return pageDirection;
	}

	/**
	 * @param parPageDirection
	 *            The pageDirection to set.
	 */
	public void setPageDirection(String parPageDirection) {
		pageDirection = parPageDirection;
	}

	/**
	 * @return Returns the pageFlow.
	 */
	public String getPageFlow() {
		return pageFlow;
	}

	/**
	 * @param parPageFlow
	 *            The pageFlow to set.
	 */
	public void setPageFlow(String parPageFlow) {
		pageFlow = parPageFlow;
	}

	/**
	 * @return Returns the pageTitle.
	 */
	public String getPageTitle() {
		return pageTitle;
	}

	/**
	 * @param parPageTitle
	 *            The pageTitle to set.
	 */
	public void setPageTitle(String parPageTitle) {
		pageTitle = parPageTitle;
	}

	public final static void main(String[] args) {
		new PageBean(1).buildControls();
	}
	
}