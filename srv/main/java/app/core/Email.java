package app.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amazonaws.services.simpleemail.AWSJavaMailTransport;


public class Email {
	
	
	private final StringBuilder Error = new StringBuilder();
	private final StringBuilder Log   = new StringBuilder();	
	private final Config        cfg   = new Config();
	

	//private String Version = "1.0.0";
	private String Data    = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	private String n = "null";
	private String messageid = "";
	private String emailname = "";
	
	private final List<EmailList> EmailList = new LinkedList<EmailList>();
	
	public String enviar(String xml) {
		
		  	try {
		  		
		  	Log.append("********************* CONECTANDO AMAZON ********************** \n") ;	
	  	    Setup(xml);
	  	  
	  	    Log.append("*************** ANALISANDO ESTRUTURA DO XML ****************** \n") ;
	  	    
  		    ListEmail(xml);
	  		  
	  		Log.append("************************************************************** \n") ;
		//	Log.append("* AmazonAPi ver " + Version + " - " + Data + "\n");
			Log.append("************************************************************** \n");
			Log.append("*     (TLS)='T' / (SSL)='S' / (SSL+TLS)='M' / Nenhum='N' \n");
			Log.append("**************************************************************  \n");		
			Log.append("* Servidor: <" + cfg.getSmtp() + ">:<" + cfg.getPort() + "> \n");
			Log.append("* Autenticacao: <"+ cfg.getAutentication() + ">  \n");
			Log.append("**************************************************************  \n");
			
			Log.append("  \n");
			
			
			for ( int i = 0; i< EmailList.size(); i++ ) {
				
			 EmailList.get(i);
		
		     Log.append("************************ Enviando ****************************  \n");
		     Log.append("* Email      : " + EmailList.get(i).getEmailTo()   + "\n");
		     Log.append("* Reply Name : " + EmailList.get(i).getReplyName() + "\n");
		     Log.append("* CC         : " + EmailList.get(i).getEmailCC()   + "\n");
		     Log.append("* BCC        : " + EmailList.get(i).getEmailCCO()  + "\n");		     
		     Log.append("**************************************************************  \n");	
		 
		     Log.append("************************   Anexo   ***************************  \n");
		     Log.append("* Mime : " + EmailList.get(i).getMimeType() + "\n");
		     Log.append("* File : " + EmailList.get(i).getAttach0() + "\n");
		     Log.append("* File : " + EmailList.get(i).getAttach1() + "\n");
		     Log.append("**************************************************************  \n");	
		     
		     Log.append(EmailList.get(i));
		     
		     
			  Boolean isok = attach(EmailList.get(i));
			  
				if (isok){
					Log.append("  \n");
					Log.append("************************ Situação ****************************  \n");
					Log.append(" Enviado com sucesso !  trasmitido " +  Data.toString()+" \n");
				}	
				
				Log.append("**************************************************************  \n");
			//	System.out.println(Log);
			}
			
			
			
	  		   
			} catch (Exception e) {
				
				System.out.println(e.getMessage());
				Log.append(e.getMessage() + "\n");
			}
		  	
		  String XMLRetorno = "";
		  	
		  	
		  	XMLRetorno += "<AmazonSes xmlns=\"wvetroevo\">" + 
			  			  "<MessageID>"+ messageid +"</MessageID>" + 
			  			  "<EmailName>"+ emailname +"</EmailName>" + 
			  			  "<EmailTo>" +  EmailList.get(0).getEmailTo()   + "</EmailTo>" + 
			  			  "<ReplyName>"+ EmailList.get(0).getReplyName()   +"</ReplyName>" + 
			  			  "<EmailCC>"+   EmailList.get(0).getEmailCC()  +"</EmailCC>" + 
			  			  "<EmailCCO>"+  EmailList.get(0).getEmailCCO()  +"</EmailCCO>" + 
			  			  "<Attach0>"+   EmailList.get(0).getAttach0()  +"</Attach0>" + 
			  			  "<Attach1>"+   EmailList.get(0).getAttach1()  +"</Attach1>" + 
			  			  "<Log>"+ Log.toString().trim() +"</Log>" + 
			  			  "</AmazonSes>";
			  	
		  		
		return XMLRetorno;
	}
	
	public String s3storage( ) {
		
		
		
		return "";
	}
	
	
	private void ListEmail (String xml) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
	    
