/**
 *  ****************  Wake Me Up Child App  ****************
 *
 *  Design Usage:
 *  An alarm clock that knows your schedule, creating a better way to wake up. With slowly rising light levels, random announcements and more.
 *	
 *  Copyright 2020 Bryan Turcotte (@bptworld)
 * 
 *  This App is free.  If you like and use this app, please be sure to mention it on the Hubitat forums!  Thanks.
 *
 *  Remember...I am not a programmer, everything I do takes a lot of time and research!
 *  Donations are never necessary but always appreciated.  Donations to support development efforts are accepted via: 
 *
 *  Paypal at: https://paypal.me/bptworld
 *
 *-------------------------------------------------------------------------------------------------------------------
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
 *  App and Driver updates can be found at https://github.com/bptworld/Hubitat/
 *
 * ------------------------------------------------------------------------------------------------------------------------------
 *
 *  Changes:
 *
 *  1.1.3 - 12/04/20 - Updates by @Mavrrick58
 *  1.1.2 - 08/27/20 - Adjusted sunset/sunrise triggers
 *  1.1.1 - 08/25/20 - Added a staggered dim where it will start with the highest value and then each light will join in as its level is reached.
 *  1.1.0 - 08/22/20 - Added sunRestrictions and onDemand to Triggers, other adjustments
 *  ---
 *  1.0.0 - 07/29/20 - Initial release
 *
 */

import groovy.time.TimeCategory
import java.text.SimpleDateFormat

def setVersion(){
    state.name = "Wake Me Up"
	state.version = "1.1.3"
}

definition(
    name: "Wake Me Up Child",
    namespace: "BPTWorld",
    author: "Bryan Turcotte",
    description: "An alarm clock that knows your schedule, creating a better way to wake up. With slowly rising light levels, random announcements and more.",
    category: "",
	parent: "BPTWorld:Wake Me Up",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
    importUrl: "https://raw.githubusercontent.com/bptworld/Hubitat/master/Apps/Wake%20Me%20Up/WMU-child.groovy",
)

preferences {
    page(name: "pageConfig")
}

