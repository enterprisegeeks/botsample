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

/** Computer vision OCR �Ƃ̐ڑ����s��Retrofit Interface�ł��B */
public interface OCR {
	  @POST("ocr") // �x�[�XURL����̑��΃p�X
	  @Headers({"Content-type: application/octet-stream", "Accept: application/json"}) //�Œ�w�b�_
	  Call<OCRResult> recognizeText(
			  @Header("Ocp-Apim-Subscription-Key")String subscrption, // ���I�w�b�_,�L�[
			  @Body RequestBody binaryImage); //���N�G�X�g�{�f�B�B
	  
	  /** 
	   * OCR����.
	   * 
	   * �e�L�X�g�𔻒肵���̈�̏�񂪁A�傫���̈�(region)->1�s�P�ʂ̗̈�(line)->�P��P��(word)�̗̈� �Ńl�X�g���Ă���B
	   * ���̌��ʂ��g�p���āA�摜���̂ǂ����e�L�X�g�Ȃ̂����������Ƃ��ł��邪�A
	   * ����͌Ăяo�����ŉ摜���H����K�v������B
	   */
	  public static class OCRResult {
		  /** ���� */
		  public String language; 

		  /** �e�L�X�g�̌X��*/
		  public double textAngle;
		  /** ���� */
		  public String orientation;

		  /** �e�L�X�g�̔���̈� */
		  public List<Region> regions = new ArrayList<>();
		  @Override
		  public String toString(){return regions.stream().map(Region::toString).collect(Collectors.joining());}
	  }
	  /** �̈� */
	  public static class Region{
		  /** �e�L�X�g�𔻒肵���l�p�`�̈�ɂ��� x, y, ��,���� �̏��œ����Ă���B */
		  public String boundingBox;
		  /** �̈���̍s�P�ʂ̍ו��������̈�*/
		  public List<Line> lines = new ArrayList<>();
		  @Override
		  public String toString(){return lines.stream().map(Line::toString).collect(Collectors.joining());}

	  }
	  /** �s */
	  public static class Line{
		  /** �e�L�X�g�𔻒肵���l�p�`�̈�ɂ��� x, y, ��,���� �̏��œ����Ă���B */
		  public String boundingBox;
		  /** �P��̈�̈ꗗ */
		  public List<Word> words = new ArrayList<>();

		  @Override
		  public String toString(){return words.stream().map(Word::toString).collect(Collectors.joining());}
	  }
	  /** �P�� */
	  public static class Word{

		  /** �P��𔻒肵���l�p�`�̈�ɂ��� x, y, ��,���� �̏��œ����Ă���B */
		  public String boundingBox;
		  /** �P�� */
		  public String text;

		  @Override 
		  public String toString(){return text;}
	  }
}

