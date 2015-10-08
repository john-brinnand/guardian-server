package spongecell.guardian.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

@Slf4j
@Getter @Setter
public class SimpleMailClient {
	private boolean sendMail = false;
	private boolean valid = true;
	
	public void send () throws EmailException {
		// NOTE: put these values into a configuration file
		// and let Spring configure the client.
		//***************************************************
		log.info ("Sending mail.");
		// Turning off till we get a group account with general access, 
		// or a more secure account. 
		
//		Email email = new SimpleEmail();
//		email.setHostName("smtp.googlemail.com");
//		email.setSmtpPort(465);
//		email.setAuthenticator(new DefaultAuthenticator("username", "password"));
//		email.setSSLOnConnect(true);
//		email.setFrom("joe@gmail.com");
//		email.setSubject("TestMail");
//		email.setMsg("This is a test mail ... :-)");
//		email.addTo("joe@gmail.com");
//		email.send();	
	}
}
