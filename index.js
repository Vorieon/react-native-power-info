import { NativeModules, Platform } from 'react-native';

function getNativeModule() {
  const module = NativeModules.PowerInfo;

  if(!module) {
    throw new Error('react-native-power-info: Native module not linked. Rebuild the app.');
  }

  return module;
}

export function getPowerInfo() {
  if (Platform.OS !== 'android') {
    throw new Error('react-native-power-info: Android only');
  }

  return getNativeModule().getPowerInfo();
}

export default { getPowerInfo };