def pageConfig() {
    dynamicPage(name: "", title: "", install: true, uninstall: true, refreshInterval:0) {
		display() 
        section("Instructions:", hideable: true, hidden: true) {
			paragraph "<b>Notes:</b>"
			paragraph "An alarm clock that knows your schedule, creating a better way to wake up. With slowly rising light levels, random announcements and more."
            paragraph "- This app is designed to work with Hue devices. while other brands may work, some options may have unexpected results."
		}
        
        section(getFormat("header-green", "${getImage("Blank")}"+" Select Trigger Type")) {
            input "days", "enum", title: "Activate on these days", description: "Days to Activate", required: false, multiple: true, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]

            input "sunRestriction", "bool", title: "Use Sunset or Sunrise?", description: "sun", defaultValue:false, submitOnChange:true, width:6
            if(sunRestriction) { 
                input "riseSet", "bool", title: "Sunrise (off) or Sunset (on)", description: "sun", defaultValue:false, submitOnChange:true, width:6
                app.removeSetting("startTime")
                if(riseSet) {
                    paragraph "<b>Sunset Offset</b>"
                    input "setBeforeAfter", "bool", title: "Before (off) or After (on)", defaultValue:false, submitOnChange:true, width:6
                    input "offsetSunset", "number", title: "Offset (minutes)", width:6
                    app.removeSetting("offsetSunrise") 
                } else { 
                    paragraph "<b>Sunrise Offset</b>"
                    input "riseBeforeAfter", "bool", title: "Before (off) or After (on)", defaultValue:false, submitOnChange:true, width:6
                    input "offsetSunrise", "number", title: "Offset (minutes)", width:6
                    app.removeSetting("offsetSunset")
                }
            } else {
                input "startTime", "time", title: "Time to activate", description: "Time", required: false, width:12
                app.removeSetting("sunRestriction") 
            }
            input "onDemand", "capability.switch", title: "Run anytime this Switch is turned On", required: false, multiple: false
        }

        section(getFormat("header-green", "${getImage("Blank")}"+" Lighting Options")) {
            paragraph "If selecting more than one option, be sure to use different devices (lights) or you <i>will</i> see unexpected results."
            input "oSetLC", "bool", defaultValue: false, title: "<b>Turn Light On, Set Level and Color</b>", description: "Light On", submitOnChange: true, width: 6
            input "oDimUp", "bool", defaultValue: false, title: "<b>Slowly Dim Lighting UP</b>", description: "Dim Up", submitOnChange: true, width: 6
            input "oDimDn", "bool", defaultValue: false, title: "<b>Slowly Dim Lighting DOWN</b>", description: "Dim Down", submitOnChange: true
            
            if(oSetLC) {
                paragraph "<hr>"
                paragraph "<b>Turn Light On, Set Level and Color</b>"
                input "setOnLC", "capability.switchLevel", title: "Select dimmers to turn on", required: true, multiple: true
                input "levelLC", "number", title: "On Level (1 to 99)", required: true, multiple: false, defaultValue: 99, range: '1..99'
                input "colorLC", "enum", title: "Color", required: true, multiple:false, options: [
                    ["Soft White":"Soft White - Default"],
                    ["White":"White - Concentrate"],
                    ["Daylight":"Daylight - Energize"],
                    ["Warm White":"Warm White - Relax"],
                    "Red","Green","Blue","Yellow","Orange","Purple","Pink"]
            } else {
                app.removeSetting("setOnLC")
                app.removeSetting("levelLC")
                app.removeSetting("colorLC")
            }

            if(oDimUp) {
                paragraph "<hr>"
                paragraph "<b>Slowly Dim Lighting UP</b>"
                input "slowDimmerUp", "capability.switchLevel", title: "Select dimmer devices to slowly rise", required: true, multiple: true
                input "minutesUp", "number", title: "Takes how many minutes to raise (1 to 60)", required: true, multiple: false, defaultValue:15, range: '1..60'
                input "startLevelHigh", "number", title: "Starting Level (5 to 99)", required: true, multiple: false, defaultValue: 1, range: '5..99'
                input "targetLevelHigh", "number", title: "Target Level (5 to 99)", required: true, multiple: false, defaultValue: 99, range: '5..99'
                input "colorUp", "enum", title: "Color", required: true, multiple:false, options: [
                    ["Soft White":"Soft White - Default"],
                    ["White":"White - Concentrate"],
                    ["Daylight":"Daylight - Energize"],
                    ["Warm White":"Warm White - Relax"],
                    "Red","Green","Blue","Yellow","Orange","Purple","Pink"]
                paragraph "Slowly raising a light level is a great way to wake up in the morning. If you want everything to delay happening until the light reaches its target level, turn this switch on."
                input "oDelay", "bool", defaultValue: false, title: "<b>Delay Until Finished</b>", description: "Future Options", submitOnChange: true
            } else {
                app.removeSetting("slowDimmerUp")
                app.removeSetting("minutesUp")
                app.removeSetting("startLevelHigh")
                app.removeSetting("targetLevelHigh")
                app.removeSetting("colorUp")
            }

            if(oDimDn) {
                paragraph "<hr>"
                paragraph "<b>Slowly Dim Lighting DOWN</b>"
                input "slowDimmerDn", "capability.switchLevel", title: "Select dimmer devices to slowly dim", required: true, multiple: true
                input "minutesDn", "number", title: "Takes how many minutes to dim (1 to 60)", required: true, multiple: false, defaultValue:15, range: '1..60'
                
                input "useMaxLevel", "bool", title: "Use a set starting level for all lights (off) or dim from the current level of each light (on)", defaultValue:false, submitOnChange:true
                if(useMaxLevel) {
                    paragraph "The highest level light will start the process of dimming, each light will join in as the dim level reaches the lights current value"
                    app.removeSetting("startLevelLow")
                } else {
                    input "startLevelLow", "number", title: "Starting Level (5 to 99)", required: true, multiple: false, defaultValue: 99, range: '5..99'
                }
                
                input "targetLevelLow", "number", title: "Target Level (5 to 99)", required: true, multiple: false, defaultValue: 0, range: '5..99'
                input "dimDnOff", "bool", defaultValue: false, title: "<b>Turn dimmer off after target is reached?</b>", description: "Dim Off Options", submitOnChange: true
                input "colorDn", "enum", title: "Color", required: true, multiple:false, options: [
                    ["Soft White":"Soft White - Default"],
                    ["White":"White - Concentrate"],
                    ["Daylight":"Daylight - Energize"],
                    ["Warm White":"Warm White - Relax"],
                    "Red","Green","Blue","Yellow","Orange","Purple","Pink"]
            } else {
                app.removeSetting("slowDimmerDn")
                app.removeSetting("minutesDn")
                app.removeSetting("startLevelLow")
                app.removeSetting("targetLevelLow")
                app.removeSetting("dimDnOff")
                app.removeSetting("colorDn")
            }
        }
        
        section(getFormat("header-green", "${getImage("Blank")}"+" Device Options")) {
            paragraph "Great for turning on/off alarms, lighting, fans, coffee makers, etc."
            input "switchesOn", "capability.switch", title: "Turn these switches ON", required: false, multiple: true
            input "switchesOff", "capability.switch", title: "Turn these switches OFF", required: false, multiple: true
            if(xDate || xDay) input "newMode", "mode", title: "Change Mode", required: false, multiple: false
        }
        
        section(getFormat("header-green", "${getImage("Blank")}"+" Notification Options")) { 
            paragraph "All BPTWorld Apps use <a href='https://community.hubitat.com/t/release-follow-me-speaker-control-with-priority-messaging-volume-controls-voices-and-sound-files/12139' target=_blank>Follow Me</a> to process Notifications.  Please be sure to have Follow Me installed before trying to send any notifications."
            input "useSpeech", "bool", title: "Use Speech through Follow Me", defaultValue:false, submitOnChange:true
            if(useSpeech) input "fmSpeaker", "capability.speechSynthesis", title: "Select your Follow Me device", required: true, submitOnChange:true
            paragraph "<hr>"
            input "pushMessage", "capability.notification", title: "Send a Push notification to certain users", multiple:true, required:false, submitOnChange:true
        }
        
        if(useSpeech || pushMessage) {
            section(getFormat("header-green", "${getImage("Blank")}"+" Message Options")) {
                input "message", "text", title: "Random Message to be spoken - Separate each message with ; (semicolon)",  required: false
                input "oM1List", "bool", defaultValue: false, title: "Show a list view of random messages?", description: "List View", submitOnChange: true
                if(oM1List) {
                    def valuesM1 = "${message}".split(";")
                    listMapM1 = ""
                    valuesM1.each { itemM1 -> listMapM1 += "${itemM1}<br>" }
                    paragraph "${listMapM1}"
                }

                input "oRepeat", "bool", defaultValue: false, title: "<b>Repeat Message?</b>", description: "Repeat Message", submitOnChange: true
                if(oRepeat) {
                    paragraph "Repeat message every X seconds until 'Control Switch' is turned off OR max number of repeats is reached."
                    input "repeatSeconds", "number", title: "Repeat message every X seconds (1 to 600 seconds - 300=5 min, 600=10 min)", required: true, defaultValue:20, range: '1..600', submitOnChange: true
                    input "maxRepeats", "number", title: "Max number of repeats (1 to 100)", required: true, defaultValue:10, range: '1..100', submitOnChange: true
                    if(repeatSeconds) {
                        paragraph "Message will repeat every ${repeatSeconds} seconds until the app on/off Switch is turned off <b>OR</b> the Max number of repeats is reached (${maxRepeats})"
                        repeatTimeSeconds = (repeatSeconds * maxRepeats)
                        int inputNow=repeatTimeSeconds
                        int nDayNow = inputNow / 86400
                        int nHrsNow = (inputNow % 86400 ) / 3600
                        int nMinNow = ((inputNow % 86400 ) % 3600 ) / 60
                        int nSecNow = ((inputNow % 86400 ) % 3600 ) % 60
                        paragraph "In this case, it would take ${nHrsNow} Hours, ${nMinNow} Mins and ${nSecNow} Seconds to reach the max number of repeats (if Control Switch is not turned off)"
                    }
                }
            }

            section(getFormat("header-green", "${getImage("Blank")}"+" Control Switches")) {
                paragraph "Alarm Switch is required anytime speech and/or push are involved. This is how you will stop the alarm from either going off or from the repeating message from continuing. Just like a regular alarm clock, except this one can be controlled with voice assistants, dashboards, etc.!"
                input "controlSwitch", "capability.switch", title: "Set the Alarm to on or off with this switch", required: true, multiple: false
                input "snoozeSwitch", "capability.switch", title: "Snooze Switch - Get a few extra minutes when activating this switch", required: false
                if(snoozeSwitch) {
                    paragraph "Set the snooze time (in minutes)"
                    input "snoozeTime", "number", title: "Delay (1 to 60)", required: true, multiple: false, range: '1..60'
                } else {
                    app.removeSetting("snoozeTime")
                }
            }
        } else {
            app.removeSetting("message")
            app.removeSetting("oM1List")
            app.removeSetting("oRepeat")
            app.removeSetting("repeatSeconds")
            app.removeSetting("maxRepeats")
            app.removeSetting("controlSwitch")
            app.removeSetting("snoozeSwitch")
            app.removeSetting("snoozeTime")
        }
        
        section(getFormat("header-green", "${getImage("Blank")}"+" App Control")) {
            input "pauseApp", "bool", title: "Pause App", defaultValue:false, submitOnChange:true            
            if(pauseApp) {
                if(app.label) {
                    if(!app.label.contains(" (Paused)")) {
                        app.updateLabel(app.label + " (Paused)")
                    }
                }
            } else {
                if(app.label) {
                    app.updateLabel(app.label - " (Paused)")
                }
            }
            paragraph "This app can be enabled/disabled by using a switch. The switch can also be used to enable/disable several apps at the same time."
            input "disableSwitch", "capability.switch", title: "Switch Device(s) to Enable / Disable this app", submitOnChange:true, required:false, multiple:true
        }

		section(getFormat("header-green", "${getImage("Blank")}"+" General")) {
            label title: "Enter a name for this automation", required: false
            input "logEnable", "bool", defaultValue: true, title: "Enable Debug Logging", description: "debugging"
		}
		display2()
	}
}

