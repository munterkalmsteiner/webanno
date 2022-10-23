<%--Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. --%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
 <link rel="stylesheet" type="text/css" href="./wicket/resource/de.tudarmstadt.ukp.clarin.webanno.ui.core.bootstrap.CustomBootstrapSassReference/bootstrap-ver-C850FFDFD7B8FBFC7B1F3A401C0E134C.scss" />
<script type="text/javascript" src="./wicket/resource/de.agilecoders.wicket.core.markup.html.references.JQueryMigrateJavaScriptReference/js/jquery-migrate-1.3.0-ver-322AF89581C7A1AD21A8149F51B8CC16.js"></script>
<script type="text/javascript" src="./wicket/resource/de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference/webjars/popper.js/1.14.1/umd/popper-ver-2747E9FA7990920F98CD9959A0DAB50A.js"></script>
<script type="text/javascript" id="bootstrap-js" src="./wicket/resource/de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference/webjars/bootstrap/4.5.3/js/bootstrap-ver-3B601FA8FDF58E99E53CD2281B8D08D5.js"></script>
<link rel="stylesheet" type="text/css" href="./wicket/resource/com.googlecode.wicket.kendo.ui.theme.Initializer/kendo.common-bootstrap.min-ver-83B07F422B440D28AE690EA66CFE3F1C.css" />
<link rel="stylesheet" type="text/css" href="./wicket/resource/com.googlecode.wicket.kendo.ui.theme.Initializer/kendo.bootstrap.min-ver-0018E43B5CC1E11D97EC81B0C8CD3824.css" />
<script type="text/javascript" src="./wicket/resource/com.googlecode.wicket.kendo.ui.resource.KendoUIJavaScriptResourceReference/kendo.ui.core.min-ver-10175E9BDDC20AC9642EDA22448C3D1D.js"></script>
<script type="text/javascript" src="./wicket/resource/org.apache.wicket.ajax.AbstractDefaultAjaxBehavior/res/js/wicket-ajax-jquery-ver-4D09ABFD59C4D1E8C40853E2941D8163.js"></script>
<script type="text/javascript" src="./wicket/resource/de.tudarmstadt.ukp.clarin.webanno.ui.core.kendo.WicketJQueryFocusPatch/wicket-jquery-focus-patch-ver-49505C1C9E7166E834F4D8958FCCE782.js"></script>
<link rel="stylesheet" type="text/css" href="./wicket/resource/com.googlecode.wicket.jquery.ui.theme.Initializer/jquery-ui-ver-D9A6894D9BF9260FD94A1E062A165890.css" />
<link rel="stylesheet" type="text/css" href="./wicket/resource/de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference/webjars/font-awesome/5.11.2/css/all-ver-41D394990448B2C2B1AFE840E837DC8E.css" />
<script type="text/javascript" src="./wicket/resource/de.tudarmstadt.ukp.clarin.webanno.ui.core.css.CssBrowserSelectorResourceReference/css_browser_selector-ver-4C9B08D5A93F0000CE85CE69866C7EF8.js"></script>
<link rel="stylesheet" type="text/css" href="./wicket/resource/de.tudarmstadt.ukp.clarin.webanno.ui.core.page.WebAnnoCssReference/webanno-ver-095861B1E2847C7D732AAF24E1159DF3.css" />
<script type="text/javascript" src="./wicket/resource/de.tudarmstadt.ukp.clarin.webanno.ui.core.page.WebAnnoJavascriptReference/webanno-ver-2319C90C40429F42F25A6A1B723CB33C.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
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

<meta charset="ISO-8859-1">
<title>Insert title here</title>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>

<script>

$(document).ready(function(){
	localStorage.removeItem("msg");
	if(localStorage.getItem("notice")!=null){
		$("#manageNotice").show();
		$("#setNotice").hide()
	}
	$("#yes").click(function(){
		localStorage.removeItem("notice");
		$("#manageNotice").hide();
		$("#setNotice").show()
	})
	$("#no").click(function(){
		window.location.href="http://localhost:8080/webanno-webapp-4.0.0-SNAPSHOT/telemetry.html?3-1.-menubar-logoutPanel-logout";
	})
	$("#cancel").click(function(){
		localStorage.removeItem("notice");
		window.location.href="http://localhost:8080/webanno-webapp-4.0.0-SNAPSHOT/telemetry.html?3-1.-menubar-logoutPanel-logout";
	})
	
	$("#noticeBtn").click(function(){
		
		localStorage.setItem("notice",$("#notice").val())
		//alert(localStorage.getItem("notice"));
		window.location.href="http://localhost:8080/webanno-webapp-4.0.0-SNAPSHOT/telemetry.html?3-1.-menubar-logoutPanel-logout";
		
	})
	
});


</script>

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
      
      <div class="navbar-nav">
        
      
        <div id="id1" hidden="" data-wicket-placeholder=""></div>
    
        
      </div>
    </nav>
  
  </div>
<h3 style="color:green">Set Notice</h3>
<div id="manageNotice" style="display:none">
<h3>You have already set one maintainance notice</h3>
<li> Want to Remove that and set another - Yes</li>
<li> Continue with Old Notice - No</li>
<li> Remove Notice - Cancel</li>
<input id="yes" type="button" class="btn btn-primary" value="Yes">
<input id="no" type="button" class="btn btn-info" value="No">
<input id="cancel" type="button" class="btn btn-danger" value="Cancel">
</div>

<div id="setNotice" class="scrolling flex-content flex-h-container flex-centered">
      <div class="flex-content flex-v-container flex-centered">
        <div class="form-group form-row mb-3">
            Enter Notice:
              <input id="notice" type="text" name="notice" class="form-control" placeholder="Notice"/>
            </div>
    
       <div class="form-group form-row mb-3">
              <input id="noticeBtn" type="button" class="btn btn-login btn-primary w-100" value="Set"/>
            </div>
</div>
</div>

</body>
</html>