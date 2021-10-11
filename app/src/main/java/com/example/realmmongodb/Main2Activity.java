package com.example.realmmongodb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.bson.Document;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class Main2Activity extends AppCompatActivity {
    String Appid = "mongodbcourse-ywvxt";
    private App app;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    EditText editText;
    Button button,button1,button2;
    TextView textView;
    String data;
    User user;
    MongoCollection<Document> mongoCollection;
    ArrayList<String> strings = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        editText = findViewById(R.id.data);
        button = findViewById(R.id.addData);
        button1 = findViewById(R.id.findDataButton);
        textView = findViewById(R.id.findData);
        button2 = findViewById(R.id.signin);
        Realm.init(getApplicationContext());
        app = new App(new AppConfiguration.Builder(Appid).build());
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.currentUser().logOutAsync(result -> {
                    if(result.isSuccess())
                    {
                        Log.v("Logged Out","Logged Out");
                        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
                            @Override
                            public void onResult(App.Result<User> result) {
                                if(result.isSuccess())
                                {
                                    Log.v("User","Logged In Successfully");
                                    user = app.currentUser();
                                    mongoClient = user.getMongoClient("mongodb-atlas");
                                    mongoDatabase = mongoClient.getDatabase("CourseData");
                                    mongoCollection = mongoDatabase.getCollection("TestData");
                                    Toast.makeText(getApplicationContext(),"Login Succesful",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Log.v("User","Failed to Login");
                                }
                            }
                        });
                    }
                });

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.v("adding","adding");
//                Document document = new Document().append("data",editText.getText().toString()).append("myid","1234").append("userid",user.getId());
//                mongoCollection.insertOne(document).getAsync(result -> {
//                    if(result.isSuccess())
//                    {
//                        Log.v("adding","result");
//                        Toast.makeText(getApplicationContext(),"Inserted",Toast.LENGTH_LONG).show();
//                    }
//                    else
//                    {
//                        Log.v("adding","result failed"+result.getError().toString());
//                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
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
                            String data = editText.getText().toString();
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
                            String data = editText.getText().toString();
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

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = app.currentUser();
                data = "";
                Document queryFilter = new Document().append("uniqueId","1234");

                RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(queryFilter).iterator();
//                 mongoCollection.findOne(queryFilter).getAsync(result -> {
//                     if(result.isSuccess())
//                     {
//                         Toast.makeText(getApplicationContext(),"Found",Toast.LENGTH_LONG).show();
//                         Log.v("Data",result.toString());
//                         Document resultdata = result.get();
//                         textView.setText(resultdata.getString("data"));
//                     }
//                     else
//                     {
//                         Toast.makeText(getApplicationContext(),"Not found",Toast.LENGTH_LONG).show();
//                         Log.v("Data",result.getError().toString());
//                     }
//                 });

                findTask.getAsync(task->{
                    if(task.isSuccess())
                    {
                        MongoCursor<Document> results = task.get();
                        if(!results.hasNext())
                        {
                            Log.v("Result","Couldnt Find");
                        }
                        while(results.hasNext())
                        {
                            Document currentDoc = results.next();
                            strings = (ArrayList<String>) currentDoc.get("strings");
                            if(strings == null)
                            {
                                Log.v("FindTask","Strings has size 0");
                            }
                            else
                            {
                                int i=0;
                                for(i=0;i<strings.size();i++)
                                {
                                    if(data == null)
                                    {
                                        data = "";
                                        data = data + " & " + strings.get(i);
                                        textView.setText(data);
                                    }
                                    else
                                    {
                                        data = data + " & " + strings.get(i);
                                        textView.setText(data);
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        Log.v("Task Error",task.getError().toString());
                    }
                });
            }
        });




    }
}