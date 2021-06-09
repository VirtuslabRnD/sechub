// SPDX-License-Identifier: MIT
package com.daimler.sechub.integrationtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SecurityTestHelper {

    public enum TestTargetType {
        PDS_SERVER("pds"),

        SECHUB_SERVER("server"),

        ;

        private String id;

        private TestTargetType(String id) {
            this.id = id;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(SecurityTestHelper.class);

    /* no longer accepted */
    public static final String SSL_V3 = "sslv3";
    public static final String TLS_V1_0 = "TLSv1";
    public static final String TLS_V1_1 = "TLSv1.1";

    /* accepted */
    public static final String TLS_V1_3 = "TLSv1.3";
    public static final String TLS_V1_2 = "TLSv1.2";

    private URL testURL;

    private TestTargetType targetType;

    private CipherTestData cipherTestData;

    public SecurityTestHelper(TestTargetType targetType, URL testURL) {
        this.testURL = testURL;
        this.targetType = targetType;
    }

    public void assertProtocolNOTAccepted(String protocol) throws Exception {
        SSLTestContext context = new SSLTestContext();
        context.protocol = protocol;
        context.expectProtocolNotAccepted = true;

        callTestURLWithProtocol(context);
    }

    public void assertProtocolAccepted(String protocol) throws Exception {
        SSLTestContext context = new SSLTestContext();
        context.protocol = protocol;
        context.expectProtocolNotAccepted = false;

        callTestURLWithProtocol(context);
    }
    
    String getMac(CipherCheck check) {
        String cipher  =check.cipher;
        if (cipher==null) {
            return null;
        }
        int lastIndex = cipher.lastIndexOf("-");
        if (lastIndex==-1) {
            return null;
        }
        return cipher.substring(lastIndex+1);
    }

    private class SSLTestContext {
        String protocol;
        boolean expectProtocolNotAccepted;
    }

    private void callTestURLWithProtocol(SSLTestContext context) throws Exception {
        LOG.info("********************************************************************************");
        LOG.info("** Start test for protocol:{}, expect to be accepted:{}", context.protocol, !context.expectProtocolNotAccepted);
        LOG.info("** TestURL: {}", testURL);
        LOG.info("********************************************************************************");
        SSLContext sslContext = SSLContext.getInstance(context.protocol);

        TrustManager tm = createAcceptAllTrustManger();
        sslContext.init(null, new TrustManager[] { tm }, null);

        URLConnection urlConnection = testURL.openConnection();
        HttpsURLConnection httpsConnection = (HttpsURLConnection) urlConnection;

        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        httpsConnection.setSSLSocketFactory(socketFactory);

        /*
         * next fetch of conent is also necessary and we do also getotherwise we have a
         * "java.lang.IllegalStateException: connection not yet open"
         */
        print_content(httpsConnection);
        /*
         * next connect is important, otherwise we have a
         * "java.lang.IllegalStateException: connection not yet open"
         */
        httpsConnection.connect();

        fetchContentAndCheckForSSLHandshakeFailures(context, httpsConnection);
    }

    private void print_content(HttpsURLConnection con) {
        if (con == null) {
            throw new IllegalArgumentException("con may not be null!");
        }

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String input;

            while ((input = br.readLine()) != null) {
                LOG.info(input);
            }
            br.close();

        } catch (IOException e) {
            /* just ignore */
            LOG.info("no content found - reason:{}", e.getMessage());
        }

    }

    private void fetchContentAndCheckForSSLHandshakeFailures(SSLTestContext context, HttpsURLConnection con) {
        if (con == null) {
            throw new IllegalArgumentException("con may not be null!");
        }
        SSLHandshakeException handshakeException = null;
        try {

            LOG.info("Response Code : " + con.getResponseCode());
            LOG.info("Cipher Suite : " + con.getCipherSuite());

            Certificate[] certs = con.getServerCertificates();
            for (Certificate cert : certs) {
                LOG.info("Cert Type : " + cert.getType());
                LOG.info("Cert Hash Code : " + cert.hashCode());
                LOG.info("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
                LOG.info("Cert Public Key Format : " + cert.getPublicKey().getFormat());
            }

        } catch (SSLPeerUnverifiedException e) {
            throw new IllegalStateException("should not happen in test case");
        } catch (SSLHandshakeException e) {
            handshakeException = e;
        } catch (IOException e) {
            throw new IllegalStateException("should not happen in test case");
        }
        if (context.expectProtocolNotAccepted) {
            if (handshakeException == null) {
                fail("Protocol " + context.protocol + " was accepted! There was no handshake exception !");
            }
        } else {
            if (handshakeException != null) {
                handshakeException.printStackTrace();
                fail("Protocol " + context.protocol + " was NOT accepted! There was a handshake exception:" + handshakeException.getMessage());
            }
        }

    }

    private TrustManager createAcceptAllTrustManger() {
        TrustManager tm = new X509TrustManager() {

            private X509Certificate[] emptyCertificatesArray = new X509Certificate[] {};

            public void /* NOSONAR */ checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                /* we do not check the client - we trust all */
            }

            public void /* NOSONAR */ checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                /* we do not check the server - we trust all */
            }

            public X509Certificate[] getAcceptedIssuers() {
                return emptyCertificatesArray;
            }
        };
        return tm;
    }
    
    public void assertNotContainedMacsInCiphers(String... notAllowedMacs) throws Exception {
        ensureCipherTestDone();

        for (CipherCheck check : cipherTestData.cipherChecks) {
            if ("true".equals(check.verified)) {
                String mac = getMac(check);
                
                if (mac==null) {
                    fail("mac is null in test cipher - should not happen!");
                }
                
                for (String notAllowedMac: notAllowedMacs) {
                    
                    if (mac.equalsIgnoreCase(notAllowedMac)){
                        fail("Not wanted mac:"+mac+" found inside verfified cipher:"+check.cipher);
                    }
                }
            }
        }


    }

    public void assertOnlyAcceptedSSLCiphers(String... cipherNames) throws Exception {
        ensureCipherTestDone();

        for (String cipherName : cipherNames) {
            assertSSLCipher(cipherName, true);
        }

        int verified = 0;
        for (CipherCheck check : cipherTestData.cipherChecks) {
            if ("true".equals(check.verified)) {
                verified++;
            }
        }

        assertEquals("Amount of verified not as expected", cipherNames.length, verified);

    }

    public void assertSSLCipherNotAccepted(String cipherName) throws Exception {
        assertSSLCipher(cipherName, false);
    }

    public void assertSSLCipherAccepted(String cipherName) throws Exception {
        assertSSLCipher(cipherName, true);
    }

    private void assertSSLCipher(String cipherName, boolean cipherShallBeVerifiedWithTrue) throws Exception {
        ensureCipherTestDone();

        boolean found = false;
        for (CipherCheck check : cipherTestData.cipherChecks) {
            if (!cipherName.equals(check.cipher)) {
                continue;
            }
            found = true;
            if ("true".equalsIgnoreCase(check.verified)) {
                if (cipherShallBeVerifiedWithTrue) {
                    return;
                } else {
                    fail("Cipher:" + cipherName + " was accepted by " + targetType + ", but should not!");
                }
            } else if ("false".equalsIgnoreCase(check.verified)) {
                if (!cipherShallBeVerifiedWithTrue) {
                    return;
                } else {
                    fail("Cipher:" + cipherName + " was NOT accepted by " + targetType + ", but should!");
                }
            }
            throw new IllegalStateException("The expected cipher was found in cipher test data, but it was not possible to check verification :" + cipherName
                    + " was verified as " + check.verified);
        }
        if (!found) {
            throw new IllegalStateException("The expected cipher:" + cipherName + " \n was NOT found in cipher test data:" + cipherName + " at " + targetType
                    + "\n" + collectionOfSupportedCiphers());
        }
    }

    private String collectionOfSupportedCiphers() throws Exception {
        ensureCipherTestDone();

        StringBuilder sb = new StringBuilder();
        sb.append("Accepted ciphers from server:\n");
        for (CipherCheck check : cipherTestData.cipherChecks) {
            if ("true".equalsIgnoreCase(check.verified)) {
                sb.append(check.cipher);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void ensureCipherTestDone() throws Exception {
        if (cipherTestData != null) {
            return;
        }
        List<String> commands = new ArrayList<>();
        commands.add("./ciphertest.sh");
        commands.add("localhost:" + testURL.getPort());
        commands.add(targetType.id);

        /*
         * now we call ciphertest.sh with parameters - will create
         * /sechub-integrationtest/build/testresult/ciphertest/sechub-pds.json or
         * /sechub-integrationtest/build/testresult/ciphertest/sechub-server.json
         */

        ProcessBuilder pb = new ProcessBuilder(commands);
        Process process = pb.start();
        boolean exited = process.waitFor(10, TimeUnit.SECONDS);
        if (!exited) {
            throw new IllegalStateException("Was not able to wait for ciphertest.sh result");
        }
        TextFileReader reader = new TextFileReader();
        File file = new File("./build/test-results/ciphertest/sechub-" + targetType.id + ".json");
        String text = reader.loadTextFile(file);

        ObjectMapper mapper = JSONTestSupport.DEFAULT.createObjectMapper();
        cipherTestData = mapper.readValue(text.getBytes(), CipherTestData.class);

    }
}
