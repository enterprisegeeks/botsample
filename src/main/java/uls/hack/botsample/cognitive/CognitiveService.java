package uls.hack.botsample.cognitive;

import java.io.File;

import uls.hack.botsample.cognitive.luis.Luis.LuisResult;
import uls.hack.botsample.cognitive.ocr.OCR.OCRResult;

/** Cognitive���g�p�����T�[�r�X */
public interface CognitiveService {

	/** �摜�t�@�C�����̃e�L�X�g����͂��� */
	OCRResult recognizeText(File file);
	
	/** �Ӑ}���͂��s�� */
	LuisResult getIntent(String query);
}
