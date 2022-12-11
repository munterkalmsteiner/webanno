<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ page import="javax.mail.internet.InternetAddress,javax.mail.internet.MimeMessage,java.util.*,de.tudarmstadt.ukp.clarin.webanno.security.*,de.tudarmstadt.ukp.clarin.webanno.security.model.*,javax.mail.*" %>    
<!DOCTYPE html>
<!--
  Licensed to the Technische Universität Darmstadt under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The Technische Universität Darmstadt 
  licenses this file to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.
   
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<html xmlns:wicket="http://wicket.apache.org">
<head>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">

      </script>
  <wicket:head>
    <style>
      :root {
        --input-padding-x: 1.5rem;
        --input-padding-y: 1.5rem;
      }
      
      .form-group input[type=text],input[type=password] {
        padding: var(--input-padding-y) var(--input-padding-x);
        border-radius: 2rem;
      }
      
      .btn-login {
        font-size: 0.9rem !important;
        letter-spacing: 0.05rem !important;
        padding: 0.75rem 1rem !important;
        border-radius: 2rem !important;
      }
    </style>
  </wicket:head>
</head>
<body>


<div class="page-header">
    
    <nav class="navbar navbar-expand navbar-dark bg-dark px-2">
      <div class="navbar-header">
        <a class="navbar-brand" href="welcome.html">
          <span>
            <img class="pageicon" src="images/user_go.png">
            Log in 
          </span>
        </a>
      </div>
  
      <div class="navbar-nav mr-auto">
        <div class="nav-item">
          <a class="nav-link" href="welcome.html">
            <i class="fas fa-home" aria-hidden="true"></i>
            <span class="nav-link active p-0 d-none d-md-inline">
              Home
            </span>
          </a>
          
          
        </div>
      </div>
      
 <script>
      $(document).ready(function() {
    	  localStorage.removeItem("message");
    	  $("#forgotBtn").click(function(){
    		  localStorage.setItem("message","Password reset Successfully!");
    		  if($("password").val()!==$("cpassword").val()){
    			  alert("Password & Confirm Password doesn't match");
    			 // return false;
    			  
    		  }else{
    			 
    			  $("#forgotPassFrm").attr("action","forgotPass.jsp?sendMail=true");
    		  }
    		  
    		  
    	  })
    	  
      });
      </script>
      <div class="navbar-nav">
        
      
        <div id="id1" hidden="" data-wicket-placeholder=""></div>
    
        
      </div>
    </nav>
  
  </div>
<%if(request.getParameter("sendMail")!=null) {%>

<%


// Recipient's email ID needs to be mentioned.
String to = "moinamazon123@gmail.com";

// Sender's email ID needs to be mentioned
String from = "multipurposemail23@gmail.com";

// Assuming you are sending email from through gmails smtp
String host = "smtp.gmail.com";

// Get system properties
Properties properties = new Properties();

// Setup mail server
properties.put("mail.smtp.host", host);
properties.put("mail.smtp.port", "465");
properties.put("mail.transport.protocol", "smtp");


properties.put("mail.smtp.starttls.enable", "true");
properties.put("mail.smtp.ssl.enable", "true");
properties.put("mail.smtp.auth", "true");

// Get the Session object.// and pass username and password
Session session1 = Session.getInstance(properties, new javax.mail.Authenticator() {

    protected PasswordAuthentication getPasswordAuthentication() {

        return new PasswordAuthentication("multipurposemail23@gmail.com", "growwell@17");

    }

});

// Used to debug SMTP issues
session1.setDebug(true);

try {
    // Create a default MimeMessage object.
    MimeMessage message = new MimeMessage(session1);

    // Set From: header field of the header.
    message.setFrom(new InternetAddress(from));

    // Set To: header field of the header.
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

    // Set Subject: header field
    message.setSubject("This is the Subject Line!");

    // Now set the actual message
    message.setText("This is actual message");

   out.println("sending...");
   response.sendRedirect("http://localhost:8080/webanno-webapp-4.0.0-SNAPSHOT/telemetry.html");
    // Send message
    Transport.send(message);
   out.println("Sent message successfully....");

} catch (MessagingException mex) {
    mex.printStackTrace();
}







%>


<%} %>
<%if(request.getParameter("change")!=null) {%>

<div class="scrolling flex-content flex-h-container flex-centered">
      <div class="flex-content flex-v-container flex-centered">
        <form id="forgotPassFrm" class="card" method="POST" action="forgotPass.jsp?sendMail=true" style="background: white; padding: 3rem 3rem; min-width: 440px; max-width: 30%;">
          <div class="card-body container-sm">
            <h5 class="card-title mb-3 text-center">
              Forgot Password 

            </h5>
            <input type="hidden" id="urlfragment" wicket:id="urlfragment" />
            <div class="form-group form-row mb-3">
            Enter your Password:
              <input id="password" type="password" name="password" class="form-control" placeholder="Password"/>
            </div>
              <div class="form-group form-row mb-3">
            Retype Password:
              <input id="cpassword" type="password" name="cpassword" class="form-control" placeholder="Confirm Password"/>
            </div>
            <div class="form-group form-row mb-3">
              <input id="forgotBtn" type="submit" class="btn btn-login btn-primary w-100" value="Submit"/>
            </div>
           
          </div>
        </form>
      </div>
    </div>

<% } else { %>
<%! UserDao userDao =  new UserDaoImpl();
%>

    <div class="scrolling flex-content flex-h-container flex-centered">
      <div class="flex-content flex-v-container flex-centered">
        <form id="forgotPassFrm" class="card" method="POST" action="forgotpasspage.html" style="background: white; padding: 3rem 3rem; min-width: 440px; max-width: 30%;">
          <div class="card-body container-sm">
            <h5 class="card-title mb-3 text-center">
              Forgot Password 

            </h5>
            <input type="hidden" id="urlfragment" wicket:id="urlfragment" />
            <div class="form-group form-row mb-3">
            Enter your Email:
              <input id="username" type="text" name="username" class="form-control" placeholder="Email Address"/>
            </div>
            <div class="form-group form-row mb-3">
              <input id="forgotBtn1" type="submit" class="btn btn-login btn-primary w-100" value="Reset Password"/>
            </div>
           
          </div>
        </form>
      </div>
    </div>
    <%} %>
 
</body>
</html>
