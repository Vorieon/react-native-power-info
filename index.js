import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const MODULE = NativeModules.PowerInfo;

function checkModuleStatus() {
  if (Platform.OS !== 'android') {
    throw new Error('react-native-power-info: This module is Android only.');
  }
  if(!MODULE) {
    throw new Error('react-native-power-info: Native module not linked. Rebuild the app.');
  }
}

const emitter = MODULE ? new NativeEventEmitter(MODULE) : null;

// ------------------------------
// One-shot Getters
// ------------------------------

export function getVoltage() {
  checkModuleStatus();
  return MODULE.getVoltage();
}

export function getCurrent(type='now') {
  checkModuleStatus();
  return MODULE.getCurrent(type);
}

export function getBatteryLevel() {
  checkModuleStatus();
  return MODULE.getBatteryLevel();
}

export function isCharging() {
  checkModuleStatus();
  return MODULE.isCharging();
}

export function getChargerType() {
  checkModuleStatus();
  return MODULE.getChargerType();
}

export function getBatteryHealth() {
  checkModuleStatus();
  return MODULE.getBatteryHealth();
}

export function getBatteryTemperature() {
  checkModuleStatus();
  return MODULE.getBatteryTemperature();
}

export function getBatteryCycleCount() {
  checkModuleStatus();
  return MODULE.getBatteryCycleCount();
}

// ------------------------------
// Event-based Power State
// ------------------------------

export function addPowerStateListener() {
  checkModuleStatus();
  MODULE.addPowerStateListener();
}

export function removePowerStateListener() {
  checkModuleStatus();
  MODULE.removePowerStateListener();
}

export function onPowerStateChanged(callback) {
  checkModuleStatus();

  if (!emitter) {
    throw new Error('react-native-power-info: Event emitter not available.');
  }

  return emitter.addListener('powerState', callback);
}

export default {
  // One-shot Getters
  getVoltage,
  getCurrent,
  getBatteryLevel,
  isCharging,
  getChargerType,
  getBatteryHealth,
  getBatteryTemperature,
  getBatteryCycleCount,
  // Event-based Power State
  addPowerStateListener,
  removePowerStateListener,
  onPowerStateChanged,
};
