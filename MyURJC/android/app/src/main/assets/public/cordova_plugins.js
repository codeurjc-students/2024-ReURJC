
  cordova.define('cordova/plugin_list', function(require, exports, module) {
    module.exports = [
      {
          "id": "cordova-plugin-bluetoothle.BluetoothLe",
          "file": "plugins/cordova-plugin-bluetoothle/www/bluetoothle.js",
          "pluginId": "cordova-plugin-bluetoothle",
        "clobbers": [
          "window.bluetoothle"
        ]
        }
    ];
    module.exports.metadata =
    // TOP OF METADATA
    {
      "cordova-plugin-bluetoothle": "6.7.4"
    };
    // BOTTOM OF METADATA
    });
    