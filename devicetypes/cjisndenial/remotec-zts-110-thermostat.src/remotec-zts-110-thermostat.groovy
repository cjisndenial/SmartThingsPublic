// QuickTest 

/**
 *  Remotec ZTS-110 Thermostat
 *
 *  Copyright 2017 CJ Saretto (CJS)
 *  
 *  - Merged code from Dennis Spanogle with current SmartThings zwave-thermostat template as of 11/23/2017
 *  - 
 *  
 *  Copyright 2016 Dennis Spanogle (DES)
 *
 *  Modified Smart Things generic Z-Wave Thermostat
 *    added 3 configuration parameters  (for now)
 *    added battery status report (in case user selects battery)
 
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Remotec ZTS-110 Thermostat", namespace: "cjisndenial", author: "CJ Saretto") {
		capability "Actuator"
		capability "Temperature Measurement"
		capability "Thermostat"
		capability "Refresh"
		capability "Sensor"
		capability "Health Check"
        capability "Battery"   				// DES added

		attribute "thermostatFanState", "string"
        attribute "Calibration", "number"   // DES added 
        attribute "Autorpt_DegF", "number"  // DES added
        attribute "Autorpt_Hr", "number"    // DES added
        attribute "Swing", "number"			// CJS added
        attribute "Differential", "number"	// CJS added

		command "switchMode"
		command "switchFanMode"
		command "lowerHeatingSetpoint"
		command "raiseHeatingSetpoint"
		command "lowerCoolSetpoint"
		command "raiseCoolSetpoint"
		command "poll"

		// CJS: fingerprint matched to raw description from my Remotec ZTS-110 and in new zwave format
		// raw descrition:  zw:L type:0806 mfr:5254 prod:0200 model:8031 ver:3.14 zwv:3.67 lib:06 cc:20,31,40,42,43,44,45,47,70,72,81,85,86
		fingerprint mfr: "5254", prod: "0200", model: "8031"
		
		/* CJS: DES has a strange device fingerprint.  Maybe he has a ZTS-100 or a different revision of ZTS-110 
		// DES replaced fingerprint to match the raw descrption from theRomotec ZTS-110
        // raw description for ZTS-110: 	0 0 0x1001 0 0 0 4 0x25 0x27 0x86 0x72
		fingerprint deviceId: "0x1001"
		fingerprint inClusters: "0x25,0x27,0x86,0x72", manufacturer: "Remotec", model: "ZTS-110"
		*/

		/* Original SmartThings fingerprint section
		fingerprint deviceId: "0x08"
		fingerprint inClusters: "0x43,0x40,0x44,0x31"
		fingerprint mfr:"0039", prod:"0011", model:"0001", deviceJoinName: "Honeywell Z-Wave Thermostat" 
		*/
	}

	tiles {
		multiAttributeTile(name:"temperature", type:"generic", width:3, height:2, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("temperature", label:'${currentValue}°', icon: "st.alarm.temperature.normal",
					backgroundColors:[
							// Celsius
							[value: 0, color: "#153591"],
							[value: 7, color: "#1e9cbb"],
							[value: 15, color: "#90d2a7"],
							[value: 23, color: "#44b621"],
							[value: 28, color: "#f1d801"],
							[value: 35, color: "#d04e00"],
							[value: 37, color: "#bc2323"],
							// Fahrenheit
							[value: 40, color: "#153591"],
							[value: 44, color: "#1e9cbb"],
							[value: 59, color: "#90d2a7"],
							[value: 74, color: "#44b621"],
							[value: 84, color: "#f1d801"],
							[value: 95, color: "#d04e00"],
							[value: 96, color: "#bc2323"]
					]
				)
			}
		}
		standardTile("mode", "device.thermostatMode", width:2, height:2, inactiveLabel: false, decoration: "flat") {
			state "off", action:"switchMode", nextState:"...", icon: "st.thermostat.heating-cooling-off"
			state "heat", action:"switchMode", nextState:"...", icon: "st.thermostat.heat"
			state "cool", action:"switchMode", nextState:"...", icon: "st.thermostat.cool"
			state "auto", action:"switchMode", nextState:"...", icon: "st.thermostat.auto"
			state "emergency heat", action:"switchMode", nextState:"...", icon: "st.thermostat.emergency-heat"
			state "...", label: "Updating...",nextState:"...", backgroundColor:"#ffffff"
		}
		standardTile("fanMode", "device.thermostatFanMode", width:2, height:2, inactiveLabel: false, decoration: "flat") {
			state "auto", action:"switchFanMode", nextState:"...", icon: "st.thermostat.fan-auto"
			state "on", action:"switchFanMode", nextState:"...", icon: "st.thermostat.fan-on"
			state "circulate", action:"switchFanMode", nextState:"...", icon: "st.thermostat.fan-circulate"
			state "...", label: "Updating...", nextState:"...", backgroundColor:"#ffffff"
		}
		standardTile("lowerHeatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", action:"lowerHeatingSetpoint", icon:"st.thermostat.thermostat-left"
		}
		valueTile("heatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", label:'${currentValue}° heat', backgroundColor:"#ffffff"
		}
		standardTile("raiseHeatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", action:"raiseHeatingSetpoint", icon:"st.thermostat.thermostat-right"
		}
		standardTile("lowerCoolSetpoint", "device.coolingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "coolingSetpoint", action:"lowerCoolSetpoint", icon:"st.thermostat.thermostat-left"
		}
		valueTile("coolingSetpoint", "device.coolingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "coolingSetpoint", label:'${currentValue}° cool', backgroundColor:"#ffffff"
		}
		standardTile("raiseCoolSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "heatingSetpoint", action:"raiseCoolSetpoint", icon:"st.thermostat.thermostat-right"
		}
		standardTile("thermostatOperatingState", "device.thermostatOperatingState", width: 2, height:1, decoration: "flat") {
			state "thermostatOperatingState", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		standardTile("refresh", "device.thermostatMode", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		
        // CJS added
		valueTile("Swing", "device.Swing", decoration: "flat", width: 2, height: 1) {
			state "Swing", label:'Swing: ${currentValue}°'        
		}
		valueTile("Differential", "device.Differential", decoration: "flat", width: 2, height: 1) {
			state "Differential", label:'Differential: ${currentValue}°'        
		}
		
        // DES added
        valueTile("Calibration", "device.Calibration", decoration: "flat", width: 2, height: 1) {
			state "Calibration", label:'Calibration: ${currentValue}°'        
		}
		valueTile("Autorpt_Hr", "device.Autorpt_Hr", decoration: "flat", width: 2, height: 1) {
			state "Autorpt_Hr", label:'Report: ${currentValue}hr'        
		}        
		valueTile("Autorpt_DegF", "device.Autorpt_DegF", decoration: "flat", width: 2, height: 1) {
			state "Autorpt_DegF", label:'Report: ${currentValue}°F'      
		} 
		valueTile("battery", "device.battery", decoration: "flat", width: 2, height: 1) {
			state "battery", label:'Battery: ${currentValue}%', unit:""
		} 

		main "temperature"
		details(["temperature", "lowerHeatingSetpoint", "heatingSetpoint", "raiseHeatingSetpoint", "lowerCoolSetpoint",
				"coolingSetpoint", "raiseCoolSetpoint", "mode", "fanMode", "thermostatOperatingState", "refresh", 
                // CJS recomended display for consumers
                //"battery"])
                // CJS recomended display for debugging
                "battery", "Swing", "Differential", "Calibration", "Autorpt_Hr", "Autorpt_DegF"])
	}

	//  Get inputs for the configuration (only 5 of the possible configurations are included at this time)
	preferences {
		//   parm 1 = swing  1,2,3,4
		input "swing", "number", title: "Swing: °F from setpoint to engage 1st stage heat",
				range:"1..4", defaultValue: 2, required: true
		//   parm 2 =   1,2,3,4
		input "differential", "number", title: "Differential: °F from swing to engage 2nd stage heat",
				range:"1..4", defaultValue: 2, required: true
		//   parm 13 = calibration  -3,-2,-1,0,1,2, 3 Etc
		input "cal", "number", title: "Calibration: Adjustment to reported temp in °F",
                range:"-10..10", defaultValue: 0, required: true  
		//   parm 12 = autoreport time trigger:  0 = never, 1 = 30min, 2 = 1 hr (default), max 16 = 8hr
		input "RptTime", "number", title: "Auto Report after elapsed time  in 30min increments (ex:0 = never, 2 = 1hr)",
                range:"0..16", defaultValue: 2,
				required: true 
		//   parm 11 Trigger autoreport if room temp different from last report 
		//          1 = 1 deg F,  2 = 2 degree F,  3 = 3,  4 = 4 deg F = default   Set to 1
		input "RptDelta", "number", title: "Auto Report after temp shift in °F",
				range:"0..8", defaultValue: 4, required: true  
	}
}

