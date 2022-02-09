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

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * FakeX509TrustManager.
 */
public class FakeX509TrustManager implements X509TrustManager {

    /**
     * Empty array of certificate authority certificates.
     */
    private static final X509Certificate[] ACCEPTED_ISSUERS = new X509Certificate[] {};

    /**
     * Always trust for client SSL chain peer certificate chain with any authType authentication types.
     *
     * @param chain the peer certificate chain.
     * @param authType the authentication type based on the client certificate.
     */
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
        // Do nothing
    }

    /**
     * Always trust for server SSL chain peer certificate chain with any authType exchange algorithm types.
     *
     * @param chain the peer certificate chain.
     * @param authType the key exchange algorithm used.
     */
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
        // Do nothing
    }

    /**
     * Return an empty array of certificate authority certificates which are trusted for authenticating peers.
     *
     * @return a empty array of issuer certificates.
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return ACCEPTED_ISSUERS;
    }
}
