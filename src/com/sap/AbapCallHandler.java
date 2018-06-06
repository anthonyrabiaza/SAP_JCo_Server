package com.sap;

import java.io.File;

import javax.ws.rs.ProcessingException;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerFunctionHandler;

public class AbapCallHandler implements JCoServerFunctionHandler {
	
	private String functionName;
	private File directory;
	
	/**
	 * This handler only supports one function with name {@code Z_SAMPLE_ABAP_CONNECTOR_CALL}.
	 */
	//public static final String FUNCTION_NAME = "Z_SAMPLE_ABAP_CONNECTOR_CALL";
	
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	public void setDirectory(String directoryName) {
		directory = new File(directoryName);
	    if (! directory.exists()){
	        directory.mkdirs();
	    }
	}
	
	private static Logger logger = Logger.getLogger(AbapCallHandler.class);
	
	private void printRequestInformation(JCoServerContext serverCtx, JCoFunction function) {
		logger.info("----------------------------------------------------------------");
        logger.info("call              : " + function.getName());
        logger.info("ConnectionId      : " + serverCtx.getConnectionID());
        logger.info("SessionId         : " + serverCtx.getSessionID());
        logger.info("TID               : " + serverCtx.getTID());
        logger.info("repository name   : " + serverCtx.getRepository().getName());
        logger.info("is in transaction : " + serverCtx.isInTransaction());
        logger.info("is stateful       : " + serverCtx.isStatefulSession());
        logger.info("----------------------------------------------------------------");
        logger.info("gwhost: " + serverCtx.getServer().getGatewayHost());
        logger.info("gwserv: " + serverCtx.getServer().getGatewayService());
        logger.info("progid: " + serverCtx.getServer().getProgramID());
        logger.info("----------------------------------------------------------------");
        logger.info("attributes  : ");
        logger.info(serverCtx.getConnectionAttributes().toString());
        logger.info("----------------------------------------------------------------");
	}

	public void handleRequest(JCoServerContext serverCtx, JCoFunction function) {
		
		if(functionName==null) {
			logger.error("Function is not properly initialized");
			return;
		}
		
		// Check if the called function is the supported one.
		if(!function.getName().equals(functionName)) {
			logger.error("Function '"+function.getName()+"' is no supported to be handled!");
			return;
		}
        printRequestInformation(serverCtx, function);
        
        logger.info("Content received:" + functionCall2message(function));

//        // Get the URI provided from Abap.
//        String uri = function.getImportParameterList().getString("IV_URI");
//        
//		HttpCaller main = new HttpCaller();
//		main.initializeSslContext();
//		main.initializeClient();
//		String payload = null;
//		try {
//			payload = main.invokeGet(uri);
//		} catch(ProcessingException pe) {
//			// Provide the exception as payload.
//			payload = pe.getMessage();
//		}
//		// Provide the payload as exporting parameter.
//        function.getExportParameterList().setValue("EV_RESPONSE_PAYLOAD", payload);
    }
	
	private String functionCall2message(JCoFunction function) {
		JCoParameterList input = function.getImportParameterList();
		String result;
		try {
			result = input.getString(0);
			logger.info("1st try: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		try {
			result = "<request function=\""+function.getName()+"\">";

			JCoParameterList tables = function.getTableParameterList();
			
			if (input!=null) {
				result+=input.toXML();
			}
			if (tables!=null) {
				result+=tables.toXML();
			}
			result+="</request>";
			logger.info("2nd try: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return "";
	}
}