package com.zemnuhov.stressapp.AuthorizationResources;

import android.util.Log;
import android.widget.Toast;

import com.zemnuhov.stressapp.ConstantAndHelp;
import com.zemnuhov.stressapp.ServerAPI.AuthorizationRequest;
import com.zemnuhov.stressapp.ServerAPI.Controller;
import com.zemnuhov.stressapp.ServerAPI.StressAppAPI;
import com.zemnuhov.stressapp.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zemnuhov.stressapp.ConstantAndHelp.getContext;

public class AuthorizationController extends Controller implements Callback<User> {

    private static StartAppCallback callbackStart;

    public interface StartAppCallback{
        void startApp();
    }

    public static void registerCallback(StartAppCallback callback){
        callbackStart=callback;
    }

    public void authorization(String login, String password){

        StressAppAPI api = start();
        Call<User> call = api.authorization(new AuthorizationRequest(login,password));
        call.enqueue(this);
    }


    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        if(response.isSuccessful()) {
            User authUser = response.body();
            String user = authUser.getId()+"|"+authUser.getName()+"|"+authUser.getAge()+
                    "|"+authUser.getLogin()+"|"+authUser.getPass();
            ConstantAndHelp.saveAuthUser(user);
            Toast.makeText(getContext(),"Вы вошли как "+authUser.getName(),
                    Toast.LENGTH_LONG).show();
            callbackStart.startApp();

        } else {
            Log.i("Users", "Не правильный логин или пароль!");
        }
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        Log.i("Users", String.valueOf(t));
    }
}