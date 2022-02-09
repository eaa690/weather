/*
 *  Copyright (C) 2021 Gwinnett County Experimental Aircraft Association
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.eaa690.weather.utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * SSLUtilities.
 */
public class SSLUtilities {

    /**
     * SSL.
     */
    public static final String SSL = "SSL";

    /**
     * Hostname verifier.
     */
    private final HostnameVerifier hostnameVerifier;

    /**
     * Thrust managers.
     */
    private final TrustManager[] trustManagers;

    /**
     * Initializes an instance of <code>SSLUtilities</code> with the default data.
     */
    public SSLUtilities() {
        // Create a trust manager that does not validate certificate chains
        hostnameVerifier = new FakeHostnameVerifier();
        hostnameVerifier.verify(null, null);
        // Create a trust manager that does not validate certificate chains
        FakeX509TrustManager fakeX509TrustManager = new FakeX509TrustManager();
        fakeX509TrustManager.checkClientTrusted(null, null);
        trustManagers = new TrustManager[] {
                fakeX509TrustManager
        };
    }

    /**
     * Set the default Hostname Verifier to an instance of a fake class that trust all hostnames.
     */
    public void trustAllHostnames() {
        // Install the all-trusting host name verifier:
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }

    /**
     * Set the default X509 Trust Manager to an instance of a fake class that trust all certificates, even the
     * self-signed ones.
     */
    public void trustAllHttpsCertificates() {
        SSLContext context;

        // Install the all-trusting trust manager:
        try {
            context = SSLContext.getInstance(SSL);
            context.init(null, trustManagers, new SecureRandom());
        } catch (GeneralSecurityException gse) {
            throw new IllegalStateException(gse.getMessage());
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

}
