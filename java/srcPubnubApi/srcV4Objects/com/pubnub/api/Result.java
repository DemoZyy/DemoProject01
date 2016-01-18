package com.pubnub.api;

public class Result {
	protected ResultType type;
	protected int code;
	protected OperationType operation;
	protected Config config;
	protected String connectionId;
	protected String clientRequest;
	protected String serverResponse;
	
	
	public ResultType getType() {
		return type;
	}


	void setType(ResultType type) {
		this.type = type;
	}


	public int getCode() {
		return code;
	}


	void setCode(int code) {
		this.code = code;
	}


	public OperationType getOperation() {
		return operation;
	}


	void setOperation(OperationType operation) {
		this.operation = operation;
	}


	public Config getConfig() {
		return config;
	}


	void setConfig(Config config) {
		this.config = config;
	}


	public String getConnectionId() {
		return connectionId;
	}


	void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}


	public String getClientRequest() {
		return clientRequest;
	}


	void setClientRequest(String clientRequest) {
		this.clientRequest = clientRequest;
	}


	public String getServerResponse() {
		return serverResponse;
	}


	void setServerResponse(String serverResponse) {
		this.serverResponse = serverResponse;
	}


	HttpRequest getHreq() {
		return hreq;
	}


	void setHreq(HttpRequest hreq) {
		this.hreq = hreq;
	}


	PubnubCore getPubnub() {
		return pubnub;
	}


	void setPubnub(PubnubCore pubnub) {
		this.pubnub = pubnub;
	}
	
	public Result() {
		config = new Config();
	}
	
	HttpRequest hreq;
	PubnubCore pubnub;

	
	public String toString() {
		String s = "";
		
		s = s + "Code: " + code + "\n";
		s = s + "Result Type: " + type + "\n";		
		s = s + "Operation Type: " + operation + "\n";
		s = s + "Request: " + clientRequest + "\n";
		s = s + "Response: " + serverResponse + "\n";
		s = s + config + "\n";

		return s;
	}
}
