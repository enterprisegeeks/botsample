package uls.hack.botsample.bot;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import uls.hack.botsample.cognitive.CognitiveService;
import uls.hack.botsample.cognitive.luis.Luis.LuisResult;
import uls.hack.botsample.cognitive.ocr.OCR.OCRResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 *  SlackBotサンプル。
 *  
 *  https://my.slack.com/services/new/bot にて
 *  slackbot を作成してトークンを取得し、application.propertiesにトークンを書いておきます。
 *  
 *  @Contoroler アノテーション(Spring MVCとは別です) の属性の設定によって、様々な会話パターンに応じた反応を行うことができます。
 *  例えば、名指しのメッセージ、任意の正規表現のメッセージ、ピン付けや画像貼り付けなどがあります。
 *  また、特定のキーワードにより、カンバセーションという複数の会話を行う機能も提供されます。
 *  
 */
@Component // アノテーション必須
public class SlackBot extends Bot { /* Bot継承必須 */

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

    /**
     * Slack token from application.properties file. You can get your slack token
     * next <a href="https://my.slack.com/services/new/bot">creating a new bot</a>.
     */
    @Value("${slackBotToken}")
    private String slackToken;
    
    @Autowired
    private CognitiveService service;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    /**
     *　DIRECT_MENTION、DIRECT_MESSAGE は、
     *　@ボット名で　名指しで発言されたときに対応するイベント。
     *
     * @param session
     * @param event
     */
    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void onReceiveDM(WebSocketSession session, Event event) {
    	// session は、 Slack RTM API との連携で使われるもので、通常JBOT自身が管理するもので、会話のやり取りで使用することは無い。
    	// reply で返答を返す。
    	// eventから、メッセージ内容などを取得できる。
    	// slackServiceは Botクラスのフィールド、Botの設定に関する色々な情報を取得できる。自分の取得以外にはあまり使わないだろう。
    	logger.info("onReceiveDM:"+ event);
        reply(session, event, new Message("何かつぶやいてみて、何のこといっているか当ててみるから。"));
        reply(session, event, new Message("(xxxx)っていうとget wild ごっこができます。"));
        reply(session, event, new Message("画像をアップロードすると、画像内の文字を読んでみるよ"));
    }

    /**
     * EventType.MESSAGEは誰かが何かを言ったときに発生するイベント。
     * patternを使用して、正規表現に合致した場合だけ、このメソッドを実行するようなことができる。
     *　今回はカッコで始まらない発言を全て取得し、LUISで意図を判定してみる。
     * matcher を引数に取ると、パターンの一部を取得可能。
     *
     * @param session
     * @param event
     * @throws Exception 
     */
    @Controller(events = EventType.MESSAGE, pattern = "^[^(](.+)$")
    public void onReceiveMessage(WebSocketSession session, Event event, Matcher matcher) throws Exception {
    	if(isFileUploadMessage(event)) { 
    		//ファイルアップロードの場合。 サブタイプによるeventの判定ができれば、メソッドを上手く分けることができるのだが、今は対応していないため、ここで分岐させる。
    		onFileUploaded(session, event);
    	} else {
        	String text = matcher.group(0);
        	logger.info("onReceiveMessage:"+ event);
        	LuisResult res = service.getIntent(text);
        	
        	if (res == null || res.topScoringIntent == null) {

            	String message = "ちょっと何言ってるかわからない。";
                reply(session, event, new Message(message));
        	} else {

            	String intent = res.topScoringIntent.intent;
            	String entities = res.entities.stream()
            				.map(e -> e.entity).collect(Collectors.joining());
            	String message = "ひょっとして" + intent + "のこと言ってる?" + 
            				(entities.isEmpty() ? "" : " " + entities + "っていいよね");
                reply(session, event, new Message(message));
        	}
        	
    	}
    	
    }
    
    /** ファイルアップロード通知かどうか。 */
    private boolean isFileUploadMessage(Event event) {
    	return event.getText().matches(".*uploaded a file: <https://.+\\.slack\\.com/files.*");
    }

    /**
     * ファイルが共有された時のイベント。
     * 
     * <a href="https://api.slack.com/events/file_shared">file_shared</a>
     * にあるが、このイベントのときは チャネルIDがないため、reply できない。
     * 
     * Invoked when bot receives an event of type file shared.
     * NOTE: You can't reply to this event as slack doesn't send
     * a channel id for this event type. You can learn more about
     * <a href="https://api.slack.com/events/file_shared">file_shared</a>
     * event from Slack's Api documentation.
     *
     * @param session
     * @param event
     */
    @Controller(events = EventType.FILE_SHARED)
    public void onFileShared(WebSocketSession session, Event event) {
    	// なぜか2回発生する
        logger.info("File shared: {}", event);
        
    }
    // ファイルアップロードが終わった後のメッセージを受け取ることができるので、そこを契機にリプライできる。
    private void onFileUploaded(WebSocketSession ses, Event event) throws Exception{

    	if(!event.getFile().getMimetype().toLowerCase().contains("image")) {
    		return;
    	}
    	
    	OkHttpClient client = new OkHttpClient();
    	Request request = new Request.Builder().url(event.getFile().getUrlPrivateDownload())
    			.addHeader("Authorization", "Bearer " + slackToken) // 画像取得はトークン必要
    			.build();
    	Response response = client.newCall(request).execute();
    	if (!response.isSuccessful()) {
            logger.info("File download error");
    		return;
    	}
    	String name = event.getFile().getId() + "." + event.getFile().getFiletype();
    	File file = new File("/tmp/" + name); 
    	try(FileOutputStream fos = new FileOutputStream(file)){
    	    fos.write(response.body().bytes());
            logger.info("saved File;" + name);
    	}
    	
    	OCRResult ocr = service.recognizeText(file);
    	reply(ses, event, new Message("ひょっとして、\"" + ocr.toString() +"\"って書いてある?"));
    	
    }

    /**
     * Conversation 機能1。
     * 
     * startConversationで、一連の会話を開始する。
     * 今回は、 (,,,,)と言うと会話発生。
     *
     * @param session
     * @param event
     */
    @Controller(pattern = "^[\\(].+[\\)]$")
    public void step1(WebSocketSession session, Event event) {
        startConversation(event, "step2");   // conversationの開始。次のメッセージに反応するときは、step2メソッドを有効にするようにする。・
        reply(session, event, new Message("会話の開始(step1.Wildと言ってみよう)。Get?  "));
    }

    /**
     * step1からの続き。
     *
     * @param session
     * @param event
     */
    @Controller(next = "step3") // nextはnextConversation呼び出しに反応するメソッド。
    public void step2(WebSocketSession session, Event event) {
    	if (event.getText().toLowerCase().contains("wild")) {
            reply(session, event, new Message("(step2) and ? "));
            nextConversation(event);  // next属性のメソッドでイベントを待ち受けるようになる。
    	} else {
            reply(session, event, new Message("(終了step2)"));
    		stopConversation(event); // stopConversationで会話終了。
    	}
    	
    }

    /**
     * Step3
     *
     * @param session
     * @param event
     */
    @Controller()
    public void step3(WebSocketSession session, Event event) {
        if (event.getText().toLowerCase().contains("tough")) {
            reply(session, event, new Message("傷ついた夢を取り戻すよ(終了)"));
            stopConversation(event);
        } else {
            reply(session, event, new Message("(終了step3)"));
            stopConversation(event);
        }
    }
}