def installed() {
    log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {	
    if(logEnable) log.debug "Updated with settings: ${settings}"
    unsubscribe()
	unschedule()
    //if(logEnable) runIn(3600, logsOff)
	initialize()
}

def initialize() {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        setDefaults()

        if(controlSwitch) subscribe(controlSwitch, "switch", magicHappensHandler)
        if(snoozeSwitch) subscribe(snoozeSwitch, "switch", snoozeHandler)
        if(startTime) schedule(startTime, magicHappensHandler)
        if(sunRestriction) autoSunHandler()
        if(onDemand) subscribe(onDemand, "switch.on", eventHandler)
    }
}

// *********** Start sunRestriction ***********
def autoSunHandler() {
    // autoSunHandler - This is to trigger AT the exact times with offsets
    if(logEnable) log.debug "In autoSunHandler (${state.version}) - ${app.label}"
    
    def sunriseString = location.sunrise.format("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    def sunsetString = location.sunset.format("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", sunsetString)
    int theOffsetSunset = offsetSunset ?: 1    
    if(setBeforeAfter) {
        state.timeSunset = new Date(sunsetTime.time + (theOffsetSunset * 60 * 1000))
    } else {
        state.timeSunset = new Date(sunsetTime.time - (theOffsetSunset * 60 * 1000))
    }
    
    def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSZ", sunriseString)
    int theOffsetSunrise = offsetSunrise ?: 1
    if(riseBeforeAfter) {
        state.timeSunrise = new Date(sunriseTime.time + (theOffsetSunrise * 60 * 1000))
    } else {
        state.timeSunrise = new Date(sunriseTime.time - (theOffsetSunrise * 60 * 1000))
    }

    if(logEnable) log.debug "In autoSunHandler - sunsetTime: ${sunsetTime} - theOffsetSunset: ${theOffsetSunset} - setBeforeAfter: ${setBeforeAfter}"
    if(logEnable) log.debug "In autoSunHandler - sunriseTime: ${sunriseTime} - theOffsetSunrise: ${theOffsetSunrise} - riseBeforeAfter: ${riseBeforeAfter}"
    if(logEnable) log.debug "In autoSunHandler - ${app.label} - timeSunset: ${state.timeSunset} - timeAfterSunrise: ${state.timeSunrise}"

    // check for new sunset/sunrise times every day at 12:05 am
    schedule("0 5 0 ? * * *", autoSunHandler)
        
    if(riseSet) { schedule(state.timeSunset, runAtSunset) }
    if(!riseSet) { schedule(state.timeSunrise, runAtSunrise) }
}

def runAtSunset() {
    if(logEnable) log.debug "In runAtSunset (${state.version}) - ${app.label} - Starting"
    magicHappensHandler()
}

def runAtSunrise() {
    if(logEnable) log.debug "In runAtSunrise (${state.version}) - ${app.label} - Starting"
    magicHappensHandler()
}
// *********** End sunRestriction ***********

def controlSwitchHandler(evt) {
	if(controlSwitch) {
	    state.controlSwitch = controlSwitch.currentValue("switch")
    } else {
        state.controlSwitch = "on"
    }
    if(logEnable) log.debug "In disableSwitchHandler - Control Switch is set to ${state.controlSwitch}."
}

def snoozeHandler(evt) {
    if(snoozeSwitch) {
        if(logEnable) log.debug "In snoozeHandler (${state.version})"
        state.snoozeSwitch = snoozeSwitch.currentValue("switch")
        if(state.snoozeSwitch == "on"){
            if(logEnable) log.debug "In snoozeHandler - Snooze Switch is on, waiting ${snoozeDelay} minutes."
            snoozeDelay = snoozeTime * 60
            state.pauseForSnooze = "yes"
            runIn(snoozeDelay,magicHappensHandler)
        } else {
            if(logEnable) log.debug "In snoozeHandler - Snooze Switch is set to OFF - GET UP."
        }
    } else {
        state.snoozeSwitch = "off"
    }   
}

def eventHandler(evt) {
    if(logEnable) log.debug "Triggered event initiated passing to magicHappensHandler"
    magicHappensHandler()
}

def magicHappensHandler() {
	if(logEnable) log.debug "In magicHappensHandler (${state.version}) - CS: ${state.controlSwitch}"
    
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        dayOfTheWeekHandler()
        controlSwitchHandler()        
        if(state.controlSwitch == "on" && state.daysMatch) {
            if((oDelay) && (state.snoozeSwitch == "off")) {
                if(logEnable) log.debug "In magicHappensHandler - oDelay - Waiting ${minutesUp} minutes before notifications - snoozeSwitch: ${state.snoozeSwitch} - CS: ${state.controlSwitch}"
                if(minutesUp) state.realSeconds = minutesUp * 60
                if(maxRepeats) state.numRepeats = 1
                if(oDimUp) slowOnHandler()
                if(oDimDn) runIn(state.realSeconds,slowOffHandler)
                if(oSetLC) runIn(state.realSeconds,dimmerOnHandler)
                if(oMessage) runIn(state.realSeconds,messageHandler)
                if(pushMessage) runIn(state.realSeconds,pushHandler)
                if(useSpeech) runIn(state.realSeconds,letsTalk)
                if(oDevice) runIn(state.realSeconds,switchesOnHandler)
                if(oDevice) runIn(state.realSeconds,switchesOffHandler)
                if(newMode) runIn(state.realSeconds, modeHandler)
            } else if(state.snoozeSwitch == "on") {
                if(logEnable) log.debug "In magicHappensHandler - snoozeSwitch: ${state.snoozeSwitch} - CS: ${state.controlSwitch}"
                state.pauseForSnooze = "no"
                if(maxRepeats) state.numRepeats = 1
                if(oMessage) messageHandler()
                if(pushMessage) pushHandler()
                if(useSpeech) letsTalk()
                if(oDevice) switchesOnHandler()
                if(oDevice) switchesOffHandler()
                if(newMode) modeHandler()
            } else {
                if(oDimUp) slowOnHandler()
                if(oDimDn) slowOffHandler()
                if(oSetLC) dimmerOnHandler()
                if(oMessage) messageHandler()
                if(pushMessage) pushHandler()
                if(useSpeech) letsTalk()
                if(oDevice) switchesOnHandler()
                if(oDevice) switchesOffHandler()
                if(newMode) modeHandler()
            }
        }
	}
}

