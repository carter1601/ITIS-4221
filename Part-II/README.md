## File -> task map

| Task | File |
|------|------|
| 1. CSRF Add a friend | `approve-friend.html` |
| 2. CSRF Give a gift | `gift-cd.html` |
| 3. CSRF Change a password | `change-password.html` |
| 4. Broken access control | (no attack page) |
| 5. DOM-XSS and a link-triggered demonstration | `attacker-link.html` (link-triggered document), `receiver.py` (receiver) |
| 6. Clickjacking | `clickjack.html` |

---

## How to run files

### CSRF (tasks 1-3)
1. Serve the attack pages: from the `csrf/` folder run `python3 -m http.server 8000 --bind 127.0.0.1`.
2. While signed in as the victim open `http://127.0.0.1:8000/` in a new tab in the same browser.
3. For each CSRF demo visit their respective page (`approve-friend.html`, `gift-cd.html`, `change-password.html`)
4. Click the labeled link/button on each page to send the forged request.

### Broken access control (task 4)
(No attack page)

### DOM-XSS and a link-triggered demonstration (task 5)
1. Start the receiver: from `dom-xss/` run `python3 receiver.py` (listens on `http://127.0.0.1:9000`, logs anything it receives to the console)
2. Serve the attack page: from `dom-xss/` run `python3 -m http.server 8000 --bind 127.0.0.1`.
3. Open the attack page at `http://127.0.0.1:8000/attacker-link.html`
4. Click the labeled link

### Clickjacking (task 6)
1. Serve the attack page: from `clickjacking/` run `python3 -m http.server 8000 --bind 127.0.0.1`.
2. Open the attack page at `http://localhost:8000/clickjack.html` and not `127.0.0.1`
3. Setup as the attacker and send the victim a friend request
4. With the victim signed in open the page and click the decoy button