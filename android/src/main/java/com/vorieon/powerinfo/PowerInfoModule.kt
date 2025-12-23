package com.vorieon.powerinfo

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.facebook.react.bridge.*

class PowerInfoModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String = "PowerInfo"

  @ReactMethod
  fun getPowerInfo(promise: Promise) {
    try {
      val batteryIntent = reactContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

      if (batteryIntent == null) {
        promise.reject("UNAVAILABLE", "Battery info unavailable")
        return
      }

      val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
      val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
      val batteryPct = if (level >= 0 && scale > 0) level * 100f / scale else -1f

      val result = Arguments.createMap().apply {
        putDouble("batteryLevel", batteryPct.toDouble())
      }

      promise.resolve(result)

    } catch (e: Exception) {
      promise.reject("ERROR", e.message, e)
    }
  }

}
