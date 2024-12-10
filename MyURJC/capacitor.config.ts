import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'io.ionic.starter',
  appName: 'MyURJC',
  webDir: 'www',
  server: {
    url: 'http://192.168.1.17:8080',
    cleartext: true
  },
  cordova: {
    preferences: {
      bluetooth_restore_state: "true",
      accessBackgroundLocation: "false",
    },
  
},
}

export default config;
