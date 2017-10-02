

/**
 *
 * @author Shubhadeep Das
 */
import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.lang.Runtime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class Server extends javax.swing.JFrame {
static ArrayList toClients;

    /**
     * Creates new form Server
     */
    public Server() {
        initComponents();
        Thread gothread=new Thread(){
        
            @Override
            public void run() {
                super.run(); //To change body of generated methods, choose Tools | Templates.
                go();
            }
        
        };
       
        gothread.start();
        
        Thread test=new Thread(){
            @Override
            public void run() {
                super.run(); 
                 startTesting();
            }
        
        
        };
        test.start();
       
    }
  

    
    
    
    public class ClientHandler implements Runnable{
	BufferedReader br;
        PrintWriter pwr;
	Socket sock;
		public ClientHandler(Socket clientSock){//constructor
			try{
				sock=clientSock;	//socket of this client. use this to make input stream with client
				pwr=new PrintWriter(sock.getOutputStream());
                                InputStreamReader ir=new InputStreamReader(sock.getInputStream());
				br=new BufferedReader(ir);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}//end of constructor
		public void run(){
			String msg=null;
                        int flag;
			try{
				while((msg=br.readLine())!=null){
                                    System.out.println("MSG= "+msg+"\n");//when message received from a client
                                    if(msg.length()>=2){
                                        //System.out.println("LEN= "+msg.split(" ").length);
                                        String msg_flag[]=msg.split(" ");
                                        if(!(msg_flag[0].equals("SLOT")) && !(msg_flag[0].equals("DEL"))){
                                        flag=authenticate(msg);         //REPLY TO CLIENT
                                        pwr.println(String.valueOf(flag));
                                        pwr.flush();
                                        }
                                        else if(msg_flag[0].equals("SLOT")){
                                        flag=scheduleMeeting(msg);
                                        System.out.println(flag);
                                        pwr.println(String.valueOf(flag));    //REPLY to SCHEDULE
                                        pwr.flush();
                                        }
                                        else if(msg_flag[0].equals("DEL")){
                                            System.out.println("Enter..");
                                        flag=deleteMeeting(msg);
                                        pwr.println(String.valueOf(flag));
                                        pwr.flush();
                                        }
					//tellEveryone(msg);	//send message to other clients
					}
                                }
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}//end of run()

                
       //USER AUTHENTICATION
private int authenticate(String msg) {
     
try{  

Class.forName("oracle.jdbc.driver.OracleDriver");  
  

Connection con=DriverManager.getConnection(  
"jdbc:oracle:thin:@localhost:1521:xe","system","shubha");  
  

Statement stmt=con.createStatement();
String user_pass[]=msg.split(" ");
 String user=user_pass[0];
 String pass=user_pass[1];
jTextArea2.append("LOGIN ATTEMPT by USERNAME: "+user+"\n");
ResultSet rs=stmt.executeQuery("select password from userlist where username="+"'"+user+"'");
if(rs.next()){
  
if(pass.equals(rs.getString(1))){
    con.close();
    jTextArea2.append("USERNAME: "+user+ " successfully logged in.\n");
    return 01;                  //PASSWORD MATCH
    
}
else
{con.close(); 
jTextArea2.append("USERNAME: "+user+ " password mistmatch.\n");
return 02; }      //PASSWORD MISMATCH
}
else
{ con.close(); 
jTextArea2.append("Unauthorised USERNAME: "+user+ " LOGIN attempt.\n");
return 03; }     //INVALID USER
//step5 close the connection object  
 
  
}catch(Exception e){ System.out.println(e); return 04;}  
           
  
            
            
        }


//BOOKING ROOM STATUS
        private int scheduleMeeting(String msg) {
                
try{  

Class.forName("oracle.jdbc.driver.OracleDriver");  
  

Connection con=DriverManager.getConnection(  
"jdbc:oracle:thin:@localhost:1521:xe","system","shubha");  
  

Statement stmt=con.createStatement();
String user_pass[]=msg.split(" ");
 String slot=user_pass[0].toLowerCase()+user_pass[1];
 String day=user_pass[2];
 String username=user_pass[3];
 
  System.out.println(slot+ " "+day);
ResultSet rs=stmt.executeQuery("select * from schedule where day="+"'"+day+"'");
if(rs.next()){                     //IF there is a entry in the table
 System.out.println("Retrieved Query= "+rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3));
if(("slotA").equals(slot) && !(rs.getString(2).equals("available"))){
   
    con.close();
    jTextArea1.append(username+" trying to book a SLOT.\n");
    return 01;                  //SLOT already BOOKED
    
}
else if(("slotB").equals(slot) && !(rs.getString(3).equals("available")) )
{con.close(); 
jTextArea1.append(username+" trying to book a SLOT.\n");
return 01; }      //SLOT already BOOKED

else
{
    //System.out.println("Entering..");
Statement book=con.createStatement();
book.executeQuery("update schedule set "+slot+"='"+username+"' where day="+"'"+day+"'");
con.close(); 
jTextArea1.append(slot+" booked on day "+day+" by "+username+"\n".toUpperCase());
return 02; }     //SLOT BOOKED
  
 
}
else{                            //NO BOOKING MADE YET ON THAT DAY

Statement book=con.createStatement();
if(slot.equals("slotA"))
book.executeQuery("insert into schedule values('"+day+"','"+username+"','available')");
else
book.executeQuery("insert into schedule values('"+day+"','available','"+username+"')");
con.close(); 
jTextArea1.append(slot+" booked on day "+day+" by "+username+"\n".toUpperCase());
return 02;

}

}catch(Exception e){ System.out.println(e); return 04;}  
           
  
            
            
        }

        
        //DELETE A SLOT BOOKING
        private int deleteMeeting(String msg) {
            
                           
try{  

Class.forName("oracle.jdbc.driver.OracleDriver");  
  

Connection con=DriverManager.getConnection(  
"jdbc:oracle:thin:@localhost:1521:xe","system","shubha");  
  

Statement userbook=con.createStatement();
String user_pass[]=msg.split(" ");
 String slot=user_pass[1].toLowerCase()+user_pass[2];
 String day=user_pass[3];
 String username=user_pass[4];
 
  System.out.println(username+" "+slot+ " "+day);
ResultSet rs=userbook.executeQuery("select "+slot+" from schedule where day="+"'"+day+"'");
if(rs.next()){                     //IF there is a entry in the table
 System.out.println("Retrieved Query= "+rs.getString(1));
 if(username.equals(rs.getString(1)))
 {
     Statement del=con.createStatement();
     del.executeQuery("update schedule set "+slot+"='available' where day="+"'"+day+"'");
     jTextArea1.append(slot+" deleted on day "+day+" by "+username+"\n".toUpperCase());
     con.close();
     return 05;      //SLOT DELETED
 }
 else 
 {
   if(rs.getString(1).equals("available"))
   {con.close();
   jTextArea1.append(username+" trying to delete a EMPTY SLOT\n"); 
   return 07;}
   else{
   jTextArea1.append(username+" trying to delete a UNAUTHORISED SLOT\n"); 
   con.close();
   return 06;  }//SLOT NOT AUTHORISED
 }
}
else
{
    jTextArea1.append(username+" trying to delete a EMPTY SLOT\n"); 
    con.close();
    return 07;   //SLOT IS EMPTY
}
 
}catch(Exception e){ System.out.println(e); return 04;}  
           
  
            
            
        
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("SERVER STATUS"));

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setBorder(javax.swing.BorderFactory.createTitledBorder("SERVER IS RUNNING"));
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("TRAINING");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("CLIENT STATUS"));

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton2.setText("TURN ON ENERGY EFFICIENCY");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 93, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        
        //Deleting previous training data if present
         File testFileExistence=new File("D:\\trainingData.txt");
        if(testFileExistence.exists())
            testFileExistence.delete();
        
        
        new Training().setVisible(true);
        this.dispose();
       
       
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        new Testing().setVisible(true);
       
    }//GEN-LAST:event_jButton2ActionPerformed

    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
                //new Server().go();
            }
        });
    
   
   
    }
