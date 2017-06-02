package uls.hack.botsample.cognitive;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;
import uls.hack.botsample.cognitive.luis.Luis;
import uls.hack.botsample.cognitive.luis.Luis.LuisResult;
import uls.hack.botsample.cognitive.ocr.OCR;
import uls.hack.botsample.cognitive.ocr.OCR.OCRResult;

@Service
public class CognitiveServiceImpl implements CognitiveService {

	private static final Logger logger = LoggerFactory.getLogger(CognitiveServiceImpl.class);
	
	@Autowired private OCR ocr;
	@Autowired private Luis luis;
	
	@Value("${ocr.subscription}") private String ocrKey;
	@Value("${luis.subscription}") private String luisKey;
	@Value("${luis.appid}") private String luisAppId;
	
	@Override
	public OCRResult recognizeText(File file) {
		RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"),
				file);
		
		try {

			logger.info("OCR Request..");
			return ocr.recognizeText(ocrKey, body).execute().body();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	@Override
	public LuisResult getIntent(String query) {
		
		try {
			logger.info("Luis Request.." + query);
			Response<LuisResult> res = luis.query(luisAppId, luisKey, query).execute();
			if (res.isSuccessful()) {
				LuisResult r = res.body();
				logger.info("LUIS success " + r.topScoringIntent);
				return r;
			} else {
				logger.error("LUIS fail");
				throw new RuntimeException(res.errorBody().string());
			} 
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
