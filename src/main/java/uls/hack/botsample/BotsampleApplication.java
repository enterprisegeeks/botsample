package uls.hack.botsample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import uls.hack.botsample.cognitive.luis.Luis;
import uls.hack.botsample.cognitive.ocr.OCR;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "uls.hack.botsample"})
public class BotsampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotsampleApplication.class, args);
	}
	
	@Bean
	public OCR ocr(@Value("${ocr.endpoint}") String endpoint, @Autowired ObjectMapper mapper) {

        JacksonConverterFactory factory = JacksonConverterFactory.create(mapper);
		Retrofit retrofit = new Retrofit.Builder()
			    .baseUrl(endpoint)
			    .addConverterFactory(factory)
			    .build();
		return retrofit.create(OCR.class);
	}

	@Bean
	public Luis luis(@Value("${luis.endpoint}") String endpoint) {

        JacksonConverterFactory factory = JacksonConverterFactory.create(new ObjectMapper());
		Retrofit retrofit = new Retrofit.Builder()
			    .baseUrl(endpoint)
			    .addConverterFactory(factory)
			    .build();
		return retrofit.create(Luis.class);
	}
}