def slowOnHandler(evt) {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        controlSwitchHandler()
        if(state.controlSwitch == "on") {
            if(logEnable) log.debug "In slowOnHandler (${state.version})"
            state.fromWhere = "slowOn"
            state.currentLevel = startLevelHigh ?: 1
            state.color = "${colorUp}"
            setLevelandColorHandler()
            if(minutesUp == 0) return
            seconds = (minutesUp * 60) - 10
            difference = targetLevelHigh - state.currentLevel
            state.dimStep = (difference / seconds) * 10
            if(logEnable) log.debug "slowOnHandler - dimStep: ${state.dimStep} - targetLevel: ${targetLevelHigh} - color: ${state.color}"
            if(oDelay) log.info "${app.label} - Will start talking in ${minutesUp} minutes (${state.realSeconds} seconds)"
            atLeastOneUpOn = false
            runIn(5,dimStepUp)
        } else {
            log.info "${app.label} - Control Switch is OFF - Child app is disabled."
        }
    }
}

def slowOffHandler(evt) {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        controlSwitchHandler()
        if(state.controlSwitch == "on") {
            if(logEnable) log.debug "In slowOffHandler (${state.version})"
            state.fromWhere = "slowOff"
            
            if(useMaxLevel) {
                findHighestCurrentValue()
            } else {            
                state.highestLevel = startLevelLow ?: 99    
            }
            
            state.color = "${colorDn}"
            setLevelandColorHandler()
            if(minutesDn == 0) return
            seconds = (minutesDn * 60) - 10           
            difference = state.highestLevel - targetLevelLow                
            state.dimStep1 = (difference / seconds) * 10
            if(logEnable) log.debug "slowOffHandler - highestLevel: ${state.highestLevel} - targetLevel: ${targetLevelLow} - dimStep1: ${state.dimStep1} - color: ${state.color}"
            atLeastOneDnOn = false
            runIn(5,dimStepDown)
        } else {
            log.info "${app.label} - Control Switch is OFF - Child app is disabled."
        }
    }
}

