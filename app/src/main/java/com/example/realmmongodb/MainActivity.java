package com.example.realmmongodb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.bson.Document;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class MainActivity extends AppCompatActivity {
String Appid="mongodbcourse-ywvxt";
    private App app;

    public static final String TAG = "ServerAuthCodeActivity";
    private static final int RC_GET_AUTH_CODE = 9003;

    private EditText dataEditText;
    ArrayList<String> strings = new ArrayList<>();
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mAuthCodeTextView;
    private Button add,gettext,button3;
    MongoCollection<Document> mongoCollection;
    MongoDatabase mongoDatabase;
   MongoClient mongoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add = findViewById(R.id.button);
        mAuthCodeTextView=findViewById(R.id.textView);
gettext=findViewById(R.id.button2);
button3=findViewById(R.id.button3);
        Realm.init(getApplicationContext());
        dataEditText = findViewById(R.id.editText);
        final  App app = new App(new AppConfiguration.Builder(Appid).build());
//        Credentials credentials = Credentials.emailPassword("bandlavhemanth30431@gmail.com", "BVH022208");


//        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
//            @Override
//            public void onResult(App.Result<User> result) {
//                if (result.isSuccess()) {
//                    Log.v("User", "Logged In");
//
//                    Toast.makeText(MainActivity.this, result.toString(),Toast.LENGTH_LONG).show();
//                    User user=app.currentUser();
//                    mongoClient = user.getMongoClient("mongodb-atlas");
//                    mongoDatabase = mongoClient.getDatabase("CourseData");
//                    mongoCollection = mongoDatabase.getCollection("TestData");
//
//                } else {
//                    Log.v("User", "Failed");
//                }
//            }
//        });
        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if(result.isSuccess())
                {
                    Log.v("User","Logged In Successfully");
                   User user = app.currentUser();
                    mongoClient = user.getMongoClient("mongodb-atlas");
                    mongoDatabase = mongoClient.getDatabase("CourseData");
                    mongoCollection = mongoDatabase.getCollection("TestData");
                    Toast.makeText(getApplicationContext(),"Login Succesful",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Log.v("User","Failed to Login");
                }
            } });

   /*  app.getEmailPassword().registerUserAsync("bandlavhemanth30431@gmail.com","BVH022208", new App.Callback<Void>() {
         @Override
         public void onResult(App.Result<Void> it) {
             if (it.isSuccess()) {
                 Log.v("User", "Logged In");
             } else {
                 Log.v("User", "Failed");
             }
         }
     });


   */
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                User user = app.currentUser();
//                mongoClient = user.getMongoClient("mongodb-atlas");
//                mongoDatabase = mongoClient.getDatabase("CourseData");
//                MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("TestData");
//                mongoCollection.insertOne(new Document("userid", user.getId()).append("data", dataEditText.getText().toString())).getAsync(result -> {
//                    if (result.isSuccess()) {
//                        Log.v("Data", "Success");
//                    }else{
//                        System.out.print( result.getError().toString()+" "+"***************");
//                        Toast.makeText(MainActivity.this, result.getError().toString(),Toast.LENGTH_LONG).show();
//                        Log.v("Data", result.getError().toString());
//                    }
//                });
//                mongoCollection.insertOne(new Document("userid", user.getId()).append("data", dataEditText.getText().toString())).getAsync(result -> {
//                    if (result.isSuccess()) {
//                        Log.v("Data", "Success");
//                    } else {
//                        Log.v("Data", "Error");
//                    }
//                });
                Document queryFilter = new Document().append("uniqueId","1234");
                RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();

                findTask.getAsync(task -> {
                    if(task.isSuccess())
                    {
                        MongoCursor<Document> results = task.get();

                        if(results.hasNext())
                        {
                            Log.v("FindFunction","Found Something");
                            Document result = results.next();
                            strings = (ArrayList<String>) result.get("strings");
                            if(strings == null)
                            {
                                strings = new ArrayList<>();
                            }
                            String data = dataEditText.getText().toString();
                            strings.add(data);
                            result.append("strings",strings);
                            mongoCollection.updateOne(queryFilter,result).getAsync(result1 -> {
                                if (result1.isSuccess())
                                {
                                    Log.v("UpdateFunction","Updated Data");

                                }
                                else
                                {
                                    Log.v("UpdateFunction","Error"+result1.getError().toString());
                                }

                            });
                        }
                        else
                        {
                            String data = dataEditText.getText().toString();
                            if(strings == null)
                            {
                                strings = new ArrayList<>();
                            }
                            strings.add(data);
                            Log.v("FindFunction","Found Nothing");
                            mongoCollection.insertOne(new Document().append("uniqueId","1234").append("strings",strings)).getAsync(result -> {
                                if(result.isSuccess())
                                {
                                    Log.v("AddFunction","Inserted Data");
                                }
                                else
                                {
                                    Log.v("AddFunction","Error"+result.getError().toString());
                                }
                            });
                        }
                    }
                    else
                    {
                        Log.v("Error",task.getError().toString());
                    }
                });
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
Document document=new Document().append("data","Find in Working");
                mongoCollection.insertOne(document).getAsync(result -> {
                    if(result.isSuccess())
                    { Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show();}else{
                        Toast.makeText(MainActivity.this, result.getError().toString(),Toast.LENGTH_LONG).show();
                        Log.v("Data", result.getError().toString());
                    }
                });

            }
        });
        gettext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
Document queri=new Document().append("myid","1234");
                RealmResultTask<Document> findtask;

            }
        });

//
//        validateServerClientID();
//
//
//        String serverClientId = getString(R.string.server_clint_id);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
//                .requestServerAuthCode(serverClientId)
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.server_clint_id))
//                .requestEmail()
//                .build();
//        // [END configure_signin]
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//
//    }
//    private void getAuthCode() {
//        // Start the retrieval process for a server auth code.  If requested, ask for a refresh
//        // token.  Otherwise, only get an access token if a refresh token has been previously
//        // retrieved.  Getting a new access token for an existing grant does not require
//        // user consent.
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_GET_AUTH_CODE);
//    }
//
//    private void signOut() {
//        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//            }
//        });
//    }
//
//    private void revokeAccess() {
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                    }
//                });
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_GET_AUTH_CODE) {
//            // [START get_auth_code]
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                String authCode = account.getServerAuthCode();
//
//                // Show signed-un UI
//
//                // TODO(developer): send code to server and exchange for access/refresh/ID tokens
//
//                Credentials googleCredentials = Credentials.google(authCode);
//              final  App app=new App(new AppConfiguration.Builder(Appid).build());
//
//                app.loginAsync(googleCredentials, new App.Callback<User>() {
//                    @Override
//                    public void onResult(App.Result<User> it) {
//                        if (it.isSuccess()) {
//                            Log.v(TAG, "Successfully authenticated using Google OAuth.");
//                            User user = app.currentUser();
//                        } else {
//                            Log.e(TAG, it.getError().toString());
//                        }
//                    }
//                });
//
//            } catch (ApiException e) {
//                Log.w(TAG, "Sign-in failed", e);
//            }
//            // [END get_auth_code]
//        }
//    }
//
//    /**
//     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
//     * to make sure users of this sample follow the README.
//     */
//    private void validateServerClientID() {
//        String serverClientId = getString(R.string.server_clint_id);
//        String suffix = ".apps.googleusercontent.com";
//        if (!serverClientId.trim().endsWith(suffix)) {
//            String message = "Invalid server client ID in strings.xml, must end with " + suffix;
//
//            Log.w(TAG, message);
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//        }
//    }
    }
}
