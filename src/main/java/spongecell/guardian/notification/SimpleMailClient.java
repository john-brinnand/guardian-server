package spongecell.guardian.notification;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@Getter @Setter
@EnableConfigurationProperties (SimpleMailClientConfiguration.class)
public class SimpleMailClient {
	private boolean sendMail = false;
	private boolean valid = true;
	private String message;
	@Autowired SimpleMailClientConfiguration smConfig;
	
	public SimpleMailClient () {
		this.message = "Greetings from the Guardian. I hope you are having a good day.";
	}
	
	public void send () throws EmailException {
		log.info ("Sending mail.");
		Email email = new SimpleEmail();
		email.setHostName("smtp.googlemail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("guardian@spongecell.com", "677561726469616e!"));
		email.setSSLOnConnect(true);
		email.setFrom("guardian@spongecell.com");
		email.setSubject("TestMail");
		email.setMsg(message);
		email.addTo("john.brinnand@spongecell.com");
		email.send();	
	}
	
	public void send (String msg) throws EmailException {
		log.info ("Sending mail.");
		Email email = new SimpleEmail();
		email.setHostName(smConfig.getHostname());
		email.setSmtpPort(smConfig.getSmtpPort());
		email.setAuthenticator(new DefaultAuthenticator(smConfig.getUserName(), smConfig.getPwd()));
		email.setSSLOnConnect(smConfig.isSslOnConnect());
		email.setFrom(smConfig.getFrom());
		email.setSubject(smConfig.getSubject());
		email.setMsg(msg);
		for  (String consumer : Arrays.asList(smConfig.getConsumers())) {
			email.addTo(consumer);
		}
		email.send();	
	}	
}
