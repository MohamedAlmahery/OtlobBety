package com.phoenix.otlobbety.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Model.Token;

public class MyFirebaseService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String toTokenRefresh = FirebaseInstanceId.getInstance().getToken();
        if(Common.currentUser != null)
            updateTokenToFirebase(toTokenRefresh);
    }

    private void updateTokenToFirebase(String toTokenRefresh) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Token");
        Token token = new Token(toTokenRefresh,false); //false because this token from Client App
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
