/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ac.tut;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.Border;

/**
 *
 * 
 */
public class DBOperationsLibrary {
    private final JFrame ui;
    
    private final JPanel mainPnl;
    private JPanel topPnl, middlePnl, bottomPnl;
    
    private final JPanel connectionPnl;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private JButton btnConnect, btnDisconnect;
    
    private final JPanel infoPnl;
    private final JLabel lblDocID;
    private JTextField txtDocName, txtDocSurname, txtDocEmail;
    private final JSpinner spnDate;
    private final JButton btnAddDoc;
    
    private final JPanel updatePnl;
    private final JTextField txtSearchDocID;
    private JButton btnSearch, btnUpdate, btnDelete;
    private final JComboBox<String> cbxOptions;
    
    private final JPanel outputPnl;
    private final JTextArea txtOutput;
    private final JScrollPane sp;
    
    private Border defaultBorder;
    
    private Connection conn;
    private int count;

    private Connection connection;
    public DBOperationsLibrary() {
        ui = new JFrame("TheSun Surgery App");
        
        mainPnl = new JPanel(new BorderLayout());
        
        topPnl = new JPanel(new BorderLayout());
        middlePnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottomPnl = new JPanel(new BorderLayout());
        
        //top panel work
        connectionPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblUsername = new JLabel("Username  :");
        txtUsername = new JTextField(15);
        JLabel lblPassword = new JLabel("Password  :");
        txtPassword = new JPasswordField(15);
        btnConnect = new JButton("Connect");
        btnDisconnect = new JButton("Disconnect");
        
        connectionPnl.add(lblUsername); connectionPnl.add(txtUsername);
        connectionPnl.add(lblPassword); connectionPnl.add(txtPassword);
        connectionPnl.add(btnConnect);
        connectionPnl.add(btnDisconnect);
        
        //middle panel work
        infoPnl = new JPanel(new GridLayout(6, 1));
        infoPnl.setBorder(javax.swing.BorderFactory.createTitledBorder("Doctor details"));
        JLabel lblDocIDCaption = new JLabel("Doc ID");
        lblDocID = new JLabel("Generate doctor id here");
        JLabel lblDocName = new JLabel("Name        ");
        txtDocName = new JTextField(10);
        JLabel lblDocSurname = new JLabel("Surname     ");
        txtDocSurname = new JTextField(10);
        JLabel lblDocEmail = new JLabel("Email       ");
        txtDocEmail = new JTextField(10);
        JLabel lblDocHire = new JLabel("Hired date       ");
        spnDate = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DATE));
        spnDate.setEditor(new JSpinner.DateEditor(spnDate, "yyyy/MM/dd"));
        btnAddDoc = new JButton("Add Doctor");
        
        infoPnl.add(lblDocIDCaption); infoPnl.add(lblDocID);
        infoPnl.add(lblDocName); infoPnl.add(txtDocName);
        infoPnl.add(lblDocSurname); infoPnl.add(txtDocSurname);
        infoPnl.add(lblDocEmail); infoPnl.add(txtDocEmail);
        infoPnl.add(lblDocHire); infoPnl.add(spnDate);
        infoPnl.add(btnAddDoc);
        
        updatePnl = new JPanel(new GridLayout(4, 0));
        updatePnl.setBorder(javax.swing.BorderFactory.createTitledBorder("Doctor DB"));
        JLabel lblID = new JLabel("Doctor ID");
        txtSearchDocID = new JTextField(5);
        cbxOptions = new JComboBox<>(new String[] {"Choose operation...", "Search", "Update", "Delete"});
        cbxOptions.setSelectedIndex(0);
        JPanel btnsPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnSearch = new JButton("Search"); //btnSearch.setVisible(false);
        btnUpdate = new JButton("Update"); //btnUpdate.setVisible(false);
        btnDelete = new JButton("Delete"); //btnDelete.setVisible(false);
        btnsPnl.add(btnSearch);
        btnsPnl.add(btnUpdate);
        btnsPnl.add(btnDelete);
        
        updatePnl.add(cbxOptions);
        updatePnl.add(lblID);
        updatePnl.add(txtSearchDocID);
        updatePnl.add(btnsPnl);
        
        outputPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outputPnl.setBorder(javax.swing.BorderFactory.createTitledBorder("Results / Console Output"));
        txtOutput = new JTextArea(8, 70);
        sp = new JScrollPane(txtOutput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS , JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        outputPnl.add(sp);
        
        topPnl.add(connectionPnl);
        middlePnl.add(infoPnl);
        middlePnl.add(updatePnl);
        bottomPnl.add(outputPnl);
        
        mainPnl.add(topPnl, BorderLayout.NORTH);
        mainPnl.add(middlePnl, BorderLayout.WEST);
        mainPnl.add(bottomPnl, BorderLayout.SOUTH);
        
        ui.add(mainPnl);
        ui.pack();
        ui.setLocationRelativeTo(null);
        ui.setVisible(true);
        ui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        startApp();
    }

    private void startApp() {
        btnDisconnect.setEnabled(false);
        btnSearch.setVisible(false);
        btnUpdate.setVisible(false);
        btnDelete.setVisible(false);
        
        defaultBorder = txtPassword.getBorder();
        
        count = 0;
        
        
        btnConnect.addActionListener(new ConnectToDBListener());
        btnDisconnect.addActionListener(new DisconnectListener());
        btnAddDoc.addActionListener(new AddDoctorListener());
        cbxOptions.addActionListener(new ShowSelectedOperationListener());
        btnSearch.addActionListener(new SearchDoctorListener());
        btnUpdate.addActionListener(new UpdateDoctorListener());
        btnDelete.addActionListener(new DeleteDoctorListener());
    }
    
    private class ConnectToDBListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String username=txtUsername.getText();
                String Password=txtPassword.getText();
                connection=DriverManager.getConnection("jdbc:derby://localhost:1527/SurgeryData", username, Password);
                btnConnect.setEnabled(false);
                btnDisconnect.setEnabled(true);
            } catch (SQLException ex) {
                Logger.getLogger(DBOperationsLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void getDoctors() throws SQLException
    {
       String sql="SELECT * FROM DOCTORTABLE";
       PreparedStatement ps=connection.prepareStatement(sql);
       ResultSet rs=ps.executeQuery();
       
       while(rs.next()){
           txtOutput.setText("Doctor ID:"+rs.getString("ID")+"\nName: "+rs.getString("NAME")+"\nSurname:"+rs.getString("SURNAME")+"\nEmail:"+rs.getString("EMAIL")+"\nDate:"+rs.getString("DATE"));
           rs.next();
       }
    }
    
    private class DisconnectListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                connection.close();
                btnDisconnect.setEnabled(false);
                btnConnect.setEnabled(true);
            } catch (SQLException ex) {
                Logger.getLogger(DBOperationsLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private class AddDoctorListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            Random rand=new Random();
            int id=rand.nextInt(100);
            count=4;
            String DID="";
            while(count!=0 && DID.length()<=5){
                DID+=Integer.toString(id);
                id=rand.nextInt(100);
                count--;
            }
            String DocId=DID.substring(0, 5);
            String Dname=txtDocName.getText();
            String Dsurname=txtDocSurname.getText();
            String Demail=txtDocEmail.getText();
            String DateEmployed=spnDate.getNextValue().toString();
            String sql="INSERT INTO DOCTORTABLE VALUES(?,?,?,?,?)";
            try {
                PreparedStatement ps=connection.prepareStatement(sql);
                ps.setString(1, DocId);
                ps.setString(2, Dname);
                ps.setString(3, Dsurname);
                ps.setString(4, Demail);
                ps.setString(5, DateEmployed);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBOperationsLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
            txtDocName.setText("");
            txtDocSurname.setText("");
            txtDocEmail.setText("");
            try {
                getDoctors();
            } catch (SQLException ex) {
                Logger.getLogger(DBOperationsLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private class ShowSelectedOperationListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(cbxOptions.getSelectedItem()=="Search"){
                 btnSearch.setVisible(true);
                 btnUpdate.setVisible(false);
                 btnDelete.setVisible(false);
            }else{
                if(cbxOptions.getSelectedItem()=="Update"){
                     btnUpdate.setVisible(true);
                     btnSearch.setVisible(false);
                     btnDelete.setVisible(false);
                }else{
                    if(cbxOptions.getSelectedItem()=="Delete"){
                        btnDelete.setVisible(true);
                        btnSearch.setVisible(false);
                        btnUpdate.setVisible(false);
                    } else{
                        btnDelete.setVisible(false);
                        btnSearch.setVisible(false);
                        btnUpdate.setVisible(false);
                    }   
                }
            } 
            
        }
        
    }
    
    private class SearchDoctorListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            String sql="SELECT * FROM DOCTORTABLE WHERE ID=?";
            try {
                PreparedStatement ps=connection.prepareStatement(sql);
                ps.setString(1, txtSearchDocID.getText());
                ResultSet rs=ps.executeQuery();
                while(rs.next()){
                    txtOutput.setText("Doctor ID:"+rs.getString("ID")+"\nName: "+rs.getString("NAME")+"\nSurname:"+rs.getString("SURNAME")+"\nEmail:"+rs.getString("EMAIL")+"\nDate:"+rs.getString("DATE"));
                }
                rs.close();
                txtSearchDocID.setText("");
            } catch (SQLException ex) {
                Logger.getLogger(DBOperationsLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private class UpdateDoctorListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            String sql="UPDATE DOCTORTABLE SET NAME=?, SURNAME=?, EMAIL=?, DATE=?  WHERE ID=?";
            String name=JOptionPane.showInputDialog("Enter name: ");
            String surname=JOptionPane.showInputDialog("Enter surname: ");
            String mail=JOptionPane.showInputDialog("Enter email: ");
            String date=spnDate.getNextValue().toString();
            try {
                PreparedStatement ps=connection.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, surname);
                ps.setString(3, mail);
                ps.setString(4, txtSearchDocID.getText());
                ps.setString(4, date);
                ps.executeUpdate();
                ps.close();
                txtOutput.setText("Successfully  updated doctor information"+"\n");
            } catch (SQLException ex) {
                Logger.getLogger(DBOperationsLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                getDoctors();
            } catch (SQLException ex) {
                Logger.getLogger(DBOperationsLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private class DeleteDoctorListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            
            String sql="DELETE FROM DOCTORTABLE WHERE ID=?";
            try {
                PreparedStatement ps=connection.prepareStatement(sql);
                ps.setString(1, txtSearchDocID.getText());
                ps.executeUpdate();
                ps.close();
                txtOutput.setText("Successfully  deleted doctor information"+"\n");
            } catch (SQLException ex) {
                Logger.getLogger(DBOperationsLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                getDoctors();
            } catch (SQLException ex) {
                Logger.getLogger(DBOperationsLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
