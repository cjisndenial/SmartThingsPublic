/* AEON G2 Micro Switch for Pentair IntelliBrte Pool Lights v1.0
 *
 * Includes:
 *	preferences tile for setting:
 * 		reporting functions (parameter 80)	[ 0:off, 1:hail, 2:report ] set to "Report" for fastest physical updates from the device
 *		control switch type (parameter 120)	[ 0:momentary, 1:toggle, 2:three way] (2 isn't tested, not sure how its supposed to work)
 *	
 * Derived From AEON specific micro driver 1.4
 * Mike Maxwell
 * madmax98087@yahoo.com
 * 2014-12-26
 *
 * CJ Saretto
 * cj@ndenial.com
 * 2017-11-25
 *
	change log
    1.0 2017-11-25
    	-initial version
   
	AEON G2 
	0x20 Basic
	0x25 Switch Binary
	0x2C Scene Actuator Conf
	0x2B Scene Activation
	0x70 Configuration 
	0x72 Manufacturer Specific
	0x73 Powerlevel
	0x77 Node Naming
	0x85 Association
	0x86 Version
	0xEF MarkMark
	0x82 Hail
 *
 */

metadata {
	// Automatically generated. Make future change here.
	definition (name: "aeonIntellibriteSwitch", namespace: "cjisndenial", author: "CJ Saretto") {
		capability "Actuator"
		capability "Switch"
		capability "Refresh"
		capability "Sensor"
        
		attribute "ColorShow"

        command "changeColorShow", ["string"]  //cycle, party, romance, caribbean, american, sunset, royal, blue, green, red, white, magenta
		command "changeColorShow_Cycle"
        command "changeColorShow_Party"
		command "changeColorShow_Romance"
		command "changeColorShow_Caribbean"
		command "changeColorShow_American"
		command "changeColorShow_Sunset"
		command "changeColorShow_Royal"
		command "changeColorShow_Blue"
		command "changeColorShow_Green"
		command "changeColorShow_Red"
		command "changeColorShow_White"
		command "changeColorShow_Magenta"

		//aeon G2 switch (DSC26103-ZWUS)
        fingerprint deviceId: "0x1001", inClusters: "0x25,0x27,0x2C,0x2B,0x70,0x85,0x72,0x86,0xEF,0x82"

	}
    preferences {
       	input name: "param80", type: "enum", title: "State change notice:", description: "Type", required: true, options:["Off","Hail","Report"]
        input name: "param120", type: "enum", title: "Set trigger mode:", description: "Switch type", required: true, options:["Momentary","Toggle","Three Way"]
    }
  

	// simulator metadata
	simulator {
		status "on":  "command: 2003, payload: FF"
		status "off": "command: 2003, payload: 00"

		// reply messages
		reply "2001FF,delay 100,2502": "command: 2503, payload: FF"
		reply "200100,delay 100,2502": "command: 2503, payload: 00"
	}

	// tile definitions
	tiles (scale:2) {
    	multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#53a7c0"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			}
        }
		standardTile("cycle", "device.ColorShow", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "default",label:"Cycle", action:"changeColorShow_Cycle", nextState:"...", backgroundColor:"#ffffff"
			state "cycle", label:"Cycle", nextState:"...", backgroundColor:"#ff0000"
			state "...", label:"Updating...", nextState:"...", backgroundColor:"#ffffff"
		}
		standardTile("party", "device.ColorShow", width:2, height:1, inactiveLabel: false, decoration: "flat") {
			state "default",label:"Party", action:"changeColorShow_Party", nextState:"...", backgroundColor:"#ffffff"
			state "party", label:"Party", nextState:"...", backgroundColor:"#ff0000"
			state "...", label:"Updating...", nextState:"...", backgroundColor:"#ffffff"
		}
		main(["switch"])
        details(["switch"])
	}
 }

def parse(String description) {
	def result = null
	def cmd = zwave.parse(description, [0x20: 1, 0x70: 1])
    log.debug "cmd:${cmd.inspect()}"
	if (cmd.hasProperty("value")) {
		result = createEvent(zwaveEvent(cmd))
	}
    log.debug "res:${result.inspect()}"
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	//aeons return this when in mode 2
	log.debug "basicReport:${cmd.inspect()}"
	return [
    		name				: "switch"
        	,value				: cmd.value ? "on" : "off"
        	,type				: "physical"
    ]
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	// Handles all Z-Wave commands we aren't interested in or don't know about
    log.debug "udf:${cmd.inspect()}"
	return [:]
}

def on() {
	//Don't request a config report when advanced reporting is enabled
	if (settings.param80 in ["Hail","Report"]) zwave.basicV1.basicSet(value: 0xFF).format()
    else delayBetween([zwave.basicV1.basicSet(value: 0xFF).format(), zwave.basicV1.basicGet().format()], 5000)
}

def off() {
   	//Don't request a config report when advanced reporting is enabled
   	if (settings.param80 in ["Hail","Report"]) zwave.basicV1.basicSet(value: 0x00).format()
	else delayBetween ([zwave.basicV1.basicSet(value: 0x00).format(),  zwave.basicV1.basicGet().format()], 5000)
}

def refresh() {
     return zwave.basicV1.basicGet().format()
}