def findHighestCurrentValue() {
    if(logEnable) log.debug "In findHighestCurrentValue (${state.version})"
    state.highestLevel = 0
    
    slowDimmerDn.each { it->
        checkLevel = it.currentValue("level")
        if(checkLevel > state.highestLevel) state.highestLevel = checkLevel
    }
    
    if(logEnable) log.debug "In findHighestCurrentValue - currentLevel: ${state.currentLevel})"
}

def dimStepUp() {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        if(logEnable) log.debug "-------------------- dimStepUp --------------------"
        if(logEnable) log.debug "In dimStepUp (${state.version})"

        controlSwitchHandler()
        if(state.controlSwitch == "on") {
            if(state.currentLevel < targetLevelHigh) {
                state.currentLevel = state.currentLevel + state.dimStep
                if(state.currentLevel > targetLevelHigh) { state.currentLevel = targetLevelHigh }
                if(logEnable) log.debug "In dimStepUp - Setting currentLevel: ${state.currentLevel} - dimStep: ${state.dimStep} - targetLevel: ${targetLevelHigh}"
                
                slowDimmerUp.each { it->
                    deviceOn = it.currentValue("switch")
                    if(logEnable) log.debug "In dimStepUp - ${it} is: ${deviceOn}"
                    if(deviceOn == "on") {
                        atLeastOneUpOn = true
                        it.setLevel(state.currentLevel)
                    }
                }
                
                if(atLeastOneUpOn) {
                    runIn(10,dimStepUp)
                } else {
                    log.info "${app.label} - All devices are turned off"
                }    
            } else {
                if(logEnable) log.debug "-------------------- End dimStepUp --------------------"
                if(logEnable) log.info "In dimStepUp - Current Level: ${state.currentLevel} has reached targetLevel: ${targetLevelHigh}"
            }
        } else {
            if(logEnable) log.debug "-------------------- Stop dimStepUp --------------------"
            log.info "${app.label} - Control Switch is OFF - Child app is disabled."
        }
    }
}

