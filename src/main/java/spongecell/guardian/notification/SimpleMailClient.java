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
		// or a more // secure account. 
		
		Email email = new SimpleEmail();
		email.setHostName("smtp.googlemail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("guardian@spongecell.com", "677561726469616e!"));
		email.setSSLOnConnect(true);
		email.setFrom("guardian@spongecell.com");
		email.setSubject("TestMail");
		email.setMsg("Greetings from your friendly neighborhood Guardian Service. Hope you are having a good day... :-)");
		email.addTo("john.brinnand@spongecell.com");
		email.send();	
	}
}
