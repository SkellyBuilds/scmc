# Welcome to  SCMC  0.9.3.6
Server Client Mod Checker

A simple fabric mod that communicates with server mod menu so that your members can download mods

Still being worked on. Still a work in progress but its good for public release.

## Should you update?
It's a pretty minor update with a few new things. You don't need to update as there hasn't been any security problems or risks fixed/found in SCMC.

## How to use
Not much dependencies other than minecraft 1.20.1 and fabric loader 0.16.0
Just run it in your server and you are fine
## Configuration time!
Using SCMC, there will be a config file named SCMC.js generated just for you :)
What does it contain right now?
This
```js
/* ____    ____              ____      
/\  _`\ /\  _`\    /'\_/`\/\  _`\    
\ \,\L\_\ \ \/\_\ /\      \ \ \/\_\  
 \/_\__ \\ \ \/_/_\ \ \__\ \ \ \/_/_ 
   /\ \L\ \ \ \L\ \\ \ \_/\ \ \ \L\ \
   \ `\____\ \____/ \ \_\\ \_\ \____/
    \/_____/\/___/   \/_/ \/_/\/___/ 
 
 Welcome to the new SCMC configuration file. We recommand you to read the readme.md for more details about using SCMC & it's own integrated configuration system but there are basic summaries for each property below. Becareful on what you do and make sure it doesn't cause any issues for your community 
 The readme can be located at https://github.com/SkellyBuilds/scmc 
 Please be careful while commenting, if you leave extra commas or data, this mod will go kaboom! So treat it properly! 

 Config Version: 0.9.5.7-8c6b82e2*/

 module.exports = {
  "useonlyuniversalmods": true, /*Universal mods basically are both client side & server side mods. So one for both!
If you leave this enabled, server mods will not be used unless you specified otherwise*/

  "cankickplayerswithnomods": true, /*This gives you the choice to allow the mod to kick players if they are missing a mod or otherwise
If you choose false, it won't kick the player but notify the player about the missing mods so they can be aware!*/

  "enablescmccrashes": true, //When something goes critically wrong on sCMC's end, you can change if SCMC can stop your server & make a crash report.
  "usethismodsonly": [], /*This is an array that will only use the mod ids provided for the mod, nothing else.
If you leave it empty, All mods will be used by SCMC unless you configurated it otherwise*/

  "dontusethismods": [], //This will basically blacklist a mod from being registered in SCMC, if you have it usethismods, the blacklist won't be checked since the whitelist is always checked first.
  "usethismods": [], /*Universal mods basically are both client side & server side mods. So one for both!
If you leave this enabled, server mods will not be used unless you specified otherwise*/

  "optionalmods": [], //This list of mods won't be required to download, meaning they won't show up in the missing mod screen and there will be a GUI difference for the mod to clarify via servermodmenu
  "port": 27752 //Which port should this mod use for SCMC to Server Mod Menu communication - Changing the port means you require a domain for port compatibility for SCMC, learn how to do this at https://github.com/SkellyBuilds/scmc/wiki/Getting-started-using-SCMC#how-do-i-use-different-ports-properly
}
```

As stated in the previous update, comments are pretty much now apart of SCMC's config system!
Feel free to read the notes next to the keys in the json for some info about what they do.

***Experiments are also a new thing in this update, you comment in a experiment codename and it will be ran. You can use this to try out new features that haven't been fleshed out the way i would like it.***

### Available Configuration experiments
- Restore Custom (Comments that you write inside the module.exports other than the EXP name) comments which basically add a list of comments you added previously. I will obviously try my best to make this less bad and more seamless but i like a good sleeping schedule. - `EXP-RESTORE-CUSTOM-COMMENTS`

## Change Log
- Added option to disable SCMC crash logs
- Just some additional code for crash reports for later crash problems


## **Thats all for now!** 

