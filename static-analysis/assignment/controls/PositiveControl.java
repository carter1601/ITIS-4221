// PositiveControl.java
// Positive control: every method below contains a real source -> sink flow with no
// sanitizer, so the completed rules MUST report one finding per method marked "ruleid".
// If any of these is missed, the rules are too narrow (false negative).

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PositiveControl {

  // XSS 1: wrapped source param() -> string concat -> wrapped sink ok()
  void xssViaWrappers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String name = param(req, "name");
    String html = "<h1>Hello " + name + "</h1>";
    // ruleid: asmt-java-xss
    ok(resp, html);
  }

  // XSS 2: raw getParameter -> helper call (taint propagates through it) -> PrintWriter.println
  void xssDirectWriter(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String q = req.getParameter("q");
    PrintWriter out = resp.getWriter();
    // ruleid: asmt-java-xss
    out.println(wrap("p", q));
  }

  // XSS 3: a "sanitize"-named helper that does NOT encode must not hide the flow
  void xssMisleadingName(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String t = param(req, "t");
    // ruleid: asmt-java-xss
    ok(resp, sanitizeLooksSafe(t));
  }

  // SQLi 1: wrapped source -> concatenated SQL text -> Statement.executeQuery
  void sqliConcat(HttpServletRequest req, Connection conn) throws SQLException {
    String email = param(req, "email");
    String sql = "SELECT id FROM users WHERE email = '" + email + "'";
    Statement st = conn.createStatement();
    // ruleid: asmt-java-sqli
    ResultSet rs = st.executeQuery(sql);
    rs.close();
  }

  // SQLi 2: PreparedStatement used WRONGLY: tainted value concatenated into the template
  void sqliPreparedButConcatenated(HttpServletRequest req, Connection conn) throws SQLException {
    String id = req.getParameter("id");
    // ruleid: asmt-java-sqli
    PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = " + id);
    ps.executeQuery().close();
  }

  // SQLi 3: concatenated into sql FIRST, then "prepared": still injectable
  void sqliConcatThenPrepare(HttpServletRequest req, Connection conn) throws SQLException {
    String email = param(req, "email");
    String sql = "SELECT id FROM users WHERE email = '" + email + "'";
    // ruleid: asmt-java-sqli
    PreparedStatement ps = conn.prepareStatement(sql);
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

  // Name suggests sanitisation, body only trims: NOT a sanitizer.
  private static String sanitizeLooksSafe(String s) { return s.trim(); }
}
