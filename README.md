# Route Finder
Client-Server app that models a public transport system and fulfills routes administrators' and travelers' needs
- <b>Admins</b> can add/ edit stations, add/ delete routes
- <b>Users</b> have a graphical representation of the available routes and can query the database regarding the optimal route/ routes coupling. In this sense they will specify the starting station, the destination and the moment of departure (implicitly the current time). They will not receive data regarding the stations that are not found in any route.
# Motivation
This project was intended to cumulate all the knowledge we got in "Advanced Programming - Java" and "Database Systems Administration" coures
# Screenshots
<b>Authentication panel</b>
![image](https://user-images.githubusercontent.com/46360165/64067355-04a61300-cc30-11e9-9def-44ee95da850a.png)<br /><br />
<b>Admin view</b>
![image](https://user-images.githubusercontent.com/46360165/64067372-33bc8480-cc30-11e9-9b73-ab8b164d2b20.png)<br /><br />
<b>User view</b>
![image](https://user-images.githubusercontent.com/46360165/64067381-446cfa80-cc30-11e9-91b3-fd605e53e27b.png)
![image](https://user-images.githubusercontent.com/46360165/64067383-4fc02600-cc30-11e9-9ba6-31c4eeec54cb.png)
# Features
- <b>Response time optimization</b><br />
We adapted Dijkstra’s shortest path algorithm, considering both the topological distances and the waiting times in the stations<br /><br />
- <b>Real-time view</b><br />
We simulated a FTP with specialized sockets in sending/ receiving data on specific time intervals<br />
![image](https://user-images.githubusercontent.com/46360165/64067443-353a7c80-cc31-11e9-84d8-cd4c7f2d1e1d.png)