def dimStepDown() {
    checkEnableHandler()
    if(pauseApp || state.eSwitch) {
        log.info "${app.label} is Paused or Disabled"
    } else {
        if(logEnable) log.debug "-------------------- dimStepDown --------------------"
        if(logEnable) log.debug "In dimStepDown (${state.version})"

        controlSwitchHandler()
        if(state.controlSwitch == "on") {
            if(state.highestLevel > targetLevelLow) {
                state.highestLevel = state.highestLevel - state.dimStep1                   
                if(state.highestLevel < targetLevelLow) { state.highestLevel = targetLevelLow }                   
                if(logEnable) log.debug "In dimStepDown - Starting Level: ${state.highestLevel} - targetLevelLow: ${targetLevelLow}"

                slowDimmerDn.each { it->
                    deviceOn = it.currentValue("switch")
                    int cLevel = it.currentValue("level")
                    int wLevel = state.highestLevel
                    
                    if(logEnable) log.debug "In dimStepDown - ${it} is: ${deviceOn} - cLevel: ${cLevel} - wLevel: ${wLevel}"
                    if(deviceOn == "on") {
                        atLeastOneDnOn = true
                        if(wLevel <= cLevel) { it.setLevel(wLevel) }
                    }
                }

                if(atLeastOneDnOn) {
                    runIn(10,dimStepDown)
                } else {
                    log.info "${app.label} - All devices are turned off"
                }    
            } else {
                if(dimDnOff) slowDimmerDn.off()
                if(logEnable) log.debug "-------------------- End dimStepDown --------------------"
                if(logEnable) log.info "In dimStepDown - Current Level: ${state.currentLevel} has reached targetLevel: ${targetLevelLow}"
            } 
        } else{
            if(logEnable) log.debug "-------------------- Stop dimStepDown --------------------"
            log.info "${app.label} - Control Switch is OFF - Child app is disabled."
        }	
    }
}

def letsTalk() {
    if(logEnable) log.debug "In letsTalk (${state.version}) - Sending the message to Follow Me - theMsg: ${state.theMsg}"
    if(useSpeech && fmSpeaker) {
        fmSpeaker.latestMessageFrom(state.name)
        fmSpeaker.speak(state.theMsg)
    }
    state.theMsg = ""
    if(logEnable) log.debug "In letsTalk - Finished"
}

def messageHandler() {
	if(logEnable) log.debug "In messageHandler (${state.version})"
    def values = "${message}".split(";")
    vSize = values.size()
    count = vSize.toInteger()
    def randomKey = new Random().nextInt(count)
    state.theMsg = values[randomKey]
    if(logEnable) log.debug "In messageHandler - vSize: ${vSize}, randomKey: ${randomKey}, msgRandom: ${state.theMsg}"
}

def switchesOnHandler() {
	switchesOn.each { it ->
		if(logEnable) log.debug "In switchOnHandler (${state.version}) - Turning on ${it}"
		it.on()
	}
}

def switchesOffHandler() {
	switchesOff.each { it ->
		if(logEnable) log.debug "In switchOffHandler (${state.version}) - Turning off ${it}"
		it.off()
	}
}

def dimmerOnHandler() {
	if(logEnable) log.debug "In dimmerOnHandler (${state.version})"
	state.fromWhere = "dimmerOn"
	state.color = "${colorLC}"
	state.onLevel = levelLC
	setLevelandColorHandler()
}

