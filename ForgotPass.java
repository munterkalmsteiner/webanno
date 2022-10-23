/*
 * Licensed to the Technische Universität Darmstadt under one
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
 * limitations under the License.
 */

package de.tudarmstadt.ukp.clarin.webanno.ui.core.login;

import static de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior.visibleWhen;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.apache.wicket.NonResettingRestartException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.parameter.UrlRequestParametersAdapter;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.savedrequest.SavedRequest;

import de.tudarmstadt.ukp.clarin.webanno.api.SessionMetaData;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.model.Role;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.clarin.webanno.support.SettingsUtil;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.page.ApplicationPageBase;

@StatelessComponent
public class ForgotPass 
	extends ApplicationPageBase
{
    private static final long serialVersionUID = -333578034707672294L;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private @SpringBean UserDao userRepository;
    private @SpringBean(required = false) SessionRegistry sessionRegistry;
 

    private ForgotPassForm form;
 
    private final Button resetPass;
    
    public ForgotPass()
    {
        setStatelessHint(true);
        setVersioned(false);

        add(form = new ForgotPassForm("forgotPassFrm"));
        resetPass = new Button("resetPassword");
   
        form.add(resetPass);
       


        // Create admin user if there is no user yet
      
    }

    
    
    private class ForgotPassForm
    extends StatelessForm<ForgotPassForm>
{
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String urlfragment;

    public ForgotPassForm(String id)
    {
        super(id);
        setModel(new CompoundPropertyModel<>(this));
        add(new RequiredTextField<String>("username"));
       
        @SuppressWarnings("deprecation")
		Properties settings = SettingsUtil.getSettings();
        String loginMessage = settings.getProperty(SettingsUtil.CFG_LOGIN_MESSAGE);
        add(new Label("loginMessage", loginMessage).setEscapeModelStrings(false)
                .add(visibleWhen(() -> isNotBlank(loginMessage))));
    }

    @Override
    protected void onSubmit()
    {
        String redirectUrl = getRedirectUrl();
        AuthenticatedWebSession session = AuthenticatedWebSession.get();
        if (session.signIn(username, password)) {
            log.debug("Login successful");
            if (sessionRegistry != null) {
                // Form-based login isn't detected by SessionManagementFilter. Thus handling
                // session registration manually here.
                HttpSession containerSession = ((ServletWebRequest) RequestCycle.get()
                        .getRequest()).getContainerRequest().getSession(false);
                sessionRegistry.registerNewSession(containerSession.getId(), username);
            }
            setDefaultResponsePageIfNecessary(redirectUrl);
        }
        else {
            error("Login failed");
        }
    }

    private void setDefaultResponsePageIfNecessary(String aRedirectUrl)
    {
        // This does not work because it was Spring Security that intercepted the access, not
        // Wicket continueToOriginalDestination();

        if (aRedirectUrl == null || aRedirectUrl.contains(".IBehaviorListener.")
                || aRedirectUrl.contains("-logoutPanel-")) {
            log.debug("Redirecting to welcome page");
            setResponsePage(getApplication().getHomePage());
        }
        else {
            log.debug("Redirecting to saved URL: [{}]", aRedirectUrl);
            if (isNotBlank(form.urlfragment) && form.urlfragment.startsWith("!")) {
                Url url = Url.parse("http://dummy?" + form.urlfragment.substring(1));
                UrlRequestParametersAdapter adapter = new UrlRequestParametersAdapter(url);
                LinkedHashMap<String, StringValue> params = new LinkedHashMap<>();
                for (String name : adapter.getParameterNames()) {
                    params.put(name, adapter.getParameterValue(name));
                }
                Session.get().setMetaData(SessionMetaData.LOGIN_URL_FRAGMENT_PARAMS, params);
            }
            throw new NonResettingRestartException(aRedirectUrl);
        }
    }
}
    private String getRedirectUrl()
    {
        String redirectUrl = null;

        HttpSession session = ((ServletWebRequest) RequestCycle.get().getRequest())
                .getContainerRequest().getSession(false);
        if (session != null) {
            SavedRequest savedRequest = (SavedRequest) session
                    .getAttribute("SPRING_SECURITY_SAVED_REQUEST");
            if (savedRequest != null) {
                redirectUrl = savedRequest.getRedirectUrl();
            }
        }

        // There is some kind of bug that logs the user out again if the redirect page is
        // the context root and if that does not end in a slash. To avoid this, we add a slash
        // here. This is rather a hack, but I have no idea why this problem occurs. Figured this
        // out through trial-and-error rather then by in-depth debugging.
        String baseUrl = RequestCycle.get().getUrlRenderer().renderFullUrl(Url.parse(""));
        if (baseUrl.equals(redirectUrl)) {
            redirectUrl += "/";
        }

        // In case there was a URL fragment in the original URL, append it again to the redirect
        // URL.
        if (redirectUrl != null && isNotBlank(form.urlfragment)) {
            redirectUrl += "#" + form.urlfragment;
        }

        return redirectUrl;
    }
}



