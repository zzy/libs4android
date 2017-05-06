package net.ouds.android.mail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import net.ouds.android.util.COMMON;
import net.ouds.android.util.CONSTANT;
import android.annotation.SuppressLint;
import android.util.Log;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

public class Mail extends Authenticator {
	private final String TAG = getClass().getName();
	
	private String _emailAddress;
	private String _emailPassword;
	
	private boolean _auth;
	private boolean _debuggable;
	
	private Multipart _multipart;

	public Mail(String emailAddress, String emailPassword) {
		super();
		
		_emailAddress = emailAddress;
		_emailPassword = emailPassword;
		
		_debuggable = false; // debug mode on or off - default off
		_auth = true; // smtp authentication - default on
		
		_multipart = new MimeMultipart();
		
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		
		CommandMap.setDefaultCommandMap(mc);
	}
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(_emailAddress, _emailPassword);
	}

	public IMAPFolder mailFolder(String box) throws Exception {
		Log.d(TAG, "mailFolder: " + _emailAddress);

		Properties props = System.getProperties();
		props.put("mail.store.protocol", "imap");
		props.put("mail.imap.host", COMMON.property("mail.imap.host"));

		Session session = Session.getInstance(props, this);

		IMAPStore store = (IMAPStore) session.getStore("imap"); // 使用imap会话机制，连接服务器
		store.connect(_emailAddress, _emailPassword);

		IMAPFolder folder = (IMAPFolder) store.getFolder(box);
		folder.open(Folder.READ_WRITE);
		
		return folder;
	}
	
	public List<Map<String, Object>> receiveImapMails(String box, int startMsg, int endMsg) throws Exception {
		Log.d(TAG, "receiveImapMails: " + _emailAddress);
		
		IMAPFolder folder = mailFolder(box);		
		
		// 得到收件箱文件夹信息，获取邮件列表
		List<Map<String, Object>> emailList = new ArrayList<Map<String, Object>>();
		
		Message[] messages = folder.getMessages(startMsg, endMsg);
		int msgLen = messages.length;
		if (msgLen > 0) {
			Log.d(TAG, "Messages's length: " + msgLen);
			
			for (int i = msgLen-1; i >= 0 ; i--) {
				Message message = messages[i];
				Flags flags = message.getFlags();
				boolean isRead = flags.contains(Flags.Flag.SEEN); // 用来判断该邮件是否为已读
				
				Log.d(TAG, "Message " + i + "-" + isRead + " form: " + getFromPersonal(message) 
						+ "\n subject: " + getEmailSubject(message) + " sentdate: " + getSentDate(message));
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("msgUID", folder.getUID(message));
				map.put("fromPersonal", getFromPersonal(message));
				map.put("subject", getEmailSubject(message));
				map.put("sentDate", getSentDate(message));
				map.put("isRead", isRead);
				
				emailList.add(map);
			}
		}

		return emailList;
	}

	/**
	 * 获得发件人的姓名
	 */
	public String getFromPersonal(Message message) throws Exception {
		InternetAddress[] from = (InternetAddress[]) message.getFrom();
		
		String personal = from[0].getPersonal();
		if (personal == null)
			personal = "未知";
		
		return personal;
	}
	
	/**
	 * 获得发件人的地址
	 */
	public String getFromAddress(Message message) throws Exception {
		InternetAddress from[] = (InternetAddress[]) message.getFrom();
		
		String personal = from[0].getPersonal();
		if (personal == null)
			personal = "未知";
		
		String address = from[0].getAddress();
		if (address == null)
			address = "未知";
		
		return personal + "<" + address + ">";
	}

	/**
	 * 获得邮件的收件人，抄送，和密送的地址和姓名，
	 * 根据所传递的参数的不同 "to"----收件人; "cc"---抄送人地址; "bcc"---密送人地址
	 */
	@SuppressLint("DefaultLocale")
	public String getMailAddress(Message message, String addrType) throws Exception {
		String mailAddr = "";
		
		InternetAddress[] address = null;
		if (CONSTANT.EMAIL_TO.equalsIgnoreCase(addrType) 
				|| CONSTANT.EMAIL_CC.equalsIgnoreCase(addrType) 
				|| CONSTANT.EMAIL_BCC.equalsIgnoreCase(addrType)) {
			if (CONSTANT.EMAIL_TO.equalsIgnoreCase(addrType))
				address = (InternetAddress[]) message.getRecipients(Message.RecipientType.TO);
			else if (CONSTANT.EMAIL_CC.equalsIgnoreCase(addrType))
				address = (InternetAddress[]) message.getRecipients(Message.RecipientType.CC);
			else if (CONSTANT.EMAIL_BCC.equalsIgnoreCase(addrType))
				address = (InternetAddress[]) message.getRecipients(Message.RecipientType.BCC);
			
			if (null != address) {
				int addrLen = address.length;
				if (addrLen < CONSTANT.MAIL_PAGINATION) {
					for (int i = 0; i < addrLen; i++) {
						InternetAddress intAddr = address[i];
						String addr = intAddr.getAddress();
						if (null != addr)
							addr = MimeUtility.decodeText(addr);
						
						String personal = intAddr.getPersonal();
						if (null != personal)
							personal = MimeUtility.decodeText(personal);
						
						mailAddr += "," + personal + "<" + addr + ">";
					}
					
					mailAddr = mailAddr.substring(1);
				}
				else
					mailAddr += "群发邮件";
			}
			else
				mailAddr += "无";
		}
		else
			mailAddr += "获取邮箱类型错误";
		
		return mailAddr;
	}

	/**
	 * 获得邮件主题
	 */
	public String getEmailSubject(Message message) throws MessagingException {
		String subject = "无主题";
		try {
			subject = MimeUtility.decodeText(message.getSubject());
			if (subject == null)
				subject = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return subject;
	}

	/**
	 * 获得邮件发送日期
	 */
	@SuppressLint("SimpleDateFormat")
	public String getSentDate(Message message) throws Exception {
		Date sentDate = message.getSentDate();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		return format.format(sentDate);
	}

	/**
	 * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件 
	 * 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
	 */
	public String[] getMailContent(Message message) throws Exception {
		String[] mailContent = new String[2];
		
		String contentType = message.getContentType();
		if (message.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) message.getContent();
			
			int count = multipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				mailContent[0] = bodyPart.getContentType();
				mailContent[1] = bodyPart.getContent().toString();
			}
		}
		else {
			//if (message.isMimeType("text/plain") || message.isMimeType("text/html") || message.isMimeType("message/rfc822")) {
			mailContent[0] = contentType;
			mailContent[1] = message.getContent().toString();
			//}
		}
		
		return mailContent;
	}

	/**
	 * 判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"
	 */
	public boolean getReplySign(Message message) throws MessagingException {
		boolean replysign = false;
		String needreply[] = message.getHeader("Disposition-Notification-To");
		if (needreply != null)
			replysign = true;
		
		return replysign;
	}
	
	public boolean sendSmtpMail(String[] tos, String[] ccs, String[] bccs, String emailSubject, String emailContent) {
		Log.d(TAG, "sendSmtpMail: " + _emailAddress);
		
		boolean isSent = false;
		try {
			Properties props = new Properties();
	
			props.put("mail.smtp.host", COMMON.property("mail.smtp.host"));
	
			if (_debuggable)
				props.put("mail.debug", "true");
	
			if (_auth)
				props.put("mail.smtp.auth", "true");
	
			props.put("mail.smtp.port", 25);
	//		props.put("mail.smtp.socketFactory.port", _sport);
	//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
	
	
			Session session = Session.getInstance(props, this);
	
			MimeMessage msg = new MimeMessage(session);
	
			msg.setFrom(new InternetAddress(_emailAddress));
			
			InternetAddress[] addressTo = new InternetAddress[tos.length];
			for (int i = 0; i < tos.length; i++) {
				addressTo[i] = new InternetAddress(tos[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
			
			if (null != ccs) {
				InternetAddress[] addressCc = new InternetAddress[ccs.length];
				for (int i = 0; i < ccs.length; i++) {
					addressCc[i] = new InternetAddress(ccs[i]);
				}
				msg.setRecipients(MimeMessage.RecipientType.CC, addressCc);
			}
			
			if (null != bccs) {
				InternetAddress[] addressBcc = new InternetAddress[bccs.length];
				for (int i = 0; i < bccs.length; i++) {
					addressBcc[i] = new InternetAddress(bccs[i]);
				}
				msg.setRecipients(MimeMessage.RecipientType.BCC, addressBcc);
			}
	
			msg.setSubject(emailSubject);
			msg.setSentDate(new Date());
	
			// setup message body
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(emailContent);
			_multipart.addBodyPart(messageBodyPart);
	
			// Put parts in message
			msg.setContent(_multipart);
	
			// send email
			Transport.send(msg);
			
			isSent = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return isSent;
	}
	
	public void addAttachment(String filename) throws Exception {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);

		_multipart.addBodyPart(messageBodyPart);
	}
	
}