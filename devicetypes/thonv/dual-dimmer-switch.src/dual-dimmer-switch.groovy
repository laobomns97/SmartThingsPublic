metadata {
	definition (name: "Dual Dimmer Switch", namespace: "Thonv", author: "Thonv") {
		capability "Switch"
		capability "Switch Level"
		capability "Configuration"
		capability "Refresh"
		capability "Actuator"
		capability "Sensor"
       
        attribute "switch1", "string"
        attribute "switch2", "string"
        attribute "level1", "string"
        attribute "level2", "string"
        attribute "switchAll", "string"
        attribute "levelControl1", "string"
        attribute "levelControl2", "string"
        

        command "on1"
        command "off1"
        command "on2"
        command "off2"
        command "setLevel1"
        command "setLevel2"
        command "onAll"
        command "offAll"
    
		fingerprint profileId: "0104", deviceId: "0101", inClusters: "0000, 0003, 0004, 0005, 0006, 0008", outClusters: "0003", manufacturer: "Lumi R&D", model: "LM-DZ2"        
	}

	// simulator metadata
	simulator {
		// status messages
		status "on": "on/off: 1"
		status "off": "on/off: 0"

		// reply messages
		reply "zcl on-off on": "on/off: 1"
		reply "zcl on-off off": "on/off: 0"
	}

	tiles {
    	standardTile("switch1", "device.switch1", width: 1, height: 1, canChangeIcon: true) {
			state "off1", label: "Dimmer1", action: "on1",  icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "on1"
        	state "on1",  label: "Dimmer1", action: "off1", icon: "st.switches.light.on", backgroundColor: "#79b821", nextState: "off1"
		}
        valueTile("level1", "device.level1", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "level1", label: 'Dimmer 1 Level ${currentValue}%'
		}
        controlTile("levelControl1", "device.levelControl1", "slider", width: 2, height: 1) {
            state "default", label: 'Dimmer 1 Level ${currentValue}%', action:"setLevel1", backgroundColor:"#79b821"
        }
        
        standardTile("switch2", "device.switch2", width: 1, height: 1, canChangeIcon: true) {
			state "off2", label: "Dimmer2", action: "on2",  icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "on2"
        	state "on2",  label: "Dimmer2", action: "off2", icon: "st.switches.light.on", backgroundColor: "#79b821", nextState: "off2"
		}
        valueTile("level2", "device.level2", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "level2", label: 'Dimmer 2 Level ${currentValue}%'
		}
        controlTile("levelControl2", "device.levelControl2", "slider", width: 2, height: 1) {
            state "default", label: 'Dimmer 2 Level ${currentValue}%',action:"setLevel2", backgroundColor:"#79b821"
        }
        
        standardTile("switchAll", "device.switchAll", canChangeIcon: false) {
            state "onAll", label: "All", action: "offAll", icon: "st.lights.multi-light-bulb-on", backgroundColor: "#79b821", nextState: "offAll"
           state "offAll", label: "All", action: "onAll", icon: "st.lights.multi-light-bulb-off", backgroundColor: "#ffffff", nextState: "onAll"
        }
        
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}


		main (["switchAll"])
		details(["levelControl1","switch1",  "levelControl2","switch2","switchAll", "refresh"])
	}
}
// Parse incoming device messages to generate events
def parse(String description) {
//	log.debug "description is $description"

	def finalResult = isKnownDescription(description)
	if (finalResult != "false") {
		log.info finalResult
		if (finalResult.type == "update") {
			log.info "$device updates: ${finalResult.value}"
		}
		else {
			if (finalResult.type == "switch") {
            	log.info finalResult
            	if (finalResult.srcEP == "01") {
                	state.switch1 = finalResult.value
                    sendEvent(name: "switch1", value: finalResult.value=="on"?"on1":"off1")
                }
                
            	else if (finalResult.srcEP == "03") {
                	state.switch2 = finalResult.value
                    sendEvent(name: "switch2", value: finalResult.value=="on"?"on2":"off2")
                }
                
                
                if (state.switch1 == "off" && state.switch2 == "off" ) {
                	log.debug "offall"
                    sendEvent(name: "switchAll", value: "offAll")
                }
                else {
                	log.debug "onone"
                    sendEvent(name: "switchAll", value: "onAll")
                }
            }
            else if (finalResult.type == "level") {
                if (finalResult.srcEP == "01") {
                	
                    sendEvent(name: "level1", value: finalResult.value)
                    sendEvent(name: "levelControl1", value: finalResult.value)                
                    if (finalResult.value == 0)
                        sendEvent(name: "switch1", value: "off1")
                    else
                        sendEvent(name: "switch1", value: "on1")                
                }
                else if (finalResult.srcEP == "03") {
                
                    sendEvent(name: "level2", value: finalResult.value)
                    sendEvent(name: "levelControl2", value: finalResult.value)                
                    if (finalResult.value == 0)
                        sendEvent(name: "switch2", value: "off2")
                    else
                        sendEvent(name: "switch2", value: "on2")                
                }
            }
		}
	}
	else {
//		log.warn "DID NOT PARSE MESSAGE for description : $description"
//		log.debug parseDescriptionAsMap(description)
	}
}

