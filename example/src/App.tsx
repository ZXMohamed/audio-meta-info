import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { getmetadata } from 'audio-meta-info';

export default function App() {

  return (
    <View style={styles.container}>
      <Text>meta-data</Text>
      {getmetadata({ songUri: "/storage/emulated/0/Download/ty.mp3",coverFolderpath:"/storage/emulated/0/Download",covername:"cover",iconname:"icon",cover:true,icon:true,coverResizeRatio:2.0 ,iconResizeRatio:1.0}, (c) => { console.log(c); }, (c) => { console.log(c); })}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
