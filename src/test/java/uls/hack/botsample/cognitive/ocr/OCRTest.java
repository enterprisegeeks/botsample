package uls.hack.botsample.cognitive.ocr;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import uls.hack.botsample.LambdaCallback;
import uls.hack.botsample.cognitive.ocr.OCR.OCRResult;

/** ocr �ڑ��e�X�g */
@Ignore
public class OCRTest {
	
	OCR target;
	
	String key="";
	
	@Before public void prepare() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
        JacksonConverterFactory factory = JacksonConverterFactory.create(mapper);
		Retrofit retrofit = new Retrofit.Builder()
			    .baseUrl("https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/")
			    .addConverterFactory(factory)
			    .build();
		target = retrofit.create(OCR.class);
		// �L�[�̓t�@�C�����V�X�e���v���p�e�B�o�R�Ŏ擾����
		File f = new File("secret.txt");
		if(f.exists()) {
			try(FileReader r = new FileReader(f)) {

				Properties p = new Properties();
				p.load(r);
				key = p.getProperty("ocr.key");
			}
		} else {
			key = System.getProperty("ocr.key");
		}
	}
	
	/** �T���v���摜���g�p���āA���{��e�L�X�g���ǂ߂邩�e�X�g���� */
	@Test public void testReadJapaneseText() throws Exception {
		try(InputStream img = getClass().getResourceAsStream("/ocrSample.jpg")) {
			byte[] content = new byte[img.available()];
			img.read(content);
			RequestBody body = RequestBody.create(
					MediaType.parse("application/octet-stream"), content);
			OCRResult res = target.recognizeText(key, body).execute().body();
			assertThat(res.toString()).contains("�l�̋���").contains("�ē����H�ׂ���");
		}
	}

	/** �񓯊����N�G�X�g�̃e�X�g */
	@Test public void testReadJapaneseTextAsync() throws Exception {
		
		// �񓯊����s�̏ꍇ�A���N�G�X�g���I���O�Ƀe�X�g���I�����邽�߁A�ҋ@������B
		CountDownLatch latch = new CountDownLatch(1);
		
		try(InputStream img = getClass().getResourceAsStream("/ocrSample.jpg")) {
			byte[] content = new byte[img.available()];
			img.read(content);
			RequestBody body = RequestBody.create(
					MediaType.parse("application/octet-stream"), content);
			
			Call<OCRResult> async = target.recognizeText(key, body);
			async.enqueue(LambdaCallback.callback((call, resp)->{
				System.out.println("response async");
				OCRResult res = resp.body();
				assertThat(res.toString()).contains("�l�̋���").contains("�ē����H�ׂ���");
				latch.countDown();
			}));
		}
		latch.await(10, TimeUnit.SECONDS);
	}
	
}