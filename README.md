# Banking project
A simple banking project
## Run
### Docker
To start the application with database you can run:  
```bash
docker compose up --build
```
Then the application with be accessible at [`localhost:8080/banking`](http://localhost:8080/banking).

Alternatively you can use `docker compose` to only setup the database:
```bash
docker compose up -d database
```
> [!NOTE]  
> To manage accounts you need a user with `ADMIN` or `MANAGER` roles, The default username and password for `ADMIN` role is `username=admin, password=admin`

### Local
Since the application is using `TomcatEmbedded` there is no need to deploy the application to a tomcat server.  
To run the app without docker you just need to run the main class `com.campus.banking.app.Main`.  

Also note the config file for database is located at `src/main/resource/META-INF/microprofile-config.yaml`.