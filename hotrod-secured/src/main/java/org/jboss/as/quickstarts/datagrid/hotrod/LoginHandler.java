package org.jboss.as.quickstarts.datagrid.hotrod;

import javax.security.auth.callback.*;
import javax.security.sasl.RealmCallback;
import java.io.IOException;

/**
 * @author Vitalii Chepeliuk
 */
public class LoginHandler implements CallbackHandler {
    final private String login;
    final private char[] password;
    final private String realm;

    public LoginHandler(String login, char[] password, String realm) {
        this.login = login;
        this.password = password;
        this.realm = realm;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                ((NameCallback) callback).setName(login);
            } else if (callback instanceof PasswordCallback) {
                ((PasswordCallback) callback).setPassword(password);
            } else if (callback instanceof RealmCallback) {
                ((RealmCallback) callback).setText(realm);
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }
}