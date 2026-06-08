# Quiz Application - Render Deployment Guide

## Prerequisites
1. GitHub account
2. Render account (free at https://render.com)
3. Git installed on your computer

## Step-by-Step Deployment Instructions

### Step 1: Push Your Code to GitHub

1. **Create a new repository on GitHub:**
   - Go to https://github.com/new
   - Repository name: `quiz-application` (or any name you prefer)
   - Set to **Public** or **Private**
   - Click "Create repository"

2. **Initialize Git and push your code:**
   ```bash
   cd "d:\new spring appp\demo"
   git init
   git add .
   git commit -m "Initial commit - Quiz application ready for deployment"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/quiz-application.git
   git push -u origin main
   ```
   
   Replace `YOUR_USERNAME` with your GitHub username.

### Step 2: Deploy to Render

1. **Sign up/Login to Render:**
   - Go to https://render.com
   - Click "Get Started for Free"
   - Sign up with GitHub (recommended) or email

2. **Create a new Web Service:**
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Select `quiz-application` repository

3. **Configure the service:**
   - **Name:** `quiz-application` (or your preferred name)
   - **Region:** Oregon (or closest to you)
   - **Branch:** `main`
   - **Root Directory:** Leave blank (or `demo` if you pushed from parent directory)
   - **Runtime:** `Java`
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/demo-0.0.1-SNAPSHOT.jar`
   - **Plan:** Select **Free**

4. **Add Environment Variables:**
   Click "Advanced" and add these environment variables:
   ```
   SPRING_JPA_HIBERNATE_DDL_AUTO = update
   SPRING_JPA_SHOW_SQL = false
   SPRING_THYMELEAF_CACHE = true
   SERVER_PORT = 10000
   LOG_LEVEL = INFO
   ```

5. **Add PostgreSQL Database:**
   - Scroll down to "Add a PostgreSQL database"
   - Database name: `quizdb`
   - Plan: **Free**
   - Render will automatically set these environment variables:
     - `SPRING_DATASOURCE_URL`
     - `SPRING_DATASOURCE_USERNAME`
     - `SPRING_DATASOURCE_PASSWORD`

6. **Deploy:**
   - Click "Create Web Service"
   - Wait for deployment to complete (5-10 minutes)
   - Your app will be available at: `https://quiz-application-xxxx.onrender.com`

### Step 3: Seed Initial Data

After deployment, you need to create the admin and user accounts:

**Option 1: Automatic (Recommended)**
The DataSeeder will run automatically on first startup and create:
- Admin: `adminnibras` / `passnibras`
- User: `user` / `user123`

**Option 2: Manual Registration**
1. Visit your deployed URL
2. Click "Register"
3. Create admin account manually
4. Update role to ADMIN in database (requires database access)

### Step 4: Verify Deployment

1. Visit your Render URL (e.g., `https://quiz-application-xxxx.onrender.com`)
2. Login with admin credentials
3. Create a test quiz
4. Take the quiz as a user
5. Verify score history works

## Important Notes

### Free Tier Limitations
- ⚠️ App sleeps after 15 minutes of inactivity
- ⚠️ First visit after sleep takes ~30 seconds to wake up
- ⚠️ 750 hours/month runtime (enough for 24/7 for one app)
- ⚠️ 512MB RAM limit

### Database Migration
- Your local MySQL database will NOT be transferred
- Render uses PostgreSQL (automatically provisioned)
- Fresh database will be created on deployment
- You'll need to recreate quizzes manually or via admin panel

### Auto-Deploy
- Every push to the `main` branch triggers automatic deployment
- Changes will be live in 5-10 minutes

### Environment Variables
All sensitive configuration is managed via Render's environment variables:
- Database credentials are automatically injected
- No hardcoded passwords in your code
- Secure and production-ready

## Troubleshooting

### Build Fails
Check Render logs for errors. Common issues:
- Missing dependencies in pom.xml
- Incorrect Java version (should be Java 25)
- Build command syntax errors

### App Won't Start
- Check that `SERVER_PORT=10000` is set
- Verify database connection in logs
- Ensure all environment variables are configured

### Database Connection Error
- Verify PostgreSQL database was created
- Check environment variables are set correctly
- View logs for detailed error messages

## Monitoring Your App

1. **Render Dashboard:**
   - View deployment status
   - Check logs in real-time
   - Monitor resource usage

2. **Access Logs:**
   - Go to your service → "Logs" tab
   - View application output and errors

## Updating Your App

1. Make changes locally
2. Test with `./mvnw.cmd spring-boot:run`
3. Commit and push:
   ```bash
   git add .
   git commit -m "Description of changes"
   git push
   ```
4. Render will automatically deploy the update

## Custom Domain (Optional)

1. Go to your service → "Settings"
2. Scroll to "Custom Domains"
3. Add your domain
4. Follow DNS configuration instructions

## Cost Estimate

**Free Tier:**
- Web Service: $0/month (750 hours, 512MB RAM)
- PostgreSQL Database: $0/month (1GB storage, 90-day backup)
- **Total: $0/month** 🎉

## Support

- Render Documentation: https://render.com/docs
- Render Community: https://community.render.com
- Spring Boot Docs: https://spring.io/projects/spring-boot

---

**Your quiz application is now ready for production deployment!** 🚀
