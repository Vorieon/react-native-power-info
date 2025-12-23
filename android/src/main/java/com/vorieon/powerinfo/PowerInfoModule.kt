package com.vorieon.powerinfo

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import kotlin.math.roundToInt

class PowerInfoModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String = "PowerInfo"

  private val handler = Handler(Looper.getMainLooper())
  private var runnable: Runnable? = null

  private fun batteryIntent(): Intent? {
    return reactContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
  }

  private fun emit(event: String, params: WritableMap) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(event, params)
  }

  // ---------- One-time getters ----------

  @ReactMethod
  fun getVoltage(promise: Promise) {
    val intent = batteryIntent()
    val voltageMv = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
    promise.resolve(voltageMv)
  }

  @ReactMethod
  fun getCurrent(type: String?, promise: Promise) {
    val bm = reactContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val property = when (type) {
      "average" -> BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE
      else -> BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
    }

    val currentUa = bm.getIntProperty(property)
    val currentMa = if (currentUa == Int.MIN_VALUE) 0 else currentUa / 1000
    promise.resolve(currentMa)
  }

  // Get wattage... pending...

  @ReactMethod
  fun getBatteryLevel(promise: Promise) {
    val intent = batteryIntent()
    val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    
    val batteryPct = if (level >= 0 && scale > 0) ((level.toFloat() / scale) * 100).roundToInt() else -1
    promise.resolve(batteryPct)
  }

  @ReactMethod
  fun isCharging(promise: Promise) {
    val intent = batteryIntent()
    val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

    promise.resolve(
      status == BatteryManager.BATTERY_STATUS_CHARGING ||
      status == BatteryManager.BATTERY_STATUS_FULL
    )
  }

  @ReactMethod
  fun getChargerType(promise: Promise) {
    val intent = batteryIntent()
    val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1

    val type = when (plugged) {
      BatteryManager.BATTERY_PLUGGED_AC -> "ac"
      BatteryManager.BATTERY_PLUGGED_DOCK -> "dock"
      BatteryManager.BATTERY_PLUGGED_USB -> "usb"
      BatteryManager.BATTERY_PLUGGED_WIRELESS -> "wireless"
      else -> "unknown"
    }
    promise.resolve(type)
  }

  @ReactMethod
  fun getBatteryHealth(promise: Promise) {
    val intent = batteryIntent()
    val healthInt = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1

    val health = when (healthInt) {
      BatteryManager.BATTERY_HEALTH_COLD -> "cold"
      BatteryManager.BATTERY_HEALTH_DEAD -> "dead"
      BatteryManager.BATTERY_HEALTH_GOOD -> "good"
      BatteryManager.BATTERY_HEALTH_OVERHEAT -> "overheat"
      BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "over_voltage"
      BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "failure"
      else -> "unknown"
    }
    promise.resolve(health)
  }

  @ReactMethod
  fun getBatteryTemperature(promise: Promise) {
    val intent = batteryIntent()
    val tempC = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
    promise.resolve(tempC / 10.0)
  }

  @ReactMethod
  fun getBatteryCycleCount(promise: Promise) {
    if (android.os.Build.VERSION.SDK_INT < 34) {
      promise.resolve(null)
      return
    }

    val intent = batteryIntent()
    val cycles = intent?.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1) ?: -1
    promise.resolve(cycles)
  }

  // ---------- Monitoring ----------

}
