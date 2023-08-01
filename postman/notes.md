# Using Postman request Guides
To run requests in "hysteryale.postman_collection.json"
* Firstly, run "get authorization token": http://localhost:8080/oauth/token to get {access_token}.
* Secondly, replace {access_token} to other request HEADER with Authorization's value = Bearer + {token}. For examples, "Bearerb519885b-7fd9-4c5b-b8b9-f361d0727305".
* Lastly, run other requests.