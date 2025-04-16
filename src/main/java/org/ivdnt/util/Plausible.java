package org.ivdnt.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for sending events to a Plausible analytics server.
 */
public class Plausible {

    private final static Logger logger = LoggerFactory.getLogger(Plausible.class);

    /** Plausible server URL */
    private final URI apiUri;

    /** Plausible domain (site ID) */
    private final String domain;

    public Plausible(String plausibleApiUri, String domain) {
        this.apiUri = URI.create(plausibleApiUri);
        this.domain = domain;
    }

    public static String getOriginatingAddress(HttpServletRequest request) {
        if (request ==  null)
            return "127.0.0.1";
        String remoteAddr = request.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty())
            remoteAddr = request.getRemoteAddr();
        return remoteAddr;
    }

    /** Send a pageview event to Plausible.
     *
     * @param url URL of the pageview
     * @param servletRequest The servlet request object, used to get the user agent and remote ip
     */
    public void sendPlausiblePageview(String url, HttpServletRequest servletRequest) {
        String payload = String.format("""
            {
              "name": "pageview",
              "url": "%s",
              "domain": "%s"
            }
            """, StringEscapeUtils.escapeJson(url), StringEscapeUtils.escapeJson(domain));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(apiUri)
                .header("Content-Type", "application/json")
                .header("User-Agent", servletRequest == null ? "Java Plausible class" : servletRequest.getHeader("User-Agent"))
                .header("X-Forwarded-For", getOriginatingAddress(servletRequest))
                .POST(BodyPublishers.ofString(payload))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Status codes 200 OK, 201 Created, 202 Accepted, etc. are all fine
            // (BTW Plausible seems to send 202 on success)
            if (response.statusCode() / 100 != 2) {
                logger.error("Failed to send Plausible event: {} (start of body follows)", response.statusCode());
                logger.error(response.body().substring(0, 100));
            }
        } catch (Exception e) {
            logger.error("Exception thrown while sending Plausible event", e);
        }
    }

    public static void main(String[] args) {
        // How to use
        Plausible plausible = new Plausible("https://statistiek.ivdnt.org/api/event", "my-app.ivdnt.org");
        HttpServletRequest servletRequest = null; // should be the actual servlet request object of course...
        plausible.sendPlausiblePageview("https://my-app.ivdnt.org/some/path?query=string", servletRequest);
    }
}
