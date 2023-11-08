# phoenix-java-middleware
This is java integration to that Phoenix API, it can be run standalone as a microservice/middleware for calling the Interswitch Ugand aPhoenix API.
# How to run
- The /dis folder contains a jar for those that can't afford going through the build process or want a quick run.
- Start.bat contains the command required to run the jar, run start.bat in an elevated cmd or simply copy the command and run it as you please with priviledges.
- A successful run exposes a Rest API, check Posman colection for details
- Endpoints Expsed by this implementation:
   - Generate keys: localhost:8081/isw/auth/generateKeys
   - Register Client: localhost:8081/isw/auth/generateKeys
   - Key Exchange:  localhost:8081/isw/auth/keyExchange
   - Validate Account: localhost:8081/isw/payments/validation
   - Payment: localhost:8081/isw/payments/pay

# Remember to update your credentials in application.properties

# A new client secret is issued after completing registration successfully, update the client Secret again at this point
# A keyExchange call is a must daily or before each transaction, this is already catered for if your are using the jar as is

<hr>
# SOME ENDPOINTS ARE NOT IMPLEMENTED IN THIS SAMPLE, BE SURE TO SEEK SUPPORT ABOUT THOSE IF YOU HIT ANY BLOCKERS

