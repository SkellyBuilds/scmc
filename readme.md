# SCMC - 1.20.1
Server Client Mod Checker 

A simple fabric mod that communicates with server mod menu so that your members can download mods

Still being worked on. Still a work in progress but its good for public release.

## How to use
Not much dependencies other than minecraft 1.20.1 and fabric loader 0.16.0
Just run it in your server and you are fine
## How to customize
Using SCMC, there will be a config file named SCMC.json generated just for you :)
What does it contain right now?
This 
```json
{
  "cankickplayerswithnomods": true,
  "useonlyuniversalmods": true,
  "usethismods": [],
  "dontusethismods": []
}
```
Now if you leave this config alone. It should do
* Remove any server side only mods via useonlyuniversalmods
<br><br>*But what if i add some mod names into the usethismods or dontusethismods arrays?
Its pretty self explanatory*
<br><br>
* If you put any mod ids into usethismods array. SCMC will only register those mods only. If the array is empty, all mods are registered (unless useonlyuniversalmods is enabled).
<br><br>
* If you put any mod ids into dontusethismods array. SCMC will not register this mods.
<br><br>
* Turning off cankickplayerswithnomods will allow the player to join the server but they will be notified about missing mods.

Simple enough right? becareful on what you put and make sure you don't leave out any dependencies.

**Thats all for now!** 