def installed() {
	log.trace "installed()"
	
    // Configure device    
    def cmds = []
    // CJS the ZTS-110 sends heat pump relay commands on group 1, so removing that to eliminate unexpected BasicSet commands from logs
    cmds << new physicalgraph.device.HubAction(zwave.associationV1.associationRemove(groupingIdentifier:1, nodeId:[zwaveHubNodeId]).format())
	// CJS the ZTS-110 auto-reports on configuration group 3, so adding that
    cmds << new physicalgraph.device.HubAction(zwave.associationV1.associationSet(groupingIdentifier:3, nodeId:[zwaveHubNodeId]).format())
    cmds << new physicalgraph.device.HubAction(zwave.manufacturerSpecificV2.manufacturerSpecificGet().format())
	sendHubCommand(cmds)
	runIn(4, "initialize", [overwrite: true])  // Allow configure command to be sent and acknowledged before proceeding
}

def updated() {
	log.trace "updated()"
    
    // Ensure device settings are in bounds before being set.  If not, return to defaults.
	// BUG: This should be unnecesary now that bounds enforcement was added to the setting UI controls.
    // BUG: Probably should modify this code to see if SETTINGS have changed from STATE, ensure STATE is valid, then fire commands.
    log.trace "Read Settings: ${settings}"
    if(settings.swing > 4 || settings.swing < 1) {settings.swing = 2}  // swing must be between 1 and 4, default 2
    if(settings.differential > 4 || settings.differential < 1) {settings.differential = 2}  // differential must be between 1 and 4, default 2
    if(settings.cal > 10 || settings.cal < -10) {settings.cal = 0}  // calibration must be between -10 and +10, default 0
    if(settings.RptTime > 16 || settings.RptTime < 0) {settings.RptTime = 2} // report time must be between 0 and 16, default 2
    if(settings.RptDelta > 8 || settings.RptDelta < 0) {settings.RptDelta = 4}  // report temp delta must be between 0 and 8, default 4
    log.trace "Settings after bounds validation: ${settings}"
	
	// CJS Merged DES settings writes with latest ST updated() code
	def cmds = []
	// If not set update ManufacturerSpecific data
	if (!getDataValue("manufacturer")) {
		cmds << new physicalgraph.device.HubAction(zwave.manufacturerSpecificV2.manufacturerSpecificGet().format())
	} 
    // BUG:  This should be handled by Install(), but has to stay here in case the user changes out the DH in the IDE. 
    // CJS the ZTS-110 sends heat pump relay commands on group 1, which ST auto-associates to.  Removing that to eliminate unexpected BasicSet commands from logs
    cmds << new physicalgraph.device.HubAction(zwave.associationV1.associationRemove(groupingIdentifier:1, nodeId:[zwaveHubNodeId]).format())
	// CJS the ZTS-110 auto-reports on configuration group 3.  Adding that.
    cmds << new physicalgraph.device.HubAction(zwave.associationV1.associationSet(groupingIdentifier:3, nodeId:[zwaveHubNodeId]).format())
    
    // CJS send out settings
    // BUG:  Want to only send these commands when SETTINGS has drifted from STATE, or STATE has become invalid.
	cmds << new physicalgraph.device.HubAction(zwave.configurationV1.configurationSet(parameterNumber: 1, size: 1, scaledConfigurationValue: settings.swing ).format())
    cmds << new physicalgraph.device.HubAction(zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: settings.differential ).format())
    cmds << new physicalgraph.device.HubAction(zwave.configurationV1.configurationSet(parameterNumber: 13, size: 1, scaledConfigurationValue: settings.cal ).format())
    cmds << new physicalgraph.device.HubAction(zwave.configurationV1.configurationSet(parameterNumber: 12, size: 1, scaledConfigurationValue: settings.RptTime ).format())
    cmds << new physicalgraph.device.HubAction(zwave.configurationV1.configurationSet(parameterNumber: 11, size: 1, scaledConfigurationValue: settings.RptDelta ).format())
	sendHubCommand(cmds)
	runIn(8, "initialize", [overwrite: true])  // Allow configure command to be sent and acknowledged before proceeding
}

