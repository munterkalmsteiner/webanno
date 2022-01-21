package de.tudarmstadt.ukp.clarin.webanno.ui.core.resetPassword;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class ResetPasswordApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResetPasswordApplication.class, args);
	}

}
