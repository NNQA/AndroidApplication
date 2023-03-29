package hcmute.edu.vn.spotifyclone;

import static hcmute.edu.vn.spotifyclone.R.id.profile;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.Openable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class Profile extends Fragment {

    //public TextView tvUsername;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment;

        //tvUsername = getView().findViewById(R.id.username);
        System.out.println(getData("userName"));
        //tvUsername.setText(getData("userName"));
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    private String getData(String key){
        SharedPreferences sharedPreferences= getContext().getSharedPreferences("myRef", 0);
        if(sharedPreferences.contains(key)){
            String data = sharedPreferences.getString(key,null);
            return data;
        }
        else{
            return null;
        }
    }

}