def off1() {
//	log.debug "0x${device.deviceNetworkId} Endpoint 1"
//	"st cmd 0x${device.deviceNetworkId} ${endpointId} 0x0006 0 {}"
	setLevel1(0)
}

def on1() {
//	log.debug "0x${device.deviceNetworkId} Endpoint 1"
//    "st cmd 0x${device.deviceNetworkId} ${endpointId} 0x0006 1 {}"
	setLevel1(50)
}

def off2() {
//	log.debug "0x${device.deviceNetworkId} Endpoint 1"
//	"st cmd 0x${device.deviceNetworkId} ${endpointId} 0x0006 0 {}"
	setLevel2(0)
}

def on2() {
//	log.debug "0x${device.deviceNetworkId} Endpoint 1"
//    "st cmd 0x${device.deviceNetworkId} ${endpointId} 0x0006 1 {}"
	setLevel2(50)
}

def offAll() {
//	log.debug "0x${device.deviceNetworkId} Endpoint 1"
//	"st cmd 0x${device.deviceNetworkId} ${endpointId} 0x0006 0 {}"
    [
        "st cmd 0x${device.deviceNetworkId} 1 8 0 {00 0000}","delay 200",
        "st cmd 0x${device.deviceNetworkId} 3 8 0 {00 0000}","delay 200",
    ]
}

def onAll() {
//	log.debug "0x${device.deviceNetworkId} Endpoint 1"
//    "st cmd 0x${device.deviceNetworkId} ${endpointId} 0x0006 1 {}"
    [
        "st cmd 0x${device.deviceNetworkId} 1 8 0 {80 0000}","delay 200",
        "st cmd 0x${device.deviceNetworkId} 3 8 0 {80 0000}","delay 200",
    ]
}

def setLevel1(value) {
	value = value as Integer
	sendEvent(name: "level1", value: value)
	sendEvent(name: "levelControl1", value: value)
		setLevelWithRate1(value, "0000")// + on()
}
def setLevel2(value) {
	value = value as Integer
	sendEvent(name: "level2", value: value)
	sendEvent(name: "levelControl2", value: value)
		setLevelWithRate2(value, "0000")// + on()
}

def refresh() {
	[
			"st rattr 0x${device.deviceNetworkId} 1 6 0", "delay 500",
			"st rattr 0x${device.deviceNetworkId} 1 8 0", "delay 500",
        	"st rattr 0x${device.deviceNetworkId} 3 6 0", "delay 500",
			"st rattr 0x${device.deviceNetworkId} 3 8 0", "delay 500",
	]

}

def configure() {
	refresh()
}


private getEndpointId() {
	new BigInteger(device.endpointId, 16).toString()
}

private hex(value, width=2) {
	def s = new BigInteger(Math.round(value).toString()).toString(16)
	while (s.size() < width) {
		s = "0" + s
	}
	s
}

