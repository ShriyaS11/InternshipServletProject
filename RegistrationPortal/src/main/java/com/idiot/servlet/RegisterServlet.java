package com.idiot.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final String insertPersonalInfoQuery = "INSERT INTO PERSONALINFORMATION(NAME, PHONENUMBER, ALTERNATEPHONENUMBER, FATHERSNAME, FULLADDRESS, EMAILADDRESS, PINCODE) VALUES(?,?,?,?,?,?,?)";
    private static final String insertBankDetailsQuery = "INSERT INTO BANKDETAILS(PERSONAL_ID, BANKNAME, IFSCCODE, ACCOUNTNO) VALUES(?,?,?,?)";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Get print writer
        PrintWriter pw = res.getWriter();
        // Set content type
        res.setContentType("text/html");

        // Personal Information
        String NAME = req.getParameter("Name");
        String PHONENUMBER = req.getParameter("PhoneNumber");
        String ALTERNATEPHONENUMBER = req.getParameter("AlternatePhoneNumber");
        String FATHERSNAME = req.getParameter("FathersName");
        String FULLADDRESS = req.getParameter("FullAddress");
        String EMAILADDRESS = req.getParameter("Email");
        String PINCODE = req.getParameter("PinCode");

        // Bank Details
        String BANKNAME = req.getParameter("BankName");
        String IFSCCODE = req.getParameter("IFSCCode");
        String ACCOUNTNO = req.getParameter("AccountNo");

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql:///details", "root", "shriya")) {
                // Insert into PERSONALINFORMATION table
                try (PreparedStatement psPersonal = con.prepareStatement(insertPersonalInfoQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    psPersonal.setString(1, NAME);
                    psPersonal.setString(2, PHONENUMBER);
                    psPersonal.setString(3, ALTERNATEPHONENUMBER);
                    psPersonal.setString(4, FATHERSNAME);
                    psPersonal.setString(5, FULLADDRESS);
                    psPersonal.setString(6, EMAILADDRESS);
                    psPersonal.setString(7, PINCODE);
                    psPersonal.executeUpdate();

                    // Get the generated PERSONAL_ID
                    try (var rs = psPersonal.getGeneratedKeys()) {
                        if (rs.next()) {
                            int personalId = rs.getInt(1);

                            // Insert into BANKDETAILS table
                            try (PreparedStatement psBank = con.prepareStatement(insertBankDetailsQuery)) {
                                psBank.setInt(1, personalId);
                                psBank.setString(2, BANKNAME);
                                psBank.setString(3, IFSCCODE);
                                psBank.setString(4, ACCOUNTNO);
                                int bankDetailsCount = psBank.executeUpdate();
                                if (bankDetailsCount == 1) {
                                    pw.println("<h2>Record is registered successfully</h2>");
                                } else {
                                    pw.println("<h2>Failed to register bank details</h2>");
                                }
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            pw.println("<h1>" + e.getMessage() + "</h1>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }
}