def initialize() {
	log.trace "initialize()"

	// Device-Watch simply pings if no device events received for 32min(checkInterval)
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
	unschedule()
	/* CJS Assuming all is working as expected, the every 5 min polling can go
    if (getDataValue("manufacturer") != "Honeywell") {
		runEvery5Minutes("poll")  // This is not necessary for Honeywell Z-wave, but could be for other Z-wave thermostats
	}
    */
	pollDevice()
}

def parse(String description)
{
	def result = null
	if (description == "updated") {
	} else {
		// CJS:  The ZTS-110 returns some invalid zwave cmd responses.  Fixing those here.
        if (description.contains("command: 4005, payload: 2F")){
			// BUG: Should be using endsWith instead of contains, but can't get it to work.
			// Supported modes bitmask MUST NEVER contain 0010 0000, but ZTS-110 sends the bit in response.
			log.debug "FOUND BAD MODE SUPPORTED RESPONSE: $description"
            description = description.replace("payload: 2F", "payload: 0F 00")
            log.debug "MODIFIED MODE SUPPORTED RESPONSE: $description"
		} 
		// Command 4303 (Setpoint Report) sometimes sends bad data.  Other times it reports fine.  Cannot fix.
		// Command 3105 (Temperature Sensor Report) sometimes sends bad data.  Other times it reports fine.  Cannot fix.
		// Command 5303 (Schedule Report) reports very occasionaly and fails to parse. Ignoring as thermostat schedule is not relevant when zwave control is enabled.

		// DES added 0x70: COMMAND_CLASS_CONFIGURATION so this will process configuration reports
   		// DES added 0x80: COMMAND_CLASS_BATTERY so this will process battery reports		
		def zwcmd = zwave.parse(description, [0x42:1, 0x43:2, 0x31:3, 0x70:1, 0x80:1])
		if (zwcmd) {
			result = zwaveEvent(zwcmd)
		} else {
			log.debug "$device.displayName couldn't parse $description"
		}
	}
	if (!result) {
		return []
	}
	return [result]
}