public void go(){
		toClients = new ArrayList();
		try{
			ServerSocket servSock = new ServerSocket(5000);
			while(true){	//server keeps on listening for clients
				Socket clientSock = servSock.accept();
				PrintWriter pr = new PrintWriter(clientSock.getOutputStream());
				toClients.add(pr);	//add a stream to this client to arraylist
				Thread th= new Thread(new ClientHandler(clientSock));
				th.start();	//start a new thread for each client
				System.out.println("Got a connection\n");
                                jTextArea1.append("Got a connection from client..\n");
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}//end of go()

public void tellEveryone(String msg){	//for broadcasting message to all clients
		Iterator it=toClients.iterator();	//to traverse arraylist of clients
		while(it.hasNext()){
			try{//things can go wrong here, so try catch block
				PrintWriter pr= (PrintWriter)it.next();//output stream to client
				pr.println(msg);	//put message to client on output stream
				pr.flush();		//flush output stream
			}catch(Exception ex){
				ex.printStackTrace();
			}
			}
		}//end of tellEveryone()

//Called twice in a day- 1 min befor slotA time and 1min before slotB time
private void startTesting() {
         String dateSystem=new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    try{        
         //the Date and time at which you want to execute
    DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    
    Date dateSlotA = dateFormatter .parse(dateSystem+" 22::00");
     Date dateSlotB = dateFormatter .parse(dateSystem+" 22:48:00");
    //Now create the time and schedule it
    Timer timer = new Timer();

    //Use this if you want to execute it once
    timer.schedule(new MyTimeTask("slotA"), dateSlotA);
    timer.schedule(new MyTimeTask("slotB"), dateSlotB);
    //Use this if you want to execute it repeatedly
    //int period = 10000;//10secs
    //timer.schedule(new MyTimeTask(), date, period );
    
   
    }catch(Exception e){System.out.println(e);}
        
  
    }

    // Variables declaration - do not modify                     
    // End of variables declaration                   



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables




}

class MyTimeTask extends TimerTask
{
Boolean slotA=false;
Boolean slotB=false;
String slotAtime="10:00:00";
String slotBtime="17:00:00";
String slot;
MyTimeTask(String slot){
this.slot=slot;

}
    public void run()
    {
        //Extract data from database
        
        System.out.println("Task Handler Called"); 
      slotA=false;
      slotB=false;
    String dateSystem=new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    try{  

Class.forName("oracle.jdbc.driver.OracleDriver");  
  

Connection con=DriverManager.getConnection(  
"jdbc:oracle:thin:@localhost:1521:xe","system","shubha");  
  

Statement userbook=con.createStatement();

 

ResultSet rs=userbook.executeQuery("select slotA,slotB from schedule where day="+"'"+dateSystem+"'");
if(rs.next()){                     //IF there is a entry in the table
 System.out.println("Retrieved SLOTA= "+rs.getString(1));
 System.out.println("Retrieved SLOTB= "+rs.getString(2));
 if(rs.getString(1).compareTo("available")!=0)
     slotA=true;
 
if(rs.getString(2).compareTo("available")!=0)
     slotB=true;
}

con.close();

    
    
     if(slot.compareTo("slotA")==0 &&slotA==true){
       slotA=false;
         System.out.println("Slot A tasking module activated");
    //new Testing().setVisible(true);
     }
     
     else if(slot.compareTo("slotB")==0 && slotB==true){
        System.out.println("SLOT B energy module of "+dateSystem+ "activated.");
        slotB=false;
        System.out.println("Slot B tasking module activated");
       //new Testing().setVisible(true);
        
        
    }

  
}catch(Exception e){ System.out.println(e);} 
    }
}