sudo chmod +x webapp-1.0-SNAPSHOT.jar
sudo wget http://nginx.org/keys/nginx_signing.key
sudo apt-key add nginx_signing.key
sudo apt-get update
yes | sudo apt-get install nginx
yes | sudo systemctl start nginx
yes | sudo systemctl enable nginx
cd /etc/systemd/system
sudo touch app.service
sudo chmod 766 app.service
sudo echo -e "
[Unit]
Description=Spring Boot App
After=syslog.target
After=network.target[Service]
User=ubuntu
Type=simple

[Service]
ExecStart=/usr/bin/java -jar /home/ubuntu/webapp-1.0-SNAPSHOT.jar
Restart=always
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=webapp
EnvironmentFile=/etc/systemd/system/app.conf
[Install]
WantedBy=multi-user.target" > app.service
sudo systemctl start app
sudo systemctl enable app
echo $(systemctl status app)