package uls.hack.botsample.cognitive.luis;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Computer vision OCR Ç∆ÇÃê⁄ë±ÇçsÇ§Retrofit InterfaceÇ≈Ç∑ÅB */
public interface Luis {
	  @GET("{appid}?verbose=true&timezoneOffset=0") //?subscription-key=xxx&verbose=true&timezoneOffset=0&q=xxx
	  @Headers({ "Accept: application/json"})
	  Call<LuisResult> query(
			  @Path("appid") String appId,
			  @Query("subscription-key")String key, 
			  @Query("q") String query); 
	  
	  /** 
	   * Luisåãâ .
	   */
	  public static class LuisResult {
		  public String query;

		  public Intent topScoringIntent;
		  
		  public List<Intent> intents = new ArrayList<>();

		  public List<Entity> entities = new ArrayList<>();
		  
		  
	  }
	  public static class Intent{
		  public String intent;
		  public double score;
		  @Override
		  public String toString() {
			  return intent+"("+score+")";
		  }
	  }
	  public static class Entity{
		  public String entity;
		  public String type;
		  public int startIndex;
		  public int endIndex;
		  public double score;
	  }
}

