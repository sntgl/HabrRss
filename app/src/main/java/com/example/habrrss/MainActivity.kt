package com.example.habrrss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.habrrss.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var fragment : Fragment? = null
    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

//        fragment = FeedFragment()

//        supportFragmentManager.beginTransaction()
//            .replace(R.id.root_fragment, fragment!!)
//            .commit()
    }


    override fun onDestroy() {
        super.onDestroy()
//        fragment = null
    }

}