private String swapEndianHex(String hex) {
	reverseArray(hex.decodeHex()).encodeHex()
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

//Need to reverse array of size 2
private byte[] reverseArray(byte[] array) {
	byte tmp;
	tmp = array[1];
	array[1] = array[0];
	array[0] = tmp;
	return array
}

def parseDescriptionAsMap(description) {
	if (description?.startsWith("read attr -")) {
		(description - "read attr - ").split(",").inject([:]) { map, param ->
			def nameAndValue = param.split(":")
			map += [(nameAndValue[0].trim()): nameAndValue[1].trim()]
		}
	}
	else if (description?.startsWith("catchall: ")) {
		def seg = (description - "catchall: ").split(" ")
		def zigbeeMap = [:]
		zigbeeMap += [raw: (description - "catchall: ")]
		zigbeeMap += [profileId: seg[0]]
		zigbeeMap += [clusterId: seg[1]]
		zigbeeMap += [sourceEndpoint: seg[2]]
		zigbeeMap += [destinationEndpoint: seg[3]]
		zigbeeMap += [options: seg[4]]
		zigbeeMap += [messageType: seg[5]]
		zigbeeMap += [dni: seg[6]]
		zigbeeMap += [isClusterSpecific: Short.valueOf(seg[7], 16) != 0]
		zigbeeMap += [isManufacturerSpecific: Short.valueOf(seg[8], 16) != 0]
		zigbeeMap += [manufacturerId: seg[9]]
		zigbeeMap += [command: seg[10]]
		zigbeeMap += [direction: seg[11]]
		zigbeeMap += [data: seg.size() > 12 ? seg[12].split("").findAll { it }.collate(2).collect {
			it.join('')
		} : []]

		zigbeeMap
	}
}

def isKnownDescription(description) {
	if ((description?.startsWith("catchall:")) || (description?.startsWith("read attr -"))) {
		def descMap = parseDescriptionAsMap(description)
		if (descMap.cluster == "0006" || descMap.clusterId == "0006") {
			isDescriptionOnOff(descMap)
		}
		else if (descMap.cluster == "0008" || descMap.clusterId == "0008"){
			isDescriptionLevel(descMap)
		}
		else {
			return "false"
		}
	}
	else if(description?.startsWith("on/off:")) {
		def switchValue = description?.endsWith("1") ? "on" : "off"
		return	[type: "switch", value : switchValue]
	}
	else {
		return "false"
	}
}

def isDescriptionOnOff(descMap) {
	def switchValue = "undefined"
	if (descMap.cluster == "0006") {				//cluster info from read attr
		value = descMap.value
		if (value == "01"){
			switchValue = "on"
		}
		else if (value == "00"){
			switchValue = "off"
		}
	}
	else if (descMap.clusterId == "0006") {
		//cluster info from catch all
		//command 0B is Default response and the last two bytes are [on/off][success]. on/off=00, success=00
		//command 01 is Read attr response. the last two bytes are [datatype][value]. boolean datatype=10; on/off value = 01/00
		if ((descMap.command=="0B" && descMap.raw.endsWith("0100")) || (descMap.command=="01" && descMap.raw.endsWith("1001"))){
			switchValue = "on"
		}
		else if ((descMap.command=="0B" && descMap.raw.endsWith("0000")) || (descMap.command=="01" && descMap.raw.endsWith("1000"))){
			switchValue = "off"
		}
		else if(descMap.command=="07"){
			return	[type: "update", value : "switch (0006) capability configured successfully"]
		}
	}

	if (switchValue != "undefined"){
		return	[type: "switch", value : switchValue, srcEP : descMap.sourceEndpoint]
	}
	else {
		return "false"
	}

}

//@return - false or "success" or level [0-100]
def isDescriptionLevel(descMap) {
	def dimmerValue = -1
	if (descMap.cluster == "0008"){
		//TODO: the message returned with catchall is command 0B with clusterId 0008. That is just a confirmation message
		def value = convertHexToInt(descMap.value)
		dimmerValue = Math.round(value * 100 / 255)
		if(dimmerValue==0 && value > 0) {
			dimmerValue = 1						//handling for non-zero hex value less than 3
		}
	}
	else if(descMap.clusterId == "0008") {
		if(descMap.command=="0B"){
			return	[type: "update", value : "level updated successfully"]					//device updating the level change was successful. no value sent.
		}
		else if(descMap.command=="07"){
			return	[type: "update", value : "level (0008) capability configured successfully"]
		}
	}

	if (dimmerValue != -1){
		return	[type: "level", value : dimmerValue, srcEP : descMap.endpoint]
        
	}
	else {
		return "false"
	}
}
//level config for devices with min reporting interval as 5 seconds and reporting interval if no activity as 1hour (3600s)
//min level change is 01
def reportConfig() {
	[
			"zdo bind 0x${device.deviceNetworkId} 1 1 6 {${device.zigbeeId}} {}", "delay 300",
			"zcl global send-me-a-report 6 0 0x10 5 600 {01}",
			"send 0x${device.deviceNetworkId} 1 1", "delay 100",

            "zdo bind 0x${device.deviceNetworkId} 1 1 8 {${device.zigbeeId}} {}", "delay 300",
            "zcl global send-me-a-report 8 0 0x20 5 600 {01}",
			"send 0x${device.deviceNetworkId} 1 1}", "delay 100",
            
            "zdo bind 0x${device.deviceNetworkId} 1 3 6 {${device.zigbeeId}} {}", "delay 300",
			"zcl global send-me-a-report 6 0 0x10 5 600 {01}",
			"send 0x${device.deviceNetworkId} 1 3", "delay 100",

            "zdo bind 0x${device.deviceNetworkId} 1 3 8 {${device.zigbeeId}} {}", "delay 300",
            "zcl global send-me-a-report 8 0 0x20 5 600 {01}",
			"send 0x${device.deviceNetworkId} 1 3}", "delay 100"
	]
}




def setLevelWithRate1(level, rate) {
	rate = "0000"
	level = convertToHexString(level * 255 / 100) 				//Converting the 0-100 range to 0-FF range in hex
	["st cmd 0x${device.deviceNetworkId} 1 8 0 {$level $rate}"]
}

def setLevelWithRate2(level, rate) {
	rate = "0000"
	level = convertToHexString(level * 255 / 100) 				//Converting the 0-100 range to 0-FF range in hex
	["st cmd 0x${device.deviceNetworkId} 3 8 0 {$level $rate}"]
}

String convertToHexString(value, width=2) {
	def s = new BigInteger(Math.round(value).toString()).toString(16)
	while (s.size() < width) {
		s = "0" + s
	}
	s
}