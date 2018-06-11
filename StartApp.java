//IMPORTS------------------------------------------------------------------------------------------------------------

import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;


public class StartApp {
//FIELDS------------------------------------------------------------------------------------------------------------

//customizable variables																		
	public static Color logocolor = Color.getHSBColor((float)0.12, (float)0.74, (float)1.0); //how to customize this ??????
	public static String spreadsheetFP = "CounselingData.xlsx";

//other
	public static GUIScreen g;
	
//data logging info
	public static String ID = "insertID";
	public static String REASON = "";
	public static String TEACHER = "";
	public static String COUNSELOR = "";
	public static String FIRST = "insertFirstName";
	public static String LAST = "insertLastName";
	public static String HOUSE = "insertHouse";	
	public static String GRADE = "insertGrade";
	public static Fillo fillo = new Fillo();
	public static Connection connection;
	
	

//MAIN----------------------------------------------------------------------------------------------------------------------
	
	public static void main(String[] args) throws FilloException { 
		g = new GUIScreen(logocolor);	
		listenForActions(g);
		g.setVisible(true);
		connection = fillo.getConnection(spreadsheetFP);
	}
	
//METHODS--------------------------------------------------------------------------------------------------------------------

	public static void listenForActions(GUIScreen g) {
		g.cont.addActionListener((ActionListener) g);
		g.inputID.addActionListener((ActionListener) g);
		g.inputReason.addActionListener((ActionListener) g);
		
		g.cancel.addActionListener((ActionListener) g);
		
		g.finish.addActionListener((ActionListener) g);
		g.enterTeacher.addActionListener((ActionListener) g);
		g.enterCounselor.addActionListener((ActionListener) g);
	}
	
	@SuppressWarnings("deprecation")
	public static void addExcelEntry() throws FilloException {
		Date today = new Date();
		System.out.println(today);
		System.out.println(today.getDay());
		if(today.getDay()-3==1) {
			String b1 = "INSERT INTO sheet1(Date, ID, Teacher, Counselor, Reason) VALUES(";
			String b2 = "'" + " " + "'" + "," +"'" + " "+ "'" +  "," + "'" + " " + "'"+ ","+
			"'" + " " + "'"+ ","+ "'" + " " + "'";
			String strQuery = b1 + b2 + ")";
			connection.executeUpdate(strQuery);
		}
		
		String insert = "INSERT INTO sheet1(Date, ID, Teacher, Counselor, Reason) VALUES(";
		String data = "'" + today + "'" + "," + "'" + ID + "'"+ ","+
		"'" + TEACHER + "'"+ ","+ "'" + COUNSELOR + "'"+ ","+ "'" + REASON + "'";
		String strQuery = insert + data + ")";
		connection.executeUpdate(strQuery);
		//connection.close(); idk if this line is needed or not
	}

	public static void sendEmail() {
		Properties prop = new Properties();
		try {
			prop.setProperty("mail.smtp.host", "smtp." + getEmailType());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", true);
		
		Session s = Session.getDefaultInstance(prop, 
				new javax.mail.Authenticator() {  
				protected PasswordAuthentication getPasswordAuthentication() {  
					return new PasswordAuthentication(getMyEmail(), getMyPassword());  
				}  
		});
		MimeMessage msg = new MimeMessage(s);
		try{
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(teachersEmail())); 
				msg.setFrom(new InternetAddress(getMyEmail())); 				
				msg.setSubject("Student at Counseling");
				msg.setText(TEACHER + ", Please excuse your student " + ID + " from class, "	//NEEDS TO BE CHANGED TO STUDENT NAME
						+ "he/she has just arrived at counseling."); 
				Transport.send(msg);

		}
		catch(AddressException e){
			System.out.println("Invalid email address");
			e.printStackTrace();
		}
		catch(MessagingException e){
			System.out.println("Messaging error");
			e.printStackTrace();
		}

	}
	
	public static String teachersEmail() {
		String email = "";
		
		ArrayList<String> teacherz = new ArrayList<String>();
		Scanner t;
		try {
			t = new Scanner(new File("teachersList.txt"));
			while(t.hasNextLine()){
				teacherz.add(t.nextLine());
			}
			t.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		ArrayList<String> emailz = new ArrayList<String>();
		Scanner e;
		try {
			e = new Scanner(new File("emailsList.txt"));
			while(e.hasNextLine()){
				emailz.add(e.nextLine());
			}
			e.close();
		} catch (FileNotFoundException e2) {
			System.out.println(e2.getMessage());
		}
		
		int index = teacherz.indexOf(TEACHER);
		email = emailz.get(index);	
		
		return email;
	}

	public static String getMyEmail() {
		String email = "";
		Scanner kb;
		try {
			kb = new Scanner(new File("emailInfo.txt"));
			email = kb.nextLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return email;
	}
	
	public static String getMyPassword() {
		String pass = "";
		Scanner kb;
		try {
			kb = new Scanner(new File("emailInfo.txt"));
			while(kb.hasNextLine()) {	
				pass = kb.nextLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return pass;
	}
	
	public static String getEmailType() throws IOException {
		String line = Files.readAllLines(Paths.get("emailInfo.txt")).get(2);
		return line;
	}
	
	
	
	
	
	
	
	
}