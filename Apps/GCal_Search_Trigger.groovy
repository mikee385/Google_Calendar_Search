/**
 *  GCal Search Trigger Child Application v1.3.0
 *  https://raw.githubusercontent.com/HubitatCommunity/Google_Calendar_Search/main/Apps/GCal_Search_Trigger
 *
 *  Credits:
 *  Originally posted on the SmartThings Community in 2017:https://community.smartthings.com/t/updated-3-27-18-gcal-search/80042
 *  Special thanks to Mike Nestor & Anthony Pastor for creating the original SmartApp and DTH
 *      UI/UX contributions made by Michael Struck and OAuth improvements by Gary Spender
 *  Code was ported for use on Hubitat Elevation by cometfish in 2019: https://github.com/cometfish/hubitat_app_gcalsearch
 *  Further improvements made by ritchierich and posted to the HubitatCommunity GitHub Repository so other community members can continue to improve this application
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
def appVersion() { return "1.3.0" }

definition(
    name: "GCal Search Trigger",
    namespace: "HubitatCommunity",
    author: "Mike Nestor & Anthony Pastor, cometfish, ritchierich",
    description: "Integrates Hubitat with Google Calendar events to toggle virtual switch.",
    category: "Convenience",
    parent: "HubitatCommunity:GCal Search",
    documentationLink: "https://community.hubitat.com/t/release-google-calendar-search/71397",
    importUrl: "https://raw.githubusercontent.com/HubitatCommunity/Google_Calendar_Search/main/Apps/GCal_Search_Trigger",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
)

preferences {
	page(name: "selectCalendars")
}

def selectCalendars() {
    def calendars = parent.getCalendarList()
    logDebug "selectCalendars - Calendar list = ${calendars}"
    
    return dynamicPage(name: "selectCalendars", title: "Create new calendar search", install: true, uninstall: true, nextPage: "" ) {
    	section(){
			if (!state.isPaused) {
				input name: "pauseButton", type: "button", title: "Pause", backgroundColor: "Green", textColor: "white", submitOnChange: true
			} else {
				input name: "resumeButton", type: "button", title: "Resume", backgroundColor: "Crimson", textColor: "white", submitOnChange: true
			}
		}
        section("<h3><b><u>Calendar Search</u></b></h3>") {
            //we can't do multiple calendars because the api doesn't support it and it could potentially cause a lot of traffic to happen
            input name: "watchCalendars", title:"", type: "enum", required:true, multiple:false, description: "Which calendar do you want to search?", options:calendars, submitOnChange: true
            paragraph "Multiple search strings may be entered separated by commas.  By default the search string is matched to the calendar title using a starts with search. Include a * to perform a contains search or multiple non consecutive words. For example to match both Kids No School and Kids Late School enter Kids*School."
            input name: "search", type: "text", title: "Search String", required: true, submitOnChange: true
        }
        
        if ( settings.search ) {
            section("<h3><b><u>Schedule</u></b></h3>") {
                paragraph "Calendar searches can be triggered once a day or periodically. Periodic options include every N hours, every N minutes, or you may enter a Cron expression."  
                input name: "whenToRun", type: "enum", title: "When to Run", required: true, options:["Once Per Day", "Periodically"], submitOnChange: true
                if ( settings.whenToRun == "Once Per Day" ) {
                    input name: "timeToRun", type: "time", title: "Time to run", required: true
                }
                if ( settings.whenToRun == "Periodically" ) {
                    input name: "frequency", type: "enum", title: "Frequency", required: true, options:["Hours", "Minutes", "Cron String"], submitOnChange: true
                    if ( settings.frequency == "Hours" ) {
                        input name: "hours", type: "number", title: "Every N Hours: (range 1-12)", range: "1..12", required: true, submitOnChange: true
                        input name: "hourlyTimeToRun", type: "time", title: "Starting at", defaultValue: "08:00", required: true
                    }
                    if ( settings.frequency == "Minutes" ) {
                        input name: "minutes", type: "enum", title: "Every N Minutes", required: true, options:["1", "2", "3", "4", "5", "6", "10", "12", "15", "20", "30"], submitOnChange: true
                    }
                    if ( settings.frequency == "Cron String" ) {
                        paragraph "If not familiar with Cron Strings, please visit <a href='https://www.freeformatter.com/cron-expression-generator-quartz.html#' target='_blank'>Cron Expression Generator</a>"
                        input name: "cronString", type: "text", title: "Enter Cron string", required: true, submitOnChange: true
                    }
                }
                paragraph "If you would like the switch to be toggled in advanced of the calendar event start and/or end times, enter an offset below"
                input name: "offsetStart", type: "decimal", title: "Optional: Event Start Offset in minutes (+/-)", required: false
                input name: "offsetEnd", type: "decimal", title: "Optional: Event End Offset in minutes (+/-)", required: false
            }
        }
        
        if ( settings.search ) {
            section("<h3><b><u>Preferences</u></b></h3>") {
                def defName = settings.search - "\"" - "\"" //.replaceAll(" \" [^a-zA-Z0-9]+","")
                input name: "deviceName", type: "text", title: "Switch Name (Name of the Switch that gets created by this search trigger)", required: true, multiple: false, defaultValue: "${defName} Switch"
                paragraph "Set Switch Default Value to the switch value preferred when there is no calendar entry. If a calendar entry is found, the switch will toggle."
                input name: "switchValue", type: "enum", title: "Switch Default Value", required: true, defaultValue: "on", options:["on","off"]
                input name: "searchField", type: "enum", title: "Calendar field to search", required: true, defaultValue: "title", options:["title","location"]
                input name: "appName", type: "text", title: "Trigger Name (Child Search Trigger App Name)", required: true, multiple: false, defaultValue: "${defName}", submitOnChange: true
                input name: "isDebugEnabled", type: "bool", title: "Enable debug logging?", defaultValue: false, required: false
            }
        }
            
        if ( state.installed ) {
	    	section ("Remove Trigger and Corresponding Device") {
            	paragraph "ATTENTION: The only way to uninstall this trigger and the corresponding device is by clicking the button below.\n" +                		
                		  "Trying to uninstall the corresponding device from within that device's preferences will NOT work."
            }
    	}   
	}       
}

def installed() {
	state.isPaused = false
	initialize()
}

def updated() {
	unschedule()
    
	initialize()
}

def initialize() {
    state.installed = true
   	
    // Sets Label of Trigger
    updateAppLabel()
    
    state.deviceID = "GCal_${app.id}"
    def childDevice = getChildDevice(state.deviceID)
    if (!childDevice) {
        logDebug("initialize - creating device: deviceID: ${state.deviceID}")
        childDevice = addChildDevice("HubitatCommunity", "GCal Switch", "GCal_${app.id}", null, [name: "GCal Switch", label: deviceName])
        log.debug "${offsetStart}, ${offsetEnd}"
        childDevice.updateSetting("isDebugEnabled",[value:"${isDebugEnabled}",type:"bool"])
        childDevice.updateSetting("switchValue",[value:"${switchValue}",type:"enum"])
        childDevice.updateSetting("offsetStart",[value:"${offsetStart.toInteger()}",type:"decimal"])
        childDevice.updateSetting("offsetEnd",[value:"${offsetEnd.toInteger()}",type:"decimal"])
    } else {
        childDevice.updateSetting("switchValue",[value:"${switchValue}",type:"enum"])
        childDevice.updateSetting("offsetStart",[value:"${offsetStart.toInteger()}",type:"decimal"])
        childDevice.updateSetting("offsetEnd",[value:"${offsetEnd.toInteger()}",type:"decimal"])
    }
    if (!state.isPaused) {
        if ( settings.whenToRun == "Once Per Day" ) {
            schedule(timeToRun, poll)
            logDebug("initialize - creating schedule once per day at: ${timeToRun}")
        } else {
            def cronString = ""
            if ( settings.frequency == "Hours" ) {
                def hourlyTimeToRun = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSX", settings.hourlyTimeToRun)
                def hour = hourlyTimeToRun.hours
                def minute = hourlyTimeToRun.minutes
                cronString = "0 ${minute} ${hour}/${hours} * * ? *"
            } else if ( settings.frequency == "Minutes" ) {
                cronString = "0 0/${settings.minutes} * * * ?"
            } else if ( settings.frequency == "Cron String" ) {
                cronString = settings.cronString
            }
            schedule(cronString, poll)
            logDebug("initialize - creating schedule with cron string: ${cronString}")
        }
    }
}

def getDefaultSwitchValue() {
    return settings.switchValue
}

def getNextEvents() {
    def logMsg = []
    def search = (!settings.search) ? "" : settings.search
    def items = parent.getNextEvents(settings.watchCalendars, search)
    logMsg.push("getNextEvents - BEFORE search: ${search}, items: ${items} AFTER ")
    def item = []
    
    if (items && items.size() > 0) {
        def searchTerms = search.toString().split(",")
        def foundMatch = false
        for (int s = 0; s < searchTerms.size(); s++) {
            def searchTerm = searchTerms[s].trim()
            logMsg.push("searchTerm: ${searchTerm}")
            for (int i = 0; i < items.size(); i++) {
                def eventTitle = (settings.searchField == "title") ? items[i].eventTitle : items[i].eventLocation
                logMsg.push("eventTitle: ${eventTitle}")
                if (searchTerm == "*") {
                    foundMatch = true
                    item = items[i]
                    break
                } else if (searchTerm.indexOf("*") > -1) {
                    def searchList = searchTerm.toString().split("\\*")
                    for (int sL = 0; sL < searchList.size(); sL++) {
                        def searchItem = searchList[sL].trim()
                        if (eventTitle.indexOf(searchItem) > -1) {
                            foundMatch = true
                        } else {
                            foundMatch = false
                            break
                        }
                    }
                    
                    if (foundMatch) {
                        item = items[i]
                        break
                    }
                } else {
                    if (eventTitle.startsWith(searchTerm)) {
                        foundMatch = true
                        item = items[i]
                        break
                    }
                }
            }
            
            if (foundMatch) {
                break
            }
        }
    }
    
    logDebug("${logMsg}")
    return item
}

def clearEventCache() {
    parent.clearEventCache(settings.watchCalendars)
}

def poll() {
    def childDevice = getChildDevice(state.deviceID)
    logDebug "poll - childDevice: ${childDevice}"
    childDevice.poll()
}

private uninstalled() {
    logDebug "uninstalled - Delete all child devices"
    
	deleteAllChildren()
}

private deleteAllChildren() {    
    getChildDevices().each {
        logDebug "deleteAllChildren ${it.deviceNetworkId}"
        try {
            deleteChildDevice(it.deviceNetworkId)
        } catch (Exception e) {
            log.error "Fatal exception? $e"
        }
    }
}

private childCreated() {
    def isChild = getChildDevice("GCal_${app.id}") != null
    return isChild
}

private textVersion() {
    def text = "Trigger Version: ${ version() }"
}
private dVersion(){
	def text = "Device Version: ${getChildDevices()[0].version()}"
}

def appButtonHandler(btn) {
    switch(btn) {
        case "pauseButton":
			state.isPaused = true
            break
		case "resumeButton":
			state.isPaused = false
			break
    }
    updated()
}

def updateAppLabel() {
    String appName = settings.appName
    
	if (state.isPaused) {
		appName = appName + '<span style="color:Crimson"> (Paused)</span>'
    }
    app.updateLabel(appName)
}

private logDebug(msg) {
    if (isDebugEnabled != false) {
        if (msg instanceof List && msg.size() > 0) {
            msg = msg.join(", ");
        }
        log.debug "$msg"
    }
}
