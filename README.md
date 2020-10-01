# LoggedOn

  Simple authentification plugin for registering and login on players on a Minecraft Server. Protects the non-premium servers from malicious user impersonating legit players.
 
## Features

- Lightweight size and footprint on storage and performance required 
	- *tested on a 1GB RAM, 1 CPU Core Paper Server*	
- Simple database structure that can be easily expanded
	
## Note:
**This is made specifically to work in a low-powered server that gets at most 10 players, and stability and security may be questionable on larger server**
	
## For the future
	* Improve performance even more by integrating and convering existing code to syncronous operations
	* Adding settings in a config file so that the user can tailor his plugin to his needs
	* Add support for multiple languages so that your server appears more proffesional
	* Adding more options for server administators, changing passwords for users, seeing more user stats and many more
	* Adding support for an embeded database for admins that only need a test instance or don't need a fully fledged MySQL server
	* And many more to come

## Instalation

This plugin needs the following to compile:
  * Java 8 or later
  * [Maven](https://maven.apache.org/download.cgi)
  
And the following to fully work in a server:
  * A Bukkit Minecraft Server or any flavor of it you want (for now, development happened in a Paper 1.15.2 server)
  * A MySQL database that supports BLOBs data types (recommending [MySQL](https://dev.mysql.com/downloads/mysql/))  

## License
This program comes as it is and it's released on terms based of [GNU General Public License v3.0](https://choosealicense.com/licenses/gpl-3.0/) 
