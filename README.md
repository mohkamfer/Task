Usage:
-------

To set up the application, head to res/values/strings.xml and edit the following:

  - cloudURI   - The URI for the required host to connect to
  
  - username   - The username for the user
  
  - password   - User's password
  
  - user_topic - The topic to listen to in the Receive tab
 
For testing, https://www.cloudmqtt.com/ was used

Use "Login" and register or login using one of the supported methods.


Issues with the application:
----------------------------

The infamous fragment lifecycle rotation issues
Persistence problem in the Receive tab, but not much work was put into persistence handling.
