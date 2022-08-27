import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'audio-meta-info' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const AudioMetaInfo = NativeModules.AudioMetaInfo  ? NativeModules.AudioMetaInfo  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function getmetadata(options:any,onsuccess:any,onerror:any) {
  return AudioMetaInfo.getSongByPath(options,onsuccess,onerror);
}
