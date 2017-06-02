package uls.hack.botsample.cognitive.ocr;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/** Computer vision OCR との接続を行うRetrofit Interfaceです。 */
public interface OCR {
	  @POST("ocr") // ベースURLからの相対パス
	  @Headers({"Content-type: application/octet-stream", "Accept: application/json"}) //固定ヘッダ
	  Call<OCRResult> recognizeText(
			  @Header("Ocp-Apim-Subscription-Key")String subscrption, // 動的ヘッダ,キー
			  @Body RequestBody binaryImage); //リクエストボディ。
	  
	  /** 
	   * OCR結果.
	   * 
	   * テキストを判定した領域の情報が、大きい領域(region)->1行単位の領域(line)->単語単位(word)の領域 でネストしている。
	   * この結果を使用して、画像内のどこがテキストなのかを示すことができるが、
	   * それは呼び出し側で画像加工する必要がある。
	   */
	  public static class OCRResult {
		  /** 言語 */
		  public String language; 

		  /** テキストの傾き*/
		  public double textAngle;
		  /** 向き */
		  public String orientation;

		  /** テキストの判定領域 */
		  public List<Region> regions = new ArrayList<>();
		  @Override
		  public String toString(){return regions.stream().map(Region::toString).collect(Collectors.joining());}
	  }
	  /** 領域 */
	  public static class Region{
		  /** テキストを判定した四角形領域について x, y, 幅,高さ の順で入っている。 */
		  public String boundingBox;
		  /** 領域内の行単位の細分化した領域*/
		  public List<Line> lines = new ArrayList<>();
		  @Override
		  public String toString(){return lines.stream().map(Line::toString).collect(Collectors.joining());}

	  }
	  /** 行 */
	  public static class Line{
		  /** テキストを判定した四角形領域について x, y, 幅,高さ の順で入っている。 */
		  public String boundingBox;
		  /** 単語領域の一覧 */
		  public List<Word> words = new ArrayList<>();

		  @Override
		  public String toString(){return words.stream().map(Word::toString).collect(Collectors.joining());}
	  }
	  /** 単語 */
	  public static class Word{

		  /** 単語を判定した四角形領域について x, y, 幅,高さ の順で入っている。 */
		  public String boundingBox;
		  /** 単語 */
		  public String text;

		  @Override 
		  public String toString(){return text;}
	  }
}