// Event Generation
// DES add battery report handling
def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	def map = [ name: "battery", unit: "%" ]
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "${device.displayName} Low Battery"
		map.isStateChange = true
	} else {
		map.value = cmd.batteryLevel
	}
	state.lastbatt = now()
	log.debug "Got Battery Status: ${map}"
	sendEvent(map)
}

// DES add configuration report handling
def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
	def map = [:]
	def value = cmd.configurationValue[0]
	switch (cmd.parameterNumber) {
		case 1:
			map.name = "Swing"
			map.value = value
			break
        case 2:
			map.name = "Differential"
			map.value = value
			break
        case 11:
			map.name = "Autorpt_DegF"
			map.value = value
			break
		case 12:
			map.name = "Autorpt_Hr"
            map.value = value/2    // report in hours instead of half hours as entered.
			break
		case 13:
			map.name = "Calibration"
			map.value = value
			break
	}
    log.debug "Got Configuration Status: ${map}"
	sendEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv2.ThermostatSetpointReport cmd) {
	def cmdScale = cmd.scale == 1 ? "F" : "C"
	def setpoint = getTempInLocalScale(cmd.scaledValue, cmdScale)
	def unit = getTemperatureScale()
	switch (cmd.setpointType) {
		case 1:
			sendEvent(name: "heatingSetpoint", value: setpoint, unit: unit, displayed: false)
			updateThermostatSetpoint("heatingSetpoint", setpoint)
			break;
		case 2:
			sendEvent(name: "coolingSetpoint", value: setpoint, unit: unit, displayed: false)
			updateThermostatSetpoint("coolingSetpoint", setpoint)
			break;
		default:
			log.debug "unknown setpointType $cmd.setpointType"
			return
	}
	// So we can respond with same format
	state.size = cmd.size
	state.scale = cmd.scale
	state.precision = cmd.precision
	// Make sure return value is not result from above expresion
	return 0
}