def modeHandler() {
	if(logEnable) log.debug "In modeHandler (${state.version}) - Changing mode to ${newMode}"
	setLocationMode(newMode)
}

def dayOfTheWeekHandler() {
	if(logEnable) log.debug "In dayOfTheWeek (${state.version})"    
    if(days) {
        def df = new java.text.SimpleDateFormat("EEEE")
        df.setTimeZone(location.timeZone)
        def day = df.format(new Date())
        def dayCheck = days.contains(day)

        if(dayCheck) {
            if(logEnable) log.debug "In dayOfTheWeekHandler - Days of the Week Passed"
            state.daysMatch = true
        } else {
            if(logEnable) log.debug "In dayOfTheWeekHandler - Days of the Week Check Failed"
            state.daysMatch = false
        }
    } else {
        state.daysMatch = true
    }
    if(logEnable) log.debug "In dayOfTheWeekHandler - daysMatch: ${state.daysMatch}"
}

def pushHandler(){
	count = 0
	if(count == 0) {
		if(logEnable) log.debug "In pushNow (${state.version})"
		theMessage = "${app.label} - ${state.theMsg}"
		if(logEnable) log.debug "In pushNow - Sending message: ${theMessage}"
    	pushMessage.deviceNotification(theMessage)
		count = count + 1
	}
}

def setLevelandColorHandler() {
	if(logEnable) log.debug "In setLevelandColorHandler (${state.version}) - fromWhere: ${state.fromWhere} - color: ${state.color}"
    
    theColor = state.color
    switch(theColor) {
            case "White":
            hueColor = 52
            saturation = 19
            break;
        case "Daylight":
            hueColor = 53
            saturation = 91
            break;
        case "Soft White":
            hueColor = 23
            saturation = 56
            break;
        case "Warm White":
            hueColor = 20
            saturation = 80
            break;
        case "Blue":
            hueColor = 70
            break;
        case "Green":
            hueColor = 39
            break;
        case "Yellow":
            hueColor = 25
            break;
        case "Orange":
            hueColor = 10
            break;
        case "Purple":
            hueColor = 75
            break;
        case "Pink":
            hueColor = 83
            break;
        case "Red":
            hueColor = 100
            break;
    }
    
    if(hueColor == null) { def hueColor = 0 }
    if(saturation == null) { def saturation = 100 }
 
	if(state.fromWhere == "dimmerOn") {
        state.onLevel = levelLC ?: 99
    	setOnLC.each {
        	if (it.hasCommand('setColor')) {
                value = [hue: hueColor, saturation: saturation, level: state.onLevel]
            	if(logEnable) log.debug "In setLevelandColorHandler - $it.displayName - setColor: $value"
            	it.setColor(value)
        	} else if (it.hasCommand('setLevel')) {
            	if(logEnable) log.debug "In setLevelandColorHandler - $it.displayName - setLevel: $state.onLevel"
            	it.setLevel(state.onLevel)
        	} else {
            	if(logEnable) log.debug "In setLevelandColorHandler - $it.displayName - on()"
            	it.on()
        	}
    	}
	}
    
	if(state.fromWhere == "slowOn") {
        if(logEnable) log.debug "In setLevelandColorHandler - slowOn"
        state.onLevel = startLevelHigh ?: 5
    	slowDimmerUp.each {
        	if (it.hasCommand('setColor')) {
            	value = [hue: hueColor, saturation: saturation, level: state.onLevel]
            	if(logEnable) log.debug "In setLevelandColorHandler - $it.displayName - setColor: $value"
            	it.setColor(value)
        	} else if (it.hasCommand('setLevel')) {
            	if(logEnable) log.debug "In setLevelandColorHandler - $it.displayName - setLevel: $state.onLevel"
            	it.setLevel(state.onLevel)
        	} else {
            	if(logEnable) log.debug "In setLevelandColorHandler - $it.displayName - on()"
            	it.on()
        	}
    	}
	}
    
	if(state.fromWhere == "slowOff") {
        if(logEnable) log.debug "In setLevelandColorHandler - slowOff"
    	slowDimmerDn.each { it ->
            if(useMaxLevel && (it.hasCommand('setColor') || it.hasCommand('setLevel'))) {
                state.onLevel = it.currentValue("level")                   
            } else {
                state.onLevel = startLevelLow ?: 99
            }
            
        	if (it.hasCommand('setColor')) {                
                value = [hue: hueColor, saturation: saturation, level: state.onLevel]
            	if(logEnable) log.debug "In setLevelandColorHandler - $it.displayName - setColor: $value"
            	it.setColor(value)
        	} else if (it.hasCommand('setLevel')) {
            	if(logEnable) log.debug "In setLevelandColorHandler - $it.displayName - setLevel: $state.onLevel"
            	it.setLevel(state.onLevel)
        	} else {
            	if(logEnable) log.debug "In setLevelandColorHandler - $it.displayName - on()"
            	it.on()
        	}
    	}
	}
}