// manage IntelliBrite mode switching
def changeColorShow(String newColorShow) { 

	log.debug "changeColorShow: Called with parameter $newColorShow"

	// If no change from current state, do nothing
	if (newColorShow == ColorShow){
		// If we didn't optimize this, the pool would go dark while the identical show was reprogrammed
		return
	}
	
	// AEON blink parameters
	//1: blink duration in seconds 1-255
    //2: cycle time in .1 seconds (cycle = on + off) 1-255
    def pBlink = []

	//cycle, party, romance, caribbean, american, sunset, royal, blue, green, red, white, magenta
    switch (newColorShow) {
		case "cycle": // 1 pulse
			pBlink.add(1) - 10/1=10 - 1*10=10
            pBlink.add(10)
            break
		case "party": // 2 pulse - 10/2=5 - 5*2=10
            pBlink.add(1)
            pBlink.add(5)
            break
        case "romance": // 3 pulse - 10/3=3.33 - 3*3=9
            pBlink.add(1)
            pBlink.add(3)
            break
		case "caribbean": // 4 pulse - 20/4=5 - 5*4=20
        	pBlink.add(2)
            pBlink.add(5)
            break
		case "american": // 5 pulse - 20/5=4 - 4*5=20
            pBlink.add(2)
            pBlink.add(4)
            break
        case "sunset": // 6 pulse - 20/6=3.33 - 3*6=18
            pBlink.add(2)
            pBlink.add(3)
            break
		case "royal": // 7 pulse - 30/7=4.28 - 4*7=28
        	pBlink.add(3)
            pBlink.add(4)
            break
		case "blue": // 8 pulse - 40/8=5 - 5*8=40
            pBlink.add(4)
            pBlink.add(5)
            break
        case "green": // 9 pulse - 40/9=4.44 - 4*9=36
            pBlink.add(4)
            pBlink.add(9)
            break
		case "red": // 10 pulse
        	pBlink.add(5)
            pBlink.add(5)
            break
		case "white": // 11 pulse
            pBlink.add(11)
            pBlink.add(10)
            break
        case "magenta": // 12 pulse
            pBlink.add(6)
            pBlink.add(5)
            break
		default: 
			// Ignore invalid input
			return
            break
	}

	// Remember that we are cyclingthe Colorshow, which can take up to 10 seconds
	ColorShow = "..."

	// If the switch is currently off, turn it on and wait 1 second   
    if (device.currentValue("switch") == "on") {
		delayBetween([zwave.basicV1.basicSet(value: 0xFF).format(), zwave.configurationV1.configurationSet(configurationValue: pBlink, parameterNumber: 2, size: 2).format()], 1000
	} else{
		zwave.configurationV1.configurationSet(configurationValue: pBlink, parameterNumber: 2, size: 2).format()
	}

	runIn(10, "changeColorShowComplete", [overwrite: true, data: [flag: $newColorShow]])
}

def changeColorShowComplete (data) {
	ColorShow = data.flag
}

// Creating necesary functions for UI buttons to call, as I can't figure out how to pass a string parameter from a button
def changeColorShow_Cycle {changeColorShow("cycle")}
def changeColorShow_Party {changeColorShow("party")}
def changeColorShow_Romance {changeColorShow("romance")}
def changeColorShow_Caribbean {changeColorShow("caribbean")}
def changeColorShow_American {changeColorShow("american")}
def changeColorShow_Sunset {changeColorShow("sunset")}
def changeColorShow_Royal {changeColorShow("royal")}
def changeColorShow_Blue {changeColorShow("blue")}
def changeColorShow_Green {changeColorShow("green")}
def changeColorShow_Red {changeColorShow("red")}
def changeColorShow_White {changeColorShow("white")}
def changeColorShow_Magenta {changeColorShow("magenta")}

//capture preference changes
def updated() {
    log.debug "before settings: ${settings.inspect()}, state: ${state.inspect()}" 
    
    //get requested reporting preferences
    Short p80
    switch (settings.param80) {
		case "Off":
			p80 = 0
            break
		case "Hail":
			p80 = 1
            break
		default:
			p80 = 2	//Report
            break
	}    
    
	//get requested switch function preferences
    Short p120
    switch (settings.param120) {
		case "Momentary":
			p120 = 0
            break
		case "Three Way":
			p120 = 2
            break
		default:
			p120 = 1	//Toggle
            break
	}    
  
	//update if the settings were changed
    if (p80 != state.param80)	{
    	log.debug "update 80:${p80}"
        state.param80 = p80 
        return response(zwave.configurationV1.configurationSet(parameterNumber: 80, size: 1, configurationValue: [p80]).format())
    }
	if (p120 != state.param120)	{
    	log.debug "update 120:${p120}"
        state.param120 = p120
        return response(zwave.configurationV1.configurationSet(parameterNumber: 120, size: 1, configurationValue: [p120]).format())
    }

	log.debug "after settings: ${settings.inspect()}, state: ${state.inspect()}"
}

def configure() {
	settings.param80 = "Report"
    settings.param120 = "Toggle"
	delayBetween([
		zwave.configurationV1.configurationSet(parameterNumber: 80, size: 1, configurationValue: 2).format(),
		zwave.configurationV1.configurationSet(parameterNumber: 120, size: 1, configurationValue: 1).format()
	])
}