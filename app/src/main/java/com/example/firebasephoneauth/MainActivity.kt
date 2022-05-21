package com.example.firebasephoneauth

import android.R.attr.phoneNumber
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasephoneauth.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? =null
    private var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? =null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var TAG = "MAIN_TAG"

    //private lateinit var progressDialog: progressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.phonell.visibility = View.VISIBLE
        binding.codeEt.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

//        progressDialog = progressDialog(this)
//        progressDialog.setTitle("please wait")
//        progressDialog.setCan
        mCallBacks = object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInwithPhoneAuthCredential(phoneAuthCredential)
                Log.d(TAG, "onVerificationCompleted: ")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d(TAG, "onVerificationFailed: ${e.message}")
                Toast.makeText(this@MainActivity,"${e.message}",Toast.LENGTH_LONG).show()
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                Log.d(TAG, "onCodeAutoRetrievalTimeOut: $verificationId")
                mVerificationId = verificationId
                var token = null
                forceResendingToken = token

                Log.d(TAG, "onCodeAutoRetrievalTimeOut: $verificationId")

                binding.phonell.visibility = View.GONE
                binding.codell.visibility = View.VISIBLE
                Toast.makeText(this@MainActivity,"Verification Code sent...",Toast.LENGTH_LONG).show()
                binding.codesentDescriptionTv.text ="Please type they verification code we sent to ${binding.phoneEt.text.toString().trim()}"

            }
        }
        binding.phoneContinueBtn.setOnClickListener {

            val phone = "+91" + binding.phoneEt.text.toString().trim()

            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@MainActivity,"Please enter phone number",Toast.LENGTH_LONG).show()
            }
            else{
                startPhoneNumberVerification(phone)
            }
        }
        binding.resendCodeTv.setOnClickListener {
            val phone = binding.phoneEt.text.toString().trim()

            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this@MainActivity,"Please enter phone number",Toast.LENGTH_LONG).show()
            }
            else{
                resendVerificationCode(phone,forceResendingToken)
            }
        }
        binding.codeSubmitBtn.setOnClickListener {
            val  code = binding.codeEt.text.toString().trim()
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this@MainActivity,"Please enter verification code",Toast.LENGTH_LONG).show()
            }
            else{
                verifyPhoneNumberwithcode(mVerificationId,code)
            }

        }


    }
    private fun startPhoneNumberVerification(phone: String){
        Log.d(TAG, "startPhoneNumberVerification: $phone")
//        progressDialog.setmessage("Verification Phone Number...")
            var options = mCallBacks?.let {
                PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(it)
                    .build()
            }

        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        }
    private fun resendVerificationCode(phone: String, token: PhoneAuthProvider.ForceResendingToken?){
        Log.d(TAG, "resendVerificationCode: $phone")
//        val options = mCallBacks?.let {
//            if (token != null) {
//                PhoneAuthOptions.newBuilder(firebaseAuth)
//                    .setPhoneNumber(phone)
//                    .setTimeout(60L, TimeUnit.SECONDS)
//                    .setActivity(this)
//                    .setCallbacks(it)
//                    .setForceResendingToken(token)
//                    .build()
//            }
        val options = mCallBacks?.let {
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(it) // OnVerificationStateChangedCallbacks
                .build()
        }
        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

      //  PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun verifyPhoneNumberwithcode(verificationId: String?,code:String){
        Log.d(TAG, "verifyPhoneNumberwithcode: $verificationId $code")
        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it,code) }
        if (credential != null) {
            signInwithPhoneAuthCredential(credential)
        }

    }

    private fun signInwithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "signInwithPhoneAuthCredential:   ")
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val phone = firebaseAuth.currentUser?.phoneNumber
                Toast.makeText(this,"Logged In as $phone",Toast.LENGTH_LONG).show()
                startActivity(Intent(this,ProfileActivity2::class.java))
                finish()
            }
            .addOnFailureListener {e->
                Toast.makeText(this,"${e.message}",Toast.LENGTH_LONG).show()

            }

    }
}
