# Deployment Guide - Get Your Permanent UI Link

Three options, from fastest to most powerful. All of them run the exact same
jar / source you have in this repo.

---

## Option A - Render (recommended: free + one click)

**What you get:** a permanent public URL like
`https://student-management-system-xxxx.onrender.com`

### Steps (about 5 minutes)

1. **Push this project to GitHub** (repo: `student-management-system`, public):
   ```bash
   cd student-management-system
   git remote add origin https://github.com/yashkajal123/student-management-system.git
   git push -u origin main
   ```
2. Go to <https://render.com> and sign up with your GitHub account (free).
3. Open the repo page on GitHub and click the **Deploy to Render** button in
   the README - or on Render press **New +** -> **Blueprint** and select your
   repo. The `render.yaml` in this repo configures everything for you.
4. Approve the blueprint. Render will build with `./mvnw package` and start
   the jar. Wait ~3-5 minutes for the first deploy.
5. Your permanent link appears at the top of the service page
   (`https://student-management-system-xxxx.onrender.com`). Open it, log in
   with `admin@sms.com` / `admin123`.

### What the blueprint sets (already in `render.yaml`)

| Setting | Value | Why |
|---------|-------|-----|
| `SPRING_PROFILES_ACTIVE` | `demo` | embedded H2 database - no DB setup needed |
| `LOG_DIR` | `/tmp/logs` | logs land somewhere writable on Linux |
| `startCommand` | `java -Xmx320m -jar ...` | fits the free plan's 512 MB RAM |
| `healthCheckPath` | `/login` | Render keeps the service healthy |

### Free plan caveats (know these before interviews)

- **Sleeps after ~15 min without visitors** - first request after sleep takes
  ~30-60 s. That's normal. For an "always awake" demo, create a free
  [UptimeRobot](https://uptimerobot.com) monitor pinging your URL every
  5 minutes - recruiters then never hit the cold start.
- **512 MB RAM** - this app fits with the `-Xmx320m` flag already set.
- **H2 data resets on each deploy/restart** - perfect for a demo: the app
  re-seeds sample data automatically (`DataInitializer`).

### Want real MySQL later?

Attach any MySQL (Railway plugin, Aiven, your own server) and swap the env:

| Key | Value |
|-----|-------|
| `SPRING_PROFILES_ACTIVE` | *(remove / leave empty)* |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://host:3306/student_management_db` |
| `SPRING_DATASOURCE_USERNAME` | your user |
| `SPRING_DATASOURCE_PASSWORD` | your password |

Spring Boot reads these over `application.properties` automatically.

---

## Option B - Railway (free $5 monthly credit)

1. Push to GitHub (same as above).
2. <https://railway.app> -> **New Project** -> **Deploy from GitHub repo**.
3. Settings: Build `./mvnw package -DskipTests`, Start
   `java -jar target/student-management-system-1.0.0.jar`, add env
   `SPRING_PROFILES_ACTIVE=demo`.
4. Generate a domain -> you get `https://xxxx.up.railway.app`.

---

## Option C - Always-on VM (Oracle Cloud Free Tier, never sleeps)

If you want a link that is truly always on, without credit card costs:

1. Create an Oracle Cloud **Always Free** account + an Ampere A1 VM (4 OCPU,
   24 GB RAM, permanently free).
2. Install Java: `sudo apt install openjdk-17-jdk`.
3. Upload the jar (`target/student-management-system-1.0.0.jar` after
   `./mvnw package`) and run it with `systemd` (or `nohup`).
4. Open port 8080 in the VCN security list + Ubuntu firewall
   (`sudo ufw allow 8080`).
5. Your link: `http://<vm-public-ip>:8080` (put nginx + a free domain in
   front if you want HTTPS).

More setup than A/B, but it never sleeps and you can run real MySQL on the
same VM.

---

## After any deployment

1. Open your URL, log in (`admin@sms.com` / `admin123`), click through the
   screens once - good to verify before sharing.
2. Put the URL on your GitHub repo: README -> *Live Demo* section, and in the
   repository "Website" field on the right side of the repo page.
3. Add the link to your resume under the project entry.

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| Build fails: "no mvnw permission" | `git update-index --chmod=+x mvnw` and push (Render needs the exec bit) |
| Boot fails with OOM | Keep `-Xmx320m`, or use the $7/mo Starter plan (1 GB) |
| 502 from Render | Check logs tab; usually means boot is still running (wait 30 s) or PORT env was overridden - don't set PORT manually, Render injects it |
| App up but pages 500 | Make sure `SPRING_PROFILES_ACTIVE=demo` is set (else it looks for MySQL) |
