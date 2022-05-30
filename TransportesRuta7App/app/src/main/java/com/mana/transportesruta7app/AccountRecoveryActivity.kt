package com.mana.transportesruta7app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mana.transportesruta7app.databinding.ActivityAccountRecoveryBinding

class AccountRecoveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountRecoveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

       /* binding.sendEmailAppCompatButton.setOnClickListener {
            val emailAddress = binding.emailEd.text.toString()
            Firebase.auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val intent = Intent(this, LoginActivity::class.java)
                    this.startActivity(intent)
                } else {
                    Toast.makeText(this, "Ingrese un email de una cuenta valida.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }*/
    }
}