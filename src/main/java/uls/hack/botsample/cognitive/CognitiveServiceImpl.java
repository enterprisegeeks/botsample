package uls.hack.botsample.cognitive;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import uls.hack.botsample.cognitive.ocr.OCR;
import uls.hack.botsample.cognitive.ocr.OCR.OCRResult;

@Service
public class CognitiveServiceImpl implements CognitiveService {

	@Autowired private OCR ocr;
	
	@Value("${ocr.subscription}") private String key;
	
	@Override
	public OCRResult recognizeText(File file) {
		RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"),
				file);
		
		try {
			return ocr.recognizeText(key, body).execute().body();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
