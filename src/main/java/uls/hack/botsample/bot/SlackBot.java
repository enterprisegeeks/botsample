package uls.hack.botsample.bot;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.FileOutputStream;
import java.util.regex.Matcher;

/**
 *  SlackBot�T���v���B
 *  
 *  https://my.slack.com/services/new/bot �ɂ�
 *  slackbot ���쐬���ăg�[�N�����擾���Aapplication.properties�Ƀg�[�N���������Ă����܂��B
 *  
 *  @Contoroler �A�m�e�[�V����(Spring MVC�Ƃ͕ʂł�) �̑����̐ݒ�ɂ���āA�l�X�ȉ�b�p�^�[���ɉ������������s�����Ƃ��ł��܂��B
 *  �Ⴆ�΁A���w���̃��b�Z�[�W�A�C�ӂ̐��K�\���̃��b�Z�[�W�A�s���t����摜�\��t���Ȃǂ�����܂��B
 *  �܂��A����̃L�[���[�h�ɂ��A�J���o�Z�[�V�����Ƃ��������̉�b���s���@�\���񋟂���܂��B
 *  
 */
@Component // �A�m�e�[�V�����K�{
public class SlackBot extends Bot { /* Bot�p���K�{ */

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

    /**
     * Slack token from application.properties file. You can get your slack token
     * next <a href="https://my.slack.com/services/new/bot">creating a new bot</a>.
     */
    @Value("${slackBotToken}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    /**
     *�@DIRECT_MENTION�ADIRECT_MESSAGE �́A
     *�@@�{�b�g���Ł@���w���Ŕ������ꂽ�Ƃ��ɑΉ�����C�x���g�B
     *
     * @param session
     * @param event
     */
    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void onReceiveDM(WebSocketSession session, Event event) {
    	// session �́A Slack RTM API �Ƃ̘A�g�Ŏg������̂ŁA�ʏ�JBOT���g���Ǘ�������̂ŁA��b�̂����Ŏg�p���邱�Ƃ͖����B
    	// reply �ŕԓ���Ԃ��B
    	// event����A���b�Z�[�W���e�Ȃǂ��擾�ł���B
    	// slackService�� Bot�N���X�̃t�B�[���h�ABot�̐ݒ�Ɋւ���F�X�ȏ����擾�ł���B�����̎擾�ȊO�ɂ͂��܂�g��Ȃ����낤�B
    	logger.info("onReceiveDM:"+ event);
        reply(session, event, new Message("���̖��O�́A" + slackService.getCurrentUser().getName()+ "�ł��B" 
        			+ event.getText() + "����Ȃ���B"));
    }

    /**
     * EventType.MESSAGE�͒N�����������������Ƃ��ɔ�������C�x���g�B
     * pattern���g�p���āA���K�\���ɍ��v�����ꍇ�����A���̃��\�b�h�����s����悤�Ȃ��Ƃ��ł���B
     *�@����̓J�b�R�Ŏn�܂�Ȃ�������S�Ď擾����B
     * matcher �������Ɏ��ƁA�p�^�[���̈ꕔ���擾�\�B
     *
     * @param session
     * @param event
     * @throws Exception 
     */
    @Controller(events = EventType.MESSAGE, pattern = "^[^(](.+)$")
    public void onReceiveMessage(WebSocketSession session, Event event, Matcher matcher) throws Exception {
    	if(isFileUploadMessage(event)) { 
    		//�t�@�C���A�b�v���[�h�̏ꍇ�B �T�u�^�C�v�ɂ��event�̔��肪�ł���΁A���\�b�h����肭�����邱�Ƃ��ł���̂����A���͑Ή����Ă��Ȃ����߁A�����ŕ��򂳂���B
    		onFileUploaded(session, event);
    	} else {
        	String text = matcher.group(0);
        	logger.info("onReceiveMessage:"+ event);
            reply(session, event, new Message(text + "���Č�����?"));
    	}
    	
    }
    
    private boolean isFileUploadMessage(Event event) {
    	return event.getText().matches(".*uploaded a file: <https://.+\\.slack\\.com/files.*");
    }

    /**
     * �t�@�C�������L���ꂽ���̃C�x���g�B
     * 
     * <a href="https://api.slack.com/events/file_shared">file_shared</a>
     * �ɂ��邪�A���̃C�x���g�̂Ƃ��� �`���l��ID���Ȃ����߁Areply �ł��Ȃ��B
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
    	// �Ȃ���2�񔭐�����
        logger.info("File shared: {}", event);
        
    }
    // �������A�t�@�C���A�b�v���[�h���I�������̃��b�Z�[�W���󂯎�邱�Ƃ��ł���̂ŁA�������_�@�Ƀ��v���C�ł���B
    public void onFileUploaded(WebSocketSession ses, Event event) throws Exception{

    	if(!event.getFile().getMimetype().toLowerCase().contains("image")) {
    		return;
    	}
    	
    	OkHttpClient client = new OkHttpClient();
    	Request request = new Request.Builder().url(event.getFile().getUrlPrivateDownload())
    			.addHeader("Authorization", "Bearer " + slackToken) // �摜�擾�̓g�[�N���K�v
    			.build();
    	Response response = client.newCall(request).execute();
    	if (!response.isSuccessful()) {
            logger.info("File download error");
    		return;
    	}
    	String name = event.getFile().getId() + "." + event.getFile().getFiletype();
    	try(FileOutputStream fos = new FileOutputStream("c:/tmp/" +name)){
    	    fos.write(response.body().bytes());

            logger.info("saved File;" + name);
    	}
    	
    }

    /**
     * Conversation �@�\1�B
     * 
     * startConversation�ŁA��A�̉�b���J�n����B
     * ����́A (,,,,)�ƌ����Ɖ�b�����B
     *
     * @param session
     * @param event
     */
    @Controller(pattern = "^[\\(].+[\\)]$")
    public void step1(WebSocketSession session, Event event) {
        startConversation(event, "step2");   // conversation�̊J�n�B���̃��b�Z�[�W�ɔ�������Ƃ��́Astep2���\�b�h��L���ɂ���悤�ɂ���B�E
        reply(session, event, new Message("��b�̊J�n(step1.Wild�ƌ����Ă݂悤)�BGet?  "));
    }

    /**
     * step1����̑����B
     *
     * @param session
     * @param event
     */
    @Controller(next = "step3") // next��nextConversation�Ăяo���ɔ������郁�\�b�h�B
    public void step2(WebSocketSession session, Event event) {
    	if (event.getText().toLowerCase().contains("wild")) {
            reply(session, event, new Message("(step2) and ? "));
            nextConversation(event);  // next�����̃��\�b�h�ŃC�x���g��҂��󂯂�悤�ɂȂ�B
    	} else {
            reply(session, event, new Message("(�I��step2)"));
    		stopConversation(event); // stopConversation�ŉ�b�I���B
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
            reply(session, event, new Message("�������������߂���(�I��)"));
            stopConversation(event);
        } else {
            reply(session, event, new Message("(�I��step3)"));
            stopConversation(event);
        }
    }
}