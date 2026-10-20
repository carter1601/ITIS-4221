#!/usr/bin/env python3
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse, parse_qs
import datetime

HOST, PORT = "127.0.0.1", 9000


def log(kind, data):
    stamp = datetime.datetime.now().isoformat(timespec="seconds")
    line = f"[{stamp}] {kind} received: {data}"
    print(line, flush=True)
    with open("received.log", "a") as fh:
        fh.write(line + "\n")


class Handler(BaseHTTPRequestHandler):
    def _ok(self):
        self.send_response(200)
        self.send_header("Content-Type", "text/html")
        self.end_headers()
        self.wfile.write(b"<h1>Lab receiver: data captured.</h1>")

    def do_GET(self):
        params = parse_qs(urlparse(self.path).query)
        log("GET", params)
        self._ok()

    def do_POST(self):
        length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(length).decode("utf-8", "replace")
        log("POST", parse_qs(body))
        self._ok()

    def log_message(self, *args):
        pass  # silence default request logging; our log() is the record


if __name__ == "__main__":
    print(f"Lab receiver listening on http://{HOST}:{PORT}  (Ctrl+C to stop)")
    HTTPServer((HOST, PORT), Handler).serve_forever()
