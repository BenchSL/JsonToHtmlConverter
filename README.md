# JsonToHtmlConverter

### Build application
```bash
mvn clean install
```
### Startup application
```bash
mvn spring-boot:run
```
### curl commands
check if controller is working
```
curl -X GET http://localhost:8080/backend/ping
```
convert json file to html
```
curl -X POST http://localhost:8080/backend/convert \
  -H "Content-Type: multipart/form-data" \
  -F "file=@path/to/your/input.json"
```