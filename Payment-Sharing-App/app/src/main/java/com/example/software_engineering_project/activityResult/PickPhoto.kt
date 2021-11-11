package com.example.software_engineering_project.activityResult

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity

class PickPhoto: ActivityResultContract<Int, Uri?>() {
    companion object{
        val TAG = "PICK PHOTO"
    }
    override fun createIntent(context: Context, ringtoneType: Int) : Intent {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        return intent
    }

    override fun parseResult(resultCode: Int, result: Intent?) : Uri? {
        if (resultCode != AppCompatActivity.RESULT_OK) {
            Log.i(TAG,"NULL")
            return null
        }
        return result?.data
    }
}
