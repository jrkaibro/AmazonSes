package app.core;


public class EmailList {
	
	private String Title, Name, Message, MessageHtml, ReplyTo , ReplyName, EmailTo , EmailCCO, EmailCC, Attach0, Attach1,FileName,FileName1, MimeType;

	public String getMessageHtml() {
		return MessageHtml;
	}

	public void setMessageHtml(String messageHtml) {
		MessageHtml = messageHtml;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getTitle() {
		return Title;
	}

	public String getEmailCC() {
		return EmailCC;
	}

	public void setEmailCC(String emailCC) {
		EmailCC = emailCC;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getReplyTo() {
		return ReplyTo;
	}

	public void setReplyTo(String replyTo) {
		ReplyTo = replyTo;
	}

	public String getReplyName() {
		return ReplyName;
	}

	public void setReplyName(String replyName) {
		ReplyName = replyName;
	}

	public String getEmailTo() {
		return EmailTo;
	}

	public void setEmailTo(String emailTo) {
		EmailTo = emailTo;
	}

	public String getEmailCCO() {
		return EmailCCO;
	}

	public void setEmailCCO(String emailCCO) {
		EmailCCO = emailCCO;
	}

	public String getAttach0() {
		return Attach0;
	}

	public void setAttach0(String attach0) {
		Attach0 = attach0;
	}

	public String getAttach1() {
		return Attach1;
	}

	public void setAttach1(String attach1) {
		Attach1 = attach1;
	}
	
	public String getFileName() {
		return FileName;
	}

	public void setFileName(String filename) {
		FileName = filename;
	}
	
	public String getFileName1() {
		return FileName1;
	}

	public void setFileName1(String filename) {
		FileName1 = filename;
	}	
	
	public void setMimeType(String mimetype) {
		MimeType = mimetype;
	}
	
	public String getMimeType() {
		return MimeType;
	}
	
	

}