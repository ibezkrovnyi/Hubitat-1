/**
 *  ****************  Device Watchdog Tile Driver  ****************
 *
 *  Design Usage:
 *  This driver formats the Device Watchdog data to be used with Hubitat's Dashboards.
 *
 *  Copyright 2019 Bryan Turcotte (@bptworld)
 *  
 *  This App is free.  If you like and use this app, please be sure to give a shout out on the Hubitat forums to let
 *  people know that it exists!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research (then MORE research)!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  If modifying this project, please keep the above header intact and add your comments/credits below - Thank you! -  @BPTWorld
 *
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *
 *  V1.0.2 - 03/18/19 - Added support for mutiple tiles
 *  V1.0.1 - 02/25/19 - Added Device Status attribute
 *  V1.0.0 - 01/28/19 - Initial release
 */

metadata {
	definition (name: "Device Watchdog Tile", namespace: "BPTWorld", author: "Bryan Turcotte") {
   		capability "Actuator"

		command "sendWatchdogActivityMap1", ["string"]
		command "sendWatchdogActivityMap2", ["string"]
		command "sendWatchdogActivityMap3", ["string"]
		command "sendWatchdogActivityMap4", ["string"]
		command "sendWatchdogActivityMap5", ["string"]
		
		command "sendWatchdogBatteryMap1", ["string"]
		command "sendWatchdogBatteryMap2", ["string"]
		command "sendWatchdogBatteryMap3", ["string"]
		command "sendWatchdogBatteryMap4", ["string"]
		command "sendWatchdogBatteryMap5", ["string"]
		
		command "sendWatchdogStatusMap", ["string"]
		
    	attribute "watchdogActivity1", "string"
		attribute "watchdogActivity2", "string"
		attribute "watchdogActivity3", "string"
		attribute "watchdogActivity4", "string"
		attribute "watchdogActivity5", "string"
		
		attribute "watchdogBattery1", "string"
		attribute "watchdogBattery2", "string"
		attribute "watchdogBattery3", "string"
		attribute "watchdogBattery4", "string"
		attribute "watchdogBattery5", "string"
		
		attribute "watchdogStatus", "string"
	}
	preferences() {    	
        section(""){
			input("fontSize", "text", title: "Font Size", required: true, defaultValue: "40")
            input "debugMode", "bool", title: "Enable logging", required: true, defaultValue: true
        }
    }
}

def sendWatchdogActivityMap1(activityMap1) {
    LOGDEBUG("In Device Watchdog Tile - Received new Activity data!")
	state.activityDevice1 = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.activityDevice1+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${activityMap1}</div>"
	state.activityDevice1+= "</td></tr></table>"
	sendEvent(name: "watchdogActivity1", value: state.activityDevice1, displayed: true)
}

def sendWatchdogActivityMap2(activityMap2) {
    LOGDEBUG("In Device Watchdog Tile - Received new Activity data!")
	state.activityDevice2 = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.activityDevice2+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${activityMap2}</div>"
	state.activityDevice2+= "</td></tr></table>"
	sendEvent(name: "watchdogActivity2", value: state.activityDevice2, displayed: true)
}

def sendWatchdogActivityMap3(activityMap3) {
    LOGDEBUG("In Device Watchdog Tile - Received new Activity data!")
	state.activityDevice3 = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.activityDevice3+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${activityMap3}</div>"
	state.activityDevice3+= "</td></tr></table>"
	sendEvent(name: "watchdogActivity3", value: state.activityDevice3, displayed: true)
}

def sendWatchdogActivityMap4(activityMap4) {
    LOGDEBUG("In Device Watchdog Tile - Received new Activity data!")
	state.activityDevice4 = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.activityDevice4+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${activityMap4}</div>"
	state.activityDevice4+= "</td></tr></table>"
	sendEvent(name: "watchdogActivity4", value: state.activityDevice4, displayed: true)
}

def sendWatchdogActivityMap5(activityMap5) {
    LOGDEBUG("In Device Watchdog Tile - Received new Activity data!")
	state.activityDevice5 = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.activityDevice5+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${activityMap5}</div>"
	state.activityDevice5+= "</td></tr></table>"
	sendEvent(name: "watchdogActivity5", value: state.activityDevice5, displayed: true)
}

def sendWatchdogBatteryMap1(batteryMap1) {
    LOGDEBUG("In Device Watchdog Tile - Received new Battery data!")
	state.batteryDevice1 = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.batteryDevice1+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${batteryMap1}</div>"
	state.batteryDevice1+= "</td></tr></table>"
	sendEvent(name: "watchdogBattery1", value: state.batteryDevice1, displayed: true)
}

def sendWatchdogBatteryMap2(batteryMap2) {
    LOGDEBUG("In Device Watchdog Tile - Received new Battery data!")
	state.batteryDevice2 = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.batteryDevice2+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${batteryMap2}</div>"
	state.batteryDevice2+= "</td></tr></table>"
	sendEvent(name: "watchdogBattery2", value: state.batteryDevice2, displayed: true)
}

def sendWatchdogBatteryMap3(batteryMap3) {
    LOGDEBUG("In Device Watchdog Tile - Received new Battery data!")
	state.batteryDevice3 = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.batteryDevice3+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${batteryMap3}</div>"
	state.batteryDevice3+= "</td></tr></table>"
	sendEvent(name: "watchdogBattery3", value: state.batteryDevice3, displayed: true)
}

def sendWatchdogBatteryMap4(batteryMap4) {
    LOGDEBUG("In Device Watchdog Tile - Received new Battery data!")
	state.batteryDevice4 = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.batteryDevice4+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${batteryMap4}</div>"
	state.batteryDevice4+= "</td></tr></table>"
	sendEvent(name: "watchdogBattery4", value: state.batteryDevice4, displayed: true)
}

def sendWatchdogBatteryMap5(batteryMap5) {
    LOGDEBUG("In Device Watchdog Tile - Received new Battery data!")
	state.batteryDevice5 = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.batteryDevice5+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${batteryMap5}</div>"
	state.batteryDevice5+= "</td></tr></table>"
	
	sendEvent(name: "watchdogBattery5", value: state.batteryDevice5, displayed: true)
}

def sendWatchdogStatusMap(statusMap) {
    LOGDEBUG("In Device Watchdog Tile - Received new Status data!")
	state.statusDevice = "<table width='100%'><tr><td width='10'><td align='left'>"
	state.statusDevice+= "<div style='line-height=50%;margin-top:0em;margin-bottom:0em;font-size:.${fontSize}em;'>${statusMap}</div>"
	state.statusDevice+= "</td></tr></table>"
	sendEvent(name: "watchdogStatus", value: state.statusDevice, displayed: true)
}
	
def LOGDEBUG(txt) {
    try {
    	if (settings.debugMode) { log.debug("${txt}") }
    } catch(ex) {
    	log.error("LOGDEBUG unable to output requested data!")
    }
}
	
