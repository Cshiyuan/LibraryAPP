package com.cczq.booksearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cczq.booksearch.fragment.BookFragment;
import com.cczq.booksearch.fragment.PersonalFragment;
import com.cczq.booksearch.fragment.RecordFragment;
import com.cczq.booksearch.fragment.SettingsFragment;
import com.cczq.booksearch.loginresgister.LoginActivity;
import com.cczq.booksearch.loginresgister.utils.SessionManager;
import com.cczq.booksearch.utils.SQLiteHandler;
import com.cczq.booksearch.widget.CanaroTextView;
import com.yalantis.guillotine.animation.GuillotineAnimation;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    private final static int SCANNIN_GREQUEST_CODE = 1;
    private final static int BOOKLIST_GREQUEST_CODE = 2;

    private static final long RIPPLE_DURATION = 250;
    private SQLiteHandler db;
    public SessionManager session;
    private GuillotineAnimation guillotineAnimation;
    private FragmentManager fragmentManager;
//    private ContextMenuDialogFragment mMenuDialogFragment;
    private Menu mMenu;
    private Fragment myFragment;


    public String oldStartModelFid;
    public String oldEndModelFid;
    public int defaultColor;


//    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.root)
    FrameLayout root;
//    @BindView(R.id.content_hamburger)
    View contentHamburger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        root = (FrameLayout)findViewById(R.id.root);
        contentHamburger = findViewById(R.id.content_hamburger);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        //创建碎片管理器
        fragmentManager = getSupportFragmentManager();

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        if (findViewById(R.id.fragment_container) != null) {

            //如果savedInstanceState不为空，说明只是从先前状态恢复，不必做任何工作直接返回
            if (savedInstanceState != null) {
//                return;
            }
        }
        //创建Fragmen
        BookFragment firstFragment = new BookFragment();
        myFragment = firstFragment;
        //取Intent的附加数据作为fragment构造函数的参数
        firstFragment.setArguments(getIntent().getExtras());
        //把fragment嵌入到容器
        fragmentManager.beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        ((TextView) findViewById(R.id.titleBar)).setText("图书");

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);
        //GuillotineBuilder的第一个参数为菜单的View,
        //第二个参数为关闭菜单的View也就是菜单布局中的按钮,
        //第三个参数为打开菜单的View也就是主页面中的按钮
        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();
        guillotineMenu.setOnClickListener(this);

        findViewById(R.id.personal_group).setOnClickListener(this);
        findViewById(R.id.group_group).setOnClickListener(this);
        findViewById(R.id.mission_group).setOnClickListener(this);
        findViewById(R.id.settings_group).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        jumpToTheFragment(v.getId());
    }

    /**
     * @param i 通过点击的资源id来跳转到想要的fragement
     */
    private void jumpToTheFragment(int i) {
        //    hiddenMenu();
        clearSelect();
        Fragment jumpToFragment = null;
        switch (i) {
            case R.id.personal_group:
                ((TextView) findViewById(R.id.titleBar)).setText("个人资料");
                ((CanaroTextView) findViewById(R.id.personal)).setTextAppearance(this, R.style.TextView_GuillotineItem_Selected);
                jumpToFragment = new PersonalFragment();
                break;
            case R.id.group_group:
                ((TextView) findViewById(R.id.titleBar)).setText("图书");
                ((CanaroTextView) findViewById(R.id.group)).setTextAppearance(this, R.style.TextView_GuillotineItem_Selected);
                jumpToFragment = new BookFragment();
//                BookFragment bookFragment = (BookFragment)jumpToFragment;
//                bookFragment.initMap();
//                (BookFragment)jumpToFragment.initMap();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //文章fragment--newFragment完全取代容器上的一个fragment
//                transaction.replace(R.id.fragment_container, myFragment,"fragmentTag");
//                transaction.commit();
//                guillotineAnimation.close();
//                return;
                break;
            case R.id.mission_group:
                ((TextView) findViewById(R.id.titleBar)).setText("记录");
                ((CanaroTextView) findViewById(R.id.mission)).setTextAppearance(this, R.style.TextView_GuillotineItem_Selected);
                jumpToFragment = new RecordFragment();
                break;
            case R.id.settings_group:
                ((TextView) findViewById(R.id.titleBar)).setText("设置");
                ((CanaroTextView) findViewById(R.id.settings)).setTextAppearance(this, R.style.TextView_GuillotineItem_Selected);
                jumpToFragment = new SettingsFragment();
                break;
            default:
                guillotineAnimation.close();
                return;
        }
        myFragment = jumpToFragment;  //存储当前的fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //文章fragment--newFragment完全取代容器上的一个fragment
        transaction.replace(R.id.fragment_container, jumpToFragment,"fragmentTag");
        //增加一个返回堆栈，用户可以导航回来
        //transaction.addToBackStack(null);
        //提交事务
        transaction.commit();
        //关闭guillotineAnimation
        guillotineAnimation.close();
    }

    /**
     * 清除相应的选择
     */
    private void clearSelect() {
        ((CanaroTextView) findViewById(R.id.personal)).setTextAppearance(this, R.style.TextView_GuillotineItem);
        ((CanaroTextView) findViewById(R.id.group)).setTextAppearance(this, R.style.TextView_GuillotineItem);
        ((CanaroTextView) findViewById(R.id.mission)).setTextAppearance(this, R.style.TextView_GuillotineItem);
        ((CanaroTextView) findViewById(R.id.settings)).setTextAppearance(this, R.style.TextView_GuillotineItem);
    }

    public void logoutUser() {
        session.setLogin(false, session.UID());
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
//	 * requestCode 请求码，即调用startActivityForResult()传递过去的值
//	 * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Toast.makeText(this,
                            "扫描结果： " + bundle.getString("result"),
                            Toast.LENGTH_SHORT).show(); // 输出例子书架提醒
//                    fragmentManager = getSupportFragmentManager();
//                    BookFragment bookFragment = (BookFragment)getSupportFragmentManager().findFragmentByTag("fragmentTag");

                    BookFragment bookFragment = (BookFragment)myFragment;
                    bookFragment.addPoint(bundle.getString("result"), 0);
//                    bookFragment.endPoint = bundle.getString("result");
                }

                break;
            case BOOKLIST_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Toast.makeText(this,
                            "所在书架： " + bundle.getString("slf_name"),
                            Toast.LENGTH_SHORT).show(); // 输出例子书架提醒
                    BookFragment bookFragment = (BookFragment)myFragment;
//                    BookFragment bookFragment = (BookFragment)getSupportFragmentManager().findFragmentByTag("fragmentTag");
                    bookFragment.addPoint(bundle.getString("slf_name"), 1);
//                    bookFragment.endPoint = bundle.getString("slf_name");
                }
                break;
        }
    }

}