def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv3.SensorMultilevelReport cmd) {
	def map = [:]
	if (cmd.sensorType == 1) {
		map.value = getTempInLocalScale(cmd.scaledSensorValue, cmd.scale == 1 ? "F" : "C")
		map.unit = getTemperatureScale()
		map.name = "temperature"
		updateThermostatSetpoint(null, null)
	} else if (cmd.sensorType == 5) {
		map.value = cmd.scaledSensorValue
		map.unit = "%"
		map.name = "humidity"
	}
	sendEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport cmd) {
	def map = [name: "thermostatOperatingState"]
	switch (cmd.operatingState) {
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_IDLE:
			map.value = "idle"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_HEATING:
			map.value = "heating"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_COOLING:
			map.value = "cooling"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_FAN_ONLY:
			map.value = "fan only"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_PENDING_HEAT:
			map.value = "pending heat"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_PENDING_COOL:
			map.value = "pending cool"
			break
		case physicalgraph.zwave.commands.thermostatoperatingstatev1.ThermostatOperatingStateReport.OPERATING_STATE_VENT_ECONOMIZER:
			map.value = "vent economizer"
			break
	}
	// Makes sure we have the correct thermostat mode
	sendHubCommand(new physicalgraph.device.HubAction(zwave.thermostatModeV2.thermostatModeGet().format()))
	sendEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatfanstatev1.ThermostatFanStateReport cmd) {
	def map = [name: "thermostatFanState", unit: ""]
	switch (cmd.fanOperatingState) {
		case 0:
			map.value = "idle"
			break
		case 1:
			map.value = "running"
			break
		case 2:
			map.value = "running high"
			break
	}
	sendEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport cmd) {
	def map = [name: "thermostatMode", data:[supportedThermostatModes: state.supportedModes]]
	switch (cmd.mode) {
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_OFF:
			map.value = "off"
			break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_HEAT:
			map.value = "heat"
			break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_AUXILIARY_HEAT:
			map.value = "emergency heat"
			break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_COOL:
			map.value = "cool"
			break
		case physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport.MODE_AUTO:
			map.value = "auto"
			break
	}
	sendEvent(map)
	updateThermostatSetpoint(null, null)
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeReport cmd) {
	def map = [name: "thermostatFanMode", data:[supportedThermostatFanModes: state.supportedFanModes]]
	switch (cmd.fanMode) {
		case physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeReport.FAN_MODE_AUTO_LOW:
			map.value = "auto"
			break
		case physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeReport.FAN_MODE_LOW:
			map.value = "on"
			break
		case physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeReport.FAN_MODE_CIRCULATION:
			map.value = "circulate"
			break
	}
	sendEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeSupportedReport cmd) {
	log.debug "WE ARE PARSING MODE SUPPORTED REPORT: $cmd"
	def supportedModes = []
	if(cmd.off) { supportedModes << "off" }
	if(cmd.heat) { supportedModes << "heat" }
	if(cmd.cool) { supportedModes << "cool" }
	if(cmd.auto) { supportedModes << "auto" }
	if(cmd.auxiliaryemergencyHeat) { supportedModes << "emergency heat" }

	state.supportedModes = supportedModes
	sendEvent(name: "supportedThermostatModes", value: supportedModes, displayed: false)
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatfanmodev3.ThermostatFanModeSupportedReport cmd) {
	
	log.debug "WE ARE PARSING FAN MODE SUPPORTED REPORT: $cmd"
	def supportedFanModes = []
	if(cmd.auto) { supportedFanModes << "auto" }
	if(cmd.circulation) { supportedFanModes << "circulate" }
	if(cmd.low) { supportedFanModes << "on" }

	state.supportedFanModes = supportedFanModes
	sendEvent(name: "supportedThermostatFanModes", value: supportedFanModes, displayed: false)
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	if (cmd.manufacturerName) {
		updateDataValue("manufacturer", cmd.manufacturerName)
	}
	if (cmd.productTypeId) {
		updateDataValue("productTypeId", cmd.productTypeId.toString())
	}
	if (cmd.productId) {
		updateDataValue("productId", cmd.productId.toString())
	}
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	log.debug "Zwave BasicReport: $cmd"
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.warn "Unexpected zwave command $cmd"
}

// Command Implementations
def poll() {
	// Call refresh which will cap the polling to once every 2 minutes
	refresh()
}

def refresh() {
	// Only allow refresh every 2 minutes to prevent flooding the Zwave network
	def timeNow = now()
	if (!state.refreshTriggeredAt || (2 * 60 * 1000 < (timeNow - state.refreshTriggeredAt))) {
		state.refreshTriggeredAt = timeNow
		// use runIn with overwrite to prevent multiple DTH instances run before state.refreshTriggeredAt has been saved
		runIn(2, "pollDevice", [overwrite: true])
	}
}

def pollDevice() {
	def cmds = []
	cmds << new physicalgraph.device.HubAction(zwave.thermostatModeV2.thermostatModeSupportedGet().format())
	cmds << new physicalgraph.device.HubAction(zwave.thermostatFanModeV3.thermostatFanModeSupportedGet().format())
	cmds << new physicalgraph.device.HubAction(zwave.thermostatModeV2.thermostatModeGet().format())
	cmds << new physicalgraph.device.HubAction(zwave.thermostatFanModeV3.thermostatFanModeGet().format())
	cmds << new physicalgraph.device.HubAction(zwave.sensorMultilevelV2.sensorMultilevelGet().format()) // current temperature
	cmds << new physicalgraph.device.HubAction(zwave.thermostatOperatingStateV1.thermostatOperatingStateGet().format())
	cmds << new physicalgraph.device.HubAction(zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1).format())
	cmds << new physicalgraph.device.HubAction(zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 2).format())
	// CJS configuration gets from DES moved here 
	cmds << new physicalgraph.device.HubAction(zwave.configurationV1.configurationGet(parameterNumber: 1).format()) 
    cmds << new physicalgraph.device.HubAction(zwave.configurationV1.configurationGet(parameterNumber: 2).format())
	cmds << new physicalgraph.device.HubAction(zwave.configurationV1.configurationGet(parameterNumber: 13).format()) 
    cmds << new physicalgraph.device.HubAction(zwave.configurationV1.configurationGet(parameterNumber: 12).format())
    cmds << new physicalgraph.device.HubAction(zwave.configurationV1.configurationGet(parameterNumber: 11).format())
	cmds << new physicalgraph.device.HubAction(zwave.batteryV1.batteryGet().format())
	sendHubCommand(cmds)
}

