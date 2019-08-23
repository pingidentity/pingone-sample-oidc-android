# OIDC Authentication Android Sample
Provides an example login android  application for use with PingOne for Customers. This sample application is written in Kotlin

Sample config
```
{
  "environment_id": "{env_id}",
  "client_id": "{client_id}",
  "redirect_uri": "{redirect_schema}",
  "authorization_scope": "{auth_scopes}",
  "discovery_uri": "https://auth.pingone.com/%s/as/.well-known/openid-configuration",
  "token_method": "{CLIENT_SECRET_POST || CLIENT_SECRET_BASIC || NONE}",
  "client_secret": "{client_secret}"
}
```
where
- `environment_id`: *Required*. Your application's Environment ID. You can find this value at your Application's Settings under 
**Configuration** tab from the admin console( extract `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx` string that specifies the environment 128-bit universally unique identifier ([UUID](https://tools.ietf.org/html/rfc4122)) right from `https://auth.pingone
.com/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx/as/authorize` 
*AUTHORIZATION URL* ). Or from the *Settings* main menu (*ENVIRONMENT ID* variable)
- `client_id`: *Required*. Your application's client UUID. You can also find this value at Application's Settings right under the 
Application name.
- `redirect_uri`: *Required*. The URL to which the PingOne will redirect the user's browser after authorization has been granted by 
the user. *REDIRECT URLS* values corresponds to this data. The Access and ID Token will be available in the hash fragment of this URL.
- `authorization_scope`:  standard OIDC or PingOne custom scopes, separated by a space which you want to request authorization for.
 [PingOne platform scopes](https://apidocs.pingidentity.com/pingone/customer/v1/api/auth/p1-a_AccessServices/#PingOne-platform-scopes-and-endpoint-operations) 
 are configured under "Access" tab in PingOne Admin Console

## Getting Started

### Prerequisites

You will need your own PingOne for Customers tenant.  You can [sign up for a trial](https://developer.pingidentity.com/).

* PingOne for Customers Account - If you don’t have an existing one, please register it.
* An OpenID Connect Application, configured in Native App mode. Also make sure that it is enabled plus redirect URL's and access grants by scopes are properly set.
* At least one user in the same environment as the application (not assigned)
* Update https://github.com/pingidentity/pingone-customers-sample-oidc-android/blob/master/app/src/main/res/raw/auth_config.json with your tenant's variables


### Register your Application Connection

Once you have your own tenant, use the PingOne for Customers Admin Console to add an application connection. To create the application connection:

1. Click **Connections**.
2. Click + **Application**.
3. Select the **Single Page App** type.
4. Click **Configure**.
5. Create the application profile by entering the following information:
* **Application name**: pingone-customers-sample-login or other name
6. Click **Next**.
7. **Redirect URI**: The URL where dist/login/ will live.  For example, https://www.example.com/login/
8. Click **Save and Continue**
9. At a minimum, add the following scope: **profile**
10. Click **Save and Close**

The Applications page shows the new application.  Click the toggle switch to enable the application.  View the details of your application and make note of its **Client ID**.

11. **Edit** the Application (click the pencil icon)
12. On the Profile Tab of your new application, populate **SignOn URL** with the location that dist/login/ will live.  For example, https://www.example.com/login/
13. On the Configuration Yab of your new application, populat **SignOff URLs** with the location that dist/logout/ will live.  For example, https://www.example.com/logout/
14. Click **Save**

### Get your Environment ID

To get your **Environment ID**, in the Admin Console, click Settings, then Environment, then Properties. The Properties page shows the environment ID.

### Create Test User

To create your test user:

1. Click **Users**.
2. Click + **Add User**.
3. At a minimum, specify a **username** such as michael@example.com.
4. Click **Save**
5. View your new user and select **Reset Password**
6. Specify an initial password, such as **4Science**.
7. Click **Save**

### Clone this Repository
```
gil clone git@github.com:pingidentity/pingone-customers-sample-oidc-android.git
```
Edit ``https://github.com/pingidentity/pingone-customers-sample-oidc-android/blob/master/app/src/main/res/raw/auth_config.json`` with your tenant's variables (clientId, environmentId, others)

### Import/run app

Open Android Studio and import this app via gradle import. And click ``Run app``

