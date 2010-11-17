<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.mobile.trippy.web.server.service.Utils" %>
<%@ page import="com.google.mobile.trippy.web.server.service.EulaUtil" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.Properties" %>

<%
  String ua = request.getHeader( "User-Agent" );
  boolean isMSIE = ( ua != null && ua.indexOf( "MSIE" ) != -1 );
  if(isMSIE) {
    response.sendRedirect("/internetExplorer.html");
  }

  InputStream is = getServletContext().getResourceAsStream("/config/config.properties");
  Properties  p  = new Properties();
  p.load(is);
  is.close();
  
  String mode = p.getProperty("mode");
  if (mode.equals("migration")) {
%>
<HTML><BODY>The system is under migration. Please try again later.</BODY></HTML> 
<%
  } else {
%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
	   <!-- <meta content="width=device-width,minimum-scale=1.0,maximum-scale=1.0" name="viewport"/> -->
    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    <link type="text/css" rel="stylesheet" href="Trippy.css">

    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>Trippy</title>

    <script src="modernizr-1.5.min.js"></script>
    <script>
      if (Modernizr.geolocation) {
        if (Modernizr.localstorage) {
        } else {
          alert("Your browser does not seem to be HTML5 compliant.  Trippy may not function properly.");
        }
      } else {
        alert("Your browser does not seem to be HTML5 compliant.  Trippy may not function properly.");
      }          
    </script>
    <script type="text/javascript">
      <%
  		  User user = Utils.getCurrentUser();
      	if (user == null) {
      	  response.sendRedirect(Utils.getLoginUrl(request.getRequestURI()));
      	  return;
      	}
      	if (!(new EulaUtil()).isEulaUserExists(user.getEmail())) {
          response.sendRedirect("/showEula.html");
          return;
        }
      %>
      var userEmail_ = "<%= user.getEmail() %>";
      var userNickName_ = "<%= user.getNickname() %>";
      var loginUrl_ = "<%= Utils.getLoginUrl() %>";
      var logoutUrl_ = "<%= Utils.getLogoutUrl() %>";
      
      <%
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2010, 7, 9, 23, 0);
      %>
      var versionTime = "<%= cal.getTimeInMillis() + "" %>";
      
      TLS = new Object();
      TLS.keys = [];
      TLS.store = {};
      TLS.getLength = function() {
        return this.keys.length;
      }
      TLS.key = function(i) {
        return this.keys[i];
      }
      TLS.getItem = function(key) {
        return this.store[key];
      }
      TLS.setItem = function(key, data) {
        this.store[key] = data;
      }
      TLS.clear = function() {
        this.store = {};
        this.keys = [];
      }
    </script>
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="trippy/trippy.nocache.js"></script>
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    <div id="main"></div>
    <!-- <script src='http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js' /> -->
  
  </body>
</html>
<%
  }
%>
