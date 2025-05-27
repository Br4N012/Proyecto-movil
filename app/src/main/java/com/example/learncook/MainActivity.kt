package com.example.learncook
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.learncook.databinding.ActivityMainBinding
import com.example.learncook.fragmentos.HomeFragment
import com.example.learncook.fragmentos.PerfilFragment
import com.example.learncook.fragmentos.RecetaFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var idUsuario = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigation: BottomNavigationView = binding.bottonNavigation
        idUsuario = intent.getIntExtra("idUsuario",-1)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_home -> {
                    openFragment(HomeFragment.newInstance(idUsuario))
                    true
                }
                R.id.item_perfil -> {
                    openFragment(PerfilFragment.newInstance(idUsuario))
                    true
                }
                R.id.item_receta -> {
                    openFragment(RecetaFragment.newInstance(idUsuario))
                    true
                }
                else -> false
            }
        }


        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.item_home
        }

    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_contenedor, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
