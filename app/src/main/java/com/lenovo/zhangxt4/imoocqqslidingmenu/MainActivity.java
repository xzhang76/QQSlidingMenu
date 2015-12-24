package com.lenovo.zhangxt4.imoocqqslidingmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import view.SlidingMenu;

public class MainActivity extends AppCompatActivity {

    private view.SlidingMenu mLeftMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);
    }
    public void toggleMenu(View view){
        mLeftMenu.toggle();
    }
}
