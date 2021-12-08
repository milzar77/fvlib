package com.blogspot.fravalle.util;

/**
 * @author antares
 */
public interface UtilConstants {

	public final String CTX_PARAM_DB_FLOW_CONFIG = "config_flow";
	public final String CTX_PARAM_DB_FLOW_CONTENT = "content_flow";
	public final String CTX_PARAM_DB_FLOW_LOG = "log_flow";
	public final String CTX_PARAM_DB_SCHEMA_LOG = "schema";
	public final String CTX_PARAM_DB_POPULATION_LOG = "population";
	public final String CTX_PARAM_DB_MAXSTAT_LOG = "max_statements";
	public final String CTX_PARAM_DB_STATDELIM_LOG = "statement_delimiter";
	
	public final String CTX_PARAM_XML_ARCHIVE_PATH = "xml_archive_path";
	public final String CTX_PARAM_XSL_ARCHIVE_PATH = "xsl_base_path";
	
	/* 2DO: da rinominare in CTX_BEAN_DB_FLOW */
	public final String CTX_BEAN_DB_FLOW_CONTENT = "connection";
	public final String CTX_BEAN_DB_FLOW = "connection";
	
	
	
	public final int CONSOLE_LOG_LABEL_START = 0, CONSOLE_LOG_LABEL_END = 1; 
	public final String[] CONSOLE_LOG_INSERT = {"executing error log insert: [","]"};
	public final String[] CONSOLE_DEF_INSERT = {"executing default insert: [","]"};
	public final String[] CONSOLE_LOG_GENERIC = {"<[","]>"};
	
	public final String DB_SEED_SEPARATOR = ".";
	
	// public void getConfigParam(final String paramName);
	
	// request document structure
	public final String PAR_KEY = "key";
	public final String PAR_KEY_SEPARATOR = ",";
	
	public final String PAR_XML_EXPORT_CONVERSION = "conversionExport";
	public final String PAR_DB_EXPORT = "exportDb";
	
}
