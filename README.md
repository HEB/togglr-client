# Togglr Spring Boot Client Library


## How to Use the Togglr Client

### Add the Togglr Client as a dependency

TODO:  Get an artifact published to include through Maven or Gradle.

### Configure Spring

#### Let Togglr create the endpoints

In your main class, add the following annotation:
```java
@ComponentScan( basePackages = {"com.example.my-spring-app", "com.heb.togglr"})
public class MySpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
```

When configuring Togglr with a component scan, Togglr will setup a Controller from you to accept webhook when updates are made.  
The endpoint created will be at `/togglr/update`, so make sure to configure your security to allow calls to this path.


#### Create the Endpoints

If you want more control over the Togglr Client, you'll need to implement a few things.

1) To accept webhooks, you'll need to create a Controller.  The method will need to call to an instance of `togglrUpdateNotifier.registerNewUpdate()`
2) Create a Togglr Client.  The rest of the classes should autowire.


### Properties
Add the following properties to your Spring Application (or configure them through Environment Variables):

**Required Properties**

`heb.togglr.client.app-id`  is the application Id within your Togglr server.

`heb.togglr.client.server-url`  is the endpoint URL of your Togglr server.

`heb.togglr.cache-time` is the amount of time you want Togglr features to be cached for. (Updates to Togglr will cause a cache refresh regardless of this value being set)

`heb.togglr.client.cache-type` Value can be either `redis` or `in-memory`.  Will default to `in-memory` if absent.

**REDIS Properties**

If the `cache-type` is set to `redis` you'll need to include the following properties:

`heb.togglr.redis.host` Host for Redis

`heb.togglr.redis.port` Port for Redis

You can get your application ID from the Togglr Server once it's been registered.


### Integrate Your Code

Where you need to get the Togglr configurations for users (generally in your `AuthenticationSuccessHandler` implementation) auto-wire
the Togglr Client: 

```java

    private TogglrClient togglrClient;

    public RestAuthSuccessHandler(TogglrClient togglrClient){
        this.togglrClient = togglrClient;
    }
```

To get the Active Featuers for a user, build an ActiveFeatureRequest and make the call to the Togglr Client.
You *do not* have to set the `applicationId` in the ActiveFeatureRequest, it will be set by the client.

```java
        ActiveFeaturesRequest featuresRequest = new ActiveFeaturesRequest();
        featuresRequest.getConfigs().put("user", user.getUsername());

        List<GrantedAuthority> roles = this.togglrClient.getFeaturesForConfig(featuresRequest, user.getUsername());
```

The roles you are returned can then be added to your UserDetails.

The call to `this.togglrClient.getFeaturesForConfig()` accepts two parameters. The first is the ActiveFeatureRequest.
The second value is the identifier for the user, and is only used to cache values.

For example, if you wanted to configure your server to have logic depending on if you had a feature set, you could call 

```java
this.togglrClient.getFeaturesForConfig(featuresRequest, "system")
```

## Building From Source

If you wish to build the library from source, it can be compiled into a jar with the following command:

```
mvn org.apache.maven.plugins:maven-jar-plugin:3.0.2:jar
```