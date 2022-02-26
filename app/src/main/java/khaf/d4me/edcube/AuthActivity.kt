package khaf.d4me.edcube

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private val GOOGLE_SING_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {

        //splash
        Thread.sleep(2000)//HACK
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message","Integracion de firebase completa")
        analytics.logEvent("InitScreen",bundle)

        //Setup
        notification()
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        layauth.visibility = View.VISIBLE
    }

    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("Email",null)
        val provider = prefs.getString("Provider",null)

        if(email!=null && provider!=null) {
            layauth.visibility = View.INVISIBLE
            showhome(email, MainActivity.ProviderType.valueOf(provider))
        }
    }
    private fun notification(){
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            it.result?.token?.let {
                println("Este es el token del dispositivo: ${it}")
            }
        }
        //Temas
        FirebaseMessaging.getInstance().subscribeToTopic("Tuto")
        //Recuperar info
        val url = intent.getStringExtra("url")
        url?.let{
            println("Ha llegado info en una push: ${it}")
        }
    }
    private fun setup(){
        title = "Autenticacion"
        btnRegistrar.setOnClickListener(View.OnClickListener {
            if(txtEmail.text.isNotEmpty() && txtPass.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().
                createUserWithEmailAndPassword(txtEmail.text.toString(),
                    txtPass.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        showhome(it.result?.user?.email ?: "", MainActivity.ProviderType.BASIC)
                    }else{
                        showalert()
                    }
                }
            }
        })
        btnAcceder.setOnClickListener(View.OnClickListener {
            if(txtEmail.text.isNotEmpty() && txtPass.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmail.text.toString(),
                    txtPass.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        showhome(it.result?.user?.email ?: "", MainActivity.ProviderType.BASIC)
                    }else{
                        showalert()
                    }
                }
            }
        })
        btnGoogle.setOnClickListener(View.OnClickListener {
            val googleconf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleclient = GoogleSignIn.getClient(this,googleconf)
            googleclient.signOut()
            startActivityForResult(googleclient.signInIntent,GOOGLE_SING_IN)
        })
    }
    private fun showalert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showhome(email: String, provider: MainActivity.ProviderType){
        val homeIntent = Intent(this,MainActivity::class.java).apply {
            putExtra("Email",email)
            putExtra("Provider",provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SING_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if(account!=null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful){
                            showhome(account.email?:"", MainActivity.ProviderType.GOOGLE)
                        }
                        else
                        {
                            showalert()
                        }
                    }
                }
            }catch (e:ApiException){
                showalert()
            }
        }
    }

}