package wsg.tools.internet.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to import, export, or edit cookies manually.
 *
 * @author Kingen
 * @since 2021/1/7
 */
public class CookieEditor {

    /**
     * Import cookies from the given file into the site.
     * <p>
     * The content of the file should be a json string of {@link List<ExportedCookie>}.
     */
    public static void importCookies(File file, BaseSite site) throws IOException {
        List<ExportedCookie> exportedCookies = new ObjectMapper().readValue(file, new TypeReference<>() {});
        List<Cookie> cookies = new ArrayList<>();
        for (ExportedCookie exportedCookie : exportedCookies) {
            BasicClientCookie cookie = new BasicClientCookie(exportedCookie.getName(), exportedCookie.getValue());
            cookie.setDomain(exportedCookie.getDomain());
            cookie.setExpiryDate(exportedCookie.getExpirationDate());
            cookie.setPath(exportedCookie.getPath());
            cookie.setSecure(exportedCookie.isSecure());
            cookies.add(cookie);
        }
        site.addCookies(cookies);
    }
}
