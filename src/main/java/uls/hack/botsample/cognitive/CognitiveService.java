package uls.hack.botsample.cognitive;

import java.io.File;

import uls.hack.botsample.cognitive.ocr.OCR.OCRResult;

/** Cognitiveを使用したサービス */
public interface CognitiveService {

	/** 画像ファイル内のテキストを解析する */
	OCRResult recognizeText(File file);
	
}
