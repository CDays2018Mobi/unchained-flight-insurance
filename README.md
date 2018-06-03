# unchained-flight-insurance

Comment obtenir la liste des vols entre hier et demain:
http://localhost:9000/api/hello/v1/flight/flights

Comment s'assurer pour un vol:
http://localhost:9000/api/hello/v1/contracts/create?flightNumber=TP946&arrivalDate=2018-05-30

Comment rafraichir la liste des vols: 
http://localhost:9000/api/hello/v1/flight/refresh

Estimer le risque de retard:
http://localhost:9000/api/hello/v1/flight/delay?companyName=AIR%20FRANCE&arrivalDate=2018-06-03T13:00&minDelay=10
minDelay=10|60 (par défaut 60)