def raiseHeatingSetpoint() {
	alterSetpoint(true, "heatingSetpoint")
}

def lowerHeatingSetpoint() {
	alterSetpoint(false, "heatingSetpoint")
}

def raiseCoolSetpoint() {
	alterSetpoint(true, "coolingSetpoint")
}

def lowerCoolSetpoint() {
	alterSetpoint(false, "coolingSetpoint")
}

// Adjusts nextHeatingSetpoint either .5° C/1° F) if raise true/false
def alterSetpoint(raise, setpoint) {
	def locationScale = getTemperatureScale()
	def deviceScale = (state.scale == 1) ? "F" : "C"
	def heatingSetpoint = getTempInLocalScale("heatingSetpoint")
	def coolingSetpoint = getTempInLocalScale("coolingSetpoint")
	def targetValue = (setpoint == "heatingSetpoint") ? heatingSetpoint : coolingSetpoint
	def delta = (locationScale == "F") ? 1 : 0.5
	targetValue += raise ? delta : - delta

	def data = enforceSetpointLimits(setpoint, [targetValue: targetValue, heatingSetpoint: heatingSetpoint, coolingSetpoint: coolingSetpoint])
	// update UI without waiting for the device to respond, this to give user a smoother UI experience
	// also, as runIn's have to overwrite and user can change heating/cooling setpoint separately separate runIn's have to be used
	if (data.targetHeatingSetpoint) {
		sendEvent("name": "heatingSetpoint", "value": getTempInLocalScale(data.targetHeatingSetpoint, deviceScale),
				unit: getTemperatureScale(), eventType: "ENTITY_UPDATE", displayed: false)
	}
	if (data.targetCoolingSetpoint) {
		sendEvent("name": "coolingSetpoint", "value": getTempInLocalScale(data.targetCoolingSetpoint, deviceScale),
				unit: getTemperatureScale(), eventType: "ENTITY_UPDATE", displayed: false)
	}
	if (data.targetHeatingSetpoint && data.targetCoolingSetpoint) {
		runIn(5, "updateHeatingSetpoint", [data: data, overwrite: true])
	} else if (setpoint == "heatingSetpoint" && data.targetHeatingSetpoint) {
		runIn(5, "updateHeatingSetpoint", [data: data, overwrite: true])
	} else if (setpoint == "coolingSetpoint" && data.targetCoolingSetpoint) {
		runIn(5, "updateCoolingSetpoint", [data: data, overwrite: true])
	}
}

def updateHeatingSetpoint(data) {
	updateSetpoints(data)
}

def updateCoolingSetpoint(data) {
	updateSetpoints(data)
}

def enforceSetpointLimits(setpoint, data) {
	def locationScale = getTemperatureScale() 
	def minSetpoint = (setpoint == "heatingSetpoint") ? getTempInDeviceScale(40, "F") : getTempInDeviceScale(50, "F")
	def maxSetpoint = (setpoint == "heatingSetpoint") ? getTempInDeviceScale(90, "F") : getTempInDeviceScale(99, "F")
	def deadband = (state.scale == 1) ? 3 : 2  // 3°F, 2°C
	def targetValue = getTempInDeviceScale(data.targetValue, locationScale)
	def heatingSetpoint = null
	def coolingSetpoint = null
	// Enforce min/mix for setpoints
	if (targetValue > maxSetpoint) {
		targetValue = maxSetpoint
	} else if (targetValue < minSetpoint) {
		targetValue = minSetpoint
	}
	// Enforce 3 degrees F deadband between setpoints
	if (setpoint == "heatingSetpoint") {
		heatingSetpoint = targetValue 
		coolingSetpoint = (heatingSetpoint + deadband > getTempInDeviceScale(data.coolingSetpoint, locationScale)) ? heatingSetpoint + deadband : null
	}
	if (setpoint == "coolingSetpoint") {
		coolingSetpoint = targetValue
		heatingSetpoint = (coolingSetpoint - deadband < getTempInDeviceScale(data.heatingSetpoint, locationScale)) ? coolingSetpoint - deadband : null
	}
	return [targetHeatingSetpoint: heatingSetpoint, targetCoolingSetpoint: coolingSetpoint]
}

def setHeatingSetpoint(degrees) {
	if (degrees) {
		state.heatingSetpoint = degrees.toDouble()
		runIn(2, "updateSetpoints", [overwrite: true])
	}
}

def setCoolingSetpoint(degrees) {
	if (degrees) {
		state.coolingSetpoint = degrees.toDouble()
		runIn(2, "updateSetpoints", [overwrite: true])
	}
}

