package com.org.util;

import java.io.Serializable;


import com.outjected.email.api.MailMessage;
import com.outjected.email.api.SessionConfig;
import com.outjected.email.impl.MailMessageImpl;
import java.io.IOException;


public class Mailer implements Serializable{

	private static final long serialVersionUID = 1L;

	private SessionConfig sessionConfig;
	
	public MailMessage nuevoEmail(String vruc) throws IOException{
            MailConfigProducer mcp = new MailConfigProducer();
            sessionConfig = mcp.getMailConfig(vruc);
		return new MailMessageImpl(this.sessionConfig);
	}
	
}
