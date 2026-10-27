// NegativeControl.java
// Negative control: each method has a source and a sink, but the flow is broken by a
// real HTML-text encoder (escapeHtml4) or by a fixed SQL template with a bound value.
// The completed rules MUST report zero findings in this file.
// Any finding here is a false positive.

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;

public class NegativeControl {

  // XSS safe 1: encoded with the local escape() wrapper (escapeHtml4) before ok()
  void xssEscapedWrapper(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String name = param(req, "name");
    // ok: asmt-java-xss
    ok(resp, "<h1>Hello " + escape(name) + "</h1>");
  }

  // XSS safe 2: encoded directly with StringEscapeUtils before PrintWriter.println
  void xssEscapedDirect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String q = req.getParameter("q");
    String safe = StringEscapeUtils.escapeHtml4(q);
    PrintWriter out = resp.getWriter();
    // ok: asmt-java-xss
    out.println(wrap("p", safe));
  }

  // SQLi safe 1: constant SQL text with placeholder, tainted value bound as a parameter
  void sqliPrepared(HttpServletRequest req, Connection conn) throws SQLException {
    String email = param(req, "email");
    // ok: asmt-java-sqli
    try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE email = ?")) {
      ps.setString(1, email);
      try (ResultSet rs = ps.executeQuery()) {
        rs.next();
      }
    }
  }

  // SQLi safe 2: same, with the template held in a constant (mirrors the patched servlet)
  private static final String LOOKUP = "SELECT id, email FROM users WHERE email = ?";
  void sqliPreparedConstant(HttpServletRequest req, Connection conn) throws SQLException {
    String email = req.getParameter("email");
    // ok: asmt-java-sqli
    PreparedStatement ps = conn.prepareStatement(LOOKUP);
    ps.setString(1, email);
    ps.executeQuery().close();
  }

  // ---- local helpers, same shape as AssignmentServlet ----
  private String param(HttpServletRequest req, String k) {
    String v = req.getParameter(k);
    return v == null ? "" : v;
  }

  private void ok(HttpServletResponse resp, String html) throws IOException {
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getWriter().println(html);
  }

  private static String wrap(String tag, String c) { return "<" + tag + ">" + c + "</" + tag + ">"; }

  private static String escape(String s) { return s == null ? "" : StringEscapeUtils.escapeHtml4(s); }
}