	    InputSource inputStream = new InputSource();		  
	    inputStream.setCharacterStream(new StringReader(xml));
	  
	    Document document = documentBuilder.parse(inputStream);
	    
	    NodeList sdt = document.getElementsByTagName("Emails");
	    
	    for (int index = 0; index < sdt.getLength(); index++) {
	      
	    	Node node = sdt.item(index);
	    	EmailList eml = new EmailList();
	    	
	      if (node.getNodeType() == Node.ELEMENT_NODE) {
	        
	    	Element element = (Element) node;
	        
	        NodeList nameNodeList = element.getElementsByTagName("*");
	        
	        for (int eIndex = 0; eIndex < nameNodeList.getLength(); eIndex++) {
	        	
	          if (nameNodeList.item(eIndex).getNodeType() == Node.ELEMENT_NODE) {
	              
	        	    Element nameElement = (Element) nameNodeList.item(eIndex);
	        	    switch (nameElement.getNodeName()) {
					case "Title":
						eml.setTitle(nameElement.getFirstChild().getNodeValue().trim());
						break;
					case "Name":
						eml.setName(nameElement.getFirstChild().getNodeValue().trim());
						break;
					case "Message":
						eml.setMessage(nameElement.getFirstChild().getNodeValue().trim());						
						break;
					case "MessageHtml":
						eml.setMessageHtml(nameElement.getFirstChild().getNodeValue().trim());						
						break;
					case "ReplyTo":
						eml.setReplyTo(nameElement.getFirstChild().getNodeValue().trim());
						break;
					case "ReplyName":						
						eml.setReplyName(nameElement.getFirstChild().getNodeValue().trim());
						break;
					case "EmailTo":						
						eml.setEmailTo(nameElement.getFirstChild().getNodeValue().trim());
						break;	
					case "EmailCC":						
						eml.setEmailCC(nameElement.getFirstChild().getNodeValue().trim());
						break;
					case "EmailCCO":						
						eml.setEmailCCO(nameElement.getFirstChild().getNodeValue().trim());
						break;						
					case "Attach0":						
						eml.setAttach0(nameElement.getFirstChild().getNodeValue().trim());
						break;
					case "Attach1":
						eml.setAttach1(nameElement.getFirstChild().getNodeValue().trim());
						break;
					case "FileName":
						eml.setFileName(nameElement.getFirstChild().getNodeValue().trim());
						break;
					case "FileName1":
						eml.setFileName1(nameElement.getFirstChild().getNodeValue().trim());
						break;
					case "MimeType":
						eml.setMimeType(nameElement.getFirstChild().getNodeValue().trim());
						break;	
					default:
						Error.append("TAG NOT FOUND");
						break;
					}
	          	}
	        }

	        EmailList.add(eml);
	      }
	    }	
	}
	
	private void Setup(String xml) {
				
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder documentBuilder;
	    
		try {
			
			  documentBuilder = factory.newDocumentBuilder();
			  InputSource inputStream = new InputSource();		  
			  inputStream.setCharacterStream(new StringReader(xml));			  
			  Document document = documentBuilder.parse(inputStream);
			  
			  cfg.setUser(document.getElementsByTagName("User").item(0).getTextContent());
			  cfg.setPassword(document.getElementsByTagName("Password").item(0).getTextContent());
			  cfg.setAutentication(document.getElementsByTagName("Autentication").item(0).getTextContent());
			  cfg.setSecurity(document.getElementsByTagName("Security").item(0).getTextContent());
			  cfg.setPort(document.getElementsByTagName("Port").item(0).getTextContent());
			  cfg.setEmail(document.getElementsByTagName("Email").item(0).getTextContent());
			  cfg.setSmtp(document.getElementsByTagName("Smtp").item(0).getTextContent());
			  cfg.setApi(document.getElementsByTagName("Api").item(0).getTextContent());
			  
		} catch (Exception e) {
			
			Error.append(e.getMessage());
		}		
		
	}
	
	@SuppressWarnings("resource")
	private Boolean attach(EmailList e) throws AddressException, MessagingException, IOException {
		
		String fileNameInEmail  = e.getFileName();
		String fileNameInEmail1 = e.getFileName1();
		String mimetype = "null"; 
				
		//String fileMime = "application/pdf";

		if (!e.getMimeType().equals(n)) {
			mimetype = e.getMimeType();
		} else {
			mimetype = "application/pdf";
		}
		
		Properties props = new Properties();		
		props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.port", cfg.getPort()); 
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.auth", "true");
				
		Session AWSsession = Session.getInstance(props);
		
		
		AWSJavaMailTransport AWSTransport = new AWSJavaMailTransport(AWSsession, null);
		
		AWSTransport.connect();
		
		MimeMessage message = new MimeMessage(AWSsession);	

		message.setFrom(new InternetAddress(cfg.getEmail(),e.getName()));		
		
		message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(e.getEmailTo(),e.getName()));		
	
		
		if (!e.getEmailCC().equals(n.intern())) {
			message.addRecipient(MimeMessage.RecipientType.CC, new InternetAddress(e.getEmailCC()));
		}
		
		if (!e.getEmailCCO().equals(n)) {
			message.addRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(e.getEmailCCO()));
		}	
				
		
		Address address = new InternetAddress(e.getReplyTo(),e.getReplyName());		
		message.reply(true);
		message.setReplyTo(new Address[] { address });
		message.setSubject(e.getTitle());
		
				
		Multipart multipart = new MimeMultipart();
		BodyPart messageBodyPart = new MimeBodyPart();
		
		String html = e.getMessageHtml();
		String text = e.getMessage();
		
		if (!html.equals(n)) {
			messageBodyPart.setContent(html, "text/html; charset=utf-8");
			messageBodyPart.setHeader("Content-Type", "text/html; charset=\"utf-8\"");			
			messageBodyPart.setHeader("Content-Transfer-Encoding", "quoted-printable");
		}else {		
			messageBodyPart.setContent(text, "text/plain; charset=utf-8\"");
			messageBodyPart.setHeader("Content-Type", "text/html; charset=\"utf-8\"");			
			messageBodyPart.setHeader("Content-Transfer-Encoding", "quoted-printable");
		}
		
		multipart.addBodyPart(messageBodyPart);
		
		String archive0 = e.getAttach0();
		String archive1 = e.getAttach1();
		
		
		if (!archive0.equals(n.intern()) ) { 
					
			MimeBodyPart messageBodyPart1 = new MimeBodyPart();
			messageBodyPart1.setHeader("Content-Type", "text/html; charset=\"utf-8\"");			
			messageBodyPart1.setHeader("Content-Transfer-Encoding", "quoted-printable");
		    
		    byte[] fileContent = null;
		    
		    try {
		    	File file = new File(archive0);
		    	 
				FileInputStream fin = null;
			    fin = new FileInputStream(file);
			    fileContent = new byte[(int) file.length()];
			    fin.read(fileContent);
				
				ByteArrayDataSource source = new ByteArrayDataSource(fileContent, mimetype);
				messageBodyPart1.setDataHandler(new DataHandler(source));
				messageBodyPart1.setFileName(fileNameInEmail);
				multipart.addBodyPart(messageBodyPart1);
					
			} catch (Exception e2) {
				System.out.println(e2.getMessage());
			}
		   		
		}
		
		
		if(!archive1.equals(n.intern())) { 
			
			MimeBodyPart messageBodyPart2 = new MimeBodyPart();
		    messageBodyPart2.setHeader("Content-Type", "text/html; charset=\"utf-8\"");			
		    messageBodyPart2.setHeader("Content-Transfer-Encoding", "quoted-printable");
		    
		    byte[] fileContent = null;
		    
		    
		    try {
			    File file = new File(archive1);
				
				FileInputStream fin = null;
			    fin = new FileInputStream(file);
			    fileContent = new byte[(int) file.length()];
			    fin.read(fileContent);
				
				ByteArrayDataSource source = new ByteArrayDataSource(fileContent, mimetype);
				messageBodyPart2.setDataHandler(new DataHandler(source));
				messageBodyPart2.setFileName(fileNameInEmail1);
				multipart.addBodyPart(messageBodyPart2);		
			} catch (Exception e2) {
				
				Log.append(e2.getMessage());
			}
		    
	
		}
	
	

		message.setContent(multipart);			
		message.saveChanges();
		

		
		
		
		AWSTransport.sendMessage(message, null);
		
		messageid = AWSTransport.getLastMessageId();
		emailname = AWSTransport.getURLName().toString();
	
		
		return true;
	}
	
	
	
	

}
