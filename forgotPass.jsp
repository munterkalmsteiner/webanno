<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
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

    <div class="scrolling flex-content flex-h-container flex-centered">
      <div class="flex-content flex-v-container flex-centered">
        <form id="forgotPassFrm" class="card" action="forgotPassAction.jsp" style="background: white; padding: 3rem 3rem; min-width: 440px; max-width: 30%;">
          <div class="card-body container-sm">
            <h5 class="card-title mb-3 text-center">
              Forgot Password
            </h5>
            <input type="hidden" id="urlfragment" wicket:id="urlfragment" />
            <div class="form-group form-row mb-3">
            Enter your Email Address:
              <input id="username" type="text" name="username" class="form-control" placeholder="User ID"/>
            </div>
            <div class="form-group form-row mb-3">
              <input id="forgotBtn" type="submit" class="btn btn-login btn-primary w-100" value="Reset Password"/>
            </div>
           
          </div>
        </form>
      </div>
    </div>
 
</body>
</html>
