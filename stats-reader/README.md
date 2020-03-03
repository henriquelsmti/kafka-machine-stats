This project is used to read output of top and iotop and send to kafka topics.
This project use [Micronaut](https://micronaut.io/) and require Java 11, top, iotop and gradle instaled.


iotop requires sudo privileges to work 

(Ubuntu)

~$ sudo visudo

add this line and save

your-user ALL=(ALL) NOPASSWD:/usr/sbin/iotop
