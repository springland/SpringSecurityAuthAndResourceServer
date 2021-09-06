###Spring Security Authorization Server

Because [spring security is deperciating oauth2 project](https://spring.io/projects/spring-security-oauth) the compatibility is a headache


This project is using spring boot 2.3.x release with Spring cloud Hoxton in order to get oauth2 jar
Latest spring cloud 2020.x has removed oauth2 jars

Authorization server has
* User  (UserDetails)
* Client (ClientDetails)

This example is using password grant type. It would take a lot more efforot to implement something supports authorization token etc.
The high level workflow is same


##Two profiles

-simple plain token

-jwt  jwt symmetric key based

jwtasym jwt asymmetric key based

##Get Authorization token

###Password grant type
curl -XPOST -u client:secret "http://localhost:8080/oauth/token?grant_type=password&username=john&password=12345&scope=read"

{"access_token":"cf220c22-7931-490e-b22d-6b96fcd06fb7","token_type":"bearer","refresh_token":"7fa4368d-e0af-41b7-b5ce-9d33b56b5965","expires_in":43199,"scope":"read"}(base) 



### Authorization Code

Use hello.html to test authorization
Sample redirect , it returns the code http://localhost:9090/?code=WKskCA

Then send the code to get access token , Mind that an authorization code can only be used once.

curl -v -XPOST -u client:secret "http://localhost:8080/oauth/token?grant_type=authorization_code&scope=read&code=WKskCA"
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
* Server auth using Basic with user 'client'
> POST /oauth/token?grant_type=authorization_code&scope=read&code=WKskCA HTTP/1.1
> Host: localhost:8080
> Authorization: Basic Y2xpZW50OnNlY3JldA==
> User-Agent: curl/7.71.1
> Accept: */*
>
* Mark bundle as not supporting multiuse
  < HTTP/1.1 200
  < Cache-Control: no-store
  < Pragma: no-cache
  < X-Content-Type-Options: nosniff
  < X-XSS-Protection: 1; mode=block
  < X-Frame-Options: DENY
  < Content-Type: application/json;charset=UTF-8
  < Transfer-Encoding: chunked
  < Date: Sun, 05 Sep 2021 00:40:50 GMT
  <
* Connection #0 to host localhost left intact
  {"access_token":"0d7068bf-21f6-49b8-b850-9ddd53ea1dd7","token_type":"bearer","ex(base)

### Client Credential
curl -v -XPOST -u client_credential:secret "http://localhost:8080/oauth/token?grant_type=client_credentials&scope=info"
{"access_token":"a642a682-0b46-4c0a-9454-dc237b0a19d8","token_type":"bearer","expires_in":43199,"scope":"info"}(base)

### Refresh token

***The sample does not include how to use the refresh token to get a new access token***

curl -v -XPOST -u clientpassword:secret "http://localhost:8080/oauth/token?grant_type=password&username=john&password=12345&scope=read"

{"access_token":"c502eb58-a683-488f-a874-18701ee7703c","token_type":"bearer","refresh_token":"f6ec5e0c-ab1b-4cc3-9ca0-9672075160be","expires_in":43185,"scope":"read"}(base)


###  Check if a access token is valid

1. curl -v -XPOST -u clientpassword:secret "http://localhost:8080/oauth/token?grant_type=password&username=john&password=12345&scope=read"
   {"access_token":"c502eb58-a683-488f-a874-18701ee7703c","token_type":"bearer","refresh_token":"f6ec5e0c-ab1b-4cc3-9ca0-9672075160be","expires_in":43185,"scope":"read"}

2. curl -XPOST -u clientpassword:secret "http://localhost:8080/oauth/check_token?token=c502eb58-a683-488f-a874-18701ee7703c"
{"active":true,"exp":1630902070,"user_name":"john","authorities":["read"],"client_id":"clientpassword","scope":["read"]}(base)

3. call from resource sever
 curl -H "Authorization: bearer c502eb58-a683-488f-a874-18701ee7703c"   "http://localhost:9090/hello"


### JWT Based

curl -v -XPOST -u clientpassword:secret "http://localhost:8080/oauth/token?grant_type=password&username=john&password=12345&scope=read"

{"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzA5MTkxMDksInVzZXJfbmFtZSI6ImpvaG4iLCJhdXRob3JpdGllcyI6WyJyZWFkIl0sImp0aSI6IjAwZjU1MTI2LTI3NGUtNDY1Ni1hNzdlLTMwYTA2ZDVmMDdlZSIsImNsaWVudF9pZCI6ImNsaWVudHBhc3N3b3JkIiwic2NvcGUiOlsicmVhZCJdfQ.Dk5F2n-a84V0FvwoOtMYPIFecnSkq7FbxfdXJVHV-qQ","token_type":"bearer","refresh_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJqb2huIiwic2NvcGUiOlsicmVhZCJdLCJhdGkiOiIwMGY1NTEyNi0yNzRlLTQ2NTYtYTc3ZS0zMGEwNmQ1ZjA3ZWUiLCJleHAiOjE2MzM0Njc5MDksImF1dGhvcml0aWVzIjpbInJlYWQiXSwianRpIjoiOTc4ZTY3NDQtYThhZi00OTc5LTkyNGEtMDRiZjJmMDZhNGI5IiwiY2xpZW50X2lkIjoiY2xpZW50cGFzc3dvcmQifQ.PYm85EQU80Rma04jhR_bCFO9X_BVlkVyJRmdUQlKn8c","expires_in":43199,"scope":"read","jti":"00f55126-274e-4656-a77e-30a06d5f07ee"}(base)

*Head*

echo eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9 | base64 -d
{"alg":"HS256","typ":"JWT"}(base) 

*Body*
echo eyJleHAiOjE2MzA5MTkxMDksInVzZXJfbmFtZSI6ImpvaG4iLCJhdXRob3JpdGllcyI6WyJyZWFkIl0sImp0aSI6IjAwZjU1MTI2LTI3NGUtNDY1Ni1hNzdlLTMwYTA2ZDVmMDdlZSIsImNsaWVudF9pZCI6ImNsaWVudHBhc3N3b3JkIiwic2NvcGUiOlsicmVhZCJdfQ | base64 -d

{"user_name":"john","scope":["read"],"ati":"00f55126-274e-4656-a77e-30a06d5f07ee","exp":1633467909,"authorities":["read"],"jti":"978e6744-a8af-4979-924a-04bf2f06a4b9","client_id":"clientpassword"}base64: invalid input

*Signature*


1. curl -v -XPOST -u clientpassword:secret "http://localhost:8080/oauth/token?grant_type=password&username=john&password=12345&scope=read"
   {"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzA5MjEzNDUsInVzZXJfbmFtZSI6ImpvaG4iLCJhdXRob3JpdGllcyI6WyJyZWFkIl0sImp0aSI6IjJiNzc4OWUxLWM3YTAtNDg4Ni05ODMxLTc2ZmE3MzM3NDQ1MCIsImNsaWVudF9pZCI6ImNsaWVudHBhc3N3b3JkIiwic2NvcGUiOlsicmVhZCJdfQ.Qg83P4dD1AdwrXeOnwmObHe8H2PEpduaia153CmBWGI","token_type":"bearer","refresh_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJqb2huIiwic2NvcGUiOlsicmVhZCJdLCJhdGkiOiIyYjc3ODllMS1jN2EwLTQ4ODYtOTgzMS03NmZhNzMzNzQ0NTAiLCJleHAiOjE2MzM0NzAxNDUsImF1dGhvcml0aWVzIjpbInJlYWQiXSwianRpIjoiOWE4NWI5MzMtMGViMC00OTc4LThhMTUtZGJiMDI2NjYyZGZkIiwiY2xpZW50X2lkIjoiY2xpZW50cGFzc3dvcmQifQ.5iwHrw8PGbzFdKTDiOK6-hNs2vaZyAwKKrHRnWv4Y9s","expires_in":43199,"scope":"read","jti":"2b7789e1-c7a0-4886-9831-76fa73374450"}(base)

2. curl -XPOST -u clientpassword:secret "http://localhost:8080/oauth/check_token?eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzA5MjEzNDUsInVzZXJfbmFtZSI6ImpvaG4iLCJhdXRob3JpdGllcyI6WyJyZWFkIl0sImp0aSI6IjJiNzc4OWUxLWM3YTAtNDg4Ni05ODMxLTc2ZmE3MzM3NDQ1MCIsImNsaWVudF9pZCI6ImNsaWVudHBhc3N3b3JkIiwic2NvcGUiOlsicmVhZCJdfQ.Qg83P4dD1AdwrXeOnwmObHe8H2PEpduaia153CmBWGI"
  There is an issue at this step , it returns {"timestamp":"2021-09-05T21:55:07.537+00:00","status":400,"error":"Bad Request","message":"","path":"/oauth/check_token"}(base) , however #3 works , then #2 should work

3. call from resource sever
   curl -H "Authorization: bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzA5MjEzNDUsInVzZXJfbmFtZSI6ImpvaG4iLCJhdXRob3JpdGllcyI6WyJyZWFkIl0sImp0aSI6IjJiNzc4OWUxLWM3YTAtNDg4Ni05ODMxLTc2ZmE3MzM3NDQ1MCIsImNsaWVudF9pZCI6ImNsaWVudHBhc3N3b3JkIiwic2NvcGUiOlsicmVhZCJdfQ.Qg83P4dD1AdwrXeOnwmObHe8H2PEpduaia153CmBWGI"   "http://localhost:9090/hello"


### Asymmetric Key JWT

Generate private key
keytool -genkeypair -alias authsrv -keyalg RSA -keypass auth123 -keystore authsrv.jks -storepass authsrv

Obtain public key
keytool -list -rfc --keystore authsrv.jks | openssl x509 -inform pem -pubkey


-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvbhEj/gyAT0N+WRvs+yf
j/Dhzk0DnAekecQOrLQ+plGE7uSCwrwXz/JCKo4itvECxHkWyQRnsKoWmwrKPrql
teqDdLwZ5wGfNmFwlqCd9a6cw5AZ3YKDZmhXllsP+vrofpMIYchRRHIQuvKCEp2J
WzGwMlY22F315oOEYPQofCxtnbOyzPfyT3WLhnauCQyS+ylLGFjN6Z09iq3Jovoa
eRB1YX0EZ5QJzMuf6cJ9m/pFvFBhPYRpt6rnDzloNMgViHH3BNPmMm1+JC4ikyKO
TIFQjwfgMYBS+zVOBrrC2utA2DOXshzBRhMpm3S2qxbxJrknbKKszaXpnZdylBU7
LwIDAQAB
-----END PUBLIC KEY-----
-----BEGIN CERTIFICATE-----
MIIDdzCCAl+gAwIBAgIEUdeK0zANBgkqhkiG9w0BAQsFADBsMRAwDgYDVQQGEwdV
bmtub3duMRAwDgYDVQQIEwdVbmtub3duMRAwDgYDVQQHEwdVbmtub3duMRAwDgYD
VQQKEwdVbmtub3duMRAwDgYDVQQLEwdVbmtub3duMRAwDgYDVQQDEwdVbmtub3du
MB4XDTIxMDkwNTIyMzM0OVoXDTIxMTIwNDIyMzM0OVowbDEQMA4GA1UEBhMHVW5r
bm93bjEQMA4GA1UECBMHVW5rbm93bjEQMA4GA1UEBxMHVW5rbm93bjEQMA4GA1UE
ChMHVW5rbm93bjEQMA4GA1UECxMHVW5rbm93bjEQMA4GA1UEAxMHVW5rbm93bjCC
ASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL24RI/4MgE9Dflkb7Psn4/w
4c5NA5wHpHnEDqy0PqZRhO7kgsK8F8/yQiqOIrbxAsR5FskEZ7CqFpsKyj66pbXq
g3S8GecBnzZhcJagnfWunMOQGd2Cg2ZoV5ZbD/r66H6TCGHIUURyELryghKdiVsx
sDJWNthd9eaDhGD0KHwsbZ2zssz38k91i4Z2rgkMkvspSxhYzemdPYqtyaL6GnkQ
dWF9BGeUCczLn+nCfZv6RbxQYT2Eabeq5w85aDTIFYhx9wTT5jJtfiQuIpMijkyB
UI8H4DGAUvs1Tga6wtrrQNgzl7IcwUYTKZt0tqsW8Sa5J2yirM2l6Z2XcpQVOy8C
AwEAAaMhMB8wHQYDVR0OBBYEFBNJVnuEBmfS7FDNMuVUR/ZMTEcAMA0GCSqGSIb3
DQEBCwUAA4IBAQBiGWhtRiNOeil/LC2NAYi8dIYrQQIX0TDxo47mCcCj2topaiHZ
TVYAFs309llXx5H2nuA5T9WxxlZEo98Z6Hpgk7UhDVbl2sfOj4LkliJj+GgzZTgx
HzN7ZBl0+dGQewels6dWbonxB+2YedHfhYsn2tVmdJFNVuL7/CQeV6p1RgKrSJT3
OuQ98p1FIJ4lerlAfWxn5BLHMqdOkutcIjRsuO/jGZqvQrn7HChTbJNiTARAeePX
6g4H+zjp9dG8iqHmhbHIKnxkmrgODaWXrZLTWf2Q6Q+fcxqlCct49KalAaWZUnZV
lDw/l6+mCerZ6ny5tJZijX7Ap4NLoebTlEsP
-----END CERTIFICATE-----


1. curl -v -XPOST -u clientpassword:secret "http://localhost:8080/oauth/token?grant_type=password&username=john&password=12345&scope=read"
{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzA5MjY0NjEsInVzZXJfbmFtZSI6ImpvaG4iLCJhdXRob3JpdGllcyI6WyJyZWFkIl0sImp0aSI6IjJhODQ2MmFkLTAwNWQtNDJkMC04Yzc3LTE0MzYwZTE2ODFiNSIsImNsaWVudF9pZCI6ImNsaWVudHBhc3N3b3JkIiwic2NvcGUiOlsicmVhZCJdfQ.i-fEburPFLzGiSzDnmGUJMbWjDq5ZWrElwMNffmmglsR1n-mU8nF4XeDD255Jzd9kq4OSzS-y5zhs60qRp0ZyCvEt--lCw-MzEdgfdWc3X0xyZBvD6KIWdKlhgKAoEs500KqffmI0QTEVv4ebH1FhfE-N63GvSEHPl9umapAvEkHo1q0Pbf0iplZXkvyO6soDQuK53y0Xj8ZeoXv5cRuV02RG48f-s7gGyz4rjkE13vIm759gv0abC7apIEhMrJ36ZMThKApOk9Gf4TLF8SjBXlj51okpieEo19kHr4rrZf6MygSdUjCOvmadLGP5mI-klvW_Rh5RPvI1GectZhhkw","token_type":"bearer","refresh_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJqb2huIiwic2NvcGUiOlsicmVhZCJdLCJhdGkiOiIyYTg0NjJhZC0wMDVkLTQyZDAtOGM3Ny0xNDM2MGUxNjgxYjUiLCJleHAiOjE2MzM0NzUyNjEsImF1dGhvcml0aWVzIjpbInJlYWQiXSwianRpIjoiNDVmNzA3YTYtM2Q4ZC00ZWM1LWEyNTMtN2M2YWUwMzRlNWYyIiwiY2xpZW50X2lkIjoiY2xpZW50cGFzc3dvcmQifQ.SlKZwgHDalESf9UKSDmVY8Ncrefji5Mo7nXOsKeoYXHG-9lbvIH3dy84qkAKCFxHgt-z8DKCGxl473m2_z-5* Connection #0 to host localhost left intact
Hi5wro31yOZvWEU3SBh3SlS4AyuvJRu7y_bKdQEsJhsIugB2NuxNKBhjCJnZ1_9eeQOpKCYytYz50ylQYsKcuDxZbkgNxnrECpIFzVGfQPD6mOrzfsPpLEhgnfR4rCqqQk17Apwa7zrKYYznI12pa4gzRD-HNEkxtRAidwJ7dSC9G8w5vyw0gQuDcS2tzkH8My7RinWggpDvYDNkQAgc73DjdboEeZwqEhnXnFMXtF9oE7zRWtKeRhMQrFbwLJGu0A","expires_in":43199,"scope":"read","jti":"2a8462ad-005d-42d0-8c77-14360e1681b5"}(base)


2. call from resource sever
   curl -H "Authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzA5MjY3NjUsInVzZXJfbmFtZSI6ImpvaG4iLCJhdXRob3JpdGllcyI6WyJyZWFkIl0sImp0aSI6Ijc1NTU4MDViLWE0ZDItNGIzOS04OTg4LTc3ZGQ5MmE0MTY3MiIsImNsaWVudF9pZCI6ImNsaWVudHBhc3N3b3JkIiwic2NvcGUiOlsicmVhZCJdfQ.ocpi6bahVqDV0vRZYUiplxHcZyfFApTW3v9fQ4cXlL2LP5EYCT8x-AQnmiCwRpPK6WQxWSbteuTM1_Vu1phVtIyyadWrluAX91q4xTzeMRExbEJvPh2HDKfw7Po7BmxkqGpoG6twy2r6ISXePm8L09-lU82siIH1M9vxHy1wmZF4B6tUMOpViDaSjTxiDb4jW6iIghdWPkgRKaJpkcfw3ZAxoEVRnHspqVt39cJEr_CtJmipD6zMBQJgo3xG7YKQAFwaQkuNNx2zOD0DtxQe4OcmQI_NTxpvLrExfm_iyqoKIUNOVvbTCQkgPMUn2zOR3jZ-cTUzHOykoVBTF-CKmw"   "http://localhost:9090/hello"



