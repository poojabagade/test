package com.ticketservice.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MessageProperties {
	public final static Properties msgProp = getProperties();

	public static Properties getProperties() {
		Properties msgProp = new Properties();
		InputStream input = null;

		try {

			input = MessageProperties.class.getClassLoader().getResourceAsStream("messages.properties");

			// load a properties file
			msgProp.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return msgProp;
	}
}
