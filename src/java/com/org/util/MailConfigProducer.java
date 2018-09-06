package com.org.util;

import com.org.factories.Util;
import com.outjected.email.api.SessionConfig;
import com.outjected.email.impl.SimpleMailConfig;
import java.io.IOException;

public class MailConfigProducer {


	public SessionConfig getMailConfig(String vruc) throws IOException{
		
		SimpleMailConfig config = new SimpleMailConfig();
		config.setServerHost(Util.getMailHost(vruc));
		config.setServerPort(Integer.parseInt(Util.getMailPort(vruc)));
		config.setEnableSsl(Boolean.parseBoolean(Util.getMailSsl(vruc)));
		config.setAuth(Boolean.parseBoolean(Util.getMailAuth(vruc)));
		config.setUsername(Util.getMailUser(vruc));
		config.setPassword(Util.getMailPass(vruc));
		return config;
	}
	
}
