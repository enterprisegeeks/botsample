package uls.hack.botsample.cognitive.luis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import uls.hack.botsample.cognitive.luis.Luis.LuisResult;

/** ocr �ڑ��e�X�g */
@Ignore
public class LuisTest {
	
	Luis target;
	
	String key="";
	String appId="";
	
	@Before public void prepare() throws IOException {
		
		// �L�[�̓t�@�C�����V�X�e���v���p�e�B�o�R�Ŏ擾����
		File f = new File("secret.txt");
		String url;
		if(f.exists()) {
			try(FileReader r = new FileReader(f)) {

				Properties p = new Properties();
				p.load(r);
				key = p.getProperty("luis.subscription");
				url = p.getProperty("luis.endpoint");
				appId = p.getProperty("luis.appid");
			}
		} else {
			throw new RuntimeException();
		}
		
		ObjectMapper mapper = new ObjectMapper();
        JacksonConverterFactory factory = JacksonConverterFactory.create(mapper);
		Retrofit retrofit = new Retrofit.Builder()
			    .baseUrl(url)
			    .addConverterFactory(factory)
			    .build();
		target = retrofit.create(Luis.class);
	}
	
	/** �G���e�B�e�B�F���e�X�g */
	@Test public void testLuis() throws Exception {
		Response<LuisResult> res = target.query(appId, key, "���[�����H�ׂ���").execute();
		
		if(res.isSuccessful()) {
			LuisResult r = res.body();
			System.out.println(r.topScoringIntent.intent);
			System.out.println(r.entities.get(0).entity);
			assertThat(r.topScoringIntent.intent).isEqualTo("���Ȃ��󂢂�");
			assertThat(r.entities.get(0).entity).isEqualTo("���[����");
		} else {
			System.out.println(res.errorBody().string());
			System.out.println(res.message());
			fail(res.errorBody().string());
		}
		
	}
	
}