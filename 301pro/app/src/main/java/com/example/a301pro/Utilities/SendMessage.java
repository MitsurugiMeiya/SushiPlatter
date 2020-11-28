package com.example.a301pro.Utilities;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.a301pro.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SendMessage {
    private String senderUserName;
    private String receiverUserName;
    private String message;

    protected FirebaseFirestore db;
    String TAG = "send a message";


    public SendMessage(final String senderUserName, final String receiverUserName, final String message){
        db = FirebaseFirestore.getInstance();
        String receiverUID;
        CollectionReference collectionReference = db.collection("userDict");
        DocumentReference docRef = collectionReference.document(receiverUserName);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String UID = (String) document.getData().get("UID");
                        addMessageToDB(UID ,senderUserName , message, receiverUserName);
                    }
                } else {
                }
            }
        });
    }

    public void addMessageToDB(String UID, String senderUserName, String message, String receiverUserName){
        final CollectionReference CollectRef = db.collection("Users");
        Map<String, Object> messageMap = new HashMap<>();

        Timestamp ts=new Timestamp(System.currentTimeMillis());
        String mstTime = ts.toString();
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        messageMap.put("sender", senderUserName);
        messageMap.put("message", message);
        messageMap.put("time", mstTime);
        messageMap.put("timeStamp", timeStamp);
        messageMap.put("receiver", receiverUserName);

        CollectRef
                .document(UID)
                .collection("Messages")
                .document(timeStamp)
                .set(messageMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded
                        Log.d(TAG, "Message has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.d(TAG, "Message could not be added!" + e.toString());
                    }
                });
    }


}