// ********** Normal Stuff **********
def logsOff() {
    log.info "${app.label} - Debug logging auto disabled"
    app?.updateSetting("logEnable",[value:"false",type:"bool"])
}

def checkEnableHandler() {
    state.eSwitch = false
    if(disableSwitch) { 
        if(logEnable) log.debug "In checkEnableHandler - disableSwitch: ${disableSwitch}"
        disableSwitch.each { it ->
            state.eSwitch = it.currentValue("switch")
            if(state.eSwitch == "on") { state.eSwitch = true }
        }
    }
}

def setDefaults(){
	if(state.enablerSwitch == null){state.enablerSwitch = "off"}
	if(state.controlSwitch == null){state.controlSwitch = "off"}
	if(state.snoozeSwitch == null){state.snoozeSwitch = "off"}
	if(state.snoozeDelay == null){state.snoozeDelay = 0}
	if(state.minutesUp == null){state.minutesUp = 0}
	if(state.numRepeats == null){state.numRepeats = 1}
}

def getImage(type) {					// Modified from @Stephack Code
    def loc = "<img src=https://raw.githubusercontent.com/bptworld/Hubitat/master/resources/images/"
    if(type == "Blank") return "${loc}blank.png height=40 width=5}>"
    if(type == "checkMarkGreen") return "${loc}checkMarkGreen2.png height=30 width=30>"
    if(type == "optionsGreen") return "${loc}options-green.png height=30 width=30>"
    if(type == "optionsRed") return "${loc}options-red.png height=30 width=30>"
    if(type == "instructions") return "${loc}instructions.png height=30 width=30>"
    if(type == "logo") return "${loc}logo.png height=60>"
}

def getFormat(type, myText="") {			// Modified from @Stephack Code   
	if(type == "header-green") return "<div style='color:#ffffff;font-weight: bold;background-color:#81BC00;border: 1px solid;box-shadow: 2px 3px #A9A9A9'>${myText}</div>"
    if(type == "line") return "<hr style='background-color:#1A77C9; height: 1px; border: 0;'>"
    if(type == "title") return "<h2 style='color:#1A77C9;font-weight: bold'>${myText}</h2>"
}

def display() {
    setVersion()
    getHeaderAndFooter()
    theName = app.label
    if(theName == null || theName == "") theName = "New Child App"
    section (getFormat("title", "${getImage("logo")}" + " ${state.name} - ${theName}")) {
        paragraph "${state.headerMessage}"
		paragraph getFormat("line")
	}
}

def display2() {
	section() {
		paragraph getFormat("line")
		paragraph "<div style='color:#1A77C9;text-align:center;font-size:20px;font-weight:bold'>${state.name} - ${state.version}</div>"
        paragraph "${state.footerMessage}"
	}       
}

def getHeaderAndFooter() {
    timeSinceNewHeaders()   
    if(state.totalHours > 4) {
        if(logEnable) log.debug "In getHeaderAndFooter (${state.version})"
        def params = [
            uri: "https://raw.githubusercontent.com/bptworld/Hubitat/master/info.json",
            requestContentType: "application/json",
            contentType: "application/json",
            timeout: 30
        ]

        try {
            def result = null
            httpGet(params) { resp ->
                state.headerMessage = resp.data.headerMessage
                state.footerMessage = resp.data.footerMessage
            }
        }
        catch (e) { }
    }
    if(state.headerMessage == null) state.headerMessage = "<div style='color:#1A77C9'><a href='https://github.com/bptworld/Hubitat' target='_blank'>BPTWorld Apps and Drivers</a></div>"
    if(state.footerMessage == null) state.footerMessage = "<div style='color:#1A77C9;text-align:center'>BPTWorld Apps and Drivers<br><a href='https://github.com/bptworld/Hubitat' target='_blank'>Donations are never necessary but always appreciated!</a><br><a href='https://paypal.me/bptworld' target='_blank'><b>Paypal</b></a></div>"
}

def timeSinceNewHeaders() { 
    if(state.previous == null) { 
        prev = new Date()
    } else {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        prev = dateFormat.parse("${state.previous}".replace("+00:00","+0000"))
    }
    def now = new Date()
    use(TimeCategory) {       
        state.dur = now - prev
        state.days = state.dur.days
        state.hours = state.dur.hours
        state.totalHours = (state.days * 24) + state.hours
    }
    state.previous = now
    //if(logEnable) log.warn "In checkHoursSince - totalHours: ${state.totalHours}"
}