def updateSetpoints() {
	def deviceScale = (state.scale == 1) ? "F" : "C"
	def data = [targetHeatingSetpoint: null, targetCoolingSetpoint: null]
	def heatingSetpoint = getTempInLocalScale("heatingSetpoint")
	def coolingSetpoint = getTempInLocalScale("coolingSetpoint")
	if (state.heatingSetpoint) {
		data = enforceSetpointLimits("heatingSetpoint", [targetValue: state.heatingSetpoint,
				heatingSetpoint: heatingSetpoint, coolingSetpoint: coolingSetpoint])
	}
	if (state.coolingSetpoint) {
		heatingSetpoint = data.targetHeatingSetpoint ? getTempInLocalScale(data.targetHeatingSetpoint, deviceScale) : heatingSetpoint
		coolingSetpoint = data.targetCoolingSetpoint ? getTempInLocalScale(data.targetCoolingSetpoint, deviceScale) : coolingSetpoint
		data = enforceSetpointLimits("coolingSetpoint", [targetValue: state.coolingSetpoint,
				heatingSetpoint: heatingSetpoint, coolingSetpoint: coolingSetpoint])
		data.targetHeatingSetpoint = data.targetHeatingSetpoint ?: heatingSetpoint
	}
	state.heatingSetpoint = null
	state.coolingSetpoint = null
	updateSetpoints(data)
}

def updateSetpoints(data) {
	def cmds = []
	if (data.targetHeatingSetpoint) {
		cmds << new physicalgraph.device.HubAction(zwave.thermostatSetpointV1.thermostatSetpointSet(
					setpointType: 1, scale: state.scale, precision: state.precision, scaledValue: data.targetHeatingSetpoint).format())
	}
	if (data.targetCoolingSetpoint) {
		cmds << new physicalgraph.device.HubAction(zwave.thermostatSetpointV1.thermostatSetpointSet(
					setpointType: 2, scale: state.scale, precision: state.precision, scaledValue: data.targetCoolingSetpoint).format())
	}
	sendHubCommand(cmds)
}

// thermostatSetpoint is not displayed by any tile as it can't be predictable calculated due to
// the device's quirkiness but it is defined by the capability so it must be set, set it to the most likely value
def updateThermostatSetpoint(setpoint, value) {
	def scale = getTemperatureScale()
	def heatingSetpoint = (setpoint == "heatingSetpoint") ? value : getTempInLocalScale("heatingSetpoint")
	def coolingSetpoint = (setpoint == "coolingSetpoint") ? value : getTempInLocalScale("coolingSetpoint")
	def mode = device.currentValue("thermostatMode")
	def thermostatSetpoint = heatingSetpoint    // corresponds to (mode == "heat" || mode == "emergency heat")
	if (mode == "cool") {
		thermostatSetpoint = coolingSetpoint
	} else if (mode == "auto" || mode == "off") {
		// Set thermostatSetpoint to the setpoint closest to the current temperature
		def currentTemperature = getTempInLocalScale("temperature")
		if (currentTemperature > (heatingSetpoint + coolingSetpoint)/2) {
			thermostatSetpoint = coolingSetpoint
		}
	}
	sendEvent(name: "thermostatSetpoint", value: thermostatSetpoint, unit: getTemperatureScale())
}

/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
	log.debug "ping() called"
	// Just get Operating State there's no need to flood more commands
	sendHubCommand(new physicalgraph.device.HubAction(zwave.thermostatOperatingStateV1.thermostatOperatingStateGet().format()))
}

def switchMode() {
	def currentMode = device.currentValue("thermostatMode")
	def supportedModes = state.supportedModes
	// Old version of supportedModes was as string, make sure it gets updated
	if (supportedModes && supportedModes.size() && supportedModes[0].size() > 1) {
		def next = { supportedModes[supportedModes.indexOf(it) + 1] ?: supportedModes[0] }
		def nextMode = next(currentMode)
		runIn(2, "setGetThermostatMode", [data: [nextMode: nextMode], overwrite: true])
	} else {
		log.warn "supportedModes not defined"
		getSupportedModes()
	}
}

def switchToMode(nextMode) {
	def supportedModes = state.supportedModes
	// Old version of supportedModes was as string, make sure it gets updated
	if (supportedModes && supportedModes.size() && supportedModes[0].size() > 1) {
		if (supportedModes.contains(nextMode)) {
			runIn(2, "setGetThermostatMode", [data: [nextMode: nextMode], overwrite: true])
		} else {
			log.debug("ThermostatMode $nextMode is not supported by ${device.displayName}")
		}
	} else {
		log.warn "supportedModes not defined"
		getSupportedModes()
	}
}

