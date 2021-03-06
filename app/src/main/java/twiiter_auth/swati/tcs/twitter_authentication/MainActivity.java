package twiiter_auth.swati.tcs.twitter_authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;


public class MainActivity extends AppCompatActivity {

    private String twitter_consumer_key = "rrYR0z6y019AqMOUttpLefgYs";
    private String twitter_consumer_secret = "bjKjvdPhiaTVqSpEp1Aj1hNiPk0ylsFQxNucw4SmdtcvhMy1lh";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    TwitterAuthenticationReturn authReturn;
    String initialAuthToken = "";

    //Go here to create your search query
    //https://twitter.com/search-advanced
    static final String SearchQuery = "#Virugyaan";
    TextView status;
    EditText consumerKey;
    EditText ConsumerSecret;

    EditText searchEdit;
    EditText StatusEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity twitter = this;
        status = (TextView) findViewById(R.id.status);

        findViewById(R.id.githublink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebUrlExternal(MainActivity.this, "https://github.com/swatikurra17/Twitter_authentication");
            }
        });
        consumerKey  = (EditText) findViewById(R.id.consumer_key);
        StatusEdit  = (EditText) findViewById(R.id.status_edit);

        searchEdit  = (EditText) findViewById(R.id.query);
        ConsumerSecret  = (EditText) findViewById(R.id.consumer_secret);

        if (!TextUtils.isEmpty(consumerKey.getText().toString())) {
            twitter_consumer_key = consumerKey.getText().toString();

        } if (!TextUtils.isEmpty(ConsumerSecret.getText().toString())) {
            twitter_consumer_secret = ConsumerSecret.getText().toString();
        }

        findViewById(R.id.authorize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, String>() {

                    final ProgressDialog progressDialog = new ProgressDialog(twitter);
                    StringBuffer pinURL = new StringBuffer("https://api.twitter.com/oauth/authenticate?oauth_token");

                    @Override
                    protected String doInBackground(Void... params) {


                        System.out.println("Starting Authentication..");
                        String authToken = twitter.startTwitterAuthentication(pinURL);
                        return authToken;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog.setMessage("Authorizing...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String aVoid) {
                        super.onPostExecute(aVoid);
                        initialAuthToken = aVoid;
                        status.setText("Authorized Successfully..");
                        System.out.println("Opening Url "+pinURL+" in your default Browser");
                        System.out.println("Enter The Pin: ");
                        progressDialog.dismiss();
                        openWebUrlExternal(twitter, pinURL.toString());
                    }
                }.execute();


            }
        });

        findViewById(R.id.autheticate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText) findViewById(R.id.pin);
                final String textStr = text.getText().toString();

                if (TextUtils.isEmpty(textStr)) {
                    status.setText("Please enter valid PIN");
                    return;
                }
                new AsyncTask<Void, Void, String>() {
                    final ProgressDialog progressDialog = new ProgressDialog(twitter);

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        progressDialog.dismiss();

                        status.setText("Authenticated Successfully.. now you can proceed for tweeting ");


                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog.setMessage("Authenticating");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... voids) {

                        authReturn = twitter.getTwitterAccessTokenFromAuthorizationCode(textStr, initialAuthToken);
//                        String firstTwitterID = "";
//                        try {
//                            String response = twitter.searchTweets(SearchQuery, authReturn.access_token, authReturn.access_token_secret);
//
//                            int indexOfId = response.indexOf(new String("\"id\""));
//
//                            response = response.substring(indexOfId, response.length() - 1);
//                            firstTwitterID = response.substring(5, response.indexOf(','));
//
//                            System.out.println(firstTwitterID);
//                        } catch (Exception e) {
//
//                        }
//                        if (TextUtils.isEmpty(firstTwitterID)) {
//                            twitter.reTweets("827063773810872320", authReturn.access_token, authReturn.access_token_secret);
//                        }
//                        else {
//                            twitter.reTweets(firstTwitterID, authReturn.access_token, authReturn.access_token_secret);
//                        }

                        return null;
                    }
                }.execute();
            }
        });



        findViewById(R.id.retweet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String textStr = searchEdit.getText().toString();

                if (TextUtils.isEmpty(textStr)) {
                    status.setText("Please enter valid Query");
                    return;
                }
                new AsyncTask<Void, Void, Boolean>() {
                    final ProgressDialog progressDialog = new ProgressDialog(twitter);

                    @Override
                    protected void onPostExecute(Boolean s) {
                        super.onPostExecute(s);
                        progressDialog.dismiss();

                        if (s) {
                            status.setText("re-tweeted Successfully.. open your twitter account to verify");
                        }
                        else {
                            status.setText("fail to search the query so Re-tweeting default hard code tweet");
                        }


                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog.setMessage("re-tweeting first result of search query");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {

                        Boolean returnStatus = true;
                        String firstTwitterID = "";
                        try {
                            String response = twitter.searchTweets(textStr, authReturn.access_token, authReturn.access_token_secret);

                            int indexOfId = response.indexOf(new String("\"id\""));

                            response = response.substring(indexOfId, response.length() - 1);
                            firstTwitterID = response.substring(5, response.indexOf(','));

                            System.out.println(firstTwitterID);
                        } catch (Exception e) {

                        }
                        if (TextUtils.isEmpty(firstTwitterID)) {
                            returnStatus = false;
                            twitter.reTweets("827063773810872320", authReturn.access_token, authReturn.access_token_secret);
                        }
                        else {
                            returnStatus = true;
                            twitter.reTweets(firstTwitterID, authReturn.access_token, authReturn.access_token_secret);
                        }

                        return returnStatus;
                    }
                }.execute();
            }
        });

        findViewById(R.id.status_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String textStr = StatusEdit.getText().toString();

                if (TextUtils.isEmpty(textStr)) {
                    status.setText("Please enter valid Status ");
                    return;
                }
                new AsyncTask<Void, Void, Boolean>() {
                    final ProgressDialog progressDialog = new ProgressDialog(twitter);

                    @Override
                    protected void onPostExecute(Boolean s) {
                        super.onPostExecute(s);
                        progressDialog.dismiss();

                        if (s) {
                            status.setText("tweeted Successfully.. open your twitter account to verify");
                        }
                        else {
                            status.setText("fail to tweet your new status ");
                        }


                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog.setMessage("tweeting your new status");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {

                        Boolean returnStatus = true;
                        try {
                            twitter.updateStatus(textStr, authReturn.access_token, authReturn.access_token_secret);
                        } catch (Exception e) {
                            returnStatus = false;
                        }

                        return returnStatus;
                    }
                }.execute();
            }
        });

    }

    public String encode(String value)
    {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuilder buf = new StringBuilder(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }

    private static String computeSignature(String baseString, String keyString) throws GeneralSecurityException, UnsupportedEncodingException
    {
        SecretKey secretKey = null;

        byte[] keyBytes = keyString.getBytes();
        secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(secretKey);

        byte[] text = baseString.getBytes();

        return new String(android.util.Base64.encodeToString(mac.doFinal(text), Base64.DEFAULT)).trim();
    }

    public String startTwitterAuthentication(StringBuffer pinURLReturn)
    {
        // this particular request uses POST
        String get_or_post = "POST";

        // I think this is the signature method used for all Twitter API calls
        String oauth_signature_method = "HMAC-SHA1";

        // generate any fairly random alphanumeric string as the "nonce". Nonce = Number used ONCE.
        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauth_nonce = uuid_string; // any relatively random alphanumeric string will work here

        // get the timestamp
        Calendar tempcal = Calendar.getInstance();
        long ts = tempcal.getTimeInMillis();// get current time in milliseconds
        String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds

        // assemble the proper parameter string, which must be in alphabetical order, using your consumer key
        String parameter_string = "oauth_consumer_key=" + twitter_consumer_key + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" + oauth_signature_method + "&oauth_timestamp=" + oauth_timestamp + "&oauth_version=1.0";
        System.out.println("parameter_string=" + parameter_string); // print out parameter string for error checking, if you want

        // specify the proper twitter API endpoint at which to direct this request
        String twitter_endpoint = "https://api.twitter.com/oauth/request_token";
        String twitter_endpoint_host = "api.twitter.com";
        String twitter_endpoint_path = "/oauth/request_token";

        // assemble the string to be signed. It is METHOD & percent-encoded endpoint & percent-encoded parameter string
        // Java's native URLEncoder.encode function will not work. It is the wrong RFC specification (which does "+" where "%20" should be)...
        // the encode() function included in this class compensates to conform to RFC 3986 (which twitter requires)
        String signature_base_string = get_or_post + "&"+ encode(twitter_endpoint) + "&" + encode(parameter_string);

        // now that we've got the string we want to sign (see directly above) HmacSHA1 hash it against the consumer secret
        String oauth_signature = "";
        try {
            oauth_signature = computeSignature(signature_base_string, twitter_consumer_secret + "&");  // note the & at the end. Normally the user access_token would go here, but we don't know it yet for request_token
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // each request to the twitter API 1.1 requires an "Authorization: BLAH" header. The following is what BLAH should look like
        String authorization_header_string = "OAuth oauth_consumer_key=\"" + twitter_consumer_key + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" +
                oauth_timestamp + "\",oauth_nonce=\"" + oauth_nonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + encode(oauth_signature) + "\"";
        System.out.println("authorization_header_string=" + authorization_header_string); 	// print out authorization_header_string for error checking

        String oauth_token = "";
        String oauth_token_secret = "";
        //String oauth_callback_confirmed = "";


        try
        {
//			// initialize the HTTPS connection
//			SSLContext sslcontext = SSLContext.getInstance("TLS");
//			sslcontext.init(null, null, null);
//			SSLSocketFactory ssf = sslcontext.getSocketFactory();
//			Socket socket = ssf.createSocket();
//			socket.connect(new InetSocketAddress(twitter_endpoint_host, 443), 0);
//
//			 // for HTTP, use this instead of the above.
//			 // Socket socket = new Socket(host.getHostName(), host.getPort());
//			 // conn.bind(socket, params);

            URL url = new URL(twitter_endpoint);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestProperty("accept", "application/json");
            urlConn.setRequestProperty("Accept-Charset", UTF_8.toString());
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF_8);
//	        urlConn.setRequestProperty( "Content-Type", "application/json" );
            urlConn.setRequestProperty( "Connection", "Keep-Alive"  );
            urlConn.setRequestProperty( "Host", "api.twitter.com"  );
            urlConn.setRequestProperty("User-Agent", "HttpCore/1.1");
            urlConn.setRequestProperty( "X-Target-URI", "http://api.twitter.com"  );
            urlConn.setRequestProperty( "Authorization", authorization_header_string);
            urlConn.setConnectTimeout(120*1000);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod( "POST" );
//	        urlConn.setDoInput(true);


//	        urlConn.getOutputStream().write(autorizationRequest.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            String read;
            StringBuffer  buffer = new StringBuffer();

            while((read = reader.readLine()) != null) {
                buffer.append(read);
            }
            String response = buffer.toString();

            int beginIndex = response.indexOf('=');
            int endIndex = response.indexOf('&');

            int lastIndex = response.lastIndexOf('&');

            pinURLReturn.append(response.substring(beginIndex, lastIndex));
            oauth_token = response.substring(beginIndex+1, endIndex);
            System.out.print(response);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return oauth_token;

    }

    public TwitterAuthenticationReturn getTwitterAccessTokenFromAuthorizationCode(String verifier_or_pin, String oauth_token)
    {

        // this particular request uses POST
        String get_or_post = "POST";

        // I think this is the signature method used for all Twitter API calls
        String oauth_signature_method = "HMAC-SHA1";

        // generate any fairly random alphanumeric string as the "nonce". Nonce = Number used ONCE.
        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauth_nonce = uuid_string; // any relatively random alphanumeric string will work here

        // get the timestamp
        Calendar tempcal = Calendar.getInstance();
        long ts = tempcal.getTimeInMillis();// get current time in milliseconds
        String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds


        // the parameter string must be in alphabetical order
        String parameter_string = "oauth_consumer_key=" + twitter_consumer_key + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" + oauth_signature_method +
                "&oauth_timestamp=" + oauth_timestamp + "&oauth_token=" + encode(oauth_token) + "&oauth_version=1.0";
        System.out.println("parameter_string=" + parameter_string);

        String twitter_endpoint = "https://api.twitter.com/oauth/access_token";
        String twitter_endpoint_host = "api.twitter.com";
        String twitter_endpoint_path = "/oauth/access_token";
        String signature_base_string = get_or_post + "&"+ encode(twitter_endpoint) + "&" + encode(parameter_string);

        String oauth_signature = "";
        try {
            oauth_signature = computeSignature(signature_base_string, twitter_consumer_secret + "&");  // note the & at the end. Normally the user access_token would go here, but we don't know it yet
            System.out.println("oauth_signature=" + encode(oauth_signature));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String authorization_header_string = "OAuth oauth_consumer_key=\"" + twitter_consumer_key + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + oauth_timestamp +
                "\",oauth_nonce=\"" + oauth_nonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + encode(oauth_signature) + "\",oauth_token=\"" + encode(oauth_token) + "\"";
        // System.out.println("authorization_header_string=" + authorization_header_string);

        TwitterAuthenticationReturn retrnAuth = new TwitterAuthenticationReturn();


        try {
            URL url = new URL(twitter_endpoint);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestProperty("accept", "application/json");
            urlConn.setRequestProperty("Accept-Charset", UTF_8.toString());
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF_8);
//        urlConn.setRequestProperty( "Content-Type", "application/json" );
            urlConn.setRequestProperty( "Connection", "Keep-Alive"  );
            urlConn.setRequestProperty( "Host", "api.twitter.com"  );
            urlConn.setRequestProperty("User-Agent", "HttpCore/1.1");
            urlConn.setRequestProperty( "X-Target-URI", "http://api.twitter.com"  );
            urlConn.setRequestProperty( "Authorization", authorization_header_string);
            urlConn.setConnectTimeout(120*1000);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod( "POST" );
            urlConn.setDoInput(true);
            String entity = "oauth_verifier=" + encode(verifier_or_pin);

            urlConn.getOutputStream().write(entity.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            String read;
            StringBuffer  buffer = new StringBuffer();

            while((read = reader.readLine()) != null) {
                buffer.append(read);
            }
            String response = buffer.toString();

            StringTokenizer st = new StringTokenizer(response,"&");
            String currenttoken = "";
            while(st.hasMoreTokens())
            {
                currenttoken = st.nextToken();
                if(currenttoken.startsWith("oauth_token="))
                    retrnAuth.access_token = currenttoken.substring(currenttoken.indexOf("=") + 1);
                else if(currenttoken.startsWith("oauth_token_secret="))
                    retrnAuth.access_token_secret = currenttoken.substring(currenttoken.indexOf("=") + 1);
                else if(currenttoken.startsWith("user_id="))
                    retrnAuth.user_id = currenttoken.substring(currenttoken.indexOf("=") + 1);
                else if(currenttoken.startsWith("screen_name="))
                    retrnAuth.screen_name = currenttoken.substring(currenttoken.indexOf("=") + 1);
            }

            System.out.print(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return retrnAuth;
    }

    public String searchTweets(String q, String access_token, String access_token_secret)
    {
        String firstTwitterID = "";
        String oauth_token = access_token;
        String oauth_token_secret = access_token_secret;

        // generate authorization header
        String get_or_post = "GET";
        String oauth_signature_method = "HMAC-SHA1";

        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauth_nonce = uuid_string; // any relatively random alphanumeric string will work here

        // get the timestamp
        Calendar tempcal = Calendar.getInstance();
        long ts = tempcal.getTimeInMillis();// get current time in milliseconds
        String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds

        // the parameter string must be in alphabetical order
        // this time, I add 3 extra params to the request, "lang", "result_type" and "q".
        String parameter_string = "lang=en&oauth_consumer_key=" + twitter_consumer_key + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" + oauth_signature_method +
                "&oauth_timestamp=" + oauth_timestamp + "&oauth_token=" + encode(oauth_token) + "&oauth_version=1.0&q=" + encode(q) + "&result_type=mixed";
        System.out.println("parameter_string=" + parameter_string);
        String twitter_endpoint = "http://api.twitter.com/1.1/search/tweets.json";
        String twitter_endpoint_host = "api.twitter.com";
        String twitter_endpoint_path = "/1.1/search/tweets.json";
        String signature_base_string = get_or_post + "&"+ encode(twitter_endpoint) + "&" + encode(parameter_string);
        System.out.println("signature_base_string=" + signature_base_string);

        // this time the base string is signed using twitter_consumer_secret + "&" + encode(oauth_token_secret) instead of just twitter_consumer_secret + "&"
        String oauth_signature = "";
        try {
            oauth_signature = computeSignature(signature_base_string, twitter_consumer_secret + "&" + encode(oauth_token_secret));  // note the & at the end. Normally the user access_token would go here, but we don't know it yet for request_token
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String authorization_header_string = "OAuth oauth_consumer_key=\"" + twitter_consumer_key + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + oauth_timestamp +
                "\",oauth_nonce=\"" + oauth_nonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + encode(oauth_signature) + "\",oauth_token=\"" + encode(oauth_token) + "\"";
        System.out.println("authorization_header_string=" + authorization_header_string);

        try {
            URL url = new URL(twitter_endpoint+"?lang=en&result_type=mixed&q=" + encode(q));
            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection();

            urlConn.setRequestProperty("accept", "application/json");
            urlConn.setRequestProperty("Accept-Charset", UTF_8.toString());
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF_8);
            // urlConn.setRequestProperty( "Content-Type", "application/json" );
            urlConn.setRequestProperty("Connection", "Keep-Alive");
            urlConn.setRequestProperty("Host", "api.twitter.com");
            urlConn.setRequestProperty("User-Agent", "HttpCore/1.1");
            urlConn.setRequestProperty("X-Target-URI", "http://api.twitter.com");
            urlConn.setRequestProperty("Authorization",
                    authorization_header_string);
            urlConn.setConnectTimeout(120 * 1000);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConn.getInputStream()));

            String read;
            StringBuffer buffer = new StringBuffer();

            while ((read = reader.readLine()) != null) {
                buffer.append(read);
            }
            String response = buffer.toString();
            firstTwitterID = response;
            System.out.print(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return firstTwitterID;
    }

    public String reTweets(String id, String access_token, String access_token_secret)
    {

        String oauth_token = access_token;
        String oauth_token_secret = access_token_secret;

        // generate authorization header
        String get_or_post = "POST";
        String oauth_signature_method = "HMAC-SHA1";

        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauth_nonce = uuid_string; // any relatively random alphanumeric string will work here

        // get the timestamp
        Calendar tempcal = Calendar.getInstance();
        long ts = tempcal.getTimeInMillis();// get current time in milliseconds
        String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds

        // the parameter string must be in alphabetical order, "text" parameter added at end
        String parameter_string = "oauth_consumer_key=" + twitter_consumer_key + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" + oauth_signature_method +
                "&oauth_timestamp=" + oauth_timestamp + "&oauth_token=" + encode(oauth_token) + "&oauth_version=1.0";
        System.out.println("parameter_string=" + parameter_string);

        String twitter_endpoint = "https://api.twitter.com/1.1/statuses/retweet/" + id + ".json";
        String twitter_endpoint_host = "api.twitter.com";
        String twitter_endpoint_path = "/1.1/statuses/retweet/" + id + ".json";
        String signature_base_string = get_or_post + "&"+ encode(twitter_endpoint) + "&" + encode(parameter_string);
        System.out.println("signature_base_string=" + signature_base_string);
        String oauth_signature = "";
        try {
            oauth_signature = computeSignature(signature_base_string, twitter_consumer_secret + "&" + encode(oauth_token_secret));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String authorization_header_string = "OAuth oauth_consumer_key=\"" + twitter_consumer_key + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + oauth_timestamp +
                "\",oauth_nonce=\"" + oauth_nonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + encode(oauth_signature) + "\",oauth_token=\"" + encode(oauth_token) + "\"";
        System.out.println("authorization_header_string=" + authorization_header_string);

        String firstTwitterID = "";
        try {
            URL url = new URL(twitter_endpoint);
            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection();

            urlConn.setRequestProperty("accept", "application/json");
            urlConn.setRequestProperty("Accept-Charset", UTF_8.toString());
//			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF_8);
            // urlConn.setRequestProperty( "Content-Type", "application/json" );
            urlConn.setRequestProperty("Connection", "Keep-Alive");
            urlConn.setRequestProperty("Host", "api.twitter.com");
            urlConn.setRequestProperty("User-Agent", "HttpCore/1.1");
            urlConn.setRequestProperty("X-Target-URI", "http://api.twitter.com");
            urlConn.setRequestProperty("Authorization",
                    authorization_header_string);
            urlConn.setConnectTimeout(120 * 1000);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");



            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConn.getInputStream()));

            String read;
            StringBuffer buffer = new StringBuffer();

            while ((read = reader.readLine()) != null) {
                buffer.append(read);
            }
            String response = buffer.toString();
            firstTwitterID = response;
            System.out.print(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return firstTwitterID;
    }

//	public String reTweets(String id, String access_token, String access_token_secret)
//	{
//
//		String oauth_token = access_token;
//		String oauth_token_secret = access_token_secret;
//
//		// generate authorization header
//		String get_or_post = "POST";
//		String oauth_signature_method = "HMAC-SHA1";
//
//		String uuid_string = UUID.randomUUID().toString();
//		uuid_string = uuid_string.replaceAll("-", "");
//		String oauth_nonce = uuid_string; // any relatively random alphanumeric string will work here
//
//		// get the timestamp
//		Calendar tempcal = Calendar.getInstance();
//		long ts = tempcal.getTimeInMillis();// get current time in milliseconds
//		String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds
//
//		// the parameter string must be in alphabetical order, "text" parameter added at end
//		String parameter_string = "oauth_consumer_key=" + twitter_consumer_key + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" + oauth_signature_method +
//		    		"&oauth_timestamp=" + oauth_timestamp + "&oauth_token=" + encode(oauth_token) + "&oauth_version=1.0";
//		System.out.println("parameter_string=" + parameter_string);
//
//		String twitter_endpoint = "https://api.twitter.com/1.1/statuses/retweet/" + id + ".json";
//		String twitter_endpoint_host = "api.twitter.com";
//		String twitter_endpoint_path = "/1.1/statuses/retweet/" + id + ".json";
//		String signature_base_string = get_or_post + "&"+ encode(twitter_endpoint) + "&" + encode(parameter_string);
//		System.out.println("signature_base_string=" + signature_base_string);
//	    String oauth_signature = "";
//	    try {
//	    	oauth_signature = computeSignature(signature_base_string, twitter_consumer_secret + "&" + encode(oauth_token_secret));
//		} catch (GeneralSecurityException e) {
//			e.printStackTrace();
//		}
//	    catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//	    String authorization_header_string = "OAuth oauth_consumer_key=\"" + twitter_consumer_key + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + oauth_timestamp +
//	    		"\",oauth_nonce=\"" + oauth_nonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + encode(oauth_signature) + "\",oauth_token=\"" + encode(oauth_token) + "\"";
//	    System.out.println("authorization_header_string=" + authorization_header_string);
//
//		String firstTwitterID = "";
//		try {
//			URL url = new URL(twitter_endpoint);
//			HttpURLConnection urlConn = (HttpURLConnection) url
//					.openConnection();
//
//			urlConn.setRequestProperty("accept", "application/json");
//			urlConn.setRequestProperty("Accept-Charset", UTF_8.toString());
////			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF_8);
//			// urlConn.setRequestProperty( "Content-Type", "application/json" );
//			urlConn.setRequestProperty("Connection", "Keep-Alive");
//			urlConn.setRequestProperty("Host", "api.twitter.com");
//			urlConn.setRequestProperty("User-Agent", "HttpCore/1.1");
//			urlConn.setRequestProperty("X-Target-URI", "http://api.twitter.com");
//			urlConn.setRequestProperty("Authorization",
//					authorization_header_string);
//			urlConn.setConnectTimeout(120 * 1000);
//			urlConn.setDoOutput(true);
//			urlConn.setRequestMethod("POST");
//
//
//
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					urlConn.getInputStream()));
//
//			String read;
//			StringBuffer buffer = new StringBuffer();
//
//			while ((read = reader.readLine()) != null) {
//				buffer.append(read);
//			}
//			String response = buffer.toString();
//			firstTwitterID = response;
//			System.out.print(response);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return firstTwitterID;
//	}


    public String updateStatus(String text, String access_token, String access_token_secret)
    {

        String oauth_token = access_token;
        String oauth_token_secret = access_token_secret;

        // generate authorization header
        String get_or_post = "POST";
        String oauth_signature_method = "HMAC-SHA1";

        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauth_nonce = uuid_string; // any relatively random alphanumeric string will work here

        // get the timestamp
        Calendar tempcal = Calendar.getInstance();
        long ts = tempcal.getTimeInMillis();// get current time in milliseconds
        String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds

        // the parameter string must be in alphabetical order, "text" parameter added at end
        String parameter_string = "oauth_consumer_key=" + twitter_consumer_key + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" + oauth_signature_method +
                "&oauth_timestamp=" + oauth_timestamp + "&oauth_token=" + encode(oauth_token) + "&oauth_version=1.0&status=" + encode(text);
        System.out.println("parameter_string=" + parameter_string);

        String twitter_endpoint = "https://api.twitter.com/1.1/statuses/update.json";
        String twitter_endpoint_host = "api.twitter.com";
        String twitter_endpoint_path = "/1.1/statuses/update.json";
        String signature_base_string = get_or_post + "&"+ encode(twitter_endpoint) + "&" + encode(parameter_string);
        System.out.println("signature_base_string=" + signature_base_string);
        String oauth_signature = "";
        try {
            oauth_signature = computeSignature(signature_base_string, twitter_consumer_secret + "&" + encode(oauth_token_secret));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String authorization_header_string = "OAuth oauth_consumer_key=\"" + twitter_consumer_key + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + oauth_timestamp +
                "\",oauth_nonce=\"" + oauth_nonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + encode(oauth_signature) + "\",oauth_token=\"" + encode(oauth_token) + "\"";
        System.out.println("authorization_header_string=" + authorization_header_string);

        String firstTwitterID = "";
        try {
            URL url = new URL(twitter_endpoint);
            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection();

            urlConn.setRequestProperty("accept", "application/json");
            urlConn.setRequestProperty("Accept-Charset", UTF_8.toString());
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF_8);
            // urlConn.setRequestProperty( "Content-Type", "application/json" );
            urlConn.setRequestProperty("Connection", "Keep-Alive");
            urlConn.setRequestProperty("Host", "api.twitter.com");
            urlConn.setRequestProperty("User-Agent", "HttpCore/1.1");
            urlConn.setRequestProperty("X-Target-URI", "http://api.twitter.com");
            urlConn.setRequestProperty("Authorization",
                    authorization_header_string);
            urlConn.setConnectTimeout(120 * 1000);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");

            urlConn.setDoInput(true);
            String entity = "status=" + encode(text);

            urlConn.getOutputStream().write(entity.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            String read;
            StringBuffer buffer = new StringBuffer();

            while ((read = reader.readLine()) != null) {
                buffer.append(read);
            }
            String response = buffer.toString();
            firstTwitterID = response;
            System.out.print(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return firstTwitterID;
    }
    class TwitterAuthenticationReturn {
        public String access_token = "";
        public String access_token_secret = "";
        public String user_id = "";
        public String screen_name = "";
    }

    public static void openWebUrlExternal(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        context.startActivity(intent);
    }
}
