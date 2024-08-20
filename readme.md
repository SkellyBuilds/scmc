# SCMC - 1.20.1 - 0.9.3.5
Server Client Mod Checker 

A simple fabric mod that communicates with server mod menu so that your members can download mods

Still being worked on. Still a work in progress but its good for public release.

## How to use
Not much dependencies other than minecraft 1.20.1 and fabric loader 0.16.0
Just run it in your server and you are fine
## How to customize
Using SCMC, there will be a config file named SCMC.js generated just for you :)
What does it contain right now?
This 
```js
 module.exports = {
  "useonlyuniversalmods": true, /*Universal mods basically are both client side & server side mods. So one for both! 
  If you leave this enabled, server mods will not be used unless you specified otherwise*/

  "cankickplayerswithnomods": true, /*This gives you the choice to allow the mod to kick players if they are missing a mod or otherwise 
  If you choose false, it won't kick the player but notify the player about the missing mods so they can be aware!*/

  "usethismodsonly": [], /*This is an array that will only use the mod ids provided for the mod, nothing else. 
  If you leave it empty, All mods will be used by SCMC unless you configurated it otherwise*/

  "dontusethismods": [], //This will basically blacklist a mod from being registered in SCMC, if you have it usethismods, the blacklist won't be checked since the whitelist is always checked first. 
  "usethismods": [], //This is basically an exception, which ignores everything and registers anyways, sort of like a whitelist
  "optionalmods": [] //This list of mods won't be required to download, meaning they won't show up in the missing mod screen and there will be a GUI difference for the mod to clarify via servermodmenu
}
```

Configuration has been converted to javascript for better documentation and future planned updates related.
Don't worry, it will seemlessly convert it so you don't have to set everything back.

I will give more information related to this update soon but I should probably sleep.

### Change Log
- More dynamical exception handling for networking and the Main system
- Added more configs (Not tested, please give feedback via the issues)
- Config from JSON to JavaScript.
- Testing git stuff
**Thats all for now!** 

