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