def getSupportedModes() {
	def cmds = []
	cmds << new physicalgraph.device.HubAction(zwave.thermostatModeV2.thermostatModeSupportedGet().format())
	log.debug "getSupportedModes: $cmds"
	sendHubCommand(cmds)
}

def switchFanMode() {
	def currentMode = device.currentValue("thermostatFanMode")
	def supportedFanModes = state.supportedFanModes
	// Old version of supportedFanModes was as string, make sure it gets updated
	if (supportedFanModes && supportedFanModes.size() && supportedFanModes[0].size() > 1) {
		def next = { supportedFanModes[supportedFanModes.indexOf(it) + 1] ?: supportedFanModes[0] }
		def nextMode = next(currentMode)
		runIn(2, "setGetThermostatFanMode", [data: [nextMode: nextMode], overwrite: true])
	} else {
		log.warn "supportedFanModes not defined"
		getSupportedFanModes()
	}
}

def switchToFanMode(nextMode) {
	def supportedFanModes = state.supportedFanModes
	// Old version of supportedFanModes was as string, make sure it gets updated
	if (supportedFanModes && supportedFanModes.size() && supportedFanModes[0].size() > 1) {
		if (supportedFanModes.contains(nextMode)) {
			runIn(2, "setGetThermostatFanMode", [data: [nextMode: nextMode], overwrite: true])
		} else {
			log.debug("FanMode $nextMode is not supported by ${device.displayName}")
		}
	} else {
		log.warn "supportedFanModes not defined"
		getSupportedFanModes()
	}
}

def getSupportedFanModes() {
	def cmds = [new physicalgraph.device.HubAction(zwave.thermostatFanModeV3.thermostatFanModeSupportedGet().format())]
	log.debug "getSupportedFanModes: $cmds"
	sendHubCommand(cmds)
}

def getModeMap() { [
	"off": 0,
	"heat": 1,
	"cool": 2,
	"auto": 3,
	"emergency heat": 4
]}

def setThermostatMode(String value) {
	switchToMode(value)
}

def setGetThermostatMode(data) {
	def cmds = [new physicalgraph.device.HubAction(zwave.thermostatModeV2.thermostatModeSet(mode: modeMap[data.nextMode]).format()),
			new physicalgraph.device.HubAction(zwave.thermostatModeV2.thermostatModeGet().format())]
	sendHubCommand(cmds)
}

def getFanModeMap() { [
	"auto": 0,
	"on": 1,
	"circulate": 6
]}

def setThermostatFanMode(String value) {
	switchToFanMode(value)
}

def setGetThermostatFanMode(data) {
	def cmds = [new physicalgraph.device.HubAction(zwave.thermostatFanModeV3.thermostatFanModeSet(fanMode: fanModeMap[data.nextMode]).format()),
			new physicalgraph.device.HubAction(zwave.thermostatFanModeV3.thermostatFanModeGet().format())]
	sendHubCommand(cmds)
}

def off() {
	switchToMode("off")
}

def heat() {
	switchToMode("heat")
}

def emergencyHeat() {
	switchToMode("emergency heat")
}

def cool() {
	switchToMode("cool")
}

def auto() {
	switchToMode("auto")
}

def fanOn() {
	switchToFanMode("on")
}

def fanAuto() {
	switchToFanMode("auto")
}

def fanCirculate() {
	switchToFanMode("circulate")
}

// Get stored temperature from currentState in current local scale
def getTempInLocalScale(state) {
	def temp = device.currentState(state)
	if (temp && temp.value && temp.unit) {
		return getTempInLocalScale(temp.value.toBigDecimal(), temp.unit)
	}
	return 0
}

// get/convert temperature to current local scale
def getTempInLocalScale(temp, scale) {
	if (temp && scale) {
		def scaledTemp = convertTemperatureIfNeeded(temp.toBigDecimal(), scale).toDouble()
		return (getTemperatureScale() == "F" ? scaledTemp.round(0).toInteger() : roundC(scaledTemp))
	}
	return 0
}

def getTempInDeviceScale(state) {
	def temp = device.currentState(state)
	if (temp && temp.value && temp.unit) {
		return getTempInDeviceScale(temp.value.toBigDecimal(), temp.unit)
	}
	return 0
}

def getTempInDeviceScale(temp, scale) {
	if (temp && scale) {
		def deviceScale = (state.scale == 1) ? "F" : "C"
		return (deviceScale == scale) ? temp :
				(deviceScale == "F" ? celsiusToFahrenheit(temp).toDouble().round(0).toInteger() : roundC(fahrenheitToCelsius(temp)))
	}
	return 0
}

def roundC (tempC) {
	return (Math.round(tempC.toDouble() * 2))/2
}